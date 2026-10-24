package com.company.stock.adapters.inbound.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateStockItemRequest(
    @NotBlank(message = "Le SKU ne peut pas être vide")
    String sku,

    @NotBlank(message = "Le nom ne peut pas être vide")
    String name,

    String category,

    @PositiveOrZero(message = "Le prix unitaire doit être positif ou nul")
    BigDecimal unitPrice,

    @NotNull(message = "La quantité est obligatoire")
    @PositiveOrZero(message = "La quantité doit être positive ou nulle")
    BigDecimal quantity,

    @PositiveOrZero(message = "Le seuil de réapprovisionnement doit être positif ou nul")
    BigDecimal reorderThreshold
) {}
