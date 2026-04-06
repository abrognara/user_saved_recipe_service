package com.brognara.user_saved_recipe_service.dynamo.model;

import com.brognara.user_saved_recipe_service.model.User;
import com.brognara.user_saved_recipe_service.model.UserList;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Date;
import java.util.Set;

/**
 * DynamoDB model for user lists.
 *
 * Table key:
 *   PK: userId (internal UUID string)
 *   SK: listName (the natural unique key per user)
 *
 * recipeIds is a StringSet of recipe UUIDs embedded directly on this item,
 * eliminating the need for a separate join table.
 * Note: DynamoDB does not allow empty sets — recipeIds is absent until the first recipe is added.
 */
@DynamoDbBean
public class DynamoUserList {

    private String userId;
    private String listName;
    private Boolean isPublic;
    private Long createdAt;
    private Set<String> recipeIds;

    @DynamoDbPartitionKey
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @DynamoDbSortKey
    public String getListName() { return listName; }
    public void setListName(String listName) { this.listName = listName; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Set<String> getRecipeIds() { return recipeIds; }
    public void setRecipeIds(Set<String> recipeIds) { this.recipeIds = recipeIds; }

    public UserList toUserList(User user) {
        UserList ul = new UserList();
        ul.setUser(user);
        ul.setListName(listName);
        ul.setIsPublic(isPublic != null ? isPublic : false);
        ul.setCreatedAt(createdAt != null ? new Date(createdAt) : new Date());
        return ul;
    }

    public static DynamoUserList from(UserList userList) {
        DynamoUserList dul = new DynamoUserList();
        dul.setUserId(userList.getUser().getId().toString());
        dul.setListName(userList.getListName());
        dul.setIsPublic(userList.getIsPublic());
        dul.setCreatedAt(userList.getCreatedAt() != null ? userList.getCreatedAt().getTime() : System.currentTimeMillis());
        // recipeIds intentionally omitted — DynamoDB does not allow empty sets
        return dul;
    }
}
