package com.aliyara.authservice.config;

import com.aliyara.authservice.model.AppUser;
import com.aliyara.authservice.model.Role;
import com.aliyara.authservice.repository.RoleRepository;
import com.aliyara.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String adminRoleName = "ADMIN";
        Role adminRole = roleRepository.findByName(adminRoleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(adminRoleName).build()));

        String adminEmail = "admin@smartshop.com";
        String adminUsername = "admin";

        boolean adminExists = userRepository.existsAppUsersByEmail(adminEmail)
                || userRepository.existsAppUsersByUsername(adminUsername);

        if (!adminExists) {
            AppUser admin = new AppUser();
            admin.setEmail(adminEmail);
            admin.setUsername(adminUsername);
            admin.setFirstName("Admin");
            admin.setLastName("Smart");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setCreatedAt(LocalDateTime.now());

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);
        }
    }
}
