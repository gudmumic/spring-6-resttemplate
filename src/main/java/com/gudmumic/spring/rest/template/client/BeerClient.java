package com.gudmumic.spring.rest.template.client;

import com.gudmumic.spring.rest.template.model.BeerDTO;
import com.gudmumic.spring.rest.template.model.BeerStyle;
import org.springframework.data.domain.Page;

public interface BeerClient {

    public Page<BeerDTO> getBeerList();

    public Page<BeerDTO> getBeerList(String name, Integer pageNumber, Integer size, BeerStyle style, Boolean showInventory);
}
