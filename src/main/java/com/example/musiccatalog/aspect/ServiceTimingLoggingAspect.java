package com.example.musiccatalog.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceTimingLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceTimingLoggingAspect.class);

    @Around("within(com.example.musiccatalog.service..*) && execution(public * *(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.nanoTime();
        String method = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            log.info("Service method {} completed in {} ms", method, durationMs);
            return result;
        } catch (Throwable throwable) {
            long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            log.warn("Service method {} failed after {} ms", method, durationMs, throwable);
            throw throwable;
        }
    }
}
