package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.model.UserRecipeFolder;
import com.brognara.user_saved_recipe_service.model.UserSavedRecipe;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentSkipListSet;

public interface UserSavedRecipeService {
    Mono<String> createNewFolderForUser(String userId, UserRecipeFolder folder);
    Mono<ConcurrentSkipListSet<UserRecipeFolder>> getFoldersForUser(String userId);
    Mono<String> addRecipeToFolderForUser(String userId, String folderName, UserSavedRecipe recipe);
    Mono<String> deleteRecipeFromFolderForUser(String userId, String folderName, String recipeName);
    Mono<String> deleteFolderForUser(String userId, String folderName);
} 