package com.example.Lync.Exceptions;

public class ProductAlreadyInactiveException extends RuntimeException{

    public ProductAlreadyInactiveException(String message) {
        super(message);
    }
}
