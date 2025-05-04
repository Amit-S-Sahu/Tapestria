package com.tapestria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tapestria.dto.ReqResp;
import com.tapestria.service.UsersManagementService;



@RestController
public class UserManagementController {
    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqResp> register(@RequestBody ReqResp registrationRequest) {
        return ResponseEntity.ok(usersManagementService.register(registrationRequest));
    }
    
    @PostMapping("/auth/login")
    public ResponseEntity<ReqResp> login(@RequestBody ReqResp loginRequest) {
        return ResponseEntity.ok(usersManagementService.login(loginRequest));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqResp> refreshToken(@RequestBody ReqResp refreshTokenRequest) {
        return ResponseEntity.ok(usersManagementService.refreshToken(refreshTokenRequest));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqResp> getAllUsers() {
        return ResponseEntity.ok(usersManagementService.getAllUsers());
    }
    
    @GetMapping("/admin/get-user/{userId}")
    public ResponseEntity<ReqResp> getUsersById(@PathVariable Integer userId) {
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));
    }

    @GetMapping("/admin/get-user-by-role/{role}")
    public ResponseEntity<ReqResp> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(usersManagementService.getUsersByRole(role));
    }

    @PutMapping("/admin/enable-disable/{userId}")
    public ResponseEntity<ReqResp> enableDisableUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(usersManagementService.toggleUserActive(userId));
    }

    @PostMapping("/admin/add-librarian")
    public ResponseEntity<ReqResp> addLibrarianByAdmin(@RequestBody ReqResp addLibrarianRequest) {
        return ResponseEntity.ok(usersManagementService.addLibrarianByAdmin(addLibrarianRequest));
    }

    @PutMapping("/admin/update-user/{userId}")
    public ResponseEntity<ReqResp> updateUser(@PathVariable Integer userId, @RequestBody ReqResp userUpdateRequest) {
        return ResponseEntity.ok(usersManagementService.updateUser(userId, userUpdateRequest));
    }

    @DeleteMapping("/admin/delete-user/{userId}")
    public ResponseEntity<ReqResp> deleteUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(usersManagementService.deleteUser(userId));
    }

    @GetMapping("/alluser/get-profile")
    public ResponseEntity<ReqResp> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqResp resp = usersManagementService.getMyInfo(email);
        return ResponseEntity.status(resp.getStatusCode()).body(resp);
    }

    @PutMapping("/alluser/change-password")
    public ResponseEntity<ReqResp> changePassword(@RequestBody ReqResp changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return ResponseEntity.ok(usersManagementService.changePassword(email, changePasswordRequest));
    }
}