package com.emobile.springtodo.model.entity;

import com.emobile.springtodo.model.security.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity User.
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@FieldNameConstants
@Builder
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Long User id.
     */
    private Long id;
    /**
     * User name.
     */
    private String username;
    /**
     * User password.
     */
    private String password;
    /**
     * User email.
     */
    private String email;
    /**
     * User authorization role type.
     */
    private RoleType role;
    /**
     * User tasks.
     */
    @Builder.Default
    private List<Task> taskList = new ArrayList<>();
}
