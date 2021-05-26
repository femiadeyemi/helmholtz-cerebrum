package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entity.Organization;

public interface OrganizationRepository extends MongoRepository<Organization, String>
{
    Optional<Organization> findByUuid(String uuid);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String id);
}
