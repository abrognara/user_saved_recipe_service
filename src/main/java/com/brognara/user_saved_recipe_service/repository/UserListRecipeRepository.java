package com.brognara.user_saved_recipe_service.repository;

import com.brognara.user_saved_recipe_service.model.Recipe;
import com.brognara.user_saved_recipe_service.model.UserList;
import com.brognara.user_saved_recipe_service.model.UserListRecipe;
import com.brognara.user_saved_recipe_service.model.UserListRecipeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserListRecipeRepository extends JpaRepository<UserListRecipe, UserListRecipeId> {
    List<UserListRecipe> findByUserList(UserList userList);
    void deleteByUserListAndRecipe(UserList userList, Recipe recipe);
}
