package de.helmholtz.marketplace.cerebrum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableMongoRepositories
@SpringBootApplication
public class HelmholtzCerebrumApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(HelmholtzCerebrumApplication.class, args);
    }
}