package de.helmholtz.marketplace.cerebrum.config;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL_FORMS)
public class CerebrumConfig
{
    /**
     * FIXME: This is workaround  for the Internal Server Error
     * org.springframework.web.util.NestedServletException:
     * Request processing failed; nested exception is
     * java.lang.NullPointerException: Missing SslContextFactory
     */
    private final SslContextFactory ssl = new SslContextFactory();
    private final HttpClient httpClient = new HttpClient(ssl);
    ClientHttpConnector clientConnector = new JettyClientHttpConnector(httpClient);

    @Bean
    public WebClient authorisationServer() {
        return WebClient.builder()
                .filter(new ServletBearerExchangeFilterFunction())
                .clientConnector(clientConnector)
                .build();
    }
}
