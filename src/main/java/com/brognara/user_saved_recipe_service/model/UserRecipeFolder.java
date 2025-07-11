package com.brognara.user_saved_recipe_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRecipeFolder implements Comparable<UserRecipeFolder> {
    private String folderName;
    private String createdByUser;
    private long creationTimestamp;
    @Builder.Default
    private List<UserSavedRecipe> savedRecipes = Collections.synchronizedList(new LinkedList<>());

    public int getRecipeCount() {
        return savedRecipes.size();
    }

    @Override
    public int compareTo(UserRecipeFolder o) {
        return this.folderName.compareToIgnoreCase(o.folderName);
    }
}
