package de.helmholtz.marketplace.cerebrum.controller;

import com.fasterxml.jackson.databind.JsonNode;

import de.helmholtz.marketplace.cerebrum.entities.MarketUser;
import de.helmholtz.marketplace.cerebrum.repository.MarketUserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@RestController
@RequestMapping("${spring.data.rest.base-path}")
public class MarketUserController
{
    private final  WebClient authorisationServer;
    private final MarketUserRepository marketUserRepository;

    @Autowired
    public MarketUserController(WebClient authorisationServer, MarketUserRepository marketUserRepository) {
        this.authorisationServer = authorisationServer;
        this.marketUserRepository = marketUserRepository;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "/whoami", produces = {"application/json"})
    @Operation(
            summary = "display user information",
            description = "This display an authenticated End-User user details " +
                    "in JSON format. The result shown is by querying the UserInfo " +
                    "endpoint of HDF AAI.",
            security = @SecurityRequirement(name = "hdf-aai"),
            responses= {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MarketUser.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorised"),
                    @ApiResponse(responseCode = "500", description = "Server Error")
            }
    )
    public JsonNode whoami() {
        return this.authorisationServer
                .get()
                .uri("https://login.helmholtz-data-federation.de/oauth2/userinfo")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    public boolean isSomebody(JwtAuthenticationToken token) {
        if (Objects.isNull(token)) return false;
        String name = marketUserRepository.findBySub((String) token.getTokenAttributes().get("sub")).getFirstName();
        if (Objects.isNull(name)) return false;
        return name.trim().isEmpty();
    }
}