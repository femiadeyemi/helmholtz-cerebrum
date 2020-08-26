package de.helmholtz.marketplace.cerebrum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import de.helmholtz.marketplace.cerebrum.controller.MarketServiceController;
import de.helmholtz.marketplace.cerebrum.controller.MarketUserController;
import de.helmholtz.marketplace.cerebrum.controller.OrganizationController;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HelmholtzCerebrumApplicationTest
{
    @Autowired private OrganizationController organizationController;
    @Autowired private MarketUserController marketUserController;
    @Autowired private MarketServiceController marketServiceController;

    @Test
    void contextLoads()
    {
        assertThat(organizationController).isNotNull();
        assertThat(marketUserController).isNotNull();
        assertThat(marketServiceController).isNotNull();
    }
}