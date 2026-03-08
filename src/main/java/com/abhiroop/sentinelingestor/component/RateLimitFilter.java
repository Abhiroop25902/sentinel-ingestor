package com.abhiroop.sentinelingestor.component;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final Bucket bucket;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            // Too Many Request -> HTTP 429
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Sentinel Ingestor: Too many requests. Eventarc will retry.\"}");
        }
    }
}
