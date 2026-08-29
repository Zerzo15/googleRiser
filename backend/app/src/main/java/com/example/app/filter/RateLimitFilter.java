package com.example.app.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.app.service.RateLimitingService;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter{

    private final RateLimitingService rateLimitingService;

    public RateLimitFilter(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response, 
        FilterChain filterChain) throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String rateLimitKey = getClientIp(request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            rateLimitKey = authentication.getName();
        }

        Bucket tokenBucket  = rateLimitingService.resolveBucket(rateLimitKey);

        var probe = tokenBucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            var waitForRefill = probe.getNanosToWaitForRefill()  / 1_000_000_000;
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.setContentType("application/json");

            String jsonRespond  = """
                {
                    "status": %s,
                    "error": "Too Many Request", 
                    "message": "Exhausted API Request Quota",
                    "retryAfterSecond": %s
                }
            """.formatted(HttpStatus.TOO_MANY_REQUESTS.value(), waitForRefill);

            response.getWriter().write(jsonRespond);
        }

    }
    
    //check for x-forward-for: get the user real ip behindn proxy   
    private String getClientIp(HttpServletRequest request) {
        // Do not trust X-Forwarded-For from the public request. Without a
        // trusted proxy boundary, a caller could rotate that header to bypass
        // the unauthenticated rate limit.
        return request.getRemoteAddr();
    }
}
