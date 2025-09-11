package com.brognara.user_saved_recipe_service.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeDto {
    // TODO json ignore
    private UUID id;
    private String name;
    private String description;
    private String url;
    private String author;
    private Double rating;
    private Integer numReviews;
    private Integer prepTimeMins;
    private Integer cookTimeMins;
    private Short servings;

    private List<String> instructions;
    private List<IngredientDto> ingredients;
    // TODO json optional
    private NutritionDto nutrition;
}
