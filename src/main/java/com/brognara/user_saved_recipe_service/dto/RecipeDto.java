package com.brognara.user_saved_recipe_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeDto {

    private UUID id;
    private String url;

    @JsonProperty("scraper_used")
    private String scraperUsed;

    @JsonProperty("title")
    private String name;

    private String description;
    private String author;
    private String image;

    @JsonProperty("prep_time")
    private String prepTimeMins;

    @JsonProperty("cook_time")
    private String cookTimeMins;

    @JsonProperty("total_time")
    private String totalTime;

    private String servings;
    private String category;
    private String cuisine;
    private String calories;

    @JsonProperty("rating_average")
    private Float ratingAverage;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    private List<String> keywords;

    @JsonProperty("ingredient_groups")
    private List<IngredientGroup> ingredientGroups = new ArrayList<>();

    @JsonProperty("instruction_groups")
    private List<InstructionGroup> instructionGroups = new ArrayList<>();

    private String notes;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IngredientGroup {
        @JsonProperty("group_name")
        private String groupName;
        private List<String> ingredients = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InstructionGroup {
        @JsonProperty("group_name")
        private String groupName;
        private List<String> steps = new ArrayList<>();
    }
}
