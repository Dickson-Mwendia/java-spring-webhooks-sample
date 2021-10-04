// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.graphwebhook;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Key;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;

/**
 * Custom implementation of SigningKeyResolverAdapter that
 * retrieves the signing key from the Microsoft identity platform's
 * JWKS endpoint
 */
public class JwkKeyResolver extends SigningKeyResolverAdapter {

    private final JwkProvider keyStore;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public JwkKeyResolver(final String keyDiscoveryUrl)
            throws URISyntaxException, MalformedURLException {
        this.keyStore = new UrlJwkProvider(new URI(keyDiscoveryUrl).toURL());
    }


    /**
     * @param jwsHeader The header from a JSON web token containing the key ID
     * @param claims claims from the JSON web token
     * @return the signing key retrieved from the JWKS endpoint
     */
    @Override
    @SuppressWarnings("all")
    public Key resolveSigningKey(final JwsHeader jwsHeader, final Claims claims) {
        try {
            var keyId = jwsHeader.getKeyId();
            var publicKey = keyStore.get(keyId);
            return publicKey.getPublicKey();
        } catch (final Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
