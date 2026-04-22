package com.brognara.user_saved_recipe_service.dynamo.repository;

import com.brognara.user_saved_recipe_service.dynamo.model.DynamoUserList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Profile("dynamo")
public class DynamoUserListRepository {

    private final DynamoDbTable<DynamoUserList> table;
    private final DynamoDbIndex<DynamoUserList> listIdIndex;
    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoUserListRepository(DynamoDbEnhancedClient enhancedClient,
                                     DynamoDbClient dynamoDbClient,
                                     @Value("${dynamo.table.user-lists:user_lists}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(DynamoUserList.class));
        this.listIdIndex = table.index("listIdIndex");
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    /** Query all lists for a user by PK. */
    public List<DynamoUserList> findByUserId(String userId) {
        QueryConditional condition = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(userId).build());
        return table.query(condition).stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }

    /** O(1) GetItem — PK + SK. */
    public Optional<DynamoUserList> findByUserIdAndListId(String userId, String listId) {
        return Optional.ofNullable(table.getItem(
                Key.builder().partitionValue(userId).sortValue(listId).build()));
    }

    /**
     * Resolves a list by UUID alone via the listIdIndex GSI.
     * Used for sharing lookups where the caller may not know the owner's userId.
     */
    public Optional<DynamoUserList> findByListId(String listId) {
        QueryConditional condition = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(listId).build());
        return listIdIndex.query(condition).stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    public DynamoUserList save(DynamoUserList userList) {
        table.putItem(userList);
        return userList;
    }

    /** Deletes the list item. Returns true if an item existed and was deleted. */
    public boolean deleteByUserIdAndListId(String userId, String listId) {
        DynamoUserList deleted = table.deleteItem(
                Key.builder().partitionValue(userId).sortValue(listId).build());
        return deleted != null;
    }

    /**
     * Atomically adds a recipeId to the recipeIds StringSet.
     * Uses ADD so the set is created on first insertion and duplicates are ignored at the DB level.
     */
    public void addRecipeId(String userId, String listId, String recipeId) {
        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                        "userId", AttributeValue.fromS(userId),
                        "listId", AttributeValue.fromS(listId)
                ))
                .updateExpression("ADD recipeIds :r")
                .expressionAttributeValues(Map.of(
                        ":r", AttributeValue.builder().ss(recipeId).build()
                ))
                .build());
    }

    /**
     * Atomically removes a recipeId from the recipeIds StringSet.
     */
    public void removeRecipeId(String userId, String listId, String recipeId) {
        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                        "userId", AttributeValue.fromS(userId),
                        "listId", AttributeValue.fromS(listId)
                ))
                .updateExpression("DELETE recipeIds :r")
                .expressionAttributeValues(Map.of(
                        ":r", AttributeValue.builder().ss(recipeId).build()
                ))
                .build());
    }
}
