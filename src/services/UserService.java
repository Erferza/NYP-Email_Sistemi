package services;

import models.User;
import models.RegularUser;
import models.AdminUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Kullanıcı yönetimi servis sınıfı
 * Singleton tasarım deseni kullanılmıştır
 */
public class UserService {
    
    private static UserService instance;
    private List<User> users;
    private User currentUser;
    
    private UserService() {
        users = new ArrayList<>();
        initializeDefaultUsers();
    }
    
    /**
     * Singleton instance döndürür
     */
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    /**
     * Varsayılan kullanıcıları oluşturur
     */
    private void initializeDefaultUsers() {
        // Admin kullanıcı
        AdminUser admin = new AdminUser("admin@mail.com", "admin123", "Sistem Yöneticisi", "IT", 3);
        users.add(admin);
        
        // Normal kullanıcılar
        RegularUser user1 = new RegularUser("ahmet@mail.com", "123456", "Ahmet Yılmaz");
        RegularUser user2 = new RegularUser("ayse@mail.com", "123456", "Ayşe Demir");
        RegularUser user3 = new RegularUser("mehmet@mail.com", "123456", "Mehmet Kaya");
        
        users.add(user1);
        users.add(user2);
        users.add(user3);
    }
    
    /**
     * Kullanıcı girişi yapar
     */
    public User login(String email, String password) {
        Optional<User> user = users.stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst();
        
        if (user.isPresent()) {
            User loggedInUser = user.get();
            if (!loggedInUser.isActive()) {
                System.out.println("Bu hesap askıya alınmış!");
                return null;
            }
            loggedInUser.login(email, password);
            currentUser = loggedInUser;
            return loggedInUser;
        }
        return null;
    }
    
    /**
     * Kullanıcı çıkışı yapar
     */
    public void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        }
    }
    
    /**
     * Yeni kullanıcı kaydı yapar
     */
    public User register(String email, String password, String fullName) {
        // E-posta adresi kontrolü
        if (isEmailExists(email)) {
            System.out.println("Bu e-posta adresi zaten kayıtlı!");
            return null;
        }
        
        RegularUser newUser = new RegularUser(email, password, fullName);
        users.add(newUser);
        return newUser;
    }
    
    /**
     * E-posta adresinin kayıtlı olup olmadığını kontrol eder
     */
    public boolean isEmailExists(String email) {
        return users.stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }
    
    /**
     * E-posta adresine göre kullanıcı bulur
     */
    public User findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * ID'ye göre kullanıcı bulur
     */
    public User findById(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Tüm kullanıcıları döndürür
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    /**
     * Aktif kullanıcı sayısını döndürür
     */
    public int getActiveUserCount() {
        return (int) users.stream().filter(User::isActive).count();
    }
    
    /**
     * Mevcut oturum açmış kullanıcıyı döndürür
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Kullanıcıyı siler
     */
    public boolean deleteUser(User user) {
        return users.remove(user);
    }
    
    /**
     * Kullanıcı listesini temizler ve varsayılanları yükler
     */
    public void reset() {
        users.clear();
        currentUser = null;
        initializeDefaultUsers();
    }
}
