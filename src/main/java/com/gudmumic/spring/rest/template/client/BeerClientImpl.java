package com.gudmumic.spring.rest.template.client;

import com.gudmumic.spring.rest.template.model.BeerDTO;
import java.util.Map;

import com.gudmumic.spring.rest.template.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;;

    private static final String beerPth = "api/v1/beer";

    @Override
    public Page<BeerDTO> getBeerList() {
        return this.getBeerList(null, null, null, null, null);
    }

    @Override
    public Page<BeerDTO> getBeerList(String name, Integer pageNumber, Integer size, BeerStyle style, Boolean showInventory) {
        RestTemplate restTemplate = restTemplateBuilder.build();

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromPath(beerPth);

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

        System.out.println(stringResponse.getBody());

        return pageResponseEntity.getBody();
    }
}
