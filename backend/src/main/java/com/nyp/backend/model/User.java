package com.nyp.backend.model;

import com.nyp.backend.enums.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String email;
    private String password;
    private String fullName;
    private Date createdAt = new Date();
    private Date lastLogin;
    private boolean isActive = true;
    private String profilePicture;
    private UserRole role;

    // RegularUser fields
    private long storageUsed;
    private boolean isPremium;

    // AdminUser fields
    private String department;
    private int accessLevel;

    public User(String email, String password, String fullName, UserRole role) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = new Date();
        this.isActive = true;
    }
}
