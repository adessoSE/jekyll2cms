package de.adesso.persistence;

import org.springframework.data.repository.CrudRepository;

/**
 * This class is a repository for meta data of blog posts.
 */
public interface MetaDataRepository extends CrudRepository<PostMetaData, Long> {
}
