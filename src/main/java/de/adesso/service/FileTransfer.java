package de.adesso.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
public class FileTransfer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileTransfer.class);

    /**
     * Copy a file
     *
     * @param source Source of the file
     * @param dest   Destination of the file
     */
    void copyFile(File source, File dest) {
        try {
            if (source.lastModified() != dest.lastModified()) {
                LOGGER.debug("Copy file from " + source.getAbsolutePath() + " to " + dest.getAbsolutePath());
                if (!dest.exists()) {
                    dest.getParentFile().mkdir();
                }
                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES);
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while copying generated XML files to destination.", e);
            LOGGER.error("Exiting jekyll2cms.");
            System.exit(36);
        }
    }

    void moveGeneratedImages(File srcFolder, File destFolder) {
        if (srcFolder.isDirectory()) {

            if (!destFolder.exists()) {
                destFolder.mkdir();
            }

            String files[] = srcFolder.list();
            for (String file : files) {
                File srcFile = new File(srcFolder, file);
                File destFile = new File(destFolder, file);

                moveGeneratedImages(srcFile, destFile);
            }
        } else {
            try {
                Files.copy(srcFolder.toPath(), destFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LOGGER.debug("Copy Image " + destFolder);
            } catch (IOException e) {
                LOGGER.error("An error occurred while copying images to destination.", e);
                LOGGER.error("Exiting jekyll2cms.");
                System.exit(39);
            }
        }
    }
}
