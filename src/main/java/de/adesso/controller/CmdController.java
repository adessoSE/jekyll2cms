package de.adesso.controller;

import de.adesso.enums.Command;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CmdController {

    private final static Logger LOGGER = Logger.getLogger(CmdController.class);

    @Autowired
    private RepoController repoController;
    @Autowired
    private JekyllController jekyllController;

    private String[] arguments;
    private Options options = new Options();

    public void init(ApplicationArguments arguments) {
        this.arguments = extractNonOptionArgs(arguments);

        for (Command cmd : Command.values()) {
            options.addOption(cmd.getShortName(), cmd.getLongName(), false, cmd.getDescription());
        }
    }

    public void parse() {

        LOGGER.info("Starting to parse the commands: ");
        for (String cmd : arguments) {
            LOGGER.info("> " + cmd);
        }

        CommandLineParser parser = new BasicParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, arguments);

            if (cmd.hasOption(Command.INIT.getShortName())) {
                initLocalRepo();
            }

            if (cmd.hasOption(Command.CLONE.getShortName())) {
                cloneRepo();
            }

            if (cmd.hasOption(Command.BUILD.getShortName())) {
                runJekyll();
            }

            if (cmd.hasOption(Command.HELP.getShortName())) {
                help();
            }

            // TODO: Add more options

        } catch (ParseException e) {
            LOGGER.error("Error while parsing arguments", e);
            help();
        }
    }

    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Jekyll2cms", options);
    }

    private void initLocalRepo() {
        repoController.initLocalRepo();
    }

    private void cloneRepo() {
        repoController.cloneRemoteRepo();
    }

    private void runJekyll() {
        jekyllController.runJekyllBuild();
    }

    private String[] extractNonOptionArgs(ApplicationArguments applicationArguments) {
        List<String> nonOptionArgs = applicationArguments.getNonOptionArgs();
        return nonOptionArgs.toArray(new String[0]);
    }
}
