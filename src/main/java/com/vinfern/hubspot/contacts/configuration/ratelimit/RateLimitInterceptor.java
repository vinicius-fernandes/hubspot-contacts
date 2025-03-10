package com.vinfern.hubspot.contacts.configuration.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinfern.hubspot.contacts.dto.ApplicationError;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Configuration
public class RateLimitInterceptor implements HandlerInterceptor {
    @Autowired
    Bucket bucket;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
            var error = new ApplicationError(
                    "You have exhausted your API Request Quota",
                    "EXHAUSTED_API_REQUEST_QUOTA",
                    List.of()
            );
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.setContentType("application/json");
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write(objectMapper.writeValueAsString(error));

            return false;
        }

    }


}
