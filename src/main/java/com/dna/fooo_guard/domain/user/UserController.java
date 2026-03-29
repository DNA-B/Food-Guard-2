package com.dna.fooo_guard.domain.user;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.user.dto.UserSignUpRequest;
import com.dna.fooo_guard.domain.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public String signUp(@RequestBody UserSignUpRequest request) {
        return userService.signUp(request);
    }

    @GetMapping("/")
    public UserResponse findUserById() {
        Long id = 1L; // 예시로 ID를 1로 설정
        return userService.findUserById(id);
    }

    /*
     * groupService에서 사용
     * 
     * @GetMapping("/")
     * public void findAllUsersByGroupId() {
     * Long groupId = 1L;
     * userService.findAllUsersByGroupId(groupId);
     * }
     */

    @DeleteMapping("/")
    public void deleteUser(Long id) {
        userService.deleteUser(id);
    }

}
