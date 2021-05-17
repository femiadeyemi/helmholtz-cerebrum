package de.helmholtz.marketplace.cerebrum.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.annotation.EnableNeo4jAuditing;

@Configuration(proxyBeanMethods = false)
@EnableNeo4jAuditing
public class CerebrumDataConfig
{}
