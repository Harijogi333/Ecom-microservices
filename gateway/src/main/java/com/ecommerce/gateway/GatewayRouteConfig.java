package com.ecommerce.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayRouteConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 2, 2);
    }


    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder)
    {
        return builder.routes()
                .route("product-service",r->r
                        .path("/api/products/**")
                        .filters(f->f.circuitBreaker(config ->
                                config
                                        .setName("eComBreaker")
                                        .setFallbackUri("forward:/fallback/products"))
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
                                        .setKeyResolver(userKeyResolver())))
                        .uri("lb://product-service"))
                .route("user-service",r->r
                        .path("/api/users/**")
                        .uri("lb://user-service"))
                .route("order-service",r->r
                        .path("/api/orders/**")
                        .uri("lb://order-service"))
                .route("cart-service",r->r
                        .path("/api/cart/**")
                        .uri("lb://order-service"))
                .route("eureka-service", r -> r
                        .path("/eureka/main")
                        .filters(f -> f.setPath("/"))
                        .uri("http://localhost:8761"))

                .route("eureka-service-static", r -> r
                        .path("/eureka/**")
                        .uri("http://localhost:8761"))
                        .build();

    }
}
