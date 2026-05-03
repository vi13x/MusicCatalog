package com.example.musiccatalog.service;

import com.example.musiccatalog.dto.CounterRunResultDTO;
import com.example.musiccatalog.dto.RaceConditionDemoRequestDTO;
import com.example.musiccatalog.dto.RaceConditionDemoResultDTO;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;

@Service
public class ConcurrencyDemoService {

    public RaceConditionDemoResultDTO runRaceConditionDemo(RaceConditionDemoRequestDTO request) {
        int threadCount = request.threads();
        int incrementsPerThread = request.incrementsPerThread();

        return new RaceConditionDemoResultDTO(
                threadCount,
                incrementsPerThread,
                runUnsafeCounter(threadCount, incrementsPerThread),
                runSafeCounter(threadCount, incrementsPerThread)
        );
    }

    public CounterRunResultDTO runUnsafeCounter(int threadCount, int incrementsPerThread) {
        return IntStream.range(0, 3)
                .mapToObj(attempt -> runCounterScenario("unsafe", threadCount, incrementsPerThread, new UnsafeCounter()))
                .max(Comparator.comparingLong(CounterRunResultDTO::lostUpdates))
                .orElseThrow();
    }

    public CounterRunResultDTO runSafeCounter(int threadCount, int incrementsPerThread) {
        return runCounterScenario("safe", threadCount, incrementsPerThread, new SafeCounter());
    }

    private CounterRunResultDTO runCounterScenario(String counterType,
                                                   int threadCount,
                                                   int incrementsPerThread,
                                                   Counter counter) {
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        long expectedValue = (long) threadCount * incrementsPerThread;
        long startedAt = System.nanoTime();

        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
            IntStream.range(0, threadCount).forEach(index -> executor.submit(() -> {
                readyLatch.countDown();
                awaitLatch(startLatch);
                try {
                    for (int increment = 0; increment < incrementsPerThread; increment++) {
                        counter.increment();
                    }
                } finally {
                    doneLatch.countDown();
                }
            }));

            awaitLatch(readyLatch);
            startLatch.countDown();
            awaitLatch(doneLatch);
        }

        long actualValue = counter.get();
        return new CounterRunResultDTO(
                counterType,
                expectedValue,
                actualValue,
                expectedValue - actualValue,
                expectedValue == actualValue,
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt)
        );
    }

    private void awaitLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Concurrency demo interrupted", ex);
        }
    }

    private interface Counter {
        void increment();

        long get();
    }

    private final class UnsafeCounter implements Counter {

        private long value;

        @Override
        public void increment() {
            value++;
        }

        @Override
        public long get() {
            return value;
        }
    }

    private final class SafeCounter implements Counter {

        private final AtomicLong value = new AtomicLong();

        @Override
        public void increment() {
            value.incrementAndGet();
        }

        @Override
        public long get() {
            return value.get();
        }
    }
}
