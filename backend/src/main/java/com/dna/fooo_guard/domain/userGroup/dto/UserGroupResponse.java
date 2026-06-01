package com.dna.fooo_guard.domain.userGroup.dto;

import com.dna.fooo_guard.domain.group.entity.Group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "사용자 그룹 응답")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserGroupResponse {

    @Schema(description = "그룹 ID", example = "1")
    private Long id;

    @Schema(description = "그룹명", example = "우리집")
    private String name;

    // Entity -> DTO
    public static UserGroupResponse from(Group group) {
        return UserGroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .build();
    }
}
