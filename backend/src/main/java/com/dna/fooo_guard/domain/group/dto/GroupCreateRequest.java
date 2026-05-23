package com.dna.fooo_guard.domain.group.dto;

import com.dna.fooo_guard.domain.group.entity.Group;
import com.dna.fooo_guard.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupCreateRequest {
    private String name;
    private String description;

    public Group toEntity(User manager) {
        return Group.builder()
                .name(this.name)
                .description(this.description)
                .manager(manager)
                .build();
    }
}
