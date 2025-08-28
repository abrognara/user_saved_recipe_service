package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.model.User;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> getUserByAuthProviderAndId(final String authProvider, final String userId);
}
