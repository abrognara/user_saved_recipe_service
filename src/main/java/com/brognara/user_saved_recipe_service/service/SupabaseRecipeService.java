package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.exception.RecipeNotFoundException;
import com.brognara.user_saved_recipe_service.model.Recipe;
import com.brognara.user_saved_recipe_service.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.brognara.user_saved_recipe_service.utils.PgReactiveUtils.wrapMono;

@Service
@Profile("!dynamo")
public class SupabaseRecipeService implements RecipeService {

    private final RecipeRepository recipeRepository;

    @Autowired
    public SupabaseRecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Mono<Recipe> getRecipeById(final UUID id) {
        return wrapMono(() -> recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("Could not find recipe with id " + id)));
    }
}
