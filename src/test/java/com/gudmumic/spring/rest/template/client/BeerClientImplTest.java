package com.gudmumic.spring.rest.template.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClient beerClient;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getBeerListTest() {
        assertNotNull(beerClient);
        beerClient.getBeerList();
    }
}