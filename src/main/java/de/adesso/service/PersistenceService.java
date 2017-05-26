package de.adesso.service;

import de.adesso.persistence.Image;
import de.adesso.persistence.ImageRepository;
import de.adesso.persistence.MetaDataRepository;
import de.adesso.persistence.Post;
import de.adesso.persistence.PostMetaData;
import de.adesso.persistence.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersistenceService {

    private MetaDataRepository metaDataRepo;
    private PostRepository postRepository;
    private ImageRepository imageRepository;

    @Autowired
    public PersistenceService(MetaDataRepository metaDataRepo, PostRepository postRepository,
                              ImageRepository imageRepository) {
        this.metaDataRepo = metaDataRepo;
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
    }

    public void saveMetaData(PostMetaData metadata) {
        metaDataRepo.save(metadata);
    }

    public void savePost(Post post) {
        postRepository.save(post);
    }

    public void saveImage(Image image) {
        imageRepository.save(image);
    }
}
