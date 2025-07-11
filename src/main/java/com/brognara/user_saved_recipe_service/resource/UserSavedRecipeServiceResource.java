package com.brognara.user_saved_recipe_service.resource;

import com.brognara.user_saved_recipe_service.model.UserRecipeFolder;
import com.brognara.user_saved_recipe_service.model.UserSavedRecipe;
import com.brognara.user_saved_recipe_service.service.LocalUserSavedRecipeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

@Log4j2
@RestController
@RequestMapping("/api/recipes")
public class UserSavedRecipeServiceResource {

    private final LocalUserSavedRecipeService userSavedRecipeService;

    @Autowired
    public UserSavedRecipeServiceResource(LocalUserSavedRecipeService userSavedRecipeService) {
        this.userSavedRecipeService = userSavedRecipeService;
    }

    @PostMapping(value = "/folders", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createFolder(@RequestBody UserRecipeFolder folder) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] POST /folders - folder: {}", requestId, folder);
        return userSavedRecipeService.createNewFolderForUser("user-123", folder)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/folders", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ConcurrentSkipListSet<UserRecipeFolder>>> getFolders() {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] GET /folders", requestId);
        return userSavedRecipeService.getFoldersForUser("user-123")
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/folders/{folderName}/recipes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> addRecipeToFolder(
            @PathVariable String folderName,
            @RequestBody UserSavedRecipe recipe) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] POST /folders/{}/recipes - recipe: {}", requestId, folderName, recipe);
        return userSavedRecipeService.addRecipeToFolderForUser("user-123", folderName, recipe)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "/folders/{folderName}/recipes/{recipeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> deleteRecipeFromFolder(
            @PathVariable String folderName,
            @PathVariable String recipeName) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] DELETE /folders/{}/recipes/{}", requestId, folderName, recipeName);
        return userSavedRecipeService.deleteRecipeFromFolderForUser("user-123", folderName, recipeName)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "/folders/{folderName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> deleteFolder(
            @PathVariable String folderName) {
        final String requestId = UUID.randomUUID().toString();
        log.info("[{}] DELETE /folders/{}", requestId, folderName);
        return userSavedRecipeService.deleteFolderForUser("user-123", folderName)
                .map(ResponseEntity::ok);
    }
}
