package com.brognara.user_saved_recipe_service.dynamo.repository;

import com.brognara.user_saved_recipe_service.dynamo.model.DynamoUserList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
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
    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoUserListRepository(DynamoDbEnhancedClient enhancedClient,
                                     DynamoDbClient dynamoDbClient,
                                     @Value("${dynamo.table.user-lists:user_lists}") String tableName) {
        this.table = enhancedClient.table(tableName, TableSchema.fromBean(DynamoUserList.class));
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    public List<DynamoUserList> findByUserId(String userId) {
        QueryConditional condition = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(userId).build());
        return table.query(condition).stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }

    /** Direct GetItem — SK is listName so this is O(1). */
    public Optional<DynamoUserList> findByUserIdAndListName(String userId, String listName) {
        return Optional.ofNullable(table.getItem(
                Key.builder().partitionValue(userId).sortValue(listName).build()));
    }

    public DynamoUserList save(DynamoUserList userList) {
        table.putItem(userList);
        return userList;
    }

    /** Deletes the list item. Returns true if an item existed and was deleted. */
    public boolean deleteByUserIdAndListName(String userId, String listName) {
        DynamoUserList deleted = table.deleteItem(
                Key.builder().partitionValue(userId).sortValue(listName).build());
        return deleted != null;
    }

    /**
     * Atomically adds a recipeId to the recipeIds StringSet.
     * Uses ADD so the set is created on first insertion and duplicates are ignored at the DB level.
     */
    public void addRecipeId(String userId, String listName, String recipeId) {
        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                        "userId", AttributeValue.fromS(userId),
                        "listName", AttributeValue.fromS(listName)
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
    public void removeRecipeId(String userId, String listName, String recipeId) {
        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                        "userId", AttributeValue.fromS(userId),
                        "listName", AttributeValue.fromS(listName)
                ))
                .updateExpression("DELETE recipeIds :r")
                .expressionAttributeValues(Map.of(
                        ":r", AttributeValue.builder().ss(recipeId).build()
                ))
                .build());
    }
}
