package com.example.musiccatalog.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.musiccatalog.dto.RaceConditionDemoRequestDTO;
import org.junit.jupiter.api.Test;

class ConcurrencyDemoServiceTest {

    private final ConcurrencyDemoService concurrencyDemoService = new ConcurrencyDemoService();

    @Test
    void runRaceConditionDemoShouldExposeUnsafeCounterLossAndSafeCounterCorrectness() {
        var request = new RaceConditionDemoRequestDTO(64, 4000);

        var result = concurrencyDemoService.runRaceConditionDemo(request);

        assertThat(result.threadCount()).isEqualTo(64);
        assertThat(result.incrementsPerThread()).isEqualTo(4000);

        assertThat(result.unsafeCounter().counterType()).isEqualTo("unsafe");
        assertThat(result.unsafeCounter().expectedValue()).isEqualTo(256000L);
        assertThat(result.unsafeCounter().actualValue()).isLessThan(result.unsafeCounter().expectedValue());
        assertThat(result.unsafeCounter().lostUpdates()).isPositive();
        assertThat(result.unsafeCounter().threadSafe()).isFalse();

        assertThat(result.safeCounter().counterType()).isEqualTo("safe");
        assertThat(result.safeCounter().actualValue()).isEqualTo(256000L);
        assertThat(result.safeCounter().lostUpdates()).isZero();
        assertThat(result.safeCounter().threadSafe()).isTrue();
    }
}
