package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dto.RecipeDto;
import com.brognara.user_saved_recipe_service.dto.UserListDto;
import com.brognara.user_saved_recipe_service.model.*;
import com.brognara.user_saved_recipe_service.repository.RecipeRepository;
import com.brognara.user_saved_recipe_service.repository.UserListRecipeRepository;
import com.brognara.user_saved_recipe_service.repository.UserListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

@Service
public class SupabaseUserSavedRecipeService implements UserSavedRecipeService {

    private static final String FIREBASE = "firebase";

    private final UserListRepository userListRepository;
    private final UserListRecipeRepository userListRecipeRepository;
    private final RecipeRepository recipeRepository;
    private final UserService userService;

    @Autowired
    public SupabaseUserSavedRecipeService(
            UserListRepository userListRepository,
            UserListRecipeRepository userListRecipeRepository,
            RecipeRepository recipeRepository,
            final UserService userService
    ) {
        this.userListRepository = userListRepository;
        this.userListRecipeRepository = userListRecipeRepository;
        this.recipeRepository = recipeRepository;
        this.userService = userService;
    }

    @Override
    public Mono<UserList> createNewListForUser(final String userId, final UserListDto userListDto) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user -> createNewListIfNotExists(user, userListDto));
    }

    private Mono<UserList> createNewListIfNotExists(final User user, final UserListDto folder) {
        return Mono.fromCallable(() -> {
            Optional<UserList> userList = userListRepository.findByUserIdAndListName(user.getId(), folder.getListName());
            if (userList.isPresent()) {
                throw new IllegalArgumentException("List already exists for user: " + folder.getListName());
            }
            return userList;
        })
                .flatMap(nil ->
                        Mono.fromCallable(() -> {
                            UserList userList = new UserList();
                            userList.setUser(user);
                            userList.setListName(folder.getListName());
                            return userListRepository.save(userList);
                        }).subscribeOn(Schedulers.boundedElastic()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<List<UserList>> getListsForUser(String userId) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user ->
                        wrapMono(() ->
                                userListRepository.findByUserId(user.getId())
                        )
                );
    }

    private <T> Mono<T> wrapMono(Callable<T> callable) {
        return Mono.fromCallable(callable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    // Load UserList + Recipe, create new UserListRecipe, save it.
    // TODO If recipe names are not unique, use recipeId instead of recipeName for the lookup.
    @Override
    @Transactional
    public Mono<String> addRecipeToListForUser(String userId, String listName, RecipeDto recipe) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user ->
                    wrapMono(() -> {
                        UserList userList = userListRepository
                                .findByUserIdAndListName(user.getId(), listName)
                                .orElseThrow(() -> new IllegalArgumentException("List not found"));

                        Recipe r = recipeRepository
                                .findByRecipeName(recipe.getRecipeName())
                                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

                        if (userListRecipeRepository.existsByUserListAndRecipe(userList, r)) {
                            throw new IllegalStateException("Recipe already exists in this list");
                        }

                        UserListRecipe userListRecipe = new UserListRecipe();
                        userListRecipe.setUserList(userList);
                        userListRecipe.setRecipe(r);
                        userListRecipe.setAddedAt(new Date());

                        userListRecipeRepository.save(userListRecipe);

                        return recipe.getRecipeName();
                    })
                );
    }

    @Override
    public Mono<String> deleteRecipeFromListForUser(String userId, String listName, String recipeName) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user ->
                    wrapMono(() -> {
                        int deletedRows = userListRecipeRepository.deleteRecipeFromList(
                                user.getId(), listName, recipeName);
                        if (deletedRows == 0) {
                            throw new IllegalArgumentException("Recipe " + recipeName + " not found");
                        }
                        return recipeName;
                    })
                );
    }

    @Override
    public Mono<String> deleteListForUser(String userId, String listName) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user ->
                    wrapMono(() -> {
                        int deletedRows = userListRepository.deleteByUserIdAndListName(user.getId(), listName);
                        if (deletedRows == 0) {
                            throw new IllegalArgumentException("List " + listName + " not found");
                        }
                        return listName;
                    })
                );
    }

    @Override
    public Mono<List<Recipe>> getSavedRecipesFromList(String userId, String listName) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user ->
                    wrapMono(() ->
                            userListRecipeRepository.findRecipesByUserIdAndListName(user.getId(), listName)
                    )
                );
    }
}
