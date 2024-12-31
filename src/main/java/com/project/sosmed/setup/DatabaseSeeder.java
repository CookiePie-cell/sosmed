package com.project.sosmed.setup;

import com.project.sosmed.entity.Role;
import com.project.sosmed.entity.RoleName;
import com.project.sosmed.entity.User;
import com.project.sosmed.repository.RoleRepository;
import com.project.sosmed.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeRoles();

        initializeUsers();
    }

    private void initializeRoles() {
        if (!roleRepository.existsByName(RoleName.USER)) {
            Role userRole = new Role();
            userRole.setName(RoleName.USER);
            roleRepository.save(userRole);
        }

        if (!roleRepository.existsByName(RoleName.ADMIN)) {
            Role adminRole = new Role();
            adminRole.setName(RoleName.ADMIN);
            roleRepository.save(adminRole);
        }
    }

    private void initializeUsers() {
        // Check if the user exists by username or email
        if (!userRepository.existsByEmail("admin@example.com")) {
            // Fetch the admin role from the repository
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            // Create a new admin user
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("password1")); // You should encode the password before saving
            adminUser.setEmail("admin@example.com");
            adminUser.setEnabled(true);
            // Assign the admin role to the user
            adminUser.getRoles().add(adminRole);


            // Save the user to the database
            userRepository.save(adminUser);

            User user = userRepository.findByEmail("admin@example.com")
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + "admin@example.com"));

//            System.out.println("Authoritessss from DatabaseSeeder: " + "admin@example.com" + " - " + user.getRoles());
        }

        if (!userRepository.existsByEmail("user@example.com")) {
            Role userRole = roleRepository.findByName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("User role not found"));

            User regularUser = new User();
            regularUser.setUsername("user");
            regularUser.setPassword(passwordEncoder.encode("password1")); // Encode the password
            regularUser.setEmail("user@example.com");
            regularUser.setEnabled(true);
            regularUser.getRoles().add(userRole);

            userRepository.save(regularUser);
        }
    }
}

