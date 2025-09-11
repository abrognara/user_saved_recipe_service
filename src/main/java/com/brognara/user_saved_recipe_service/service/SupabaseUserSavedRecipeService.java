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

import static com.brognara.user_saved_recipe_service.utils.PgReactiveUtils.wrapMono;

@Service
public class SupabaseUserSavedRecipeService implements UserSavedRecipeService {

    private static final String FIREBASE = "firebase";

    private final UserListRepository userListRepository;
    private final UserListRecipeRepository userListRecipeRepository;
    private final RecipeRepository recipeRepository;
    private final UserService userService;
    private final RecipeDeduplicationFilter recipeDeduplicationFilter;

    @Autowired
    public SupabaseUserSavedRecipeService(
            UserListRepository userListRepository,
            UserListRecipeRepository userListRecipeRepository,
            RecipeRepository recipeRepository,
            final UserService userService, final RecipeDeduplicationFilter recipeDeduplicationFilter
    ) {
        this.userListRepository = userListRepository;
        this.userListRecipeRepository = userListRecipeRepository;
        this.recipeRepository = recipeRepository;
        this.userService = userService;
        this.recipeDeduplicationFilter = recipeDeduplicationFilter;
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

    // Load UserList + Recipe, create new UserListRecipe, save it.
    // TODO Does this do too many db operations?
    @Override
    @Transactional
    public Mono<String> addRecipeToListForUser(final String userId, final String listName, final Recipe recipe) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user ->
                    wrapMono(() -> {
                        UserList userList = userListRepository
                                .findByUserIdAndListName(user.getId(), listName)
                                .orElseThrow(() -> new IllegalArgumentException("List not found"));

                        // this will have the same content as 'recipe', except with normalized url
                        final Recipe r = recipeDeduplicationFilter.saveRecipeIfNotExistsAndGet(recipe);

                        if (userListRecipeRepository.existsByUserListAndRecipe(userList, r)) {
                            throw new IllegalStateException("Recipe already exists in this list");
                        }

                        UserListRecipe userListRecipe = new UserListRecipe();
                        userListRecipe.setUserList(userList);
                        userListRecipe.setRecipe(r);
                        userListRecipe.setAddedAt(new Date());

                        userListRecipeRepository.save(userListRecipe);

                        return recipe.getName();
                    })
                );
    }

    @Override
    public Mono<String> deleteRecipeFromListForUser(final String userId,
                                                    final String listName, final String recipeName) {
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
    public Mono<String> deleteListForUser(final String userId, final String listName) {
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
    public Mono<List<Recipe>> getSavedRecipesFromList(final String userId, final String listName) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user ->
                    wrapMono(() ->
                            userListRecipeRepository.findRecipesByUserIdAndListName(user.getId(), listName)
                    )
                );
    }
}
