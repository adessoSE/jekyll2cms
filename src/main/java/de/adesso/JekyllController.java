package de.adesso;

import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class JekyllController {

    private String localRepoPath;

    public void initJekyll() {
        localRepoPath = "downloads/jekyll";
    }

    public void runJekyll() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process jekyllProcess = rt.exec("C:/tools/ruby23/bin/jekyll.bat build --source " + localRepoPath);
            jekyllProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
