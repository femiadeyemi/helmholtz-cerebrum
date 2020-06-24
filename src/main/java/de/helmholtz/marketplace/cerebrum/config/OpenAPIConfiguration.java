package de.helmholtz.marketplace.cerebrum.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Helmholtz Cerebrum API",
        version = "${cerebrum.version}",
        description = "Cerebrum is the resources api for Helmholtz marketplace",
         contact = @Contact(
            name = "HIFIS Technical Platform Team",
            email = ""
         ),
        license = @License(
            name = "GNU Affero General Public License",
            url = "https://www.gnu.org/licenses/"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8090", description = "DEV Server")
    }
)
@SecurityScheme(
    name = "hdf-aai",
    type = SecuritySchemeType.OAUTH2,
    in = SecuritySchemeIn.HEADER,
    bearerFormat = "jwt",
    scheme = "bearer",
    flows = @OAuthFlows(
        implicit = @OAuthFlow(
            authorizationUrl = "https://login.helmholtz-data-federation.de/oauth2-as/oauth2-authz",
            scopes = {
                @OAuthScope(name = "credentials"),
                @OAuthScope(name = "profile"),
                @OAuthScope(name = "email")
            }
        )
    )
)
class OpenAPIConfiguration {}
