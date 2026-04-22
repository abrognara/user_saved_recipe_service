package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.model.User;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {
    Mono<User> getUserById(UUID id);
}
