package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dto.UserListDto;
import com.brognara.user_saved_recipe_service.dto.RecipeDto;
import com.brognara.user_saved_recipe_service.model.User;
import com.brognara.user_saved_recipe_service.model.UserList;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;

//@Service
//public class LocalUserSavedRecipeService implements UserSavedRecipeService {
//
//    // userId -> List of RecipeLists
//    public ConcurrentMap<String, ConcurrentSkipListSet<UserList>> localSingleLevelUserRecipeLists =
//            new ConcurrentHashMap<>();
//
//    @Override
//    public Mono<UserList> createNewListForUser(final String userId, final UserListDto userListDto) {
//        localSingleLevelUserRecipeLists.putIfAbsent(userId, new ConcurrentSkipListSet<>());
//
//        final boolean addResult = localSingleLevelUserRecipeLists.get(userId).add(userListDto);
//        if (!addResult) {
//            throw new RuntimeException("Folder " + userListDto.getListName() + " already exists");
//        }
//        UserList userList = new UserList();
//        userList.setListId(UUID.randomUUID());
//        userList.setListName(userListDto.getListName());
//        userList.setUser(getMockUser());
//        userList.setCreatedAt(new Date());
//        userList.setIsPublic(false);
//        return Mono.just(userList);
//    }
//
//    private User getMockUser() {
//        User user = new User();
//        user.setId(UUID.randomUUID());
//        user.setAuthProvider("firebase");
//        user.setAuthProviderId("abcd1234");
//        user.setCreatedAt(new Date());
//        user.setEmail("test@gmail.com");
//        user.setDisplayName("test-user");
//        user.setLastLogin(new Date());
//        return user;
//    }
//
//    public Mono<List<UserList>> getListsForUser(final String userId) {
//        localSingleLevelUserRecipeLists.putIfAbsent(userId, new ConcurrentSkipListSet<>());
//
//        return Mono.just(localSingleLevelUserRecipeLists.get(userId));
//    }
//
//    private UserListDto getUserFolderOrThrow(String userId, String folderName) {
//        return Optional.ofNullable(localSingleLevelUserRecipeLists.get(userId))
//                .flatMap(userRecipeFolders ->
//                        userRecipeFolders.stream()
//                                .filter(f -> f.getListName().equals(folderName))
//                                .findFirst())
//                .orElseThrow(() -> new RuntimeException("Folder " + folderName + " does not exist for user " + userId));
//    }
//
//    public Mono<String> addRecipeToListForUser(
//            final String userId, final String folderName, final RecipeDto recipe) {
//        final UserListDto folder = getUserFolderOrThrow(userId, folderName);
//        folder.getSavedRecipes().add(recipe);
//        return Mono.just("Success");
//    }
//
//    public Mono<String> deleteRecipeFromListForUser(final String userId, final String folderName, final String recipeName) {
//        final UserListDto folder = getUserFolderOrThrow(userId, folderName);
//        boolean removed = folder.getSavedRecipes().removeIf(r -> r.getRecipeName().equals(recipeName));
//        if (!removed) {
//            throw new RuntimeException("Recipe " + recipeName + " does not exist in folder " + folderName + " for user " + userId);
//        }
//        return Mono.just("Success");
//    }
//
//    public Mono<String> deleteListForUser(final String userId, final String folderName) {
//        final UserListDto folder = getUserFolderOrThrow(userId, folderName);
//        localSingleLevelUserRecipeLists.get(userId).remove(folder);
//        return Mono.just("Success");
//    }
//
//    public Mono<List<RecipeDto>> getSavedRecipesFromList(final String userId, final String folderName) {
//        final UserListDto folder = getUserFolderOrThrow(userId, folderName);
//        return Mono.just(folder.getSavedRecipes());
//    }
//}
