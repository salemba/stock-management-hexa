package com.company.stock.domain.model;

/**
 * <h1>MovementType</h1>
 * <p>Type de mouvement de stock effectué sur un produit.</p>
 */
public enum MovementType {
    INCOMING("Entrée en stock"),
    OUTGOING("Sortie de stock"),
    ADJUSTMENT("Ajustement d'inventaire");

    private final String description;

    MovementType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
