package com.brognara.user_saved_recipe_service.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListDto implements Comparable<UserListDto> {
    private String listId;
    @Pattern(regexp = "^[a-zA-Z0-9 _\\-]+$",
             message = "List name may only contain letters, numbers, spaces, hyphens, and underscores")
    @Size(min = 1, max = 100, message = "List name must be between 1 and 100 characters")
    private String listName;
    private String createdByUser;
    private boolean isPublic;
    private long creationTimestamp;

    // TODO recipe count if possible
    // TODO add last updated timestamp

    @Override
    public int compareTo(UserListDto o) {
        return this.listName.compareToIgnoreCase(o.listName);
    }
}
