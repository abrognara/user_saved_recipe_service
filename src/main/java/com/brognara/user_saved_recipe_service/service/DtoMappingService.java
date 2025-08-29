package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dto.RecipeDto;
import com.brognara.user_saved_recipe_service.dto.UserListDto;
import com.brognara.user_saved_recipe_service.model.Recipe;
import com.brognara.user_saved_recipe_service.model.UserList;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DtoMappingService {

    public Mono<List<UserListDto>> toListOfUserListDto(final List<UserList> userLists) {
        return Mono.just(
                userLists.stream()
                        .map(this::toUserListDto)
                        .collect(Collectors.toList())
        );
    }

    private UserListDto toUserListDto(final UserList userList) {
        return UserListDto.builder()
                .listName(userList.getListName())
                .createdByUser(userList.getUser().getDisplayName())
                .creationTimestamp(userList.getCreatedAt().toInstant().toEpochMilli())
                .isPublic(userList.getIsPublic())
                .build();
    }

    public Mono<List<RecipeDto>> toListOfRecipeDto(final List<Recipe> recipes) {
        return Mono.just(
                recipes.stream()
                        .map(this::toRecipeDto)
                        .collect(Collectors.toList())
        );
    }

    private RecipeDto toRecipeDto(final Recipe recipe) {
        return RecipeDto.builder()
                .recipeName(recipe.getRecipeName())
                .sourceUrl(recipe.getSourceUrl())
                .creationTimestamp(recipe.getCreatedAt().toInstant().toEpochMilli())
                .build();
    }
}
