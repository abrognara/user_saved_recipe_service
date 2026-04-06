package com.brognara.user_saved_recipe_service.dynamo.model;

import com.brognara.user_saved_recipe_service.model.User;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Date;
import java.util.UUID;

/**
 * DynamoDB model for users.
 *
 * Table key:
 *   PK: authProviderKey = "<authProvider>#<authProviderId>"  (e.g. "firebase#uid123")
 */
@DynamoDbBean
public class DynamoUser {

    private String authProviderKey;
    private String userId;
    private String authProvider;
    private String authProviderId;
    private String email;
    private String displayName;
    private Long createdAt;
    private Long lastLogin;

    @DynamoDbPartitionKey
    public String getAuthProviderKey() { return authProviderKey; }
    public void setAuthProviderKey(String authProviderKey) { this.authProviderKey = authProviderKey; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAuthProvider() { return authProvider; }
    public void setAuthProvider(String authProvider) { this.authProvider = authProvider; }

    public String getAuthProviderId() { return authProviderId; }
    public void setAuthProviderId(String authProviderId) { this.authProviderId = authProviderId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getLastLogin() { return lastLogin; }
    public void setLastLogin(Long lastLogin) { this.lastLogin = lastLogin; }

    public User toUser() {
        User user = new User();
        user.setId(UUID.fromString(userId));
        user.setAuthProvider(authProvider);
        user.setAuthProviderId(authProviderId);
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setCreatedAt(createdAt != null ? new Date(createdAt) : new Date());
        if (lastLogin != null) user.setLastLogin(new Date(lastLogin));
        return user;
    }

    public static DynamoUser from(User user) {
        DynamoUser du = new DynamoUser();
        du.setAuthProviderKey(user.getAuthProvider() + "#" + user.getAuthProviderId());
        du.setUserId(user.getId().toString());
        du.setAuthProvider(user.getAuthProvider());
        du.setAuthProviderId(user.getAuthProviderId());
        du.setEmail(user.getEmail());
        du.setDisplayName(user.getDisplayName());
        du.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().getTime() : System.currentTimeMillis());
        if (user.getLastLogin() != null) du.setLastLogin(user.getLastLogin().getTime());
        return du;
    }
}
