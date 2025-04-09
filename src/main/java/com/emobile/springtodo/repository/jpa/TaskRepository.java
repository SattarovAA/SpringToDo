package com.emobile.springtodo.repository.jpa;

import com.emobile.springtodo.model.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    void deleteAllByAuthor_Id(Long authorId);
}
