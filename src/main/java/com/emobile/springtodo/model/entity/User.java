package com.emobile.springtodo.model.entity;

import com.emobile.springtodo.model.security.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
//@NamedEntityGraph(
//        name = "User.withTasks",
//        attributeNodes = @NamedAttributeNode("taskList")
//)
@Entity
@Table(name = "users",
        indexes = {
                @Index(columnList = User.Fields.username, unique = true),
                @Index(columnList = User.Fields.email, unique = true)
        })
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Long User id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Fields.id)
    private Long id;
    /**
     * User name.
     */
    @Column(name = Fields.username)
    private String username;
    /**
     * User password.
     */
    @Column(name = Fields.password)
    private String password;
    /**
     * User email.
     */
    @Column(name = Fields.email)
    private String email;
    /**
     * User authorization role type.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = Fields.role)
    private RoleType role;
    /**
     * User tasks.
     */
    @ToString.Exclude
    @OneToMany(mappedBy = Task.Fields.author, orphanRemoval = true)
    @Builder.Default
    private List<Task> taskList = new ArrayList<>();

    @ToString.Include
    public String getTaskListSize() {
        return String.valueOf(taskList.size());
    }
}
