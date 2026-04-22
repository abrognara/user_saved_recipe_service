package com.brognara.user_saved_recipe_service.utils;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;

public class PgReactiveUtils {
    // since this jpa library isnt reactive, wrap pg operations in a mono with a bounded elastic scheduler
    public static <T> Mono<T> wrapMono(Callable<T> callable) {
        return Mono.fromCallable(callable)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
