package com.brognara.user_saved_recipe_service.repository;

import com.brognara.user_saved_recipe_service.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    Optional<Recipe> findByRecipeName(String recipeName);
    Optional<Recipe> findByUrl(String url);

}
