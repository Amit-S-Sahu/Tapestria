package com.tapestria.config;

import com.tapestria.model.Role;
import com.tapestria.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepo;

    public DataInitializer(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public void run(String... args) {
        List<String> roles = List.of("ADMIN", "LIBRARIAN", "STUDENT");
        roles.forEach(roleName -> {
            roleRepo.findByName(roleName).orElseGet(() -> {
                Role role = new Role();
                role.setName(roleName);
                return roleRepo.save(role);
            });
        });
    }
}