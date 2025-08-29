package com.brognara.user_saved_recipe_service.repository;


import com.brognara.user_saved_recipe_service.model.UserList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserListRepository extends JpaRepository<UserList, UUID> {

    List<UserList> findByUserId(UUID userId);

    Optional<UserList> findByUserIdAndListName(UUID userId, String listName);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserList ul WHERE ul.user.id = :userId AND ul.listName = :listName")
    int deleteByUserIdAndListName(@Param("userId") UUID userId,
                                  @Param("listName") String listName);
}
