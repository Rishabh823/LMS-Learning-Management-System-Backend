package com.cipherinfratech.lms.security;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
public class GraphConfig {

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        return new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                authorizedClientService
        );
    }

    @Bean
    public GraphServiceClient<Request> graphServiceClient(
            OAuth2AuthorizedClientManager authorizedClientManager) {

        IAuthenticationProvider authProvider =
                new SimpleAuthProvider(authorizedClientManager);

        return GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();
    }
}