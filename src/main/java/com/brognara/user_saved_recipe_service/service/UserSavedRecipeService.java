package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.model.UserRecipeFolder;
import com.brognara.user_saved_recipe_service.model.UserSavedRecipe;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public interface UserSavedRecipeService {
    Mono<String> createNewListForUser(String userId, UserRecipeFolder folder);
    Mono<ConcurrentSkipListSet<UserRecipeFolder>> getListsForUser(String userId);
    Mono<String> addRecipeToListForUser(String userId, String folderName, UserSavedRecipe recipe);
    Mono<String> deleteRecipeFromListForUser(String userId, String folderName, String recipeName);
    Mono<String> deleteListForUser(String userId, String folderName);
    Mono<List<UserSavedRecipe>> getSavedRecipesFromList(String userId, String folderName);
} 