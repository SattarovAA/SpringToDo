package com.emobile.springtodo.aop.session;

import com.emobile.springtodo.model.util.SessionAction;
import com.emobile.springtodo.model.util.TransactionalType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LazySession {
    TransactionalType type() default TransactionalType.IGNORE;

    SessionAction action() default SessionAction.CLOSE;
}
