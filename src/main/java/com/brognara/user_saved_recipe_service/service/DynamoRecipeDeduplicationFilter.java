package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dynamo.model.DynamoRecipe;
import com.brognara.user_saved_recipe_service.dynamo.repository.DynamoRecipeRepository;
import com.brognara.user_saved_recipe_service.model.Recipe;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.UUID;

@Log4j2
@Service
@Profile("dynamo")
public class DynamoRecipeDeduplicationFilter {

    private final DynamoRecipeRepository recipeRepository;

    public DynamoRecipeDeduplicationFilter(DynamoRecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    private String normalizeUrl(String url) {
        try {
            URI uri = new URI(url.toLowerCase());
            return new URI(
                    uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null,
                    null
            ).toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid recipe URL: " + url, e);
        }
    }

    public Recipe saveRecipeIfNotExistsAndGet(final Recipe recipe) {
        final String normalizedUrl = normalizeUrl(recipe.getUrl());
        log.info("Normalized url ; input={} ; output={}", recipe.getUrl(), normalizedUrl);

        return recipeRepository.findByUrl(normalizedUrl)
                .map(DynamoRecipe::toRecipe)
                .orElseGet(() -> {
                    recipe.setUrl(normalizedUrl);
                    if (recipe.getId() == null) {
                        recipe.setId(UUID.randomUUID());
                    }
                    DynamoRecipe saved = recipeRepository.save(DynamoRecipe.from(recipe));
                    return saved.toRecipe();
                });
    }
}
