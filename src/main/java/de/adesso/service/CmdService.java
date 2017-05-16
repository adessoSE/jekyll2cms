package de.adesso.service;

import de.adesso.util.Command;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * This service initialises and delegates them to the corresponding services.
 */
@Service
public class CmdService {

    private final static Logger LOGGER = Logger.getLogger(CmdService.class);

    private RepoService repoService;
    private JekyllService jekyllService;

    private String[] arguments;
    private CommandLine parsedCommands;
    private Options options = new Options();

    @Autowired
    public CmdService(RepoService repoService, JekyllService jekyllService) {
        this.repoService = repoService;
        this.jekyllService = jekyllService;
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
     */
    public void parse() {
        LOGGER.info("> Starting to parse the commands: ");
        LOGGER.info("Executing following commands: " + Arrays.toString(arguments));

        CommandLineParser parser = new BasicParser();

        try {
            parsedCommands = parser.parse(options, arguments);

        } catch (ParseException e) {
            LOGGER.error("Error while parsing arguments", e);
            help();
        }
    }

    /**
     * Executes the parsed arguments.
     *
     * Important: The local method name and the longName from the
     *            "Command" enumeration has to be the same.
     */
    public void execute() {
        LOGGER.info("> Starting to execute the commands: ");
        try {
            for (Command cmd : Command.values()) {
                if (parsedCommands.hasOption(cmd.getShortName())) {
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
