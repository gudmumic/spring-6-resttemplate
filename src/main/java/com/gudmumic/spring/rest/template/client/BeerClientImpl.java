package com.gudmumic.spring.rest.template.client;

import com.gudmumic.spring.rest.template.model.BeerDTO;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;;

    private static final String baseUrl = "http://localhost:8080";
    private static final String beerPth = "/api/v1/beer";

    @Override
    public Page<BeerDTO> getBeerList() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<String> stringResponse =
                restTemplate.getForEntity(baseUrl + beerPth, String.class);

        ResponseEntity<Map> mapResponseEntity =
                restTemplate.getForEntity(baseUrl + beerPth, Map.class);

        ResponseEntity<JsonNode> jsonNodeResponseEntity =
                restTemplate.getForEntity(baseUrl + beerPth, JsonNode.class);

        jsonNodeResponseEntity.getBody().findPath("content")
            .forEach(beer -> {
                  System.out.println("Beer name; " + beer.get("name"));
                  System.out.println("Beer price; " + beer.get("price"));
                });

        System.out.println(stringResponse.getBody());

        return null;
    }
}
