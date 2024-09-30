package com.example.Lync.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavouriteCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favCategoryId;
    private String userId;  // Reference to User entity
    private Long categoryId; // Reference to Category entity
    private LocalDateTime addedAt; // When it was marked as favorite

    // Getters and Setters
}

