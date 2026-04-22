package com.brognara.user_saved_recipe_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NutritionDto {
    private String servingSize;
    private Integer calories;
    private Double totalFat;
    private Double saturatedFat;
    private Double cholesterol;
    private Double sodium;
    private Double carbs;
    private Double fiber;
    private Double sugar;
    private Double protein;
}
