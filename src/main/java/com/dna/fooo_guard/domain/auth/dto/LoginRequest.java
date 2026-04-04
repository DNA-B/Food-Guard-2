package com.dna.fooo_guard.domain.auth.dto;

import lombok.Getter;

@Getter
public class LoginRequest {

    private String username;
    private String password;
}