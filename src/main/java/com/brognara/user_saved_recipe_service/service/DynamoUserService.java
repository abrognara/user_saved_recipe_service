package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dynamo.repository.DynamoUserRepository;
import com.brognara.user_saved_recipe_service.exception.UserNotFoundException;
import com.brognara.user_saved_recipe_service.model.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Profile("dynamo")
public class DynamoUserService implements UserService {

    private final DynamoUserRepository userRepository;

    public DynamoUserService(DynamoUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<User> getUserByAuthProviderAndId(String provider, String providerId) {
        return Mono.fromCallable(() ->
                userRepository.findByAuthProviderAndId(provider, providerId)
                        .map(du -> du.toUser())
                        .orElseThrow(() -> new UserNotFoundException(
                                "User not found for provider=" + provider + " and id=" + providerId))
        ).subscribeOn(Schedulers.boundedElastic());
    }
}
