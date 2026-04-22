package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dto.UserListDto;
import com.brognara.user_saved_recipe_service.model.*;
import com.brognara.user_saved_recipe_service.repository.UserListRecipeRepository;
import com.brognara.user_saved_recipe_service.repository.UserListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brognara.user_saved_recipe_service.utils.PgReactiveUtils.wrapMono;

@Service
@Profile("!dynamo")
public class SupabaseUserSavedRecipeService implements UserSavedRecipeService {

    private final UserListRepository userListRepository;
    private final UserListRecipeRepository userListRecipeRepository;
    private final UserService userService;
    private final RecipeDeduplicationFilter recipeDeduplicationFilter;

    @Autowired
    public SupabaseUserSavedRecipeService(
            UserListRepository userListRepository,
            UserListRecipeRepository userListRecipeRepository,
            final UserService userService,
            final RecipeDeduplicationFilter recipeDeduplicationFilter
    ) {
        this.userListRepository = userListRepository;
        this.userListRecipeRepository = userListRecipeRepository;
        this.userService = userService;
        this.recipeDeduplicationFilter = recipeDeduplicationFilter;
    }

    @Override
    public Mono<UserList> createNewListForUser(final String userId, final UserListDto userListDto) {
        return userService.getUserById(UUID.fromString(userId))
                .flatMap(user -> createNewListIfNotExists(user, userListDto));
    }

    private Mono<UserList> createNewListIfNotExists(final User user, final UserListDto list) {
        return Mono.fromCallable(() -> {
            Optional<UserList> userList = userListRepository.findByUserIdAndListName(user.getId(), list.getListName());
            if (userList.isPresent()) {
                throw new IllegalArgumentException("List already exists for user: " + list.getListName());
            }
            return userList;
        })
                .flatMap(nil ->
                        Mono.fromCallable(() -> {
                            UserList userList = new UserList();
                            userList.setUser(user);
                            userList.setListName(list.getListName());
                            return userListRepository.save(userList);
                        }).subscribeOn(Schedulers.boundedElastic()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<List<UserList>> getListsForUser(final String userId) {
        return userService.getUserById(UUID.fromString(userId))
                .flatMap(user -> wrapMono(() -> userListRepository.findByUserId(user.getId())));
    }

    @Override
    @Transactional
    public Mono<String> addRecipeToListForUser(final String userId, final String listId, final Recipe recipe) {
        return userService.getUserById(UUID.fromString(userId))
                .flatMap(user ->
                    wrapMono(() -> {
                        final UUID listIdUuid = UUID.fromString(listId);
                        UserList userList = userListRepository
                                .findByUserIdAndId(user.getId(), listIdUuid)
                                .orElseThrow(() -> new IllegalArgumentException("List not found"));

                        final Recipe r = recipeDeduplicationFilter.saveRecipeIfNotExistsAndGet(recipe);

                        if (userListRecipeRepository.existsByUserList_IdAndRecipe_Id(userList.getId(), r.getId())) {
                            throw new IllegalStateException("Recipe already exists in this list");
                        }

                        UserListRecipe userListRecipe = new UserListRecipe();
                        userListRecipe.setId(new UserListRecipeId(userList.getId(), r.getId()));
                        userListRecipe.setUserList(userList);
                        userListRecipe.setRecipe(r);
                        userListRecipe.setAddedAt(new Date());

                        userListRecipeRepository.save(userListRecipe);

                        return recipe.getName();
                    })
                );
    }

    @Override
    public Mono<String> deleteRecipeFromListForUser(final String userId, final String listId, final String recipeId) {
        return userService.getUserById(UUID.fromString(userId))
                .flatMap(user ->
                    wrapMono(() -> {
                        final UUID listIdUuid = UUID.fromString(listId);
                        final UUID recipeIdUuid = UUID.fromString(recipeId);
                        int deletedRows = userListRecipeRepository.deleteRecipeFromList(
                                user.getId(), listIdUuid, recipeIdUuid);
                        if (deletedRows == 0) {
                            throw new IllegalArgumentException("Recipe " + recipeId + " not found");
                        }
                        return recipeId;
                    })
                );
    }

    @Override
    public Mono<String> deleteListForUser(final String userId, final String listId) {
        return userService.getUserById(UUID.fromString(userId))
                .flatMap(user ->
                    wrapMono(() -> {
                        final UUID listIdUuid = UUID.fromString(listId);
                        int deletedRows = userListRepository.deleteByUserIdAndListId(user.getId(), listIdUuid);
                        if (deletedRows == 0) {
                            throw new IllegalArgumentException("List " + listId + " not found");
                        }
                        return listId;
                    })
                );
    }

    @Override
    public Mono<List<Recipe>> getSavedRecipesFromList(final String userId, final String listId) {
        return userService.getUserById(UUID.fromString(userId))
                .flatMap(user ->
                    wrapMono(() -> {
                        final UUID listIdUuid = UUID.fromString(listId);
                        return userListRecipeRepository.findRecipesByUserIdAndListId(user.getId(), listIdUuid);
                    })
                );
    }
}
