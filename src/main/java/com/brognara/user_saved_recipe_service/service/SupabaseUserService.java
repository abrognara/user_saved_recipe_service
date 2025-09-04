package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.exception.UserNotFoundException;
import com.brognara.user_saved_recipe_service.model.User;
import com.brognara.user_saved_recipe_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class SupabaseUserService implements UserService {

    private final UserRepository userRepository;

    public SupabaseUserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Look up a user by auth provider + external id (e.g. Firebase UID).
     * Throws UserNotFoundException if not found.
     */
    @Transactional(readOnly = true)
    public Mono<User> getUserByAuthProviderAndId(final String provider, final String providerId) {
        return Mono.fromCallable(() ->
                userRepository.findByAuthProviderAndAuthProviderId(provider, providerId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found for provider=" + provider + " and id=" + providerId)
                )
        ).subscribeOn(Schedulers.boundedElastic());
    }

}
