package de.adesso.service;

import de.adesso.persistence.PostMetaData;
import de.adesso.util.Command;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This service initialises and delegates them to the corresponding services.
 */
@Service
public class CmdService {

    private final static Logger LOGGER = Logger.getLogger(CmdService.class);

    private RepoService repoService;
    private JekyllService jekyllService;
    private ParseService parseService;
    private PersistenceService persistenceService;

    private String[] arguments;
    private CommandLine parsedCommands;
    private Options options = new Options();

    @Autowired
    public CmdService(RepoService repoService, JekyllService jekyllService,
                      ParseService parseService, PersistenceService persistenceService) {
        this.repoService = repoService;
        this.jekyllService = jekyllService;
        this.parseService = parseService;
        this.persistenceService = persistenceService;
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
        CommandLineParser parser = new BasicParser();

        try {
            parsedCommands = parser.parse(options, arguments);

        } catch (UnrecognizedOptionException ue) {
            System.err.println("WARN: Cannot find command " + ue.getOption());
            help();
            return false;

        } catch (ParseException e) {
            LOGGER.error("Error while parsing arguments", e);
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
        try {
            for (Command cmd : Command.values()) {
                if (parsedCommands.hasOption(cmd.getShortName())) {
                    System.out.println();
                    System.out.println(":: Executing " + cmd + " :: -------------------");
                    System.out.println();
                    callMethodByName(cmd.getLongName());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error while executing commands", e);
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
    private void migrate() {
        repoService.getAllPosts()
                .forEach(file -> {
                    PostMetaData metaData = parseService.getMetaInformationFromPost(file);
                    //TODO: Save Image and Post into metaData
                    persistenceService.saveMetaData(metaData);
                });

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
