package com.justinlopez.jobconnect.application.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe. Se traduce a HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
