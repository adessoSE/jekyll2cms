package de.adesso.persistence;

import org.springframework.data.repository.CrudRepository;

public interface AuthorRepository extends CrudRepository<Author, Long> {

    Author findByName(String name);
    Author findOneByNameAndAndGitUsername(String name, String gitUsername);

}
