package com.brognara.user_saved_recipe_service.dynamo.repository;

import com.brognara.user_saved_recipe_service.dynamo.model.DynamoUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
@Profile("dynamo")
public class DynamoUserRepository {

    private final DynamoDbTable<DynamoUser> table;

    public DynamoUserRepository(DynamoDbEnhancedClient client,
                                 @Value("${dynamo.table.users:users}") String tableName) {
        this.table = client.table(tableName, TableSchema.fromBean(DynamoUser.class));
    }

    public Optional<DynamoUser> findByAuthProviderAndId(String authProvider, String authProviderId) {
        String key = authProvider + "#" + authProviderId;
        return Optional.ofNullable(table.getItem(Key.builder().partitionValue(key).build()));
    }

    public DynamoUser save(DynamoUser user) {
        table.putItem(user);
        return user;
    }
}
