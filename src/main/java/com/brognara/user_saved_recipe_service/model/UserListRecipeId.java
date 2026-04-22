package com.brognara.user_saved_recipe_service.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Embeddable
public class UserListRecipeId implements Serializable {
    private UUID listId;
    private UUID recipeId;
}
