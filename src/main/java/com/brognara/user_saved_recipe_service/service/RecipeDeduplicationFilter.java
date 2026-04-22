package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.model.Recipe;
import com.brognara.user_saved_recipe_service.repository.RecipeRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Log4j2
@Service
@Profile("!dynamo")
public class RecipeDeduplicationFilter {

    private final RecipeRepository recipeRepository;

    public RecipeDeduplicationFilter(final RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    private String normalizeUrl(String url) {
        try {
            URI uri = new URI(url.toLowerCase());
            // rebuild URL without query/fragment
            return new URI(
                    uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null,   // drop query
                    null    // drop fragment
            ).toString();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid recipe URL: " + url, e);
        }
    }

    @Transactional
    public Recipe saveRecipeIfNotExistsAndGet(final Recipe recipe) {
        final String normalizedUrl = normalizeUrl(recipe.getUrl());
        log.info("Normalized url ; input={} ; output={}", recipe.getUrl(), normalizedUrl);

        return recipeRepository.findByUrl(normalizedUrl)
                .orElseGet(() -> {
                    recipe.setUrl(normalizedUrl);
                    return recipeRepository.save(recipe);
                });
    }
}
