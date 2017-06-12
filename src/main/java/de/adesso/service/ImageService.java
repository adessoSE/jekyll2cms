package de.adesso.service;

import de.adesso.persistence.Image;
import de.adesso.persistence.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This image service uses the ImageRepository to perform persistence tasks.
 */
@Service
public class ImageService {

    private ImageRepository imageRepo;

    @Autowired
    public ImageService(ImageRepository imageRepo) {
        this.imageRepo = imageRepo;
    }

    /**
     * retrieves all images from the posts.
     * @return List - Returns a list with the images.
     */
    public List<Image> getAllImages() {
        return this.imageRepo.findAll();
    }
}
