package com.dna.fooo_guard.domain.group.dto;

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
public class GroupResponse {

    private Long id;
    private String name;
    private String description;

    // Entity -> DTO
    public static GroupResponse from(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .build();
    }
}
