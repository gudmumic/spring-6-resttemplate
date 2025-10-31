package com.gudmumic.spring.rest.template.client;

import com.gudmumic.spring.rest.template.model.BeerDTO;
import org.springframework.data.domain.Page;

public interface BeerClient {

    public Page<BeerDTO> getBeerList();
}
