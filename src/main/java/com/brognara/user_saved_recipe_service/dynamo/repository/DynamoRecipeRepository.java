package com.brognara.user_saved_recipe_service.dynamo.repository;

import com.brognara.user_saved_recipe_service.dynamo.model.DynamoRecipe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("dynamo")
public class DynamoRecipeRepository {

    private final DynamoDbTable<DynamoRecipe> table;
    private final DynamoDbIndex<DynamoRecipe> urlIndex;

    public DynamoRecipeRepository(DynamoDbEnhancedClient client,
                                   @Value("${dynamo.table.recipes:recipes}") String tableName) {
        this.table = client.table(tableName, TableSchema.fromBean(DynamoRecipe.class));
        this.urlIndex = this.table.index("urlIndex");
    }

    public Optional<DynamoRecipe> findById(String id) {
        return Optional.ofNullable(table.getItem(Key.builder().partitionValue(id).build()));
    }

    public Optional<DynamoRecipe> findByUrl(String url) {
        QueryConditional condition = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(url).build());
        return urlIndex.query(condition).stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    public List<DynamoRecipe> findAllById(List<String> ids) {
        return ids.stream()
                .map(id -> Optional.ofNullable(table.getItem(Key.builder().partitionValue(id).build())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public DynamoRecipe save(DynamoRecipe recipe) {
        table.putItem(recipe);
        return recipe;
    }
}
