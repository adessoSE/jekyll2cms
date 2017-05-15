package de.adesso.service;

import de.adesso.util.Command;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CmdService {

    private final static Logger LOGGER = Logger.getLogger(CmdService.class);

    @Autowired
    private RepoService repoService;
    @Autowired
    private JekyllService jekyllService;

    private String[] arguments;
    private CommandLine parsedCommands;
    private Options options = new Options();

    public void init(ApplicationArguments arguments) {
        this.arguments = extractNonOptionArgs(arguments);

        for (Command cmd : Command.values()) {
            options.addOption(cmd.getShortName(), cmd.getLongName(), false, cmd.getDescription());
        }
    }

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

    private String[] extractNonOptionArgs(ApplicationArguments applicationArguments) {
        List<String> nonOptionArgs = applicationArguments.getNonOptionArgs();
        return nonOptionArgs.toArray(new String[0]);
    }

    private void callMethodByName(String funcName) throws Exception {
        this.getClass().getDeclaredMethod(funcName).invoke(this);
    }
}
