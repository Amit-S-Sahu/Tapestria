package com.tapestria.dto;

import lombok.*;
import java.util.*;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String username;
    private List<String> roles;
}