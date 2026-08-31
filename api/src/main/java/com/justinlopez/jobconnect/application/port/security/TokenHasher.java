package com.justinlopez.jobconnect.application.port.security;

/**
 * Puerto de la capa de aplicación para generar hashes de los refresh tokens
 * antes de persistirlos, evitando almacenar el token crudo.
 */
public interface TokenHasher {

    String hash(String token);
}
