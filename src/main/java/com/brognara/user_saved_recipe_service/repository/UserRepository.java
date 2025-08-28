package com.brognara.user_saved_recipe_service.repository;

import com.brognara.user_saved_recipe_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByAuthProviderAndAuthProviderId(String authProvider, String authProviderId);
}
