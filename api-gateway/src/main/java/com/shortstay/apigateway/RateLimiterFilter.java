package com.shortstay.apigateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter implements GlobalFilter, Ordered {

    private final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private final Map<String, Long> lastRequestTime = new ConcurrentHashMap<>();
    private static final long TIME_WINDOW_MS = 60000;
    private static final int MAX_REQUESTS = 3;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.contains("/api/listings") || path.contains("/api/reservations")) {
            String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            long currentTime = System.currentTimeMillis();


            lastRequestTime.putIfAbsent(clientIp, currentTime);
            if (currentTime - lastRequestTime.get(clientIp) > TIME_WINDOW_MS) {
                requestCounts.put(clientIp, 0);
                lastRequestTime.put(clientIp, currentTime);
            }

            int count = requestCounts.getOrDefault(clientIp, 0);

            if (count >= MAX_REQUESTS) {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }

            requestCounts.put(clientIp, count + 1);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() { return -1; }
}