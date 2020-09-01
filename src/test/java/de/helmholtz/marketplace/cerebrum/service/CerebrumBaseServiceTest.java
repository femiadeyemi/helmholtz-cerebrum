package de.helmholtz.marketplace.cerebrum.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;

import de.helmholtz.marketplace.cerebrum.repository.MarketUserRepository;
import de.helmholtz.marketplace.cerebrum.service.common.CerebrumServiceBase;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Existing tests covered most of CerebrumBaseService.class
 * Below are few tests that cover the untested part
 */
@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class CerebrumBaseServiceTest
{
    private class CerebrumBaseServiceStub extends CerebrumServiceBase
    {
        protected CerebrumBaseServiceStub()
        {
            super(String.class, MarketUserRepository.class);
        }

        public Method testForUnknownRepositoryMethod()
        {
            return repositoryMethod("unknownMethod", String.class);
        }

        public Method testForUnknownEntityMethod()
        {
            return entityMethod("unknownMethod", String.class);
        }
    }

    @MockBean private MarketUserRepository mockRepository;
    private CerebrumBaseServiceStub cerebrumBaseServiceStub;

    @BeforeAll
    public void before()
    {
        cerebrumBaseServiceStub = new CerebrumBaseServiceStub();
    }

    @Test void
    given_unknownEntityClass_when_create_is_call_thenReturn_ResponseStatusException()
    {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        UriComponentsBuilder uri = builder.scheme("https").host("example.com");
        assertThatExceptionOfType(ResponseStatusException.class).isThrownBy(() ->
                cerebrumBaseServiceStub.createEntity("welcome", mockRepository, uri));
    }

    @Test void
    givenUnknownRepositoryMethod_when_the_method_is_call_thenReturn_ResponseStatusException()
    {
        assertThatExceptionOfType(
                ResponseStatusException.class).isThrownBy(
                        () -> cerebrumBaseServiceStub.testForUnknownRepositoryMethod());
    }

    @Test void
    givenUnknownEntityMethod_when_the_method_is_call_thenReturn_ResponseStatusException()
    {
        assertThatExceptionOfType(
                ResponseStatusException.class).isThrownBy(
                () -> cerebrumBaseServiceStub.testForUnknownEntityMethod());
    }
}