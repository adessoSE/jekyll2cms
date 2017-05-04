package de.adesso;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

@Controller
public class JekyllController {

    private String localRepoPath;

    public void initJekyll() {
        localRepoPath = "downloads/jekyll";
    }

    public void runJekyll() {

        String line = "C:/tools/ruby23/bin/jekyll.bat build";
        CommandLine cmdLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File(localRepoPath));
        try {
            int exitValue = executor.execute(cmdLine);
            System.out.println("// " + exitValue);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
