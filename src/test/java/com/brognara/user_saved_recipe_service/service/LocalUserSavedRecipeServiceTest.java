package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.model.UserRecipeFolder;
import com.brognara.user_saved_recipe_service.model.UserSavedRecipe;
import com.brognara.user_saved_recipe_service.service.LocalUserSavedRecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.jupiter.api.Assertions.*;

public class LocalUserSavedRecipeServiceTest {
    private LocalUserSavedRecipeService service;
    private final String userId = "user-123";

    @BeforeEach
    void setUp() {
        service = new LocalUserSavedRecipeService();
    }

    @Test
    void testCreateAndGetFolder() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Breakfast").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
        String result = service.createNewFolderForUser(userId, folder).block();
        assertEquals("Breakfast", result);
        ConcurrentSkipListSet<UserRecipeFolder> folders = service.getFoldersForUser(userId).block();
        assertNotNull(folders);
        assertEquals(1, folders.size());
        assertEquals("Breakfast", folders.first().getFolderName());
    }

    @Test
    void testAddRecipeToFolder() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Lunch").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
        service.createNewFolderForUser(userId, folder).block();
        UserSavedRecipe recipe = new UserSavedRecipe("Pasta");
        String result = service.addRecipeToFolderForUser(userId, "Lunch", recipe).block();
        assertEquals("Success", result);
        ConcurrentSkipListSet<UserRecipeFolder> folders = service.getFoldersForUser(userId).block();
        assertEquals(1, folders.size());
        UserRecipeFolder lunchFolder = folders.first();
        assertEquals(1, lunchFolder.getSavedRecipes().size());
        assertEquals("Pasta", lunchFolder.getSavedRecipes().get(0).getRecipeName());
    }

    @Test
    void testDeleteRecipeFromFolder() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Dinner").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
        service.createNewFolderForUser(userId, folder).block();
        UserSavedRecipe recipe = new UserSavedRecipe("Steak");
        service.addRecipeToFolderForUser(userId, "Dinner", recipe).block();
        String result = service.deleteRecipeFromFolderForUser(userId, "Dinner", "Steak").block();
        assertEquals("Success", result);
        UserRecipeFolder dinnerFolder = service.getFoldersForUser(userId).block().first();
        assertEquals(0, dinnerFolder.getSavedRecipes().size());
    }

    @Test
    void testDeleteFolderForUser() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Snacks").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
        service.createNewFolderForUser(userId, folder).block();
        String result = service.deleteFolderForUser(userId, "Snacks").block();
        assertEquals("Success", result);
        ConcurrentSkipListSet<UserRecipeFolder> folders = service.getFoldersForUser(userId).block();
        assertNotNull(folders);
        assertEquals(0, folders.size());
    }

    @Test
    void testDuplicateFolderThrows() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Dessert").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
        service.createNewFolderForUser(userId, folder).block();
        assertThrows(RuntimeException.class, () -> service.createNewFolderForUser(userId, folder).block());
    }

    @Test
    void testDeleteNonexistentRecipeThrows() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Brunch").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
        service.createNewFolderForUser(userId, folder).block();
        assertThrows(RuntimeException.class, () -> service.deleteRecipeFromFolderForUser(userId, "Brunch", "Nonexistent").block());
    }
} 