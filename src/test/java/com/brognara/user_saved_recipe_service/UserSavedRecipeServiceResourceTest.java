package com.brognara.user_saved_recipe_service;

import com.brognara.user_saved_recipe_service.model.UserRecipeFolder;
import com.brognara.user_saved_recipe_service.model.UserSavedRecipe;
import com.brognara.user_saved_recipe_service.resource.UserSavedRecipeServiceResource;
import com.brognara.user_saved_recipe_service.service.UserSavedRecipeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@WebFluxTest(UserSavedRecipeServiceResource.class)
public class UserSavedRecipeServiceResourceTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserSavedRecipeService userSavedRecipeService;

    @Test
    void testCreateFolder() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Breakfast").createdByUser("user-123").creationTimestamp(1L).build();
        given(userSavedRecipeService.createNewFolderForUser(anyString(), any(UserRecipeFolder.class))).willReturn(Mono.just("Breakfast"));
        webTestClient.post().uri("/api/v1/lists/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(folder)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Breakfast");
    }

    @Test
    void testGetFolders() {
        ConcurrentSkipListSet<UserRecipeFolder> folders = new ConcurrentSkipListSet<>();
        folders.add(UserRecipeFolder.builder().folderName("Breakfast").createdByUser("user-123").creationTimestamp(1L).build());
        given(userSavedRecipeService.getFoldersForUser(anyString())).willReturn(Mono.just(folders));
        webTestClient.get().uri("/api/v1/lists/")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserRecipeFolder.class);
    }

    @Test
    void testAddRecipeToFolder() {
        UserSavedRecipe recipe = new UserSavedRecipe("Pasta");
        given(userSavedRecipeService.addRecipeToFolderForUser(anyString(), anyString(), any(UserSavedRecipe.class))).willReturn(Mono.just("Success"));
        webTestClient.post().uri("/api/v1/lists/Lunch/saved")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(recipe)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Success");
    }

    @Test
    void testDeleteRecipeFromFolder() {
        given(userSavedRecipeService.deleteRecipeFromFolderForUser(anyString(), anyString(), anyString())).willReturn(Mono.just("Success"));
        webTestClient.delete().uri("/api/v1/lists/Lunch/saved/Pasta")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Success");
    }

    @Test
    void testDeleteFolder() {
        given(userSavedRecipeService.deleteFolderForUser(anyString(), anyString())).willReturn(Mono.just("Success"));
        webTestClient.delete().uri("/api/v1/lists/Lunch")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Success");
    }

    @Test
    void testGetSavedRecipesFromFolder() {
        List<UserSavedRecipe> recipes = List.of(
            new UserSavedRecipe("Pasta Carbonara"),
            new UserSavedRecipe("Tiramisu")
        );
        given(userSavedRecipeService.getSavedRecipesFromFolder(anyString(), anyString())).willReturn(Mono.just(recipes));
        webTestClient.get().uri("/api/v1/lists/Italian Food/saved")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserSavedRecipe.class)
                .hasSize(2)
                .contains(recipes.toArray(new UserSavedRecipe[0]));
    }
} 