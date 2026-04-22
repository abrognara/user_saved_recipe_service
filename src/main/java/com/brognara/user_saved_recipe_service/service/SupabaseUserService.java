package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.exception.UserNotFoundException;
import com.brognara.user_saved_recipe_service.model.User;
import com.brognara.user_saved_recipe_service.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
@Profile("!dynamo")
public class SupabaseUserService implements UserService {

    private final UserRepository userRepository;

    public SupabaseUserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<User> getUserById(final UUID id) {
        return Mono.fromCallable(() ->
                userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User not found: " + id))
        ).subscribeOn(Schedulers.boundedElastic());
    }
}
