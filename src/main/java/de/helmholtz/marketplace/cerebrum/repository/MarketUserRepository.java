package de.helmholtz.marketplace.cerebrum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import de.helmholtz.marketplace.cerebrum.entity.MarketUser;

public interface MarketUserRepository extends MongoRepository<MarketUser, Long>
{
    MarketUser findBySub(@Param("sub") String sub);

    Optional<MarketUser> findByScreenName(@Param("screenName") String screenName);

    Optional<MarketUser> findByUuid(String uuid);

    @Query(value = "{'affiliations.$id' : ?0 }", fields = "{'affiliations' : 0}")
    Page<MarketUser> findAllMembers(String uuid, PageRequest page);

    @SuppressWarnings("UnusedReturnValue")
    Long deleteByUuid(String uuid);
}