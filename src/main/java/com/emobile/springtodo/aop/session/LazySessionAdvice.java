package com.emobile.springtodo.aop.session;

import com.emobile.springtodo.model.util.SessionAction;
import com.emobile.springtodo.model.util.TransactionalType;
import com.emobile.springtodo.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
public class LazySessionAdvice {
    private final SessionUtil sessionUtil;

    @Before(value = "@annotation(param)")
    public void beforeLazySession(LazySession param) {
        if (param.type() == TransactionalType.AROUND) {
            sessionUtil.getSession().beginTransaction();
        }
    }

    @AfterThrowing(value = "@annotation(LazySession)")
    public void afterThrowingLazySession(JoinPoint joinPoint) {
        Transaction transaction = sessionUtil.getSession().getTransaction();
        if (transaction != null) {
            transaction.rollback();
        }
        sessionUtil.closeSession();
    }

    @AfterReturning(value = "@annotation(param)")
    public void afterReturnLazySession(LazySession param) {
        if (param.type() == TransactionalType.AROUND) {
            sessionUtil.getSession().getTransaction().commit();
        }
        if (param.action() == SessionAction.CLOSE){
            sessionUtil.closeSession();
        }
        if (param.action() == SessionAction.CHECK){
            boolean oneOf = sessionUtil.getSession()
                    .getTransaction()
                    .getStatus()
                    .isOneOf(TransactionStatus.NOT_ACTIVE);
            if (oneOf){
                sessionUtil.closeSession();
            }
        }
    }
}
