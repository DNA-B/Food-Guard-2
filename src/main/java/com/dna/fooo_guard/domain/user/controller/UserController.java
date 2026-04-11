package com.dna.fooo_guard.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.user.dto.UserResponse;
import com.dna.fooo_guard.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> findUserById(@AuthenticationPrincipal Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
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

}
