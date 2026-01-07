package models;

import enums.UserRole;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Kullanıcı sınıfı
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String email;
    private String password;
    private String fullName;
    private Date createdAt;
    private Date lastLogin;
    private boolean isActive;
    private String profilePicture;
    private UserRole role;

    public User() {
        this.createdAt = new Date();
        this.isActive = true;
        this.role = UserRole.REGULAR;
    }

    public User(String email, String password, String fullName) {
        this();
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    public User(String email, String password, String fullName, UserRole role) {
        this(email, password, fullName);
        this.role = role;
    }

    // Kullanıcı metodları

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        if (role == UserRole.ADMIN) {
            permissions.add("VIEW_USERS");
            permissions.add("DELETE_USERS");
            permissions.add("VIEW_ALL_EMAILS");
        }
        permissions.add("SEND_EMAIL");
        permissions.add("READ_EMAIL");
        permissions.add("DELETE_EMAIL");
        return permissions;
    }

    public String getWelcomeMessage() {
        if (role == UserRole.ADMIN) {
            return "Hoşgeldiniz Admin " + fullName;
        }
        return "Hoşgeldiniz " + fullName;
    }

    /**
     * Kullanıcı girişi yapar
     */
    public boolean login(String email, String password) {
        if (this.email.equals(email) && this.password.equals(password)) {
            this.lastLogin = new Date();
            return true;
        }
        return false;
    }

    /**
     * Kullanıcı çıkışı yapar
     */
    public void logout() {
        // Oturum bilgilerini temizle
        System.out.println(fullName + " çıkış yaptı.");
    }

    /**
     * Profil güncelleme
     */
    public void updateProfile(String fullName, String profilePicture) {
        this.fullName = fullName;
        this.profilePicture = profilePicture;
    }

    /**
     * Şifre değiştirme
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (this.password.equals(oldPassword)) {
            this.password = newPassword;
            return true;
        }
        return false;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + getRole() +
                '}';
    }
}
