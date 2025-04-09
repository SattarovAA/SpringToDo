package com.emobile.springtodo.model.util;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Required to parameterize findAll queries.
 */
public record PageInfo(
        @NotNull(message = "Field pageSize must be filled!")
        @Min(value = MIN_PAGE_SIZE,
                message = "Field pageSize must not be less then "
                          + MIN_PAGE_SIZE)
        @Max(value = MAX_PAGE_SIZE,
                message = "Field pageSize must not be larger then "
                          + MAX_PAGE_SIZE)
        Integer pageSize,
        @Min(value = MIN_NUMBER_SIZE,
                message = "Field pageNumber must not be less then "
                          + MIN_NUMBER_SIZE)
        @NotNull(message = "Field pageNumber must be filled!")
        Integer pageNumber
) {
    /**
     * Minimum size of the page field.
     */
    private static final int MIN_PAGE_SIZE = 1;
    /**
     * Maximum size of the page field.
     */
    private static final int MAX_PAGE_SIZE = 20;
    /**
     * Minimum size of the number field.
     */
    private static final int MIN_NUMBER_SIZE = 0;
}
