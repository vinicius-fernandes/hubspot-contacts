package com.vinfern.hubspot.contacts.configuration.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Value("${ratelimit.tokens}")
    private Long tokens;

    @Value("${ratelimit.duration.seconds}")
    private Long duration;

    @Bean
    public Bucket limitedBucket() {
        Bandwidth limit = Bandwidth.classic(tokens, Refill.greedy(tokens, Duration.ofMinutes(duration)));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

}
