package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dynamo.repository.DynamoRecipeRepository;
import com.brognara.user_saved_recipe_service.exception.RecipeNotFoundException;
import com.brognara.user_saved_recipe_service.model.Recipe;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@Profile("dynamo")
public class DynamoRecipeService implements RecipeService {

    private final DynamoRecipeRepository recipeRepository;

    public DynamoRecipeService(DynamoRecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Mono<Recipe> getRecipeById(UUID id) {
        return Mono.fromCallable(() ->
                recipeRepository.findById(id.toString())
                        .map(dr -> dr.toRecipe())
                        .orElseThrow(() -> new RecipeNotFoundException("Could not find recipe with id " + id))
        ).subscribeOn(Schedulers.boundedElastic());
    }
}
