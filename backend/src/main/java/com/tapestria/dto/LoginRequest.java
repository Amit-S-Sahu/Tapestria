package com.tapestria.dto;

import lombok.*;

@Data
public class LoginRequest {
    private String username;
    private String password;
}