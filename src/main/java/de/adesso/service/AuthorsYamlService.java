package de.adesso.service;

import de.adesso.persistence.Author;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorsYamlService {

    public List<Author> parseAuthorsYaml(String yamlFile) {
        List<Author> authors = new ArrayList<>();

        Yaml yaml = new Yaml(new Constructor(List.class));
        List authorsYamlAsList = null;
        InputStream fin = null;

        try {
            fin = new FileInputStream(new File(yamlFile));
            authorsYamlAsList = (List) yaml.load(fin);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (Exception e) {
                    // no-op
                }
            }
        }

        for(int i = 0; i < authorsYamlAsList.size(); i++) {

            // for explanation of Map<?, ?>... go to https://stackoverflow.com/a/13387897
            Map<?, ?> authorInfos = (LinkedHashMap<?, ?>) authorsYamlAsList.get(i);
            Author author = new Author();
            for (Map.Entry<?, ?> entry : authorInfos.entrySet()) {


                String key = (String) entry.getKey();
                if(key.equals("git_username")) author.setGitUsername((String) entry.getValue());

                if(key.equals("first_name")) author.setFirstName((String) entry.getValue());

                if(key.equals("last_name")) author.setLastName((String) entry.getValue());

                if(key.equals("email")) author.setEmailAddress((String) entry.getValue());

                if(key.equals("bio")) author.setBio((String) entry.getValue());

                if(key.equals("picture_url")) author.setPictureUrl((String) entry.getValue());

                if(key.equals("github")) author.setGithubUrl((String) entry.getValue());

            }
            authors.add(author);
            System.out.println(author);
            System.out.println("=============================");

        }
        return authors;

    }
}
