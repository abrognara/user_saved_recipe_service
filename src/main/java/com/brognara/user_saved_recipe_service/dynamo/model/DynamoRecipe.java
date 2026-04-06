package com.brognara.user_saved_recipe_service.dynamo.model;

import com.brognara.user_saved_recipe_service.model.Recipe;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * DynamoDB model for recipes.
 *
 * Table key:
 *   PK: id (UUID string)
 *
 * GSI:
 *   urlIndex — PK: url  (used for deduplication lookups)
 */
@DynamoDbBean
public class DynamoRecipe {

    private String id;
    private String name;
    private String description;
    private String url;
    private String author;
    private Double rating;
    private Integer numReviews;
    private Integer prepTimeMins;
    private Integer cookTimeMins;
    private Integer servings;
    private List<String> instructions;
    private String ingredients;
    private String nutrition;
    private Long createdAt;
    private Long updatedAt;

    @DynamoDbPartitionKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @DynamoDbSecondaryPartitionKey(indexNames = {"urlIndex"})
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getNumReviews() { return numReviews; }
    public void setNumReviews(Integer numReviews) { this.numReviews = numReviews; }

    public Integer getPrepTimeMins() { return prepTimeMins; }
    public void setPrepTimeMins(Integer prepTimeMins) { this.prepTimeMins = prepTimeMins; }

    public Integer getCookTimeMins() { return cookTimeMins; }
    public void setCookTimeMins(Integer cookTimeMins) { this.cookTimeMins = cookTimeMins; }

    public Integer getServings() { return servings; }
    public void setServings(Integer servings) { this.servings = servings; }

    public List<String> getInstructions() { return instructions; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getNutrition() { return nutrition; }
    public void setNutrition(String nutrition) { this.nutrition = nutrition; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }

    public Recipe toRecipe() {
        Recipe r = new Recipe();
        r.setId(UUID.fromString(id));
        r.setName(name);
        r.setDescription(description);
        r.setUrl(url);
        r.setAuthor(author);
        r.setRating(rating);
        r.setNumReviews(numReviews);
        r.setPrepTimeMins(prepTimeMins);
        r.setCookTimeMins(cookTimeMins);
        if (servings != null) r.setServings(servings.shortValue());
        r.setInstructions(instructions != null ? instructions : new ArrayList<>());
        r.setIngredients(ingredients);
        r.setNutrition(nutrition);
        r.setCreatedAt(createdAt != null ? new Date(createdAt) : new Date());
        r.setUpdatedAt(updatedAt != null ? new Date(updatedAt) : new Date());
        return r;
    }

    public static DynamoRecipe from(Recipe recipe) {
        DynamoRecipe dr = new DynamoRecipe();
        dr.setId(recipe.getId() != null ? recipe.getId().toString() : UUID.randomUUID().toString());
        dr.setName(recipe.getName());
        dr.setDescription(recipe.getDescription());
        dr.setUrl(recipe.getUrl());
        dr.setAuthor(recipe.getAuthor());
        dr.setRating(recipe.getRating());
        dr.setNumReviews(recipe.getNumReviews());
        dr.setPrepTimeMins(recipe.getPrepTimeMins());
        dr.setCookTimeMins(recipe.getCookTimeMins());
        if (recipe.getServings() != null) dr.setServings(recipe.getServings().intValue());
        dr.setInstructions(recipe.getInstructions());
        dr.setIngredients(recipe.getIngredients());
        dr.setNutrition(recipe.getNutrition());
        dr.setCreatedAt(recipe.getCreatedAt() != null ? recipe.getCreatedAt().getTime() : System.currentTimeMillis());
        dr.setUpdatedAt(recipe.getUpdatedAt() != null ? recipe.getUpdatedAt().getTime() : System.currentTimeMillis());
        return dr;
    }
}
