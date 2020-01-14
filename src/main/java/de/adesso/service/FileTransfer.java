package de.adesso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

@Service
public class FileTransfer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileTransfer.class);

    @Value("${repository.local.htmlposts.path}")
    private String LOCAL_HTML_POSTS;

    @Value("${repository.local.image.path}")
    private String LOCAL_SITE_IMAGE;

    @Value("${repository.local.image.destination.path}")
    private String LOCAL_DEST_IMAGE;

    /**
     * Copy a file
     *
     * @param source Source of the file
     * @param dest   Destination of the file
     */
    void copyFile(File source, File dest) {
        try {
            if (source.lastModified() != dest.lastModified()) {
                LOGGER.info("Copy file from " + source.getAbsolutePath() + " to " + dest.getAbsolutePath());
                if (!dest.exists()) {
                    dest.getParentFile().mkdir();
                }
                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES);
            }
        } catch (IOException e) {
            LOGGER.error("An error occured while copying generated XML files to destinantion");
        }
    }

    /*
     * New: 2017-08-18
     * Function to delete all the XML-Files
     * from the _site/blog-post/YYYY-MM_DD/...
     * Folder
     */
    void deleteXmlFromSiteFolder() {
        LOGGER.info("Paths: " + Paths.get(LOCAL_HTML_POSTS));
        try (Stream<Path> stream = Files.find(Paths.get(LOCAL_HTML_POSTS), 5,
                (path, attr) -> path.getFileName().toString().endsWith(".xml"))) {
            stream.forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void moveGeneratedImages(File srcFolder, File destFolder){
        if(srcFolder.isDirectory()){

            if(!destFolder.exists()){
                destFolder.mkdir();
            }

            String files[] = srcFolder.list();
            for (String file : files){
                File srcFile = new File(srcFolder, file);
                File destFile = new File(destFolder, file);

                moveGeneratedImages(srcFile, destFile);
            }
        }
        else{
            try {
                Files.copy(srcFolder.toPath(), destFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Copy Image "+ destFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void deleteImages (File source) {
        if (source.isDirectory()) {
            try {
                Files.walkFileTree(Paths.get(source.getAbsolutePath()), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exec) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
