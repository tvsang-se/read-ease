package com.readease.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private RestClient restClient;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            if (validator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                    throw new RuntimeException("missing authorization header");
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.writeWith(Mono.empty());
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }
                String result = restClient.get()
                        .uri("http://localhost:8082/api/auth/validate?token="+authHeader)
                        .retrieve()
                        .body(String.class);
                if(result.equals(String.valueOf(HttpStatus.PROXY_AUTHENTICATION_REQUIRED))) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
                    return response.writeWith(Mono.empty());
                } else if(result.equals(String.valueOf(HttpStatus.CONFLICT))) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.CONFLICT);
                    return response.writeWith(Mono.empty());
                } else if(result.equals(String.valueOf(HttpStatus.BAD_REQUEST))) {
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.writeWith(Mono.empty());
                } else {
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("userId", result)
                            .build();
                    return chain.filter(exchange.mutate().request(request).build());
                }
            }

            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}
