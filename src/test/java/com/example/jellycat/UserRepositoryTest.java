package com.example.jellycat;

import com.example.jellycat.entity.User;
import com.example.jellycat.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUser() {

        // Create a user
        User user = new User();

        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed-password");
        user.setDisplayName("Test User");
        user.setAvatarUrl(null);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Save the user
        User savedUser = userRepository.save(user);

        // Verify that the user received an ID
        assertNotNull(savedUser.getId());

        // Find the user
        User foundUser = userRepository
                .findByUsername("testuser")
                .orElseThrow();

        // Verify the data
        assertEquals("testuser", foundUser.getUsername());
        assertEquals("test@example.com", foundUser.getEmail());
        assertEquals("Test User", foundUser.getDisplayName());
    }
}