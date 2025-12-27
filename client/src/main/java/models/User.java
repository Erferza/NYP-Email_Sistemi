package models;

import enums.UserRole;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Soyut (Abstract) Kullanıcı sınıfı
 * Tüm kullanıcı türleri bu sınıftan türetilir
 */
public abstract class User implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String id;
    protected String email;
    protected String password;
    protected String fullName;
    protected Date createdAt;
    protected Date lastLogin;
    protected boolean isActive;
    protected String profilePicture;

    public User() {
        this.createdAt = new Date();
        this.isActive = true;
    }

    public User(String email, String password, String fullName) {
        this();
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    // Abstract metodlar - alt sınıflar implement etmek zorunda

    /**
     * Kullanıcının rolünü döndürür
     */
    public abstract UserRole getRole();

    /**
     * Kullanıcının yetkilerini döndürür
     */
    public abstract List<String> getPermissions();

    /**
     * Kullanıcıya özel hoşgeldin mesajı
     */
    public abstract String getWelcomeMessage();

    // Concrete metodlar

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
