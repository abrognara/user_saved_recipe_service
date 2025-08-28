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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;  // internal app ID

    @Column(nullable = false)
    private String authProvider;

    @Column(nullable = false)
    private String authProviderId;  // e.g. Firebase uid

    @Column(unique = true)
    private String email;

    private String displayName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLogin;

}
