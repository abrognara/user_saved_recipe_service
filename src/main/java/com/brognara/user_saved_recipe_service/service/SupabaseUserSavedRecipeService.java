package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.model.*;
import com.brognara.user_saved_recipe_service.repository.RecipeRepository;
import com.brognara.user_saved_recipe_service.repository.UserListRecipeRepository;
import com.brognara.user_saved_recipe_service.repository.UserListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class SupabaseUserSavedRecipeService {

    private static final String FIREBASE = "firebase";

    private final UserListRepository userListRepository;
    private final UserListRecipeRepository userListRecipeRepository;
    private final UserService userService;

    @Autowired
    public SupabaseUserSavedRecipeService(
            UserListRepository userListRepository,
            UserListRecipeRepository userListRecipeRepository, final UserService userService
    ) {
        this.userListRepository = userListRepository;
        this.userListRecipeRepository = userListRecipeRepository;
        this.userService = userService;
    }

    @Override
    public Mono<UserList> createNewListForUser(final String userId, final UserRecipeFolder folder) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user -> createNewListIfNotExists(user, folder));
    }

    private Mono<UserList> createNewListIfNotExists(final User user, final UserRecipeFolder folder) {
        return Mono.fromCallable(() -> {
            Optional<UserList> userList = userListRepository.findByUserIdAndListName(user.getId(), folder.getFolderName());
            if (userList.isPresent()) {
                throw new IllegalArgumentException("List already exists for user: " + folder.getFolderName());
            }
            return userList;
        })
                .flatMap(nil ->
                        Mono.fromCallable(() -> {
                            UserList userList = new UserList();
                            userList.setUser(user);
                            userList.setListName(folder.getFolderName());
                            return userListRepository.save(userList);
                        }).subscribeOn(Schedulers.boundedElastic()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<ConcurrentSkipListSet<UserRecipeFolder>> getFoldersForUser(String userId) {
        return userListRepository.findByUserId(userId);
    }

    @Override
    public Mono<String> addRecipeToFolderForUser(String userId, String folderName, UserSavedRecipe recipe) {
        return userListRecipeRepository.save(new UserListRecipe(userList, recipe));
    }

    @Override
    public Mono<String> deleteRecipeFromFolderForUser(String userId, String folderName, String recipeName) {
        return userListRecipeRepository.deleteByUserListAndRecipe(userList, recipe);
    }

    @Override
    public Mono<String> deleteFolderForUser(String userId, String folderName) {
        return userListRepository.delete(userList);
    }

    @Override
    public Mono<List<UserSavedRecipe>> getSavedRecipesFromFolder(String userId, String folderName) {
        return userListRecipeRepository.findByUserList(userList);
    }
}
