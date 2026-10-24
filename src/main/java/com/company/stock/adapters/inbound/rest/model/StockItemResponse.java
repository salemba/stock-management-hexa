package com.company.stock.adapters.inbound.rest.model;

import com.company.stock.domain.model.Product;

import java.math.BigDecimal;
import java.util.UUID;

public record StockItemResponse(
    UUID id,
    String sku,
    String name,
    String category,
    BigDecimal unitPrice,
    BigDecimal quantity,
    BigDecimal reorderThreshold,
    boolean reorderNeeded
) {
    public static StockItemResponse fromDomain(Product product) {
        return new StockItemResponse(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getCategory() != null ? product.getCategory().name() : null,
            product.getUnitPrice(),
            product.getCurrentQuantity() != null ? product.getCurrentQuantity().getValue() : BigDecimal.ZERO,
            product.getReorderThreshold() != null ? product.getReorderThreshold().getValue() : BigDecimal.ZERO,
            product.isReorderNeeded()
        );
    }
}
