package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dynamo.model.DynamoUserList;
import com.brognara.user_saved_recipe_service.dynamo.repository.DynamoRecipeRepository;
import com.brognara.user_saved_recipe_service.dynamo.repository.DynamoUserListRepository;
import com.brognara.user_saved_recipe_service.dto.UserListDto;
import com.brognara.user_saved_recipe_service.model.Recipe;
import com.brognara.user_saved_recipe_service.model.UserList;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.brognara.user_saved_recipe_service.utils.PgReactiveUtils.wrapMono;

@Service
@Profile("dynamo")
public class DynamoUserSavedRecipeService implements UserSavedRecipeService {

    private static final String FIREBASE = "firebase";

    private final UserService userService;
    private final DynamoUserListRepository userListRepository;
    private final DynamoRecipeRepository recipeRepository;
    private final DynamoRecipeDeduplicationFilter recipeDeduplicationFilter;

    public DynamoUserSavedRecipeService(
            UserService userService,
            DynamoUserListRepository userListRepository,
            DynamoRecipeRepository recipeRepository,
            DynamoRecipeDeduplicationFilter recipeDeduplicationFilter) {
        this.userService = userService;
        this.userListRepository = userListRepository;
        this.recipeRepository = recipeRepository;
        this.recipeDeduplicationFilter = recipeDeduplicationFilter;
    }

    @Override
    public Mono<UserList> createNewListForUser(final String userId, final UserListDto userListDto) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user -> wrapMono(() -> {
                    String internalUserId = user.getId().toString();
                    String listName = userListDto.getListName();

                    if (userListRepository.findByUserIdAndListName(internalUserId, listName).isPresent()) {
                        throw new IllegalArgumentException("List already exists for user: " + listName);
                    }

                    UserList userList = new UserList();
                    userList.setUser(user);
                    userList.setListName(listName);
                    userList.setIsPublic(false);

                    DynamoUserList saved = userListRepository.save(DynamoUserList.from(userList));
                    return saved.toUserList(user);
                }));
    }

    @Override
    public Mono<List<UserList>> getListsForUser(final String userId) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user -> wrapMono(() -> {
                    String internalUserId = user.getId().toString();
                    return userListRepository.findByUserId(internalUserId).stream()
                            .map(dul -> dul.toUserList(user))
                            .toList();
                }));
    }

    // listName is passed as the "listId" path variable for the dynamo profile
    @Override
    public Mono<String> addRecipeToListForUser(final String userId, final String listName, final Recipe recipe) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user -> wrapMono(() -> {
                    String internalUserId = user.getId().toString();
                    DynamoUserList userList = userListRepository
                            .findByUserIdAndListName(internalUserId, listName)
                            .orElseThrow(() -> new IllegalArgumentException("List not found: " + listName));

                    Recipe deduped = recipeDeduplicationFilter.saveRecipeIfNotExistsAndGet(recipe);
                    String recipeId = deduped.getId().toString();

                    Set<String> existing = userList.getRecipeIds();
                    if (existing != null && existing.contains(recipeId)) {
                        throw new IllegalStateException("Recipe already exists in this list");
                    }

                    userListRepository.addRecipeId(internalUserId, listName, recipeId);
                    return recipe.getName();
                }));
    }

    // listName is passed as the "listId" path variable for the dynamo profile
    @Override
    public Mono<String> deleteRecipeFromListForUser(final String userId, final String listName, final String recipeId) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user -> wrapMono(() -> {
                    String internalUserId = user.getId().toString();
                    DynamoUserList userList = userListRepository
                            .findByUserIdAndListName(internalUserId, listName)
                            .orElseThrow(() -> new IllegalArgumentException("List not found: " + listName));

                    Set<String> existing = userList.getRecipeIds();
                    if (existing == null || !existing.contains(recipeId)) {
                        throw new IllegalArgumentException("Recipe " + recipeId + " not found in list");
                    }

                    userListRepository.removeRecipeId(internalUserId, listName, recipeId);
                    return recipeId;
                }));
    }

    // listName is passed as the "listId" path variable for the dynamo profile
    @Override
    public Mono<String> deleteListForUser(final String userId, final String listName) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user -> wrapMono(() -> {
                    String internalUserId = user.getId().toString();
                    boolean deleted = userListRepository.deleteByUserIdAndListName(internalUserId, listName);
                    if (!deleted) {
                        throw new IllegalArgumentException("List not found: " + listName);
                    }
                    return listName;
                }));
    }

    // listName is passed as the "listId" path variable for the dynamo profile
    @Override
    public Mono<List<Recipe>> getSavedRecipesFromList(final String userId, final String listName) {
        return userService.getUserByAuthProviderAndId(FIREBASE, userId)
                .flatMap(user -> wrapMono(() -> {
                    String internalUserId = user.getId().toString();
                    DynamoUserList userList = userListRepository
                            .findByUserIdAndListName(internalUserId, listName)
                            .orElseThrow(() -> new IllegalArgumentException("List not found: " + listName));

                    Set<String> recipeIds = userList.getRecipeIds();
                    if (recipeIds == null || recipeIds.isEmpty()) {
                        return Collections.<Recipe>emptyList();
                    }

                    return recipeRepository.findAllById(List.copyOf(recipeIds)).stream()
                            .map(dr -> dr.toRecipe())
                            .toList();
                }));
    }
}
