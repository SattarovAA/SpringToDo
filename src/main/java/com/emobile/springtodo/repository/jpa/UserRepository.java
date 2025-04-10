package com.emobile.springtodo.repository.jpa;

import com.emobile.springtodo.model.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(value = "User.withTasks", type = EntityGraph.EntityGraphType.FETCH)
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long currentUserId);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long currentUserId);
}
