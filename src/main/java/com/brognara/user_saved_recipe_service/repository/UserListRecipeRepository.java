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

    boolean existsByUserListAndRecipe(UserList userList, Recipe recipe);

    @Query("SELECT ulr.recipe " +
            "FROM UserListRecipe ulr " +
            "JOIN ulr.userList ul " +
            "WHERE ul.user.id = :userId " +
            "AND ul.listName = :listName")
    List<Recipe> findRecipesByUserIdAndListName(@Param("userId") UUID userId,
                                                @Param("listName") String listName);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserListRecipe ulr " +
            "WHERE ulr.userList.user.id = :userId " +
            "AND ulr.userList.listName = :listName " +
            "AND ulr.recipe.recipeName = :recipeName")
    int deleteRecipeFromList(@Param("userId") UUID userId,
                             @Param("listName") String listName,
                             @Param("recipeName") String recipeName);
}
