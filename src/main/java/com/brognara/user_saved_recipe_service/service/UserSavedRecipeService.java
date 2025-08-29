package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dto.UserListDto;
import com.brognara.user_saved_recipe_service.dto.RecipeDto;
import com.brognara.user_saved_recipe_service.model.Recipe;
import com.brognara.user_saved_recipe_service.model.UserList;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserSavedRecipeService {
    Mono<UserList> createNewListForUser(final String userId, final UserListDto userListDto);
    Mono<List<UserList>> getListsForUser(String userId);
    Mono<String> addRecipeToListForUser(String userId, String listName, RecipeDto recipe);
    Mono<String> deleteRecipeFromListForUser(String userId, String listName, String recipeName);
    Mono<String> deleteListForUser(String userId, String listName);
    Mono<List<Recipe>> getSavedRecipesFromList(String userId, String listName);
} 