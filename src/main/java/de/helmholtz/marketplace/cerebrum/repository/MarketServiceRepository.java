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

    Optional<MarketService> findByEntryPoint(@Param("entryPoint") String url);

    @Query("MATCH (service:MarketService),(org:Organization) " +
            "WHERE service.uuid = $serviceUuid AND org.uuid = $orgUuid " +
            "CREATE (service)-[r:HOSTED_BY { serviceTechnicalName : $serviceTechnicalName }]->(org) " +
            "RETURN service, r")
    MarketService createHostedByRelationship(@Param("serviceUuid") String serviceUuid,
                                           @Param("orgUuid") String orgUuid,
                                           @Param("serviceTechnicalName") String serviceTechnicalName);

    @Query("MATCH (service:MarketService),(user:MarketUser) " +
            "WHERE service.uuid = $serviceUuid AND user.uuid = $orgUuid " +
            "CREATE (user)-[r:MANAGES { roles : $roles }]->(service) " +
            "RETURN service, r")
    MarketService createManagesRelationship(@Param("serviceUuid") String serviceUuid,
                                             @Param("orgUuid") String orgUuid,
                                             @Param("roles") String[] roles);

    @SuppressWarnings("UnusedReturnValue")
    @Query("MATCH (service:MarketService)-[r:HOSTED_BY]->(org:Organization) " +
            "WHERE service.uuid = $serviceUuid AND org.uuid = $orgUuid " +
            "SET r.serviceTechnicalName = $softwareName " +
            "RETURN service")
    MarketService updateServiceProviderRelationship(
            @Param("serviceUuid") String serviceUuid,
            @Param("orgUuid") String orgUuid, @Param("softwareName") String serviceTechnicalName);

    @SuppressWarnings("UnusedReturnValue")
    @Query("MATCH (service:MarketService)-[r:HOSTED_BY]->(org:Organization) " +
            "WHERE service.uuid = $serviceUuid AND org.uuid = $orgUuid " +
            "DELETE r")
    Long deleteServiceProviders(@Param("serviceUuid") String serviceUuid, @Param("orgUuid") String orgUuid);

    @SuppressWarnings("UnusedReturnValue")
    @Query("MATCH (user:MarketUser)-[r:MANAGES]->(service:MarketService) " +
            "WHERE service.uuid = $serviceUuid AND user.uuid = $userUuid " +
            "DELETE r")
    Long deleteManagementMember(@Param("serviceUuid") String serviceUuid, @Param("userUuid") String userUuid);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String id);
}
