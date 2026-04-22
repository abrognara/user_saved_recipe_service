package com.brognara.user_saved_recipe_service.resource;

import com.brognara.user_saved_recipe_service.dto.RecipeDto;
import com.brognara.user_saved_recipe_service.model.Recipe;
import com.brognara.user_saved_recipe_service.service.DtoMappingService;
import com.brognara.user_saved_recipe_service.service.RecipeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/api/v1")
public class RecipeResource {

    private final RecipeService recipeService;
    private final DtoMappingService dtoMappingService;

    @Autowired
    public RecipeResource(final RecipeService recipeService, final DtoMappingService dtoMappingService) {
        this.recipeService = recipeService;
        this.dtoMappingService = dtoMappingService;
    }

    @GetMapping(value = "/recipes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<RecipeDto>> getRecipeById(
            @RequestHeader("X-User-Id") final String userId,
            @RequestHeader("X-User-Roles") final String userRoles,
            @PathVariable final UUID id
    ) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] GET /api/v1/recipes/{} ; userId={} ; userRoles={}", requestId, id, userId, userRoles);

        return recipeService.getRecipeById(id)
                .flatMap(dtoMappingService::toRecipeDto)
                .map(ResponseEntity::ok);
    }
}
