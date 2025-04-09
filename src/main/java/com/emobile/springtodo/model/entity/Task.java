package com.emobile.springtodo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity Task.
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
@FieldNameConstants
@Builder
//@NamedEntityGraph(
//        name = "Task.withAuthor",
//        attributeNodes = @NamedAttributeNode("author")
//)
@Entity
@Table(name = "tasks")
public class Task implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Long Task id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Fields.id)
    private Long id;
    /**
     * Task name.
     */
    @Column(name = Fields.name)
    private String name;
    /**
     * Task description.
     */
    @Column(name = Fields.description)
    private String description;
    /**
     * Task {@link TaskStatus} status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = Fields.status)
    private TaskStatus status;
    /**
     * Task creation time without timezone.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    /**
     * Task update time without timezone.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    /**
     * The User who owns the Task.
     */
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;

    @EqualsAndHashCode.Include
    public Long getAuthorId() {
        return author.getId();
    }
}
