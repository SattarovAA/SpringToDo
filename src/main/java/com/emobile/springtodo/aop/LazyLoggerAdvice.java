package com.emobile.springtodo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * {@link LazyLogger} Advice.
 */
@Aspect
@Component
@Slf4j
public class LazyLoggerAdvice {
    /**
     * true - {@link #printErrorMessage(String)} use slf4j.
     * false - {@link #printErrorMessage(String)} use s.out.
     */
    @Value("${app.logger.slf4j}")
    private boolean slf4jLog;

    @Before(value = "@annotation(LazyLogger)")
    public void beforeLazyLogger(JoinPoint joinPoint) {
        String loggerMessage = getLoggerMessage(joinPoint, "Before ");
        printInfoMessage(loggerMessage);
    }

    @AfterThrowing(value = "@annotation(LazyLogger)")
    public void afterLazyLogger(JoinPoint joinPoint) {
        String loggerMessage = getLoggerMessage(joinPoint, "AfterThrowing ");
        printErrorMessage(loggerMessage);
    }

    @AfterReturning(value = "@annotation(LazyLogger)", returning = "result")
    public void afterReturnLazyLogger(JoinPoint joinPoint, Object result) {
        String loggerMessage = getLoggerMessage(joinPoint, "AfterReturning ", result);
        printInfoMessage(loggerMessage);
    }

    private String getLoggerMessage(JoinPoint joinPoint, String type) {
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        StringBuilder sb = new StringBuilder();

        sb.append(type)
                .append(joinPoint.getTarget().getClass().getSimpleName())
                .append(".")
                .append(codeSignature.getName())
                .append("(");

        for (int i = 0; i < joinPoint.getArgs().length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(codeSignature.getParameterNames()[i])
                    .append(" = ")
                    .append(joinPoint.getArgs()[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    private String getLoggerMessage(JoinPoint joinPoint, String type, Object result) {
        StringBuilder sb = new StringBuilder(getLoggerMessage(joinPoint, type));

        if (result != null) {
            sb.append(" with value : ")
                    .append(result);
        } else {
            sb.append(" with null as return value");
        }
        return sb.toString();
    }

    private void printInfoMessage(String loggerMessage) {
        if (slf4jLog) {
            log.info(loggerMessage);
        } else {
            System.out.println(loggerMessage);
        }
    }

    private void printErrorMessage(String loggerMessage) {
        if (slf4jLog) {
            log.error(loggerMessage);
        } else {
            System.err.println(loggerMessage);
        }
    }
}
