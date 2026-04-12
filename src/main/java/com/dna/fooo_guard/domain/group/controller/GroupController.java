package com.dna.fooo_guard.domain.group.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dna.fooo_guard.domain.group.dto.GroupCreateRequest;
import com.dna.fooo_guard.domain.group.dto.GroupResponse;
import com.dna.fooo_guard.domain.group.service.GroupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
@Transactional
public class GroupController {
    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Void> createGroup(@RequestBody GroupCreateRequest dto, @AuthenticationPrincipal Long userId) {
        groupService.createGroup(dto, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<GroupResponse> getMethodName(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.findGroupById(id));
    }
}
