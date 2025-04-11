package com.tapestria.controller;

import com.tapestria.dto.*;
import com.tapestria.model.*;
import com.tapestria.repository.*;
import com.tapestria.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private AuthenticationManager authManager;
    @Autowired private UserRepository userRepo;
    @Autowired private RoleRepository roleRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private UserDetailsService userDetailsService;

    @PostMapping("/signup")
    public String register(@RequestBody SignupRequest request) {
        if (userRepo.existsByUsername(request.getUsername())) {
            return "Username already exists";
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        Role role = roleRepo.findByName(request.getRole().toUpperCase()).orElseThrow();
        user.getRoles().add(role);
        userRepo.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String token = jwtUtils.generateToken(userDetails.getUsername());
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .toList();
        return new JwtResponse(token, request.getUsername(), roles);
    }
}
