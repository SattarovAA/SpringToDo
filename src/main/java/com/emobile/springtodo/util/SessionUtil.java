package com.emobile.springtodo.util;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SessionUtil {
    private final SessionFactory sessionFactory;
    private final ThreadLocal<Session> threadSession = new ThreadLocal<>();

    public Session getSession() {
        Session session = threadSession.get();
        if (session == null) {
            session = sessionFactory.openSession();
            threadSession.set(session);
        }
        return session;
    }

    public void closeSession() {
        Session session = threadSession.get();
        threadSession.remove();
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}
