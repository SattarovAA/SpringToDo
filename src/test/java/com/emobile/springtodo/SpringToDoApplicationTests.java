package com.emobile.springtodo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

class SpringToDoApplicationTests {
    @TestConfiguration
    static class CustomClockConfiguration {
        private static final LocalDateTime MILLENNIUM =
                LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0);

        @Bean
        @Primary
        public Clock fixedClock() {
            return Clock.fixed(MILLENNIUM.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        }
    }
}
