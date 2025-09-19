package com.brognara.user_saved_recipe_service.repository;

import com.brognara.user_saved_recipe_service.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface UserListRecipeRepository extends JpaRepository<UserListRecipe, UserListRecipeId> {

//    boolean existsByUserListAndRecipe(UserList userList, Recipe recipe);
    // ID-based method avoids fetching extra entities & more performant
    boolean existsByUserList_IdAndRecipe_Id(UUID listId, UUID recipeId);

    @Query("SELECT ulr.recipe " +
            "FROM UserListRecipe ulr " +
            "JOIN ulr.userList ul " +
            "WHERE ul.user.id = :userId " +
            "AND ul.id = :listId")
    List<Recipe> findRecipesByUserIdAndListId(@Param("userId") UUID userId,
                                              @Param("listId") UUID listId);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserListRecipe ulr " +
            "WHERE ulr.userList.id = :listId " +
            "AND ulr.recipe.id = :recipeId")
    int deleteRecipeFromList(@Param("userId") UUID userId,
                             @Param("listId") UUID listId,
                             @Param("recipeId") UUID recipeId);
}
