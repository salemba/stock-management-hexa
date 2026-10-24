package com.company.stock.domain.exception;

/**
 * Exception levée lorsque le stock disponible est insuffisant pour honorer une demande de retrait.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}
