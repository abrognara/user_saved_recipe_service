package com.brognara.user_saved_recipe_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnTransformer;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Table(
        name = "recipes",
        uniqueConstraints = @UniqueConstraint(columnNames = "url")
)
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(name = "scraper_used")
    private String scraperUsed;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String author;
    private String image;

    @Column(name = "prep_time_mins")
    private String prepTimeMins;

    @Column(name = "cook_time_mins")
    private String cookTimeMins;

    @Column(name = "total_time")
    private String totalTime;

    private String servings;
    private String category;
    private String cuisine;
    private String calories;

    @Column(name = "rating_average")
    private Float ratingAverage;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String keywords; // stored as JSON array

    @Column(name = "ingredient_groups", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String ingredientGroups; // stored as JSON

    @Column(name = "instruction_groups", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String instructionGroups; // stored as JSON

    private String notes;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt = new Date();
}
