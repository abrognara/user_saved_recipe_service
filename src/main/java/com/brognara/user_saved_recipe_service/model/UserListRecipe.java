package com.brognara.user_saved_recipe_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user_list_recipes")
@IdClass(UserListRecipeId.class)
public class UserListRecipe {

    @Id
    @ManyToOne
    @JoinColumn(name = "list_id", nullable = false)
    private UserList userList;

    @Id
    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Temporal(TemporalType.TIMESTAMP)
    private Date addedAt = new Date();

}
