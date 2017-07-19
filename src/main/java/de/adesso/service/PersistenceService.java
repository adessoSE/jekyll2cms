package de.adesso.service;

import de.adesso.persistence.Post;
import de.adesso.persistence.PostMetaData;
import de.adesso.persistence.PostMetaDataRepository;
import de.adesso.persistence.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersistenceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceService.class);

    private PostMetaDataRepository metaDataRepo;
    private PostRepository postRepository;
    // private ImageRepository imageRepository;
    private PostParseService postParseService;

    @Autowired
    public PersistenceService(PostMetaDataRepository metaDataRepo, PostRepository postRepository,
                              PostParseService postParseService) {
        this.metaDataRepo = metaDataRepo;
        this.postRepository = postRepository;
        this.postParseService = postParseService;
    }

    public void updateDatabase() {
        LOGGER.info("Updating database...");
        postParseService.getAllHtmlPosts()
                .forEach(post -> {
                    //savePost(post);
                    // get images of current post
//                    postParseService.extractImages(post)
//                            .forEach(image -> {
//                                saveImage(image);
//                            });
                    // get corresponding metadata file of current post
                    // TODO add PostMetaData metadata = ... 
                    postParseService.findCorrespondingMetadataFile(post);
                    //saveMetaData(metaData);
                });
        LOGGER.info("Updating database was successful.");
    }

    public void saveMetaData(PostMetaData metadata) {
        metaDataRepo.save(metadata);
    }

    public void savePost(Post post) {
        postRepository.save(post);
    }

    /*public void saveImage(Image image) {
        imageRepository.save(image);
    }

    public List<Image> loadAllImages() {
        return imageRepository.findAll();
    }*/
}
