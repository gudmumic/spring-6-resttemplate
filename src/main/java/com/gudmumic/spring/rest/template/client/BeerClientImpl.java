package com.gudmumic.spring.rest.template.client;

import com.gudmumic.spring.rest.template.model.BeerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.restclient.RestTemplateBuilder;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private final RestTemplateBuilder restTemplateBuilder;;

    @Override
    public Page<BeerDTO> getBeerList() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<String> stringResponse =
                restTemplate.getForEntity("http://localhost:8080/api/v1/beer", String.class);
        System.out.println(stringResponse.getBody());
        return null;
    }
}
