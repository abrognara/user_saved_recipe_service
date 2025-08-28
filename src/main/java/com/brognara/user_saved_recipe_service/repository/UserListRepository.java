package com.brognara.user_saved_recipe_service.repository;


import com.brognara.user_saved_recipe_service.model.UserList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserListRepository extends JpaRepository<UserList, UUID> {
    List<UserList> findByUserId(UUID userId);
    Optional<UserList> findByUserIdAndListName(UUID userId, String listName);
}
