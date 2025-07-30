package com.brognara.user_saved_recipe_service.resource;

import com.brognara.user_saved_recipe_service.model.UserRecipeFolder;
import com.brognara.user_saved_recipe_service.model.UserSavedRecipe;
import com.brognara.user_saved_recipe_service.service.UserSavedRecipeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Log4j2
@RestController
@RequestMapping("/api/v1/lists")
public class UserSavedRecipeServiceResource {

    private final UserSavedRecipeService userSavedRecipeService;

    @Autowired
    public UserSavedRecipeServiceResource(UserSavedRecipeService userSavedRecipeService) {
        this.userSavedRecipeService = userSavedRecipeService;
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createFolder(@RequestBody final UserRecipeFolder folder) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] POST /api/v1/lists - folder: {}", requestId, folder);
        return userSavedRecipeService.createNewFolderForUser("user-123", folder)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ConcurrentSkipListSet<UserRecipeFolder>>> getFolders() {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] GET /api/v1/lists", requestId);
        return userSavedRecipeService.getFoldersForUser("user-123")
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/{listName}/saved", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> addRecipeToFolder(
            @PathVariable String listName,
            @RequestBody UserSavedRecipe recipe) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] POST /api/v1/lists/{}/saved - recipe: {}", requestId, listName, recipe);
        return userSavedRecipeService.addRecipeToFolderForUser("user-123", listName, recipe)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "/{listName}/saved/{recipeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> deleteRecipeFromFolder(
            @PathVariable String listName,
            @PathVariable String recipeName) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] DELETE /api/v1/lists/{}/saved/{}", requestId, listName, recipeName);
        return userSavedRecipeService.deleteRecipeFromFolderForUser("user-123", listName, recipeName)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "/{listName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> deleteFolder(
            @PathVariable String listName) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] DELETE /api/v1/lists/{}", requestId, listName);
        return userSavedRecipeService.deleteFolderForUser("user-123", listName)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/{listName}/saved", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<UserSavedRecipe>>> getSavedRecipesFromFolder(
            @PathVariable String listName) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] GET /api/v1/lists/{}/saved", requestId, listName);
        return userSavedRecipeService.getSavedRecipesFromFolder("user-123", listName)
                .map(ResponseEntity::ok);
    }
}
