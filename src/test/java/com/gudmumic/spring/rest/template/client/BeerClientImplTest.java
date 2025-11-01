package com.gudmumic.spring.rest.template.client;

import com.gudmumic.spring.rest.template.model.BeerDTO;
import com.gudmumic.spring.rest.template.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

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
    void doGetBeerListTestNoName() {
        assertNotNull(beerClient);
        Page<BeerDTO> beerClientBeerList = beerClient.getBeerList(null, 25, 50, null,false);
        assertThat(beerClientBeerList).isNotNull();
        assertThat(beerClientBeerList.getContent().size()).isGreaterThan(0);
    }

    @Test
    void doGetBeerListTestNameParam() {
        assertNotNull(beerClient);
        Page<BeerDTO> beerClientBeerList = beerClient.getBeerList("ALE", 25, 50, null,false);
        assertThat(beerClientBeerList).isNotNull();
        assertThat(beerClientBeerList.getContent().size()).isGreaterThan(0);
    }

    @Test
    void doGetBeerById() {

        Page<BeerDTO> beerDTOS = beerClient.getBeerList();

        BeerDTO beerDTO = beerDTOS.getContent().get(0);

        BeerDTO beerById = beerClient.getBeerById(beerDTO.getId());

        assertThat(beerById).isNotNull();
        assertThat(beerById.getId()).isEqualTo(beerDTO.getId());
    }

    @Test
    void doPostCreateBeerTest() {

        BeerDTO newDto = BeerDTO.builder()
                .price(new BigDecimal("5.25"))
                .name("Blå Cola")
                .style(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123245")
                .build();

        BeerDTO savedDto = beerClient.createBeer(newDto);
        assertNotNull(savedDto);
    }

    @Test
    void doPutUpdateBeerTest() {

        BeerDTO newDto = BeerDTO.builder()
                .price(new BigDecimal("10.99"))
                .name("Grøn Cola")
                .style(BeerStyle.PILSNER)
                .quantityOnHand(500)
                .upc("123245")
                .build();

        BeerDTO beerDto = beerClient.createBeer(newDto);

        final String newName = "Royal UniBrew Grøn Cola";
        beerDto.setName(newName);
        BeerDTO updatedBeer = beerClient.updateBeer(beerDto);

        assertEquals(newName, updatedBeer.getName());
    }

    @Test
    void doDeleteBeer() {
        BeerDTO newDto = BeerDTO.builder()
                .price(new BigDecimal("10.99"))
                .name("Mango Bobs 2")
                .style(BeerStyle.IPA)
                .quantityOnHand(500)
                .upc("123245")
                .build();

        BeerDTO beerDto = beerClient.createBeer(newDto);

        beerClient.deleteBeer(beerDto.getId());

        assertThrows(HttpClientErrorException.class, () -> {
            //should error
            beerClient.getBeerById(beerDto.getId());
        });
    }

}