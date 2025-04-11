package com.tapestria.dto;

import lombok.*;

@Data
public class SignupRequest {
    private String username;
    private String password;
    private String role; // ADMIN, LIBRARIAN, STUDENT
}