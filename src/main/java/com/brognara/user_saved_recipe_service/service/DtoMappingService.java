package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dto.RecipeDto;
import com.brognara.user_saved_recipe_service.dto.UserListDto;
import com.brognara.user_saved_recipe_service.model.Recipe;
import com.brognara.user_saved_recipe_service.model.UserList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
                .listId(userList.getId() != null ? userList.getId().toString() : null)
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
                        .url(recipe.getUrl())
                        .scraperUsed(recipe.getScraperUsed())
                        .name(recipe.getName())
                        .description(recipe.getDescription())
                        .author(recipe.getAuthor())
                        .image(recipe.getImage())
                        .prepTimeMins(recipe.getPrepTimeMins())
                        .cookTimeMins(recipe.getCookTimeMins())
                        .totalTime(recipe.getTotalTime())
                        .servings(recipe.getServings())
                        .category(recipe.getCategory())
                        .cuisine(recipe.getCuisine())
                        .calories(recipe.getCalories())
                        .ratingAverage(recipe.getRatingAverage())
                        .ratingCount(recipe.getRatingCount())
                        .keywords(parseJsonList(recipe.getKeywords()))
                        .ingredientGroups(parseIngredientGroups(recipe.getIngredientGroups()))
                        .instructionGroups(parseInstructionGroups(recipe.getInstructionGroups()))
                        .notes(recipe.getNotes())
                        .build()
        );
    }

    public Mono<Recipe> toEntity(final RecipeDto dto) {
        return Mono.fromCallable(() -> {
            final Recipe recipe = new Recipe();
            recipe.setId(dto.getId());
            recipe.setUrl(dto.getUrl());
            recipe.setScraperUsed(dto.getScraperUsed());
            recipe.setName(dto.getName());
            recipe.setDescription(dto.getDescription());
            recipe.setAuthor(dto.getAuthor());
            recipe.setImage(dto.getImage());
            recipe.setPrepTimeMins(dto.getPrepTimeMins());
            recipe.setCookTimeMins(dto.getCookTimeMins());
            recipe.setTotalTime(dto.getTotalTime());
            recipe.setServings(dto.getServings());
            recipe.setCategory(dto.getCategory());
            recipe.setCuisine(dto.getCuisine());
            recipe.setCalories(dto.getCalories());
            recipe.setRatingAverage(dto.getRatingAverage());
            recipe.setRatingCount(dto.getRatingCount());
            recipe.setKeywords(writeJson(dto.getKeywords()));
            recipe.setIngredientGroups(writeJson(dto.getIngredientGroups()));
            recipe.setInstructionGroups(writeJson(dto.getInstructionGroups()));
            recipe.setNotes(dto.getNotes());
            recipe.setUpdatedAt(new Date());
            if (recipe.getCreatedAt() == null) {
                recipe.setCreatedAt(new Date());
            }
            return recipe;
        });
    }

    private List<String> parseJsonList(final String json) {
        if (json == null) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON list", e);
        }
    }

    private List<RecipeDto.IngredientGroup> parseIngredientGroups(final String json) {
        if (json == null) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<RecipeDto.IngredientGroup>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ingredient groups JSON", e);
        }
    }

    private List<RecipeDto.InstructionGroup> parseInstructionGroups(final String json) {
        if (json == null) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<RecipeDto.InstructionGroup>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse instruction groups JSON", e);
        }
    }

    private String writeJson(final Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize to JSON", e);
        }
    }
}
