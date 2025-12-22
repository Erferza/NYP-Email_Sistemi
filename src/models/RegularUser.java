package models;

import enums.UserRole;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Normal kullanıcı sınıfı - User abstract sınıfından kalıtım alır
 */
public class RegularUser extends User {
    
    private static final long serialVersionUID = 1L;
    
    private long storageUsed; // Kullanılan depolama alanı (bytes)
    private long storageLimit; // Depolama limiti (bytes)
    private boolean isPremium;
    
    public RegularUser() {
        super();
        this.storageLimit = 1024 * 1024 * 1024L; // 1 GB varsayılan limit
        this.storageUsed = 0;
        this.isPremium = false;
    }
    
    public RegularUser(String email, String password, String fullName) {
        super(email, password, fullName);
        this.storageLimit = 1024 * 1024 * 1024L; // 1 GB
        this.storageUsed = 0;
        this.isPremium = false;
    }
    
    // Abstract metodların implementasyonu
    
    @Override
    public UserRole getRole() {
        return UserRole.REGULAR;
    }
    
    @Override
    public List<String> getPermissions() {
        List<String> permissions = new ArrayList<>(Arrays.asList(
            "SEND_EMAIL",
            "RECEIVE_EMAIL",
            "CREATE_FOLDER",
            "DELETE_OWN_EMAIL",
            "MANAGE_OWN_PROFILE"
        ));
        
        if (isPremium) {
            permissions.add("UNLIMITED_STORAGE");
            permissions.add("PRIORITY_SUPPORT");
            permissions.add("CUSTOM_THEMES");
        }
        
        return permissions;
    }
    
    @Override
    public String getWelcomeMessage() {
        if (isPremium) {
            return "Hoş geldiniz Premium Üye " + getFullName() + "!";
        }
        return "Hoş geldiniz " + getFullName() + "!";
    }
    
    // RegularUser'a özel metodlar
    
    /**
     * Hesabı premium'a yükseltir
     */
    public void upgradeAccount() {
        this.isPremium = true;
        this.storageLimit = 15 * 1024 * 1024 * 1024L; // 15 GB
        System.out.println("Hesabınız Premium'a yükseltildi!");
    }
    
    /**
     * Depolama alanı kullanımını günceller
     */
    public void updateStorageUsed(long bytes) {
        this.storageUsed += bytes;
    }
    
    /**
     * Kalan depolama alanını döndürür
     */
    public long getRemainingStorage() {
        return storageLimit - storageUsed;
    }
    
    /**
     * Depolama kullanım yüzdesini döndürür
     */
    public double getStorageUsagePercentage() {
        return (double) storageUsed / storageLimit * 100;
    }
    
    /**
     * Depolama alanının dolu olup olmadığını kontrol eder
     */
    public boolean isStorageFull() {
        return storageUsed >= storageLimit;
    }
    
    // Getters and Setters
    public long getStorageUsed() {
        return storageUsed;
    }
    
    public void setStorageUsed(long storageUsed) {
        this.storageUsed = storageUsed;
    }
    
    public long getStorageLimit() {
        return storageLimit;
    }
    
    public void setStorageLimit(long storageLimit) {
        this.storageLimit = storageLimit;
    }
    
    public boolean isPremium() {
        return isPremium;
    }
    
    public void setPremium(boolean premium) {
        isPremium = premium;
    }
    
    @Override
    public String toString() {
        return "RegularUser{" +
                "id=" + getId() +
                ", email='" + getEmail() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", isPremium=" + isPremium +
                ", storageUsed=" + storageUsed +
                '}';
    }
}
