package de.helmholtz.marketplace.cerebrum.config;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
public class CerebrumConfig
{
    private final SslContextFactory.Client ssl = new SslContextFactory.Client();
    private final HttpClient httpClient = new HttpClient(ssl);
    ClientHttpConnector clientConnector = new JettyClientHttpConnector(httpClient);

    @Bean
    public WebClient authorisationServer()
    {
        return WebClient.builder()
                .filter(new ServletBearerExchangeFilterFunction())
                .clientConnector(clientConnector)
                .build();
    }
}
