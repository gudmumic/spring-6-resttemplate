package com.gudmumic.spring.rest.template.client;

import com.gudmumic.spring.rest.template.model.BeerDTO;
import com.gudmumic.spring.rest.template.model.BeerStyle;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface BeerClient {

    public Page<BeerDTO> getBeerList();

    public Page<BeerDTO> getBeerList(String name, Integer pageNumber, Integer size, BeerStyle style, Boolean showInventory);

    public BeerDTO getBeerById(UUID beerId);

    public BeerDTO createBeer(BeerDTO newBeerDto);

    public BeerDTO updateBeer(BeerDTO beerDto);

    public void deleteBeer(UUID beerId);
}
