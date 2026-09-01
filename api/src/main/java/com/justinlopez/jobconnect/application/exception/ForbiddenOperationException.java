package com.justinlopez.jobconnect.application.exception;

/**
 * Excepción lanzada cuando el usuario autenticado no tiene permiso para realizar
 * la operación sobre el recurso (no es el propietario, rol no permitido, etc.).
 * Se traduce a HTTP 403.
 */
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
