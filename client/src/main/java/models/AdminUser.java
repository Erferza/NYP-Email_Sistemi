package models;

import enums.UserRole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Admin kullanıcı sınıfı - User abstract sınıfından kalıtım alır
 */
public class AdminUser extends User {
    
    private static final long serialVersionUID = 1L;
    
    private String department;
    private int accessLevel;
    
    public AdminUser() {
        super();
        this.accessLevel = 1;
    }
    
    public AdminUser(String email, String password, String fullName) {
        super(email, password, fullName);
        this.accessLevel = 1;
    }
    
    public AdminUser(String email, String password, String fullName, String department, int accessLevel) {
        super(email, password, fullName);
        this.department = department;
        this.accessLevel = accessLevel;
    }
    
    // Abstract metodların implementasyonu
    
    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }
    
    @Override
    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>(Arrays.asList(
            "SEND_EMAIL",
            "RECEIVE_EMAIL",
            "CREATE_FOLDER",
            "DELETE_OWN_EMAIL",
            "MANAGE_OWN_PROFILE",
            "VIEW_ALL_USERS",
            "BAN_USER",
            "UNBAN_USER",
            "VIEW_SYSTEM_STATS",
            "MANAGE_SYSTEM_SETTINGS",
            "DELETE_ANY_EMAIL",
            "VIEW_USER_EMAILS"
        ));
        
        if (accessLevel >= 2) {
            permissions.add("CREATE_ADMIN");
            permissions.add("DELETE_USER");
        }
        
        if (accessLevel >= 3) {
            permissions.add("SYSTEM_MAINTENANCE");
            permissions.add("DATABASE_ACCESS");
        }
        
        return permissions;
    }
    
    @Override
    public String getWelcomeMessage() {
        return "Hoş geldiniz Yönetici " + getFullName() + "! (Seviye: " + accessLevel + ")";
    }
    
    // AdminUser'a özel metodlar
    
    /**
     * Kullanıcıyı yasaklar
     */
    public void banUser(User user) {
        user.setActive(false);
        System.out.println(user.getEmail() + " yasaklandı.");
    }
    
    /**
     * Kullanıcının yasağını kaldırır
     */
    public void unbanUser(User user) {
        user.setActive(true);
        System.out.println(user.getEmail() + " yasağı kaldırıldı.");
    }
    
    /**
     * Tüm kullanıcıları listeler
     */
    public void viewAllUsers(List<User> users) {
        System.out.println("=== Tüm Kullanıcılar ===");
        for (User user : users) {
            System.out.println(user.toString());
        }
    }
    
    /**
     * Sistem istatistiklerini görüntüler
     */
    public void viewSystemStats(int totalUsers, int totalEmails, long totalStorage) {
        System.out.println("=== Sistem İstatistikleri ===");
        System.out.println("Toplam Kullanıcı: " + totalUsers);
        System.out.println("Toplam E-posta: " + totalEmails);
        System.out.println("Toplam Depolama: " + formatBytes(totalStorage));
    }
    
    /**
     * Byte değerini okunabilir formata çevirir
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    /**
     * Yeni admin oluşturur (sadece yüksek seviyeli adminler)
     */
    public AdminUser createAdmin(String email, String password, String fullName, int newAccessLevel) {
        if (this.accessLevel >= 2 && newAccessLevel < this.accessLevel) {
            return new AdminUser(email, password, fullName, this.department, newAccessLevel);
        }
        System.out.println("Bu işlem için yetkiniz yok!");
        return null;
    }
    
    // Getters and Setters
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public int getAccessLevel() {
        return accessLevel;
    }
    
    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }
    
    @Override
    public String toString() {
        return "AdminUser{" +
                "id=" + getId() +
                ", email='" + getEmail() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", department='" + department + '\'' +
                ", accessLevel=" + accessLevel +
                '}';
    }
}
