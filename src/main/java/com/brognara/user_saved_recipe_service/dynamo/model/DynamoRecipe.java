package com.brognara.user_saved_recipe_service.dynamo.model;

import com.brognara.user_saved_recipe_service.model.Recipe;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.util.Date;
import java.util.UUID;

/**
 * DynamoDB model for recipes.
 *
 * Table key:
 *   PK: id (UUID string)
 *
 * GSI:
 *   urlIndex — PK: url  (used for deduplication lookups)
 *
 * Complex fields (ingredientGroups, instructionGroups, keywords) are stored as JSON strings.
 */
@DynamoDbBean
public class DynamoRecipe {

    private String id;
    private String url;
    private String scraperUsed;
    private String name;
    private String description;
    private String author;
    private String image;
    private String prepTimeMins;
    private String cookTimeMins;
    private String totalTime;
    private String servings;
    private String category;
    private String cuisine;
    private String calories;
    private Float ratingAverage;
    private Integer ratingCount;
    private String keywords;         // JSON array of strings
    private String ingredientGroups; // JSON
    private String instructionGroups; // JSON
    private String notes;
    private Long createdAt;
    private Long updatedAt;

    @DynamoDbPartitionKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @DynamoDbSecondaryPartitionKey(indexNames = {"urlIndex"})
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getScraperUsed() { return scraperUsed; }
    public void setScraperUsed(String scraperUsed) { this.scraperUsed = scraperUsed; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getPrepTimeMins() { return prepTimeMins; }
    public void setPrepTimeMins(String prepTimeMins) { this.prepTimeMins = prepTimeMins; }

    public String getCookTimeMins() { return cookTimeMins; }
    public void setCookTimeMins(String cookTimeMins) { this.cookTimeMins = cookTimeMins; }

    public String getTotalTime() { return totalTime; }
    public void setTotalTime(String totalTime) { this.totalTime = totalTime; }

    public String getServings() { return servings; }
    public void setServings(String servings) { this.servings = servings; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public String getCalories() { return calories; }
    public void setCalories(String calories) { this.calories = calories; }

    public Float getRatingAverage() { return ratingAverage; }
    public void setRatingAverage(Float ratingAverage) { this.ratingAverage = ratingAverage; }

    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public String getIngredientGroups() { return ingredientGroups; }
    public void setIngredientGroups(String ingredientGroups) { this.ingredientGroups = ingredientGroups; }

    public String getInstructionGroups() { return instructionGroups; }
    public void setInstructionGroups(String instructionGroups) { this.instructionGroups = instructionGroups; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }

    public Recipe toRecipe() {
        Recipe r = new Recipe();
        r.setId(UUID.fromString(id));
        r.setUrl(url);
        r.setScraperUsed(scraperUsed);
        r.setName(name);
        r.setDescription(description);
        r.setAuthor(author);
        r.setImage(image);
        r.setPrepTimeMins(prepTimeMins);
        r.setCookTimeMins(cookTimeMins);
        r.setTotalTime(totalTime);
        r.setServings(servings);
        r.setCategory(category);
        r.setCuisine(cuisine);
        r.setCalories(calories);
        r.setRatingAverage(ratingAverage);
        r.setRatingCount(ratingCount);
        r.setKeywords(keywords);
        r.setIngredientGroups(ingredientGroups);
        r.setInstructionGroups(instructionGroups);
        r.setNotes(notes);
        r.setCreatedAt(createdAt != null ? new Date(createdAt) : new Date());
        r.setUpdatedAt(updatedAt != null ? new Date(updatedAt) : new Date());
        return r;
    }

    public static DynamoRecipe from(Recipe recipe) {
        DynamoRecipe dr = new DynamoRecipe();
        dr.setId(recipe.getId() != null ? recipe.getId().toString() : UUID.randomUUID().toString());
        dr.setUrl(recipe.getUrl());
        dr.setScraperUsed(recipe.getScraperUsed());
        dr.setName(recipe.getName());
        dr.setDescription(recipe.getDescription());
        dr.setAuthor(recipe.getAuthor());
        dr.setImage(recipe.getImage());
        dr.setPrepTimeMins(recipe.getPrepTimeMins());
        dr.setCookTimeMins(recipe.getCookTimeMins());
        dr.setTotalTime(recipe.getTotalTime());
        dr.setServings(recipe.getServings());
        dr.setCategory(recipe.getCategory());
        dr.setCuisine(recipe.getCuisine());
        dr.setCalories(recipe.getCalories());
        dr.setRatingAverage(recipe.getRatingAverage());
        dr.setRatingCount(recipe.getRatingCount());
        dr.setKeywords(recipe.getKeywords());
        dr.setIngredientGroups(recipe.getIngredientGroups());
        dr.setInstructionGroups(recipe.getInstructionGroups());
        dr.setNotes(recipe.getNotes());
        dr.setCreatedAt(recipe.getCreatedAt() != null ? recipe.getCreatedAt().getTime() : System.currentTimeMillis());
        dr.setUpdatedAt(recipe.getUpdatedAt() != null ? recipe.getUpdatedAt().getTime() : System.currentTimeMillis());
        return dr;
    }
}
