package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.model.UserRecipeFolder;
import com.brognara.user_saved_recipe_service.model.UserSavedRecipe;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class LocalUserSavedRecipeService implements UserSavedRecipeService {

    // userId -> List of RecipeLists
    public ConcurrentMap<String, ConcurrentSkipListSet<UserRecipeFolder>> localSingleLevelUserRecipeLists =
            new ConcurrentHashMap<>();

    public Mono<String> createNewFolderForUser(final String userId, final UserRecipeFolder folder) {
        localSingleLevelUserRecipeLists.putIfAbsent(userId, new ConcurrentSkipListSet<>());

        final boolean addResult = localSingleLevelUserRecipeLists.get(userId).add(folder);
        if (!addResult) {
            throw new RuntimeException("Folder " + folder.getFolderName() + " already exists");
        }
        return Mono.just(folder.getFolderName());
    }

    public Mono<ConcurrentSkipListSet<UserRecipeFolder>> getFoldersForUser(final String userId) {
        localSingleLevelUserRecipeLists.putIfAbsent(userId, new ConcurrentSkipListSet<>());

        return Mono.just(localSingleLevelUserRecipeLists.get(userId));
    }

    private UserRecipeFolder getUserFolderOrThrow(String userId, String folderName) {
        return Optional.ofNullable(localSingleLevelUserRecipeLists.get(userId))
                .flatMap(userRecipeFolders ->
                        userRecipeFolders.stream()
                                .filter(f -> f.getFolderName().equals(folderName))
                                .findFirst())
                .orElseThrow(() -> new RuntimeException("Folder " + folderName + " does not exist for user " + userId));
    }

    public Mono<String> addRecipeToFolderForUser(
            final String userId, final String folderName, final UserSavedRecipe recipe) {
        final UserRecipeFolder folder = getUserFolderOrThrow(userId, folderName);
        folder.getSavedRecipes().add(recipe);
        return Mono.just("Success");
    }

    public Mono<String> deleteRecipeFromFolderForUser(final String userId, final String folderName, final String recipeName) {
        final UserRecipeFolder folder = getUserFolderOrThrow(userId, folderName);
        boolean removed = folder.getSavedRecipes().removeIf(r -> r.getRecipeName().equals(recipeName));
        if (!removed) {
            throw new RuntimeException("Recipe " + recipeName + " does not exist in folder " + folderName + " for user " + userId);
        }
        return Mono.just("Success");
    }

    public Mono<String> deleteFolderForUser(final String userId, final String folderName) {
        final UserRecipeFolder folder = getUserFolderOrThrow(userId, folderName);
        localSingleLevelUserRecipeLists.get(userId).remove(folder);
        return Mono.just("Success");
    }

}
