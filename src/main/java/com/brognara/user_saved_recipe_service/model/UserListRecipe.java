package com.brognara.user_saved_recipe_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_lists_recipes")
public class UserListRecipe {

    @EmbeddedId
    private UserListRecipeId id;

    @ManyToOne
    @MapsId("listId") // maps the embedded id's listId to the FK
    @JoinColumn(name = "list_id", nullable = false)
    private UserList userList;

    @ManyToOne
    @MapsId("recipeId") // maps the embedded id's recipeId to the FK
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(name = "added_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date addedAt = new Date();

}
