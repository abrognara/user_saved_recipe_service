package com.brognara.user_saved_recipe_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    private Integer numReviews;
    private Integer prepTimeMins;
    private Integer cookTimeMins;
    private Short servings;

    @ElementCollection
    @CollectionTable(name = "recipe_instructions", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "step", columnDefinition = "TEXT")
    private List<String> instructions = new ArrayList<>();

    @Column(columnDefinition = "jsonb")
    private String ingredients; // store raw JSON

    @Column(columnDefinition = "jsonb")
    private String nutrition;   // store raw JSON

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

}
