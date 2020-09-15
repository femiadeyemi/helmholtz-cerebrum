package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entities.MarketService;

public interface MarketServiceRepository extends Neo4jRepository<MarketService, Long>
{
    Optional<MarketService> findByUuid(String uuid);

    Optional<MarketService> findByName(@Param("name") String name);

    Optional<MarketService> findByUrl(@Param("url") String url);

    @Query("MATCH (service:MarketService),(org:Organization) " +
            "WHERE service.uuid = $serviceUuid AND org.uuid = $orgUuid " +
            "CREATE (service)-[r:HOSTED_BY { serviceTechnicalName : $serviceTechnicalName }]->(org) " +
            "RETURN service, r")
    MarketService createHostedInRelationship(@Param("serviceUuid") String serviceUuid,
                                           @Param("orgUuid") String orgUuid,
                                           @Param("serviceTechnicalName") String serviceTechnicalName);

    @SuppressWarnings("UnusedReturnValue")
    @Query("MATCH (service:MarketService)-[r:HOSTED_BY]->(org:Organization) " +
            "WHERE service.uuid = $serviceUuid AND org.uuid = $orgUuid " +
            "DELETE r")
    Long deleteServiceProviders(@Param("serviceUuid") String serviceUuid, @Param("orgUuid") String orgUuid);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String id);
}
