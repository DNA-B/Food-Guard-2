package com.dna.fooo_guard.domain.group.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dna.fooo_guard.domain.food.dto.FoodResponse;
import com.dna.fooo_guard.domain.food.entity.Food;
import com.dna.fooo_guard.domain.food.repository.FoodRepository;
import com.dna.fooo_guard.domain.group.dto.GroupCreateRequest;
import com.dna.fooo_guard.domain.group.dto.GroupEditRequest;
import com.dna.fooo_guard.domain.group.dto.GroupResponse;
import com.dna.fooo_guard.domain.group.entity.Group;
import com.dna.fooo_guard.domain.group.repository.GroupRepository;
import com.dna.fooo_guard.domain.user.entity.User;
import com.dna.fooo_guard.domain.user.repository.UserRepository;
import com.dna.fooo_guard.domain.userGroup.entity.UserGroup;
import com.dna.fooo_guard.domain.userGroup.repository.UserGroupRepository;
import com.dna.fooo_guard.global.error.CustomException;
import com.dna.fooo_guard.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserGroupRepository userGroupRepository;
    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private GroupService groupService;

    private User testUser;
    private Group testGroup;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("tester").nickname("테스터").build();
        testGroup = Group.builder().id(1L).name("공유 냉장고").description("설명").manager(testUser).build();
    }

    @Test
    @DisplayName("그룹 생성 성공")
    void createGroup_Success() {
        GroupCreateRequest dto = GroupCreateRequest.builder().name("새 그룹").description("새 설명").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupRepository.save(any(Group.class))).thenReturn(testGroup);

        groupService.createGroup(dto, 1L);

        verify(groupRepository).save(any(Group.class));
        assertEquals(1, testGroup.getMembers().size());
        assertEquals(1, testUser.getGroups().size());
    }

    @Test
    @DisplayName("그룹 생성 실패 - 유저 없음")
    void createGroup_UserNotFound() {
        GroupCreateRequest dto = GroupCreateRequest.builder().name("새 그룹").description("새 설명").build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> groupService.createGroup(dto, 1L));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    @DisplayName("그룹 단건 조회 성공")
    void findGroupById_Success() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        GroupResponse response = groupService.findGroupById(1L);

        assertEquals(1L, response.getId());
        assertEquals("공유 냉장고", response.getName());
    }

    @Test
    @DisplayName("그룹 단건 조회 실패 - 그룹 없음")
    void findGroupById_GroupNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> groupService.findGroupById(1L));

        assertEquals(ErrorCode.GROUP_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("유저의 그룹 목록 조회 성공")
    void findAllByUserId_Success() {
        UserGroup userGroup = UserGroup.builder().id(1L).user(testUser).group(testGroup).build();

        when(userGroupRepository.findAllByUserId(1L)).thenReturn(List.of(userGroup));

        List<GroupResponse> responses = groupService.findAllByUserId(1L);

        assertEquals(1, responses.size());
        assertEquals("공유 냉장고", responses.get(0).getName());
    }

    @Test
    @DisplayName("그룹 음식 목록 조회 성공")
    void findAllFoodById_Success() {
        Food food = Food.builder().id(1L).name("김밥").type("분식").user(testUser).group(testGroup).build();

        when(foodRepository.findAllByGroupId(1L)).thenReturn(List.of(food));

        List<FoodResponse> responses = groupService.findAllFoodById(1L);

        assertEquals(1, responses.size());
        assertEquals("김밥", responses.get(0).getName());
    }

    @Test
    @DisplayName("그룹 수정 성공")
    void editGroup_Success() {
        GroupEditRequest dto = GroupEditRequest.builder().name("수정 그룹").description("수정 설명").build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(testGroup));

        groupService.editGroup(1L, dto);

        assertEquals("수정 그룹", testGroup.getName());
        assertEquals("수정 설명", testGroup.getDescription());
    }

    @Test
    @DisplayName("그룹 수정 실패 - 그룹 없음")
    void editGroup_GroupNotFound() {
        GroupEditRequest dto = GroupEditRequest.builder().name("수정 그룹").description("수정 설명").build();

        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> groupService.editGroup(1L, dto));

        assertEquals(ErrorCode.GROUP_NOT_FOUND, exception.getErrorCode());
    }
}
