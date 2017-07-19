package de.adesso.service;

import de.adesso.persistence.Image;
import de.adesso.util.ImageResolution;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.im4java.core.Info;
import org.im4java.core.InfoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This service takes the images from the blog posts and transforms it into different resolutions that are predefined.
 */
@Service
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);


    @Value("${imagemagick.convert.path}")
    public String CONVERT_PATH;

    @Value("${imagemagick.convert.output.path}")
    public String OUTPUT_PATH;

    @Value("${imagemagick.commandline.output.path}")
    public String COMMAND_LINE_PATH;

    /**
     * Sets a basic convert command with the provided parameters. The command is used with the ImageMagick processor.
     *
     * @param inputFileName  - original file that is to be converted
     * @param newWidth       - the new width of input file
     * @param outputFileName - new file name of original file after transformation
     * @return String - returns the command line that is used to run ImageMagick processor.
     */
    public String setBasicConvertCommand(String inputFileName, int newWidth, String outputFileName) {
        return CONVERT_PATH + " " + inputFileName + " -resize " + newWidth + " " + OUTPUT_PATH + outputFileName;
    }

    /**
     * transforms the images of all the posts into the resolutions specified within the ImageResolution enum.
     */
    public void transformAllImages() {
        String method = "transformAllImages";

        // TODO WARNING: NullPointerException. => fill image list first
        List<Image> images = new ArrayList<>();
        for (Image image : images) {
            String imageUrl = image.getUrl();
            String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
            String imageFormat = imageUrl.substring(imageUrl.lastIndexOf(".") + 1);

            int imageWidth = 0;
            LOGGER.info("Processing image {} with image name {}", imageUrl, imageName);
            try {
                imageWidth = new Info(imageUrl, true).getImageWidth();
            } catch (InfoException e) {
                LOGGER.error("In method " + method + ": Could not get image info from image: " + imageUrl, e);
                e.printStackTrace();
            }

            for (ImageResolution imgRes : ImageResolution.values()) {
                int imageDefinedWidth = imgRes.getValue();
                if (imageWidth > imageDefinedWidth) {
                    String outputFileName =
                            String.format("%s_%s_%s.%s", imageName, imgRes.toString().toLowerCase(), imageDefinedWidth, imageFormat);
                    String commandLine = this.setBasicConvertCommand(imageUrl, imageDefinedWidth, outputFileName);
                    this.runBasicConvertCommand(commandLine);
                }

            }
        }
    }

    /**
     * runs the basic convert command provided by the parameter.
     *
     * @param commandLine
     */
    private void runBasicConvertCommand(String commandLine) {
        CommandLine cmdLine = CommandLine.parse(commandLine);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File(COMMAND_LINE_PATH));

        try {
            int exitValue = executor.execute(cmdLine);
            printImageMagickStatus(exitValue);
        } catch (IOException e) {
            LOGGER.error("Error while executing ImageMagick command: {}. {}", commandLine, e.getMessage());
        }
    }

    private void printImageMagickStatus(int exitValue) {
        if (exitValue == 0) {
            LOGGER.info("Processing image was successful.");
        } else {
            LOGGER.error("Could not process the image.");
        }
    }
}
