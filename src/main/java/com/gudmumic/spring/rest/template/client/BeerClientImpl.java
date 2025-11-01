package com.gudmumic.spring.rest.template.client;

import com.gudmumic.spring.rest.template.model.BeerDTO;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import com.gudmumic.spring.rest.template.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;;

    private static final String BEER_LIST_PATH = "api/v1/beer";
    private static final String BEER_BY_ID_PATH = BEER_LIST_PATH + "/{beerId}";

    @Override
    public Page<BeerDTO> getBeerList() {
        return this.getBeerList(null, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> getBeerList(String name, Integer pageNumber, Integer size, BeerStyle style, Boolean showInventory) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromPath(BEER_LIST_PATH);

        if (name != null && !name.isEmpty()) {
            uriComponentsBuilder.queryParam("beerName", name);
        }

        if (pageNumber != null && pageNumber >= 0) {
            uriComponentsBuilder.queryParam("pageNumber", pageNumber);
        }

        if (size != null && size > 0) {
            uriComponentsBuilder.queryParam("size", size);
        }

        if (style != null) {
            uriComponentsBuilder.queryParam("beerStyle", style);
        }

        if (showInventory != null) {
            uriComponentsBuilder.queryParam("showInventory", showInventory);
        }

        ResponseEntity<String> stringResponse =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), String.class);

        ResponseEntity<Map> mapResponseEntity =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), Map.class);

        ResponseEntity<JsonNode> jsonNodeResponseEntity =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), JsonNode.class);

        jsonNodeResponseEntity.getBody().findPath("content")
            .forEach(beer -> {
                System.out.println("Beer name; " + beer.get("name"));
                System.out.println("Beer price; " + beer.get("price"));
            });

        ResponseEntity<BeerDTOPageImpl> pageResponseEntity =
                restTemplate.getForEntity(uriComponentsBuilder.toUriString(), BeerDTOPageImpl.class);

        ResponseEntity<RestResponsePage<BeerDTO>> response = restTemplate.exchange(
                uriComponentsBuilder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestResponsePage<BeerDTO>>() {}
        );

        //System.out.println(stringResponse.getBody());

        return response.getBody();
    }

    @Override
    public BeerDTO getBeerById(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        return restTemplate.getForObject(BEER_BY_ID_PATH, BeerDTO.class, beerId);
    }

    @Override
    public BeerDTO createBeer(BeerDTO newBeerDto) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        URI location = restTemplate.postForLocation(BEER_LIST_PATH, newBeerDto);
        return restTemplate.getForObject(location.getPath(), BeerDTO.class);
    }

    @Override
    public BeerDTO updateBeer(BeerDTO beerDto) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.put(BEER_BY_ID_PATH, beerDto, beerDto.getId());
        return getBeerById(beerDto.getId());
    }

    @Override
    public void deleteBeer(UUID beerId) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.delete(BEER_BY_ID_PATH, beerId);
    }

}
