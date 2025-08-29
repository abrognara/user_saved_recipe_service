package com.brognara.user_saved_recipe_service.resource;

import com.brognara.user_saved_recipe_service.dto.UserListDto;
import com.brognara.user_saved_recipe_service.dto.RecipeDto;
import com.brognara.user_saved_recipe_service.service.DtoMappingService;
import com.brognara.user_saved_recipe_service.service.UserSavedRecipeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/api/v1")
public class UserSavedRecipeServiceResource {

    private final UserSavedRecipeService userSavedRecipeService;
    private final DtoMappingService dtoMappingService;

    @Autowired
    public UserSavedRecipeServiceResource(
            UserSavedRecipeService userSavedRecipeService,
            DtoMappingService dtoMappingService
    ) {
        this.userSavedRecipeService = userSavedRecipeService;
        this.dtoMappingService = dtoMappingService;
    }

    @PostMapping(value = "/lists", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createList(
            @RequestBody final UserListDto userListDto,
            @RequestHeader("X-User-Id") final String userId,
            @RequestHeader("X-User-Roles") final String userRoles
    ) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] POST /api/v1/lists ; list={} ; userId={} ; userRoles={}",
                requestId, userListDto, userId, userRoles);
        // TODO do we need to return the list name?
        return userSavedRecipeService.createNewListForUser(userId, userListDto)
                .map(userList -> ResponseEntity.ok(userList.getListName()));
    }

    @GetMapping(value = "/lists", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<UserListDto>>> getLists(
            @RequestHeader("X-User-Id") final String userId,
            @RequestHeader("X-User-Roles") final String userRoles
    ) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] GET /api/v1/lists ; userId={} ; userRoles={}", requestId, userId, userRoles);
        return userSavedRecipeService.getListsForUser(userId)
                .flatMap(dtoMappingService::toListOfUserListDto)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/lists/{listName}/saved", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> addRecipeToList(
            @PathVariable String listName,
            @RequestBody RecipeDto recipe,
            @RequestHeader("X-User-Id") final String userId,
            @RequestHeader("X-User-Roles") final String userRoles
    ) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] POST /api/v1/lists/{}/saved ; recipe: {} ; userId={} ; userRoles={}",
                requestId, listName, recipe, userId, userRoles);
        return userSavedRecipeService.addRecipeToListForUser(userId, listName, recipe)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "/lists/{listName}/saved/{recipeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> deleteRecipeFromList(
            @PathVariable String listName,
            @PathVariable String recipeName,
            @RequestHeader("X-User-Id") final String userId,
            @RequestHeader("X-User-Roles") final String userRoles
    ) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] DELETE /api/v1/lists/{}/saved/{} ; userId={} ; userRoles={}",
                requestId, listName, recipeName, userId, userRoles);
        return userSavedRecipeService.deleteRecipeFromListForUser(userId, listName, recipeName)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "/lists/{listName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> deleteList(
            @PathVariable final String listName,
            @RequestHeader("X-User-Id") final String userId,
            @RequestHeader("X-User-Roles") final String userRoles
    ) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] DELETE /api/v1/lists/{} ; userId={} ; userRoles={}",
                requestId, listName, userId, userRoles);
        return userSavedRecipeService.deleteListForUser(userId, listName)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/lists/{listName}/saved", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<RecipeDto>>> getSavedRecipesFromList(
            @PathVariable String listName,
            @RequestHeader("X-User-Id") final String userId,
            @RequestHeader("X-User-Roles") final String userRoles
    ) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] GET /api/v1/lists/{}/saved ; userId={} ; userRoles={}",
                requestId, listName, userId, userRoles);
        return userSavedRecipeService.getSavedRecipesFromList(userId, listName)
                .flatMap(dtoMappingService::toListOfRecipeDto)
                .map(ResponseEntity::ok);
    }
}
