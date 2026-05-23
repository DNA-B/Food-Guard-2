package com.dna.fooo_guard.domain.post.dto;

import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostCreateRequest {
    String title;
    String content;
    // TODO: 이미지 파일 업로드 기능 추가 시, 이미지 관련 필드도 여기에 추가 (예: MultipartFile image)

    public Post toEntity(User user) {
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .user(user)
                .build();
    }
}
