package com.brognara.user_saved_recipe_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeDto {
    private String recipeName;
    private String sourceUrl;
    private long creationTimestamp;
}
