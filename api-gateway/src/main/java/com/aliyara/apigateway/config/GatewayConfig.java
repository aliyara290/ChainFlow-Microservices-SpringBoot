package com.aliyara.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("customer-service", r -> r.path("/customer/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://CUSTOMER-SERVICE"))
                .route("production-service", r -> r.path("/production/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://PRODUCTION-SERVICE"))
                .route("supply-service", r -> r.path("/supply/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://SUPPLY-SERVICE"))
                .build();
    }
}