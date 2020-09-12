package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entities.Organization;

public interface OrganizationRepository extends Neo4jRepository<Organization, Long>
{
    Optional<Organization> findByName(@Param("name") String name);

    Optional<Organization> findByAbbreviation(@Param("abbreviation") String abbreviation);

    Optional<Organization> findByImg(@Param("img") String img);

    Optional<Organization> findByUrl(@Param("url") String url);

    Optional<Organization> findByUuid(String uuid);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String id);
}
