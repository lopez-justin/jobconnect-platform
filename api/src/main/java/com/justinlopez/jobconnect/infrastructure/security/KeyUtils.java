package com.justinlopez.jobconnect.infrastructure.security;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtils {

    private static final String ENV_PRIVATE_KEY = "JWT_PRIVATE_KEY";
    private static final String ENV_PUBLIC_KEY = "JWT_PUBLIC_KEY";

    private KeyUtils() {}

    static PrivateKey loadPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String key = readKey(ENV_PRIVATE_KEY, "certs/private_key.pem")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        final byte[] decoded = Base64.getDecoder().decode(key);
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    static PublicKey loadPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String key = readKey(ENV_PUBLIC_KEY, "certs/public_key.pem")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        final byte[] decoded = Base64.getDecoder().decode(key);
        final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

    /**
     * Reads the PEM key content, preferring the value from the environment variable
     * (which may contain the whole PEM block or just its base64 body) and falling
     * back to a classpath resource so the application can run without the keys
     * being committed to the repository.
     */
    private static String readKey(final String envVar, final String pemPath) {
        final String fromEnv = System.getenv(envVar);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }
        return readKeyFromResource(pemPath);
    }

    private static String readKeyFromResource(final String pemPath) {
        try (final InputStream inputStream = KeyUtils.class.getClassLoader().getResourceAsStream(pemPath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + pemPath);
            }
            return new String(inputStream.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read key from resource: " + pemPath, e);
        }
    }

}
