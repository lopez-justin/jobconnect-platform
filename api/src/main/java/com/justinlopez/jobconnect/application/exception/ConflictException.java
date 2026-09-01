package com.justinlopez.jobconnect.application.exception;

/**
 * Excepción lanzada cuando la operación entra en conflicto con el estado actual
 * del recurso (transición de estado inválida, regla de negocio violada, etc.).
 * Se traduce a HTTP 409.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
