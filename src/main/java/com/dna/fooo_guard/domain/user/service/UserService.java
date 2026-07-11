package com.dna.fooo_guard.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dna.fooo_guard.domain.comment.entity.Comment;
import com.dna.fooo_guard.domain.comment.repository.CommentRepository;
import com.dna.fooo_guard.domain.food.repository.FoodRepository;
import com.dna.fooo_guard.domain.post.entity.Post;
import com.dna.fooo_guard.domain.post.repository.PostRepository;
import com.dna.fooo_guard.domain.user.dto.UserResponse;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.domain.userGroup.repository.UserGroupRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final FoodRepository foodRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public UserResponse findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    // // TODO: N+1 속도 비교
    // public List<UserGroupResponse> findUserGroupsById(Long userId) {
    // // List<UserGroup> userGroups = userGroupRepository.findAllByUserId(userId);
    // List<UserGroup> userGroups =
    // userGroupRepository.findAllByUserIdWithGroup(userId);

    // if (userGroups.isEmpty()) {
    // throw new CustomException(ErrorCode.USER_GROUP_NOT_FOUND);
    // }

    // return userGroups.stream()
    // .map(userGroup -> UserGroupResponse.from(userGroup.getGroup()))
    // .toList();
    // }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 💡 TODO: 그룹 매니저 권한 체크 및 마지막 멤버 여부 로직

        List<Comment> userComments = commentRepository.findAllByUserId(id);
        for (Comment comment : userComments) {
            comment.delete(); // 유저가 삭제되어도 댓글은 "탈퇴한 사용자"로 나와야 함.
        }

        List<Post> userPosts = postRepository.findAllByUserId(id);
        for (Post post : userPosts) {
            List<Comment> postComments = commentRepository.findAllByPostId(post.getId());
            for (Comment comment : postComments) {
                comment.delete(); // 해당 글에 달린 댓글들 전부 소프트 삭제
            }
            postRepository.delete(post); // 하드 삭제
        }

        userGroupRepository.deleteAllByUserId(id);
        foodRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }
}