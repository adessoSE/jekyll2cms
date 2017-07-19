package de.adesso.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.adesso.persistence.Author;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This service handles all parsing functionalities of the authors.yml file.
 */
@Service
public class AuthorsYamlService {

    /**
     * Parses the given authors.yml file to a list of Authors.
     * @param authorsYamlFile - authors.yml file
     * @return List - a list of author objects.
     */
    public List<Author> parseAuthorsYamlFile(String authorsYamlFile) {
        List<Author> authors = new ArrayList<>();
        Author author = null;
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonNode root = null;
        try {
            root = mapper.readTree(new File(authorsYamlFile));
            Iterator<JsonNode> authorIterator = root.iterator();
            while(authorIterator.hasNext()) {
                JsonNode authorNode = authorIterator.next();
                author = mapper.treeToValue(authorNode, Author.class);
                authors.add(author);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return authors;
    }

    /**
     * Finds a specified author name in the given given yaml file and maps the author to an Auhtor object
     * @param authorsYamlFile - authors.yml file
     * @param name - authors name under which that authors information lives.
     * @return Author - author object
     */
    public Author findAuthorInAuthorsYamlFile(String authorsYamlFile, String name) {
        Author author = null;
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonNode root = null;
        JsonNode authorNode = null;
        try {
            root = mapper.readTree(new File(authorsYamlFile));
            authorNode = root.findPath(name);
            if(authorNode != null) {
                author = mapper.treeToValue(authorNode, Author.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return author;
    }

}
