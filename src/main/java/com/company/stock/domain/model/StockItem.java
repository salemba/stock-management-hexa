package com.company.stock.domain.model;

import java.util.UUID;

public record StockItem(
    UUID id,
    String sku,
    String name,
    Integer quantity
) {}
