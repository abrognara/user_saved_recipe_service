package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dto.IngredientDto;
import com.brognara.user_saved_recipe_service.dto.NutritionDto;
import com.brognara.user_saved_recipe_service.dto.RecipeDto;
import com.brognara.user_saved_recipe_service.dto.UserListDto;
import com.brognara.user_saved_recipe_service.model.Recipe;
import com.brognara.user_saved_recipe_service.model.UserList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DtoMappingService {

    private final ObjectMapper objectMapper;

    @Autowired
    public DtoMappingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
        return Flux.fromIterable(recipes)
                .flatMap(this::toRecipeDto)
                .collectList();
    }

    public Mono<RecipeDto> toRecipeDto(final Recipe recipe) {
        return Mono.fromCallable(() ->
                RecipeDto.builder()
                        .id(recipe.getId())
                        .name(recipe.getName())
                        .description(recipe.getDescription())
                        .url(recipe.getUrl())
                        .author(recipe.getAuthor())
                        .rating(recipe.getRating())
                        .numReviews(recipe.getNumReviews())
                        .prepTimeMins(recipe.getPrepTimeMins())
                        .cookTimeMins(recipe.getCookTimeMins())
                        .servings(recipe.getServings())
                        .instructions(recipe.getInstructions())
                        .ingredients(parseIngredients(recipe.getIngredients()))
                        .nutrition(parseNutrition(recipe.getNutrition()))
                        .build()
        );
    }

    public Mono<Recipe> toEntity(final RecipeDto dto) {
        return Mono.fromCallable(() -> {
            final Recipe recipe = new Recipe();
            recipe.setId(dto.getId()); // allow updates
            recipe.setName(dto.getName());
            recipe.setDescription(dto.getDescription());
            recipe.setUrl(dto.getUrl());
            recipe.setAuthor(dto.getAuthor());
            recipe.setRating(dto.getRating());
            recipe.setNumReviews(dto.getNumReviews());
            recipe.setPrepTimeMins(dto.getPrepTimeMins());
            recipe.setCookTimeMins(dto.getCookTimeMins());
            recipe.setServings(dto.getServings());
            recipe.setInstructions(dto.getInstructions());

            recipe.setIngredients(writeIngredients(dto.getIngredients()));
            recipe.setNutrition(writeNutrition(dto.getNutrition()));

            recipe.setUpdatedAt(new Date());
            if (recipe.getCreatedAt() == null) {
                recipe.setCreatedAt(new Date());
            }
            return recipe;
        });
    }

    private List<IngredientDto> parseIngredients(final String json) {
        if (json == null) return Collections.emptyList();
        try {
            return Arrays.asList(objectMapper.readValue(json, IngredientDto[].class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ingredients JSON", e);
        }
    }

    private NutritionDto parseNutrition(final String json) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, NutritionDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse nutrition JSON", e);
        }
    }

    private String writeIngredients(final List<IngredientDto> ingredients) {
        if (ingredients == null) return null;
        try {
            return objectMapper.writeValueAsString(ingredients);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write ingredients JSON", e);
        }
    }

    private String writeNutrition(final NutritionDto nutrition) {
        if (nutrition == null) return null;
        try {
            return objectMapper.writeValueAsString(nutrition);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write nutrition JSON", e);
        }
    }
}
