package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entities.MarketUser;

public interface MarketUserRepository extends Neo4jRepository<MarketUser, Long>
{
    MarketUser findBySub(@Param("sub") String sub);

    Optional<MarketUser> findByUuid(String uuid);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String uuid);
}