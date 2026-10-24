package com.company.stock.domain.exception;

/**
 * Exception levée lorsqu'un produit est introuvable dans le domaine.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String sku, boolean isSku) {
        super("Produit non trouvé avec le SKU : " + sku);
    }
}
