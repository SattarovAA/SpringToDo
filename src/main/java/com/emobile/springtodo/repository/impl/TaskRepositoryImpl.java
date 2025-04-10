package com.emobile.springtodo.repository.impl;

import com.emobile.springtodo.aop.logger.LazyLogger;
import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.util.Page;
import com.emobile.springtodo.model.util.PageInfo;
import com.emobile.springtodo.repository.TaskRepository;
import com.emobile.springtodo.util.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class TaskRepositoryImpl implements TaskRepository {
    private final SessionUtil sessionUtil;

    @Override
    @LazyLogger
    public Page<Task> findAll(PageInfo pageinfo) {
        int beginInd = pageinfo.pageNumber() * pageinfo.pageSize();
        Query<Task> query = sessionUtil.getSession()
                .createQuery("FROM Task", Task.class)
                .setFirstResult(beginInd)
                .setMaxResults(pageinfo.pageSize());
        return new Page<>(query.getResultList());
    }

    @Override
    @LazyLogger
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(
                sessionUtil.getSession().get(Task.class, id)
        );
    }

    @Override
    @LazyLogger
    public Task save(Task model) {
        sessionUtil.getSession().persist(model);
        return model;
    }

    @Override
    @LazyLogger
    public Task update(Task model) {
        return sessionUtil.getSession().merge(model);
    }

    @Override
    @LazyLogger
    public void deleteById(Task model) {
        sessionUtil.getSession().remove(model);
    }

    @Override
    @LazyLogger
    public void deleteAll() {
        sessionUtil.getSession()
                .createQuery("DELETE FROM Task", Task.class)
                .executeUpdate();
    }

    @Override
    @LazyLogger
    public boolean deleteAllByUserId(Long authorId) {
        return false;
    }
}
