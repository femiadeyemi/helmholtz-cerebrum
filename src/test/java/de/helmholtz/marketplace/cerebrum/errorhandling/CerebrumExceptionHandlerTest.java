package de.helmholtz.marketplace.cerebrum.errorhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import de.helmholtz.marketplace.cerebrum.entities.Organization;
import de.helmholtz.marketplace.cerebrum.repository.OrganizationRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CerebrumExceptionHandlerTest
{
    private static final String API_URI_PREFIX = "/api/v0";
    private static final String ORG_API_URI = API_URI_PREFIX + "/organizations";
    @Value("${cerebrum.test.oauth2-token}") private String TOKEN;
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private OrganizationRepository mockRepository;

    @Test void
    whenTry_thenOK() throws Exception
    {
        final MvcResult response = mockMvc.perform(get(API_URI_PREFIX))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(response.getResponse().getContentAsString());
    }

    // handleMethodArgumentTypeMismatch
    @Test void
    whenMethodArgumentMismatch_thenBadRequest() throws Exception
    {
        final MvcResult response = mockMvc.perform(get(ORG_API_URI + "?page=ccc "))
                .andExpect(status().isBadRequest())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("should be of type java.lang.Integer"));
    }

    // handleHttpMessageNotReadable
    @Test void
    whenHttpMessageNotReadable_thenBadRequest() throws Exception
    {
        Map<String, String> patch = new HashMap<>();
        patch.put("path", "/abbreviation");
        patch.put("value", "KI3T");

        final MvcResult response = mockMvc.perform(
                patch(ORG_API_URI + "/org-5189a7bc-d630-11ea-87d0-0242ac130003")
                        .header("Authorization", "Bearer " + TOKEN)
                        .accept("application/json")
                        .contentType("application/json-patch+json").content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isBadRequest())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("Malformed JSON or JSON+PATCH request"));
    }

    // handleInvalidUuid
    @Test void
    whenInvalidUuid_thenBadRequest() throws Exception
    {
        String badUuid = "abc";
        final MvcResult response = mockMvc.perform(
                get(ORG_API_URI + "/" + badUuid))
                .andExpect(status().isBadRequest())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getMessage()
                .contains(badUuid + " is an invalid uuid"));
    }

    // handleEntityNotFound
    @Test void
    whenEntityNotFound_thenNotFound() throws Exception
    {
        String nonExistingUuid = "org-5189a7bc-d630-11ea-87d0-0242ac130004";
        Organization kit = new Organization();
        kit.setName("Karlsruher Institut fuer Technologie");
        kit.setAbbreviation("KIT");
        kit.setUrl("http://www.kit.edu/");
        kit.setImg("http://www.kit.edu/img/intern/kit_logo_V2_de.svg");
        kit.setUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003");

        given(mockRepository.findByUuid("org-5189a7bc-d630-11ea-87d0-0242ac130003"))
                .willReturn(java.util.Optional.of(kit));

        final MvcResult response = mockMvc.perform(
                get(ORG_API_URI + "/" + nonExistingUuid))
                .andExpect(status().isNotFound())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getMessage()
                .contains("Could not find organization " + nonExistingUuid));
    }

    // handleMethodArgumentNotValid
    @Test void
    whenValidInput_thenBadRequest() throws Exception
    {
        Map<String, Object> organisation = new HashMap<>();
        organisation.put("organisationName", "Forschungszentrum Jülich");
        organisation.put("abbr", "FZJ");

        final MvcResult response = mockMvc.perform(post(ORG_API_URI)
                .contentType("application/json")
                .header("Authorization", "Bearer " + TOKEN)
                .content(objectMapper.writeValueAsString(organisation)))
                .andExpect(status().isBadRequest())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        assertTrue(error.getErrors().get(0).contains("must not be null"));
        assertTrue(error.getMessage().contains("Validation failed"));
    }

    //handleHttpRequestMethodNotSupported
    @Test void
    whenHttpRequestMethodNotSupported_thenMethodNotAllowed() throws Exception
    {
        final MvcResult response = mockMvc.perform(delete(ORG_API_URI)
                .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isMethodNotAllowed())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0)
                .contains("DELETE method is not supported for this request"));
    }

    // handleNoHandlerFoundException
    @Test void
    whenNoHandlerForHttpRequest_thenNotFound() throws Exception
    {
        final MvcResult response = mockMvc.perform(delete(API_URI_PREFIX + "/xx")
                .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isNotFound())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("No handler found"));
        System.out.println(response.getResponse().getContentAsString());
    }

    // handleHttpMediaTypeNotSupported
    @Test void
    whenSendInvalidHttpMediaType_thenUnsupportedMediaType() throws Exception
    {
        Map<String, Object> organisation = new HashMap<>();
        organisation.put("organisationName", "Forschungszentrum Jülich");
        organisation.put("abbr", "FZJ");

        final MvcResult response = mockMvc.perform(post(ORG_API_URI)
                .contentType("application/xml")
                .header("Authorization", "Bearer " + TOKEN)
                .content(objectMapper.writeValueAsString(organisation)))
                .andExpect(status().isUnsupportedMediaType())
                .andReturn();
        final CerebrumApiError error = objectMapper.readValue(
                response.getResponse().getContentAsString(), CerebrumApiError.class);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("media type is not supported"));
    }

    // handleHttpMediaTypeNotAcceptable
    @Test void
    whenSendInvalidHttpMediaType_thenNotAcceptableMediaType() throws Exception
    {
        final MvcResult response = mockMvc.perform(get(ORG_API_URI)
                .accept("application/xml"))
                .andExpect(status().isNotAcceptable())
                .andReturn();
        assertEquals(406, response.getResponse().getStatus());
    }

    //handleAccessDeniedException
    @Test void
    whenAccessDeniedException_thenForbidden() throws Exception
    {
        final MvcResult response =
                mockMvc.perform(get(API_URI_PREFIX + "/users/whoami")
                        .accept("application/json"))
                        .andExpect(status().isForbidden())
                        .andReturn();

        assertEquals(403, response.getResponse().getStatus());
    }
}
