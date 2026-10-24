package com.company.stock.domain.exception;

/**
 * Exception levée lorsqu'une opération de stock est invalide (ex: quantité négative, données manquantes).
 */
public class InvalidStockOperationException extends RuntimeException {

    public InvalidStockOperationException(String message) {
        super(message);
    }
}
