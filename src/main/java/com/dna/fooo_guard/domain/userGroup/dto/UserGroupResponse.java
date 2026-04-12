package com.dna.fooo_guard.domain.userGroup.dto;

import com.dna.fooo_guard.domain.group.entity.Group;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserGroupResponse {
    private Long id;
    private String name;

    // Entity -> DTO
    public static UserGroupResponse from(Group group) {
        return UserGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .build();
    }
}
