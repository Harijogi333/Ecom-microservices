package com.ecommerce.order.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Optional;

@Configuration
public class UserServiceClientConfig {

    @Bean
    public UserServiceClient userServiceClientBuild(@Qualifier("restClientBuild") RestClient.Builder restClientBuilder)
    {
        RestClient restClient=restClientBuilder
                .baseUrl("http://user-service")
                .defaultStatusHandler(HttpStatusCode::is4xxClientError,((request, response) -> Optional.empty()))
                .build();
        RestClientAdapter restClientAdapter=RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpServiceProxyFactory=HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return httpServiceProxyFactory.createClient(UserServiceClient.class);

    }
}
