package de.adesso.service;

import de.adesso.persistence.Image;
import de.adesso.persistence.ImageRepository;
import de.adesso.persistence.MetaDataRepository;
import de.adesso.persistence.Post;
import de.adesso.persistence.PostMetaData;
import de.adesso.persistence.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersistenceService {

    private MetaDataRepository metaDataRepo;
    private PostRepository postRepository;
    private ImageRepository imageRepository;
    private PostParseService postParseService;

    @Autowired
    public PersistenceService(MetaDataRepository metaDataRepo, PostRepository postRepository,
                              ImageRepository imageRepository, PostParseService postParseService) {
        this.metaDataRepo = metaDataRepo;
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
        this.postParseService = postParseService;
    }

    public void updateDatabase() {
        postParseService.getAllHtmlPosts()
                .forEach(post -> {
                    savePost(post);
                    // get images of current post
                    postParseService.extractImages(post)
                            .forEach(image -> {
                                saveImage(image);
                            });
                    // get corresponding metadata file of current post
                    PostMetaData metaData = postParseService.findCorrespondingMetadataFile(post);
                    saveMetaData(metaData);
                });
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

    public List<Image> loadAllImages() {
        return imageRepository.findAll();
    }
}
