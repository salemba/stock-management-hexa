package com.company.stock.adapters.inbound.rest;

import com.company.stock.ports.inbound.StockServicePort;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockApiController {

    private final StockServicePort stockServicePort;

    public StockApiController(StockServicePort stockServicePort) {
        this.stockServicePort = stockServicePort;
    }

    // Generated OpenAPI delegate / endpoint skeleton implementation will go here
}
