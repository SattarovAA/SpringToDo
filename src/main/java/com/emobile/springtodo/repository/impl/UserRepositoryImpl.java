package com.emobile.springtodo.repository.impl;

import com.emobile.springtodo.aop.logger.LazyLogger;
import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.util.Page;
import com.emobile.springtodo.model.util.PageInfo;
import com.emobile.springtodo.repository.UserRepository;
import com.emobile.springtodo.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final SessionUtil sessionUtil;

    @Override
    @LazyLogger
    public Page<User> findAll(PageInfo pageinfo) {
        int beginInd = pageinfo.pageNumber() * pageinfo.pageSize();
        Query<User> query = sessionUtil.getSession()
                .createQuery("FROM User", User.class)
                .setFirstResult(beginInd)
                .setMaxResults(pageinfo.pageSize());
        return new Page<>(query.getResultList());
    }

    @Override
    @LazyLogger
    public Optional<User> findById(Long id) {
        Query<User> query = sessionUtil.getSession()
                .createQuery("FROM User u LEFT JOIN FETCH u.taskList WHERE u.id = :id", User.class)
                .setParameter("id", id);
        return Optional.ofNullable(query.uniqueResult());
    }

    @Override
    @LazyLogger
    public User save(User model) {
        sessionUtil.getSession().persist(model);
        return model;
    }

    @Override
    @LazyLogger
    public User update(User model) {
        return sessionUtil.getSession().merge(model);
    }

    @Override
    @LazyLogger
    public void deleteById(User model) {
        sessionUtil.getSession().remove(model);
    }

    @Override
    @LazyLogger
    public Optional<User> findByUsername(String username) {
        Query<User> query = sessionUtil.getSession()
                .createQuery("FROM User WHERE username = :username", User.class)
                .setParameter("username", username);
        return Optional.ofNullable(query.uniqueResult());
    }

    @Override
    @LazyLogger
    public boolean existsByUsername(String username) {
        return existsByField(User.Fields.username, username);
    }

    @Override
    @LazyLogger
    public boolean existsByUsernameAndIdNot(String username, Long currentUserId) {
        return existsByField(User.Fields.username, username, currentUserId);
    }

    @Override
    @LazyLogger
    public boolean existsByEmail(String email) {
        return existsByField(User.Fields.email, email);
    }

    @Override
    @LazyLogger
    public boolean existsByEmailAndIdNot(String email, Long currentUserId) {
        return existsByField(User.Fields.email, email, currentUserId);
    }

    private boolean existsByField(String fieldName, Object value) {
        try {
            Query<Long> query = sessionUtil.getSession()
                    .createQuery(
                            "SELECT COUNT(u.id) FROM User u WHERE u." + fieldName + " = :value",
                            Long.class
                    )
                    .setParameter("value", value);
            Long count = query.uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean existsByField(String fieldName, Object value, Long currentUserId) {
        try {
            Query<Long> query = sessionUtil.getSession()
                    .createQuery(
                            "SELECT COUNT(u.id) FROM User u " +
                            "WHERE u." + fieldName + " = :value and u.id != :userId",
                            Long.class)
                    .setParameter("value", value)
                    .setParameter("userId", currentUserId);
            Long count = query.uniqueResult();
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
