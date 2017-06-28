package de.adesso.service;

import de.adesso.util.Command;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service initialises and delegates them to the corresponding services.
 */
@Service
public class CmdService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmdService.class);

    private RepoService repoService;
    private JekyllService jekyllService;
    private PersistenceService persistenceService;
    private ImageService imageService;

    private String[] arguments;
    private CommandLine parsedCommands;
    private Options options = new Options();

    @Autowired
    public CmdService(RepoService repoService, JekyllService jekyllService,
                      PersistenceService persistenceService,
                      ImageService imageService) {
        this.repoService = repoService;
        this.jekyllService = jekyllService;
        this.persistenceService = persistenceService;
        this.imageService = imageService;
    }

    /**
     * Initialises the CmdService by saving all not spring related arguments from the main
     * method and by adding all commands from the "Command" enumeration as an option.
     */
    public void init(ApplicationArguments arguments) {
        this.arguments = extractNonOptionArgs(arguments);

        for (Command cmd : Command.values()) {
            options.addOption(cmd.getShortName(), cmd.getLongName(), false, cmd.getDescription());
        }
    }

    /**
     * Parses all arguments by trying to find a fitting option for each one.
     *
     * @return true if all commands could be parsed
     */
    public boolean parse() {
        String method = "parse";
        CommandLineParser parser = new BasicParser();

        try {
            parsedCommands = parser.parse(options, arguments);

        } catch (UnrecognizedOptionException ue) {
            LOGGER.warn("In method {}: Cannot find command. Error message: {}", method, ue.getOption());
            help();
            return false;

        } catch (ParseException e) {
            LOGGER.error("In method {}: Error while parsing arguments. Error message: {}", method, e.getMessage());
            help();
            return false;
        }

        return true;
    }

    /**
     * Executes the parsed arguments.
     * <p>
     * Important: The local method name and the longName from the
     * "Command" enumeration has to be the same.
     */
    public void execute() {
        String method = "execute";
        try {
            for (Command cmd : Command.values()) {
                if (parsedCommands.hasOption(cmd.getShortName())) {
                    LOGGER.info("Executing: {}...", cmd);
                    callMethodByName(cmd.getLongName());
                }
            }
        } catch (Exception e) {
            LOGGER.error("In method " + method + ": Error while executing commands.", e);
        }
    }

    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar jekyll2cms.jar", options);
    }

    @SuppressWarnings("unused")
    private void cloneRepo() {
        repoService.cloneRemoteRepo();
    }

    @SuppressWarnings("unused")
    private void build() {
        jekyllService.runJekyllBuild();
    }

    @SuppressWarnings("unused")
    private void update() {
        persistenceService.updateDatabase();
        //imageService.transformAllImages();
    }

    /**
     * Filters out all spring related arguments.
     */
    private String[] extractNonOptionArgs(ApplicationArguments applicationArguments) {
        List<String> nonOptionArgs = applicationArguments.getNonOptionArgs();
        return nonOptionArgs.toArray(new String[0]);
    }

    private void callMethodByName(String funcName) throws Exception {
        this.getClass().getDeclaredMethod(funcName).invoke(this);
    }
}
