package com.brognara.user_saved_recipe_service.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserListRecipeId implements Serializable {
    private UUID userList;
    private UUID recipe;

    // default constructor, equals, hashCode
}
