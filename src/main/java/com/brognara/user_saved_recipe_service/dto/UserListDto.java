package com.brognara.user_saved_recipe_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListDto implements Comparable<UserListDto> {
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
