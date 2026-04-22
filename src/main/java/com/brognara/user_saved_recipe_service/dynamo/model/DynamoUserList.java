package com.brognara.user_saved_recipe_service.dynamo.model;

import com.brognara.user_saved_recipe_service.model.User;
import com.brognara.user_saved_recipe_service.model.UserList;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * DynamoDB model for user lists.
 *
 * Table key:
 *   PK: userId  (internal UUID string)
 *   SK: listId  (UUID — unique identity for the list, enables sharing)
 *
 * GSI:
 *   listIdIndex — PK: listId  (allows resolving any list by UUID for sharing lookups)
 *
 * listName is a regular attribute; uniqueness per user is enforced in the service layer.
 * recipeIds is a StringSet of recipe UUIDs embedded directly on this item.
 * Note: DynamoDB does not allow empty sets — recipeIds is absent until the first recipe is added.
 */
@DynamoDbBean
public class DynamoUserList {

    private String userId;
    private String listId;
    private String listName;
    private Boolean isPublic;
    private Long createdAt;
    private Set<String> recipeIds;

    @DynamoDbPartitionKey
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = {"listIdIndex"})
    public String getListId() { return listId; }
    public void setListId(String listId) { this.listId = listId; }

    public String getListName() { return listName; }
    public void setListName(String listName) { this.listName = listName; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Set<String> getRecipeIds() { return recipeIds; }
    public void setRecipeIds(Set<String> recipeIds) { this.recipeIds = recipeIds; }

    public UserList toUserList() {
        User stubUser = new User();
        stubUser.setId(UUID.fromString(userId));

        UserList ul = new UserList();
        ul.setId(UUID.fromString(listId));
        ul.setUser(stubUser);
        ul.setListName(listName);
        ul.setIsPublic(isPublic != null ? isPublic : false);
        ul.setCreatedAt(createdAt != null ? new Date(createdAt) : new Date());
        return ul;
    }

    public static DynamoUserList from(String userId, String listName, boolean isPublic) {
        DynamoUserList dul = new DynamoUserList();
        dul.setUserId(userId);
        dul.setListId(UUID.randomUUID().toString());
        dul.setListName(listName);
        dul.setIsPublic(isPublic);
        dul.setCreatedAt(System.currentTimeMillis());
        // recipeIds intentionally omitted — DynamoDB does not allow empty sets
        return dul;
    }
}
