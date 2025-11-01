package com.gudmumic.spring.rest.template.client;

import com.gudmumic.spring.rest.template.config.RestTemplateBuilderConfig;
import com.gudmumic.spring.rest.template.model.BeerDTO;
import com.gudmumic.spring.rest.template.model.BeerStyle;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.restclient.test.MockServerRestTemplateCustomizer;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Created by jt, Spring Framework Guru.
 */
@RestClientTest
@Import(RestTemplateBuilderConfig.class)
public class BeerClientMockTest {

    static final String URL = "http://localhost:8080";

    BeerClient beerClient;

    MockRestServiceServer server;

    @Autowired
    RestTemplateBuilder restTemplateBuilderConfigured;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RestTemplateBuilder mockRestTemplateBuilder = new RestTemplateBuilder(new MockServerRestTemplateCustomizer());

    private BeerDTO beerDTO;
    private String payload;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = restTemplateBuilderConfigured.build();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        when(mockRestTemplateBuilder.build()).thenReturn(restTemplate);
        beerClient = new BeerClientImpl(mockRestTemplateBuilder);
        beerDTO = getBeerDto();
        payload = objectMapper.writeValueAsString(beerDTO);
    }

    @Test
    void doGetListBeersTest() throws JSONException, Exception {
        String payload = objectMapper.writeValueAsString(getPage());

        server.expect(method(HttpMethod.GET))
              .andExpect(requestTo(URL + BeerClientImpl.BEER_LIST_PATH))
              .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> dtos = beerClient.getBeerList();
        assertThat(dtos.getContent().size()).isGreaterThan(0);
    }

    @Test
    void doGetBeerByIdTest() throws JSONException, Exception {
        BeerDTO beerDTO = getBeerDto();
        String payload = objectMapper.writeValueAsString(beerDTO);

        server.expect(method(HttpMethod.GET))
              .andExpect(requestToUriTemplate(URL + BeerClientImpl.BEER_BY_ID_PATH, beerDTO.getId()))
              .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        BeerDTO redponseBeerDto = beerClient.getBeerById(beerDTO.getId());
        assertThat(beerDTO.getId()).isEqualTo(redponseBeerDto.getId());
    }

    @Test
    void testListBeersWithQueryParam() throws JSONException, Exception {
        String response = objectMapper.writeValueAsString(getPage());

        URI uri = UriComponentsBuilder.fromUriString(URL + BeerClientImpl.BEER_LIST_PATH)
                .queryParam("beerName", "ALE")
                .build().toUri();

        server.expect(method(HttpMethod.GET))
                .andExpect(requestTo(uri))
                .andExpect(queryParam("beerName", "ALE"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        Page<BeerDTO> responsePage = beerClient
                .getBeerList("ALE", null, null, null, null);

        assertThat(responsePage.getContent().size()).isEqualTo(1);
    }

    @Test
    void testDeleteNotFound() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.BEER_BY_ID_PATH,
                        beerDTO.getId()))
                .andRespond(withResourceNotFound());

        assertThrows(HttpClientErrorException.class, () -> {
            beerClient.deleteBeer(beerDTO.getId());
        });

        server.verify();
    }

    @Test
    void testDeleteBeer() {
        server.expect(method(HttpMethod.DELETE))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.BEER_BY_ID_PATH,
                        beerDTO.getId()))
                .andRespond(withNoContent());

        beerClient.deleteBeer(beerDTO.getId());

        server.verify();
    }

    @Test
    void testUpdateBeer() {
        server.expect(method(HttpMethod.PUT))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.BEER_BY_ID_PATH,
                        beerDTO.getId()))
                .andRespond(withNoContent());

        mockGetOperation();

        BeerDTO responseDto = beerClient.updateBeer(beerDTO);
        assertThat(responseDto.getId()).isEqualTo(beerDTO.getId());
    }

    @Test
    void testCreateBeer()  {
        URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.BEER_BY_ID_PATH)
                .build(beerDTO.getId());

        server.expect(method(HttpMethod.POST))
                .andExpect(requestTo(URL +
                        BeerClientImpl.BEER_LIST_PATH))
                .andRespond(withAccepted().location(uri));

        mockGetOperation();

        BeerDTO responseDto = beerClient.createBeer(beerDTO);
        assertThat(responseDto.getId()).isEqualTo(beerDTO.getId());
    }

    private void mockGetOperation() {
        server.expect(method(HttpMethod.GET))
                .andExpect(requestToUriTemplate(URL +
                        BeerClientImpl.BEER_BY_ID_PATH, beerDTO.getId()))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));
    }

    private BeerDTO getBeerDto(){
        return BeerDTO.builder()
                .id(UUID.randomUUID())
                .price(new BigDecimal("10.99"))
                .name("Mango Bobs")
                .style(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123245")
                .build();
    }

    private BeerDTOPageImpl getPage(){
        return new BeerDTOPageImpl(Arrays.asList(getBeerDto()), 1, 25, 1);
    }
}
