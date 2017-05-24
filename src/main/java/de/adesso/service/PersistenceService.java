package de.adesso.service;

import de.adesso.persistence.PostMetaData;
import de.adesso.persistence.MetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersistenceService {
    private MetaDataRepository metaDataRepo;

    @Autowired
    public PersistenceService(MetaDataRepository metaDataRepo) {
        this.metaDataRepo = metaDataRepo;
    }

    public void saveMetaData(PostMetaData metadata) {
        metaDataRepo.save(metadata);
    }
}
