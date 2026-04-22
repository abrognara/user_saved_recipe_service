package com.brognara.user_saved_recipe_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngredientDto {
    private String name;
    private String amount;
}
