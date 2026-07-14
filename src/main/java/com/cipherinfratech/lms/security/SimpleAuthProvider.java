package com.cipherinfratech.lms.security;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.*;

import java.net.URL;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class SimpleAuthProvider implements IAuthenticationProvider {

    private static final String CLIENT_REGISTRATION_ID = "azure";

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public SimpleAuthProvider(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    @NotNull
    @Override
    public CompletableFuture<String> getAuthorizationTokenAsync(@NotNull URL requestUrl) {

        Authentication principal = new UsernamePasswordAuthenticationToken(
                "graph-system-client",
                null,
                Collections.emptyList()
        );

        try {
            OAuth2AuthorizeRequest authorizeRequest =
                    OAuth2AuthorizeRequest.withClientRegistrationId(CLIENT_REGISTRATION_ID)
                            .principal(principal)
                            .build();

            OAuth2AuthorizedClient authorizedClient =
                    authorizedClientManager.authorize(authorizeRequest);

            if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
                throw new IllegalStateException("Failed to obtain access token from OAuth2 client");
            }

            String token = authorizedClient.getAccessToken().getTokenValue();

            log.debug("Graph API token acquired successfully");

            return CompletableFuture.completedFuture(token);

        } catch (Exception ex) {
            log.error("Error obtaining Graph API token", ex);

            CompletableFuture<String> failed = new CompletableFuture<>();
            failed.completeExceptionally(ex);
            return failed;
        }
    }
}