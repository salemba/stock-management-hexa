package com.company.stock.adapters.inbound.rest.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record StockMovementRequest(
    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être strictement positive")
    BigDecimal quantity,

    String reason
) {}
