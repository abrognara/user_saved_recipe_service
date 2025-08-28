package com.brognara.user_saved_recipe_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID recipeId;

    @Column(nullable = false)
    private String recipeName;

    private String sourceUrl;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

}
