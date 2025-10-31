package com.gudmumic.spring.rest.template.client;

import com.gudmumic.spring.rest.template.model.BeerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClient beerClient;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getBeerListTestNoName() {
        assertNotNull(beerClient);
        Page<BeerDTO> beerClientBeerList = beerClient.getBeerList(null, 25, 50, null,false);
        assertThat(beerClientBeerList).isNotNull();
        assertThat(beerClientBeerList.getContent().size()).isGreaterThan(0);
    }

    @Test
    void getBeerListTestNameParam() {
        assertNotNull(beerClient);
        Page<BeerDTO> beerClientBeerList = beerClient.getBeerList("ALE", 25, 50, null,false);
        assertThat(beerClientBeerList).isNotNull();
        assertThat(beerClientBeerList.getContent().size()).isGreaterThan(0);
    }
}