package com.emobile.springtodo.util;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.util.Optional;

public class JdbcUtil {
    private JdbcUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Optional Extractor.
     *
     * @param mapper for map {@link T} entity
     * @param <T>    entity type for mapping
     * @return {@link Optional} mapped entity if ResultSet exist
     */
    public static <T> ResultSetExtractor<Optional<T>> optionalExtractor(
            RowMapper<? extends T> mapper) {
        return rs -> rs.next()
                ? Optional.of(mapper.mapRow(rs, 1))
                : Optional.empty();
    }
}
