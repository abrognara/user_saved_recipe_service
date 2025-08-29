package com.brognara.user_saved_recipe_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(
        name = "user_lists",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "list_name"})
)
public class UserList {

    @Id
    // col name is id?
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID listId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "list_name", nullable = false)
    private String listName;

    @Column(name = "is_public")
    private Boolean isPublic = false;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

}
