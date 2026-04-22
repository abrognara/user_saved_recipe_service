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

    private final DynamoUserListRepository userListRepository;
    private final DynamoRecipeRepository recipeRepository;
    private final DynamoRecipeDeduplicationFilter recipeDeduplicationFilter;

    public DynamoUserSavedRecipeService(
            DynamoUserListRepository userListRepository,
            DynamoRecipeRepository recipeRepository,
            DynamoRecipeDeduplicationFilter recipeDeduplicationFilter) {
        this.userListRepository = userListRepository;
        this.recipeRepository = recipeRepository;
        this.recipeDeduplicationFilter = recipeDeduplicationFilter;
    }

    @Override
    public Mono<UserList> createNewListForUser(final String userId, final UserListDto userListDto) {
        return wrapMono(() -> {
            String listName = userListDto.getListName();
            boolean nameExists = userListRepository.findByUserId(userId).stream()
                    .anyMatch(l -> listName.equalsIgnoreCase(l.getListName()));
            if (nameExists) {
                throw new IllegalArgumentException("List already exists for user: " + listName);
            }
            DynamoUserList saved = userListRepository.save(DynamoUserList.from(userId, listName, false));
            return saved.toUserList();
        });
    }

    @Override
    public Mono<List<UserList>> getListsForUser(final String userId) {
        return wrapMono(() ->
                userListRepository.findByUserId(userId).stream()
                        .map(DynamoUserList::toUserList)
                        .toList()
        );
    }

    @Override
    public Mono<String> addRecipeToListForUser(final String userId, final String listId, final Recipe recipe) {
        return wrapMono(() -> {
            DynamoUserList userList = userListRepository
                    .findByUserIdAndListId(userId, listId)
                    .orElseThrow(() -> new IllegalArgumentException("List not found: " + listId));

            Recipe deduped = recipeDeduplicationFilter.saveRecipeIfNotExistsAndGet(recipe);
            String recipeId = deduped.getId().toString();

            Set<String> existing = userList.getRecipeIds();
            if (existing != null && existing.contains(recipeId)) {
                throw new IllegalStateException("Recipe already exists in this list");
            }

            userListRepository.addRecipeId(userId, listId, recipeId);
            return recipe.getName();
        });
    }

    @Override
    public Mono<String> deleteRecipeFromListForUser(final String userId, final String listId, final String recipeId) {
        return wrapMono(() -> {
            DynamoUserList userList = userListRepository
                    .findByUserIdAndListId(userId, listId)
                    .orElseThrow(() -> new IllegalArgumentException("List not found: " + listId));

            Set<String> existing = userList.getRecipeIds();
            if (existing == null || !existing.contains(recipeId)) {
                throw new IllegalArgumentException("Recipe " + recipeId + " not found in list");
            }

            userListRepository.removeRecipeId(userId, listId, recipeId);
            return recipeId;
        });
    }

    @Override
    public Mono<String> deleteListForUser(final String userId, final String listId) {
        return wrapMono(() -> {
            boolean deleted = userListRepository.deleteByUserIdAndListId(userId, listId);
            if (!deleted) {
                throw new IllegalArgumentException("List not found: " + listId);
            }
            return listId;
        });
    }

    @Override
    public Mono<List<Recipe>> getSavedRecipesFromList(final String userId, final String listId) {
        return wrapMono(() -> {
            DynamoUserList userList = userListRepository
                    .findByUserIdAndListId(userId, listId)
                    .orElseThrow(() -> new IllegalArgumentException("List not found: " + listId));

            Set<String> recipeIds = userList.getRecipeIds();
            if (recipeIds == null || recipeIds.isEmpty()) {
                return Collections.<Recipe>emptyList();
            }

            return recipeRepository.findAllById(List.copyOf(recipeIds)).stream()
                    .map(dr -> dr.toRecipe())
                    .toList();
        });
    }
}
