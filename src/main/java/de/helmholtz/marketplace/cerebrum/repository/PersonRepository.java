package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entity.Person;

public interface PersonRepository extends MongoRepository<Person, String>
{
    Optional<Person> findByUuid(@Param("uuid") String uuid);

    Person findByFirstNameAndEmail(
            @Param("firstName") String firstName, @Param("email") String email);

    Person findByFirstNameAndLastName(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
