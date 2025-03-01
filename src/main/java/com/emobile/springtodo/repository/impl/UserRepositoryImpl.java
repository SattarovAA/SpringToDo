package com.emobile.springtodo.repository.impl;

import com.emobile.springtodo.aop.LazyLogger;
import com.emobile.springtodo.exception.NullResultSetException;
import com.emobile.springtodo.model.entity.Task;
import com.emobile.springtodo.model.entity.TaskStatus;
import com.emobile.springtodo.model.entity.User;
import com.emobile.springtodo.model.security.RoleType;
import com.emobile.springtodo.model.util.Page;
import com.emobile.springtodo.model.util.PageInfo;
import com.emobile.springtodo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.emobile.springtodo.util.JdbcUtil.optionalExtractor;

/**
 * Repository for working with entity {@link User}.
 */
@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {
    /**
     * For work with db.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Find all {@link User} objects from db
     * with {@code pageNumber} and {@code pageSize} from {@code pageInfo}.<br>
     * LIMIT - {@code pageSize}.<br>
     * OFFSET - ({@code pageNumber} -1) * {@code pageSize}.
     *
     * @return {@link Page} with {@link User} list.
     * @see #getUserMapper()
     */
    @Override
    @LazyLogger
    public Page<User> findAll(PageInfo pageInfo) {
        final String sql = """
                SELECT * FROM users
                LIMIT ? OFFSET ?
                """;
        int beginInd = (pageInfo.pageNumber() - 1) * pageInfo.pageSize();
        return new Page<>(
                jdbcTemplate.query(
                        sql,
                        getUserMapper(),
                        pageInfo.pageSize(),
                        beginInd
                )
        );
    }

    /**
     * Search object {@link User} in db.
     *
     * @param id id searched {@link User} object
     * @return {@link Optional} if exist, {@link Optional#empty()} if not
     * @see #getUserTaskMapper()
     */
    @Override
    @LazyLogger
    public Optional<User> findById(Long id) {
        final String sql = """
                SELECT u.*, t.*
                FROM users u
                         LEFT JOIN tasks t on u.id = t.author_id
                WHERE u.id= ?
                """;
        return jdbcTemplate.query(
                sql,
                optionalExtractor(getUserTaskMapper()),
                id
        );
    }
    /**
     * Find {@link User}
     * with {@code User.username} equals {@code username}.
     *
     * @param username username searched {@link User}
     * @return {@link Optional} if exist, {@link Optional#empty()} if not
     */
    @Override
    @LazyLogger
    public Optional<User> findByUsername(String username) {
        final String sql = """
                SELECT u.*, t.*
                FROM users u
                         LEFT JOIN tasks t on u.id = t.author_id
                WHERE u.username= ?
                """;
        return jdbcTemplate.query(
                sql,
                optionalExtractor(getUserTaskMapper()),
                username
        );
    }

    /**
     * Save object model of type {@link User}.
     *
     * @param model object of type {@link User} to save
     * @return object of type {@link User} that was saved
     * @throws NullResultSetException if returning value from db is null
     */
    @Override
    @LazyLogger
    public User save(User model) {
        final String sql = """
                INSERT INTO users(username, password, email, role)
                VALUES (?,?,?,?)
                RETURNING *
                """;
        return jdbcTemplate.query(sql,
                        optionalExtractor(getUserMapper()),
                        model.getUsername(),
                        model.getPassword(),
                        model.getEmail(),
                        model.getRole().name())
                .orElseThrow(NullResultSetException.create(
                        "userRepository save() query exception"
                ));
    }

    /**
     * Update object model of type {@link User}
     * by {@code T.id} value.
     *
     * @param model object of type {@link User} to update
     * @return object of type {@link User} that was updated
     * @throws NullResultSetException if returning value from db is null
     */
    @Override
    @LazyLogger
    public User update(User model) {
        final String sql = """
                UPDATE users
                SET username=?, password=?, email=?, role=?
                WHERE id=?
                RETURNING *
                """;
        return jdbcTemplate.query(sql,
                        optionalExtractor(getUserMapper()),
                        model.getUsername(),
                        model.getPassword(),
                        model.getEmail(),
                        model.getRole().name(),
                        model.getId())
                .orElseThrow(NullResultSetException.create(
                        "userRepository update() query exception"
                ));
    }

    /**
     * Delete object with {@code User.id}
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
                FROM users
                WHERE id=?
                """;
        int result = jdbcTemplate.update(sql, id);
        return result != 0;
    }

    /**
     * Check duplicate {@code username}.
     * For save {@link User}.
     *
     * @param username username searched {@link User}
     * @return {@code true} if exist, {@code false} if not
     * @see #getExistsMapper()
     */
    @Override
    @LazyLogger
    public boolean existsByUsername(String username) {
        final String sql = """
                SELECT EXISTS (SELECT *
                        FROM users
                        WHERE username = ?)
                        AS result
                """;
        return jdbcTemplate.queryForObject(sql, getExistsMapper(), username);
    }

    /**
     * Check duplicate {@code username}.
     * For update {@link User}.
     *
     * @param username username searched {@link User}
     * @return {@code true} if exist, {@code false} if not
     * @see #getExistsMapper()
     */
    @Override
    @LazyLogger
    public boolean existsByUsernameAndIdNot(String username, Long currentUserId) {
        final String sql = """
                SELECT EXISTS (SELECT *
                        FROM users
                        WHERE username = ?
                        AND id != ?)
                        AS result
                """;
        return jdbcTemplate.queryForObject(
                sql,
                getExistsMapper(),
                username,
                currentUserId
        );
    }

    /**
     * Check duplicate {@code email}.
     * For save {@link User}.
     *
     * @param email username searched {@link User}
     * @return {@code true} if exist, {@code false} if not
     * @see #getExistsMapper()
     */
    @Override
    @LazyLogger
    public boolean existsByEmail(String email) {
        final String sql = """
                SELECT EXISTS (SELECT *
                        FROM users
                        WHERE email = ?)
                        AS result
                """;
        return jdbcTemplate.queryForObject(sql, getExistsMapper(), email);
    }

    /**
     * Check duplicate {@code email}.
     * For update {@link User}.
     *
     * @param email username searched {@link User}
     * @return {@code true} if exist, {@code false} if not
     * @see #getExistsMapper()
     */
    @Override
    @LazyLogger
    public boolean existsByEmailAndIdNot(String email, Long currentUserId) {
        final String sql = """
                SELECT EXISTS (SELECT *
                        FROM users
                        WHERE email = ?
                        AND id != ?)
                        AS result
                """;
        return jdbcTemplate.queryForObject(
                sql,
                getExistsMapper(),
                email,
                currentUserId
        );
    }

    /**
     * {@link User} {@link RowMapper}.
     *
     * @return mapper for {@link User} entity
     */
    private RowMapper<User> getUserMapper() {
        return (rs, rn) -> User.builder()
                .id(rs.getLong(User.Fields.id))
                .username(rs.getString(User.Fields.username))
                .password(rs.getString(User.Fields.password))
                .email(rs.getString(User.Fields.email))
                .role(RoleType.valueOf(rs.getString(User.Fields.role)))
                .build();
    }

    /**
     * Грустно и костыльно, но работает.
     * {code taskTest} for wasNull() check.
     *
     * @return mapper for {@link User} with filled {@code taskList} fields
     * @see #fillUserFields(ResultSet, List)
     * @see #fillTaskFields(ResultSet)
     */
    private RowMapper<User> getUserTaskMapper() {
        return (rs, rn) -> {
            List<Task> taskList = new ArrayList<>();
            User user = fillUserFields(rs, taskList);
            long taskTest = rs.getLong("author_id");
            if (!rs.wasNull()) {
                do {
                    Task task = fillTaskFields(rs);
                    taskList.add(task);
                } while (rs.next());
            }
            return user;
        };
    }

    /**
     * fill {@link User} entity.
     * {@code id} columnIndex - 1.
     *
     * @param rs       resultSet for map
     * @param taskList list ref
     * @return {@link User} with filled fields
     * @throws SQLException if resultSet get exception
     */
    private User fillUserFields(ResultSet rs, List<Task> taskList) throws SQLException {
        return User.builder()
                .id(rs.getLong(1))
                .username(rs.getString(User.Fields.username))
                .password(rs.getString(User.Fields.password))
                .email(rs.getString(User.Fields.email))
                .role(RoleType.valueOf(rs.getString(User.Fields.role)))
                .taskList(taskList)
                .build();
    }

    /**
     * fill {@link Task} entity.
     * {@code id} columnIndex - 6.
     *
     * @param rs resultSet for map
     * @return {@link Task} with filled fields
     * @throws SQLException if resultSet get exception
     */
    private Task fillTaskFields(ResultSet rs) throws SQLException {
        return Task.builder()
                .id(rs.getLong(6))
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

    /**
     * {@link RowMapper} for exist check.
     *
     * @return mapper for exist check
     */
    private RowMapper<Boolean> getExistsMapper() {
        return (resultSet, rowNum) ->
                resultSet.getString("result")
                        .equals("t");
    }
}
