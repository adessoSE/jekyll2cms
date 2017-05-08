package de.adesso.controller;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CmdController {

    private final static Logger LOGGER = Logger.getLogger(CmdController.class);

    @Autowired private RepoController repoController;
    @Autowired private JekyllController jekyllController;

    private ApplicationArguments arguments;
    private Options options = new Options();

    public void init(ApplicationArguments arguments) {
        this.arguments = arguments;

        options.addOption("h", "help", false, "Show help");
        options.addOption("c", "clone", false, "Clone remote repository");
        options.addOption("b", "build", false, "Run jekyll build command locally");
    }

    public void parse() {

        LOGGER.info("Starting to parse the commands: ");
        for (String cmd : extractNonOptionArgs(arguments)) {
            LOGGER.info("> " + cmd);
        }

        CommandLineParser parser = new BasicParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, extractNonOptionArgs(arguments));

            if (cmd.hasOption("i")) {
                initLocalRepo();
            }

            if (cmd.hasOption("c")) {
                cloneRepo();
            }

            if (cmd.hasOption("b")) {
                runJekyll();
            }

            if (cmd.hasOption("h")) {
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
