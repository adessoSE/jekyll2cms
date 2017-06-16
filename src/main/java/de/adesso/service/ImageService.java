package de.adesso.service;

import de.adesso.persistence.Image;
import de.adesso.persistence.ImageRepository;
import de.adesso.util.ImageResolution;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.im4java.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This service takes the images from the blog posts and transforms it into different resolutions that are predefined.
 */
@Service
public class ImageService {

    private PersistenceService persistenceService;

    @Value("${imagemagick.convert.path}")
    public String CONVERT_PATH;

    @Value("${imagemagick.convert.output.path}")
    public String OUTPUT_PATH;

    @Value("${imagemagick.commandline.output.path}")
    public String COMMAND_LINE_PATH;

    @Autowired
    public ImageService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * Sets a basic convert command with the provided parameters. The command is used with the ImageMagick processor.
     * @param inputFileName - original file that is to be converted
     * @param newWidth - the new width of input file
     * @param outputFileName - new file name of original file after transformation
     * @return String - returns the command line that is used to run ImageMagick processor.
     */
    public String setBasicConvertCommand(String inputFileName, int newWidth, String outputFileName) {
        return CONVERT_PATH + " " + inputFileName + " -resize " + newWidth + " " + OUTPUT_PATH + outputFileName;
    }

    /**
     * transforms the images of all the posts into the resolutions specified within the ImageResolution enum.
     */
    public void runImageMagickResize() {
        List<Image> images = persistenceService.loadAllImages();
        for(Image image : images) {
            String imageUrl = image.getUrl();
            String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf(".")) ;
            System.out.println(">>>>>>> Processing image " + imageUrl + " with image name " + imageName);
            int imageWidth = 0;

            try {
                imageWidth = new Info(imageUrl, true).getImageWidth();
            } catch (InfoException e) {
                e.printStackTrace();
            }

            for (ImageResolution imgRes : ImageResolution.values()) {
                int imageDefinedWidth = imgRes.getValue();
                if(imageWidth > imageDefinedWidth) {
                    String outputFileName = imageName + "_" + imgRes.toString().toLowerCase() + "_" + imageDefinedWidth + ".png";
                    String commandLine = this.setBasicConvertCommand(imageUrl, imageDefinedWidth, outputFileName);
                    this.runResizeCommand(commandLine);
                }

            }
        }
    }

    /**
     * runs the resize command provided by the parameter.
     * @param commandLine
     */
    private void runResizeCommand(String commandLine) {
        CommandLine cmdLine = CommandLine.parse(commandLine);
        System.out.println(commandLine);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File(COMMAND_LINE_PATH));

        try {
            int exitValue = executor.execute(cmdLine);
            printImageMagickStatus(exitValue);
        } catch (IOException e) {
            System.err.println("Error while executing ImageMagick command.\n" + e);
        }
    }

    private void printImageMagickStatus(int exitValue) {
        if (exitValue == 0) {
            System.out.println();
            System.out.println("ImageMagick was successful");
        } else {
            System.err.println();
            System.err.println("ERROR: ImageMagick was not successful");
        }
    }
}
