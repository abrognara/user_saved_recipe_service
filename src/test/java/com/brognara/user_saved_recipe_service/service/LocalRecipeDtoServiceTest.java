package com.brognara.user_saved_recipe_service.service;

import com.brognara.user_saved_recipe_service.dto.UserListDto;
import com.brognara.user_saved_recipe_service.dto.RecipeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.jupiter.api.Assertions.*;

//public class LocalRecipeDtoServiceTest {
//    private LocalUserSavedRecipeService service;
//    private final String userId = "user-123";
//
//    @BeforeEach
//    void setUp() {
//        service = new LocalUserSavedRecipeService();
//    }
//
//    @Test
//    void testCreateAndGetFolder() {
//        UserListDto folder = UserListDto.builder().listName("Breakfast").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
//        String result = service.createNewListForUser(userId, folder).block();
//        assertEquals("Breakfast", result);
//        ConcurrentSkipListSet<UserListDto> folders = service.getListsForUser(userId).block();
//        assertNotNull(folders);
//        assertEquals(1, folders.size());
//        assertEquals("Breakfast", folders.first().getListName());
//    }
//
//    @Test
//    void testAddRecipeToFolder() {
//        UserListDto folder = UserListDto.builder().listName("Lunch").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
//        service.createNewListForUser(userId, folder).block();
//        RecipeDto recipe = new RecipeDto("Pasta");
//        String result = service.addRecipeToListForUser(userId, "Lunch", recipe).block();
//        assertEquals("Success", result);
//        ConcurrentSkipListSet<UserListDto> folders = service.getListsForUser(userId).block();
//        assertEquals(1, folders.size());
//        UserListDto lunchFolder = folders.first();
//        assertEquals(1, lunchFolder.getSavedRecipes().size());
//        assertEquals("Pasta", lunchFolder.getSavedRecipes().get(0).getRecipeName());
//    }
//
//    @Test
//    void testDeleteRecipeFromFolder() {
//        UserListDto folder = UserListDto.builder().listName("Dinner").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
//        service.createNewListForUser(userId, folder).block();
//        RecipeDto recipe = new RecipeDto("Steak");
//        service.addRecipeToListForUser(userId, "Dinner", recipe).block();
//        String result = service.deleteRecipeFromListForUser(userId, "Dinner", "Steak").block();
//        assertEquals("Success", result);
//        UserListDto dinnerFolder = service.getListsForUser(userId).block().first();
//        assertEquals(0, dinnerFolder.getSavedRecipes().size());
//    }
//
//    @Test
//    void testDeleteListForUser() {
//        UserListDto folder = UserListDto.builder().listName("Snacks").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
//        service.createNewListForUser(userId, folder).block();
//        String result = service.deleteListForUser(userId, "Snacks").block();
//        assertEquals("Success", result);
//        ConcurrentSkipListSet<UserListDto> folders = service.getListsForUser(userId).block();
//        assertNotNull(folders);
//        assertEquals(0, folders.size());
//    }
//
//    @Test
//    void testDuplicateFolderThrows() {
//        UserListDto folder = UserListDto.builder().listName("Dessert").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
//        service.createNewListForUser(userId, folder).block();
//        assertThrows(RuntimeException.class, () -> service.createNewListForUser(userId, folder).block());
//    }
//
//    @Test
//    void testDeleteNonexistentRecipeThrows() {
//        UserListDto folder = UserListDto.builder().listName("Brunch").createdByUser(userId).creationTimestamp(System.currentTimeMillis()).build();
//        service.createNewListForUser(userId, folder).block();
//        assertThrows(RuntimeException.class, () -> service.deleteRecipeFromListForUser(userId, "Brunch", "Nonexistent").block());
//    }
//}