package com.emobile.springtodo.repository.impl;

import com.emobile.springtodo.aop.LazyLogger;
import com.emobile.springtodo.exception.NullResultSetException;
import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.entity.TaskStatus;
import com.emobile.springtodo.model.util.Page;
import com.emobile.springtodo.model.util.PageInfo;
import com.emobile.springtodo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.emobile.springtodo.util.JdbcUtil.optionalExtractor;

/**
 * Repository for working with entity {@link Task}.
 */
@RequiredArgsConstructor
@Repository
public class TaskRepositoryImpl implements TaskRepository {
    /**
     * For work with db.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Find all {@link Task} objects from db
     * with {@code pageNumber} and {@code pageSize} from {@code pageInfo}.<br>
     * LIMIT - {@code pageSize}.<br>
     * OFFSET - ({@code pageNumber} -1) * {@code pageSize}.
     *
     * @return {@link Page} with {@link Task} list.
     * @see #getMapper()
     */
    @Override
    @LazyLogger
    public Page<Task> findAll(PageInfo pageInfo) {
        final String sql = """
                SELECT * FROM tasks
                LIMIT ? OFFSET ?
                """;
        int beginInd = (pageInfo.pageNumber() - 1) * pageInfo.pageSize();
        return new Page<>(
                jdbcTemplate.query(sql,
                        getMapper(),
                        pageInfo.pageSize(),
                        beginInd
                )
        );
    }

    /**
     * Search object {@link Task} in db.
     *
     * @param id id searched {@link Task} object
     * @return {@link Optional} if exist, {@link Optional#empty()} if not
     * @see #getMapper()
     */
    @Override
    @LazyLogger
    public Optional<Task> findById(Long id) {
        final String sql = """
                SELECT *
                FROM tasks
                WHERE id=?
                """;
        return jdbcTemplate.query(
                sql,
                optionalExtractor(getMapper()),
                id
        );
    }

    /**
     * Save object model of type {@link Task}.
     *
     * @param model object of type {@link Task} to save
     * @return object of type {@link Task} that was saved
     * @throws NullResultSetException if returning value from db is null
     */
    @Override
    @LazyLogger
    public Task save(Task model) {
        final String sql = """
                INSERT INTO tasks(name, description, status,
                 created_at, updated_at, author_id)
                VALUES (?,?,?,?,?,?)
                RETURNING *
                """;
        return jdbcTemplate.query(sql,
                optionalExtractor(getMapper()),
                model.getName(),
                model.getDescription(),
                model.getStatus().name(),
                model.getCreatedAt(),
                model.getUpdatedAt(),
                model.getAuthorId()
        ).orElseThrow(NullResultSetException.create(
                "taskRepository save() query exception"
        ));
    }

    /**
     * Update object model of type {@link Task}
     * by {@code T.id} value.
     *
     * @param model object of type {@link Task} to update
     * @return object of type {@link Task} that was updated
     * @throws NullResultSetException if returning value from db is null
     */
    @Override
    @LazyLogger
    public Task update(Task model) {
        final String sql = """
                UPDATE tasks
                SET name=?, description=?, status=?, updated_at=?, author_id =?
                WHERE id=?
                RETURNING *
                """;
        return jdbcTemplate.query(sql,
                optionalExtractor(getMapper()),
                model.getName(),
                model.getDescription(),
                model.getStatus().name(),
                model.getUpdatedAt(),
                model.getAuthorId(),
                model.getId()
        ).orElseThrow(NullResultSetException.create(
                "taskRepository update() query exception"
        ));
    }

    /**
     * Delete object with {@code Task.id}
     * equals {@code id} from database.
     *
     * @param id id of the object to be deleted
     * @return {@code true} if success
     */
    @Override
    @LazyLogger
    public boolean deleteById(Long id) {
        final String sql = """
                DELETE
                FROM tasks
                WHERE id=?
                """;
        int result = jdbcTemplate.update(sql, id);
        return result != 0;
    }

    /**
     * Delete all {@link Task} objects in database.
     *
     * @return {@code true} if success
     */
    @Override
    @LazyLogger
    public boolean deleteAll() {
        final String sql = "TRUNCATE tasks";
        int result = jdbcTemplate.update(sql);
        return result != 0;
    }

    /**
     * Delete all {@link Task} objects in database
     * for user with {@code userId}.
     *
     * @return {@code true} if success
     */
    @Override
    @LazyLogger
    public boolean deleteAllByUserId(Long userId) {
        final String sql = """
                DELETE
                FROM tasks
                WHERE author_id=?
                """;
        int result = jdbcTemplate.update(sql, userId);
        return result != 0;
    }

    /**
     * {@link Task} {@link RowMapper}.
     *
     * @return mapper for {@link Task} entity
     */
    private RowMapper<Task> getMapper() {
        return (rs, rn) -> Task.builder()
                .id(rs.getLong(Task.Fields.id))
                .name(rs.getString(Task.Fields.name))
                .description(rs.getString(Task.Fields.description))
                .status(TaskStatus.valueOf(rs.getString(Task.Fields.status)))
                .createdAt(rs.getTimestamp("created_at")
                        .toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at")
                        .toLocalDateTime())
                .authorId(rs.getLong("author_id"))
                .build();
    }
}
