package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.model.UserRecipeFolder;
import com.brognara.user_saved_recipe_service.model.UserSavedRecipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        String result = service.createNewListForUser(userId, folder).block();
        assertEquals("Breakfast", result);
        ConcurrentSkipListSet<UserRecipeFolder> folders = service.getListsForUser(userId).block();
        assertNotNull(folders);
        assertEquals(1, folders.size());
        assertEquals("Breakfast", folders.first().getFolderName());
    }

    @Test
    void testAddRecipeToFolder() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Lunch").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
        service.createNewListForUser(userId, folder).block();
        UserSavedRecipe recipe = new UserSavedRecipe("Pasta");
        String result = service.addRecipeToListForUser(userId, "Lunch", recipe).block();
        assertEquals("Success", result);
        ConcurrentSkipListSet<UserRecipeFolder> folders = service.getListsForUser(userId).block();
        assertEquals(1, folders.size());
        UserRecipeFolder lunchFolder = folders.first();
        assertEquals(1, lunchFolder.getSavedRecipes().size());
        assertEquals("Pasta", lunchFolder.getSavedRecipes().get(0).getRecipeName());
    }

    @Test
    void testDeleteRecipeFromFolder() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Dinner").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
        service.createNewListForUser(userId, folder).block();
        UserSavedRecipe recipe = new UserSavedRecipe("Steak");
        service.addRecipeToListForUser(userId, "Dinner", recipe).block();
        String result = service.deleteRecipeFromListForUser(userId, "Dinner", "Steak").block();
        assertEquals("Success", result);
        UserRecipeFolder dinnerFolder = service.getListsForUser(userId).block().first();
        assertEquals(0, dinnerFolder.getSavedRecipes().size());
    }

    @Test
    void testDeleteListForUser() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Snacks").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
        service.createNewListForUser(userId, folder).block();
        String result = service.deleteListForUser(userId, "Snacks").block();
        assertEquals("Success", result);
        ConcurrentSkipListSet<UserRecipeFolder> folders = service.getListsForUser(userId).block();
        assertNotNull(folders);
        assertEquals(0, folders.size());
    }

    @Test
    void testDuplicateFolderThrows() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Dessert").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
        service.createNewListForUser(userId, folder).block();
        assertThrows(RuntimeException.class, () -> service.createNewListForUser(userId, folder).block());
    }

    @Test
    void testDeleteNonexistentRecipeThrows() {
        UserRecipeFolder folder = UserRecipeFolder.builder().folderName("Brunch").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
        service.createNewListForUser(userId, folder).block();
        assertThrows(RuntimeException.class, () -> service.deleteRecipeFromListForUser(userId, "Brunch", "Nonexistent").block());
    }
} 