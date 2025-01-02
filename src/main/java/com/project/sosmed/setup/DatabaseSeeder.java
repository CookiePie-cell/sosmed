package com.project.sosmed.setup;

import com.project.sosmed.entity.*;
import com.project.sosmed.repository.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@AllArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final LikeRepository likeRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
//        initializeRoles();
//        initializeUsers();
//        initializePosts();
//        initializeComments();
//        initializeFollows();
//        initializeLikes();
//        initializeVerificationTokens();
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
        if (!userRepository.existsByEmail("admin@example.com")) {
            Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("password1"));
            adminUser.setEmail("admin@example.com");
            adminUser.setEnabled(true);
            adminUser.getRoles().add(adminRole);

            userRepository.save(adminUser);
        }

        if (!userRepository.existsByEmail("user1@example.com")) {
            Role userRole = roleRepository.findByName(RoleName.USER)
                    .orElseThrow(() -> new RuntimeException("User role not found"));

            for (int i = 1; i <= 5; i++) {
                User user = new User();
                user.setUsername("user" + i);
                user.setPassword(passwordEncoder.encode("password" + i));
                user.setEmail("user" + i + "@example.com");
                user.setEnabled(true);
                user.getRoles().add(userRole);

                userRepository.save(user);
            }
        }
    }

    private void initializePosts() {
        userRepository.findAll().forEach(user -> {
            for (int i = 1; i <= 2; i++) {
                Post post = new Post();
                post.setId(UUID.randomUUID());
                post.setCreatedDate(LocalDateTime.now());
                post.setBody("Post " + i + " by " + user.getUsername());
                post.setUser(user);

                postRepository.save(post);
            }
        });
    }

    private void initializeComments() {
        postRepository.findAll().forEach(post -> {
            User commenter = userRepository.findById(post.getUser().getId()).orElse(null);
            if (commenter != null) {
                Comment comment = new Comment();
                comment.setId(UUID.randomUUID());
                comment.setCreatedDate(LocalDateTime.now());
                comment.setContent("Comment on " + post.getBody());
                comment.setPost(post);
                comment.setUser(commenter);

                commentRepository.save(comment);
            }
        });
    }

    private void initializeFollows() {
        userRepository.findAll().forEach(user -> {
            userRepository.findAll().stream()
                    .filter(otherUser -> !otherUser.equals(user))
                    .limit(2)
                    .forEach(followedUser -> {
                        Follow follow = new Follow();
                        follow.setId(UUID.randomUUID());
                        follow.setCreatedDate(LocalDateTime.now());
                        follow.setFollowingUser(user);
                        follow.setFollowedUser(followedUser);

                        followRepository.save(follow);
                    });
        });
    }

    private void initializeLikes() {
        postRepository.findAll().forEach(post -> {
            userRepository.findAll().stream()
                    .filter(user -> !user.equals(post.getUser()))
                    .limit(2)
                    .forEach(liker -> {
                        Like like = new Like();
                        like.setId(UUID.randomUUID());
                        like.setCreatedDate(LocalDateTime.now());
                        like.setPost(post);
                        like.setUser(liker);

                        likeRepository.save(like);
                    });
        });
    }

    private void initializeVerificationTokens() {
        userRepository.findAll().forEach(user -> {
            VerificationToken token = new VerificationToken();
            token.setId(UUID.randomUUID());
            token.setExpiryDate(LocalDateTime.now().plusDays(7));
            token.setToken(UUID.randomUUID().toString());
            token.setUser(user);

            tokenRepository.save(token);
        });
    }
}
