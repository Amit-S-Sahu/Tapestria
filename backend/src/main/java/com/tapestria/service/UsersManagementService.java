package com.tapestria.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tapestria.dto.ReqResp;
import com.tapestria.model.User;
import com.tapestria.repository.UserRepository;

@Service
public class UsersManagementService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ReqResp register(ReqResp registrationRequest) {
        ReqResp resp = new ReqResp();
        try {
            Optional<User> existingUser = userRepository.findByEmail(registrationRequest.getEmail());
            if (existingUser.isPresent()) {
                resp.setStatusCode(400);
                resp.setMessage("Email already exists");
                return resp;
            }

            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setRole(registrationRequest.getRole());
            user.setDisplayName(registrationRequest.getDisplayName());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setEnabled(true);
            User userResult = userRepository.save(user);
            if (userResult.getId() > 0) {
                resp.setUser(userResult);
                resp.setMessage("User registered successfully");
                resp.setStatusCode(200);
            }
        }
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

        return resp;
    }

    public ReqResp login(ReqResp loginRequest) {
        ReqResp resp = new ReqResp();
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            resp.setStatusCode(200);
            resp.setToken(jwt);
            resp.setRefreshToken(refreshToken);
            resp.setExpirationTime("24Hrs");
            resp.setMessage("User logged in successfully");
        }
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

        return resp;
    }

    public ReqResp refreshToken(ReqResp refreshTokenRequest){
        ReqResp resp = new ReqResp();
        try{
            String userEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
            User users = userRepository.findByEmail(userEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                resp.setStatusCode(200);
                resp.setToken(jwt);
                resp.setRefreshToken(refreshTokenRequest.getToken());
                resp.setExpirationTime("24Hrs");
                resp.setMessage("Successfully Refreshed Token");
            }
            // resp.setStatusCode(200);
            return resp;
        }
        catch (Exception e){
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }
    }

    public ReqResp changePassword(String email, ReqResp changePasswordRequest) {
        ReqResp resp = new ReqResp();
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
                userRepository.save(user);
                resp.setStatusCode(200);
                resp.setMessage("Password changed successfully");
            } 
            else {
                resp.setStatusCode(404);
                resp.setMessage("User not found for password change");
            }
        } 
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    public ReqResp toggleUserActive(Integer userId) {
        ReqResp resp = new ReqResp();
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setEnabled(!user.isEnabled());
                User updatedUser = userRepository.save(user);
                resp.setUser(updatedUser);
                resp.setStatusCode(200);
                resp.setMessage(user.isEnabled() ? "User account enabled successfully" : "User account disabled successfully");
            } 
            else {
                resp.setStatusCode(404);
                resp.setMessage("User not found for toggling status");
            }
        } 
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    public ReqResp getAllUsers() {
        ReqResp resp = new ReqResp();
        try {
            List<User> result = userRepository.findAll();
            if (!result.isEmpty()) {
                resp.setUserList(result);
                resp.setStatusCode(200);
                resp.setMessage("Successful");
            } 
            else {
                resp.setStatusCode(404);
                resp.setMessage("No users found");
            }
            return resp;
        } 
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
            return resp;
        }
    }

    public ReqResp getUsersById(Integer id) {
        ReqResp resp = new ReqResp();
        try {
            User usersById = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            resp.setUser(usersById);
            resp.setStatusCode(200);
            resp.setMessage("Users with id '" + id + "' found successfully");
        } 
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    public ReqResp getUsersByUsername(String displayName) {
        ReqResp resp = new ReqResp();
        try {
            List<User> usersByUsername = userRepository.findByDisplayName(displayName);
            if (!usersByUsername.isEmpty()) {
                resp.setUserList(usersByUsername);
                resp.setStatusCode(200);
                resp.setMessage("Users with username '" + displayName + "' found successfully");
            } 
            else {
                resp.setStatusCode(404);
                resp.setMessage("No users found with username '" + displayName + "'");
            }
        } 
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    public ReqResp getUsersByRole(String role) {
        ReqResp resp = new ReqResp();
        try {
            List<User> usersByRole = userRepository.findByRole(role);
            if (!usersByRole.isEmpty()) {
                resp.setUserList(usersByRole);
                resp.setStatusCode(200);
                resp.setMessage("Users with role '" + role + "' found successfully");
            } 
            else {
                resp.setStatusCode(404);
                resp.setMessage("No users found with role '" + role + "'");
            }
        } 
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    public ReqResp deleteUser(Integer userId) {
        ReqResp resp = new ReqResp();
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                userRepository.deleteById(userId);
                resp.setStatusCode(200);
                resp.setMessage("User deleted successfully");
            } 
            else {
                resp.setStatusCode(404);
                resp.setMessage("User not found for deletion");
            }
        } 
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    public ReqResp updateUser(Integer userId, ReqResp updatedUser) {
        ReqResp resp = new ReqResp();
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
    
                String newEmail = updatedUser.getEmail();
                if (newEmail != null && !newEmail.equals(existingUser.getEmail())) {
                    Optional<User> emailConflict = userRepository.findByEmail(newEmail);
                    if (emailConflict.isPresent()) {
                        resp.setStatusCode(400);
                        resp.setMessage("Email already in use by another user");
                        return resp;
                    }
                    existingUser.setEmail(newEmail);
                }
    
                if (updatedUser.getDisplayName() != null) existingUser.setDisplayName(updatedUser.getDisplayName());
    
                if (updatedUser.getRole() != null) existingUser.setRole(updatedUser.getRole());
    
                String pwd = updatedUser.getPassword();
                if (pwd != null && !pwd.trim().isEmpty()) existingUser.setPassword(passwordEncoder.encode(pwd));
    
                User savedUser = userRepository.save(existingUser);
                resp.setUser(savedUser);
                resp.setStatusCode(200);
                resp.setMessage("User updated successfully");
            } 
            else {
                resp.setStatusCode(404);
                resp.setMessage("User not found for update");
            }
        } 
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage(e.getMessage());
        }
        return resp;
    }
    

    public ReqResp addLibrarianByAdmin(ReqResp addLibrarianRequest) {
        ReqResp resp = new ReqResp();
        try {
            Optional<User> existingUser = userRepository.findByEmail(addLibrarianRequest.getEmail());
            if (existingUser.isPresent()) {
                resp.setStatusCode(400);
                resp.setMessage("Email already exists");
                return resp;
            }

            User user = new User();
            user.setEmail(addLibrarianRequest.getEmail());
            user.setRole("LIBRARIAN");
            user.setDisplayName(addLibrarianRequest.getDisplayName());
            user.setPassword(passwordEncoder.encode(addLibrarianRequest.getPassword()));
            user.setEnabled(true);
            User userResult = userRepository.save(user);
            if (userResult.getId() > 0) {
                resp.setUser(userResult);
                resp.setMessage("Librarian registered successfully");
                resp.setStatusCode(200);
            }
        } 
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

        return resp;
    }

    public ReqResp getMyInfo(String email){
        ReqResp reqRes = new ReqResp();
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setUser(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } 
            else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User info not found");
            }

        }
        catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage(e.getMessage());
        }
        return reqRes;
    }
}
