package com.brognara.user_saved_recipe_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnTransformer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true)
    private String url;

    private String author;

    private Double rating; // numeric(2,1) in db

    @Column(name = "num_reviews")
    private Integer numReviews;

    @Column(name = "prep_time_mins")
    private Integer prepTimeMins;

    @Column(name = "cook_time_mins")
    private Integer cookTimeMins;

    private Short servings;

    private List<String> instructions = new ArrayList<>();

    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String ingredients; // store raw JSON

    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String nutrition;   // store raw JSON

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt = new Date();

}
