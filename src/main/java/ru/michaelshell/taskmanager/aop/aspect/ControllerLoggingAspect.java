package ru.michaelshell.taskmanager.aop.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import ru.michaelshell.taskmanager.aop.annotation.LogBefore;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ControllerLoggingAspect {

    @Pointcut("within(ru.michaelshell.taskmanager.controller.*)")
    public void isControllerLayer() {
    }

    @Around("isControllerLayer() && @annotation(ru.michaelshell.taskmanager.aop.annotation.LogExecutionTime)")
    public Object executionTimeLogging(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            log.info("Execution of method {} finished. Execution time is {}",
                    joinPoint.getSignature().getName(),
                    endTime - startTime);
        }
        return result;
    }

    /**
     * Тут использовал вариант описания пойнтката с одновременным инжектом целевых объектов
     * @param joinPoint
     * @param logBefore Аннотация на которую реагирует Advice
     * @param target Оригинальный объект у которого вызывается метод
     */
    @Before("isControllerLayer() " +
            "&& @annotation(logBefore) " +
            "&& target(target)")
    public void beforeLogging(JoinPoint joinPoint, LogBefore logBefore, Object target) {
        log.info("Invoking method {} in class {} with args: {}",
                joinPoint.getSignature().getName(),
                target.getClass().getSimpleName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "isControllerLayer() && @annotation(ru.michaelshell.taskmanager.aop.annotation.LogAfterReturning)",
            returning = "result")
    public void afterReturningLogging(JoinPoint joinPoint, Object result) {
        log.info("Successfully completed execution of method {} with args: {} Result: {}",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()),
                result);
    }

    @AfterThrowing(value = "isControllerLayer() && @annotation(ru.michaelshell.taskmanager.aop.annotation.LogAfterThrowing)",
            throwing = "exception")
    public void afterThrowingLogging(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception was thrown while executing method {}: {}",
                joinPoint.getSignature().getName(), exception.getMessage());
    }
}
