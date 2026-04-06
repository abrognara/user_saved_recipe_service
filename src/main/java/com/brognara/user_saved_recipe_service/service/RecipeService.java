package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.model.Recipe;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RecipeService {
    Mono<Recipe> getRecipeById(UUID id);
}
