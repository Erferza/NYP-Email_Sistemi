package services;

import enums.UserRole;
import models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;

public class AuthService {

    private static AuthService instance;
    private static List<User> users = new ArrayList<>();
    private User currentUser;
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static {
        // Veri klasörünü oluştur
        new File(DATA_DIR).mkdirs();
        
        // Dosyadan kullanıcıları yükle
        loadUsers();
        
        // Eğer hiç kullanıcı yoksa, varsayılan kullanıcıları ekle
        if (users.isEmpty()) {
            initializeDefaultUsers();
            saveUsers();
        } else {
            System.out.println("✅ Kullanıcılar dosyadan yüklendi: " + users.size() + " kullanıcı");
        }
    }

    private AuthService() {
    }

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    private static void saveUsers() {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            gson.toJson(users, writer);
            System.out.println("💾 Kullanıcılar kaydedildi: " + users.size() + " kullanıcı");
        } catch (IOException e) {
            System.err.println("❌ Kullanıcılar kaydedilemedi: " + e.getMessage());
        }
    }

    private static void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            System.out.println("📂 Kullanıcı dosyası bulunamadı, yeni oluşturulacak");
            return;
        }
        
        try (Reader reader = new FileReader(USERS_FILE)) {
            Type listType = new TypeToken<ArrayList<User>>(){}.getType();
            List<User> loadedUsers = gson.fromJson(reader, listType);
            if (loadedUsers != null) {
                users.clear();
                users.addAll(loadedUsers);
            }
        } catch (IOException e) {
            System.err.println("❌ Kullanıcılar yüklenemedi: " + e.getMessage());
        }
    }

    private static void initializeDefaultUsers() {
        // Admin kullanıcı
        User admin = new User();
        admin.setId(UUID.randomUUID().toString());
        admin.setEmail("admin@nyp.com");
        admin.setPassword("admin123");
        admin.setFullName("Admin Kullanıcı");
        admin.setRole(UserRole.ADMIN);
        admin.setCreatedAt(new Date());
        admin.setActive(true);
        users.add(admin);

        // Normal kullanıcı 1
        User user1 = new User();
        user1.setId(UUID.randomUUID().toString());
        user1.setEmail("user1@nyp.com");
        user1.setPassword("123456");
        user1.setFullName("Ahmet Yılmaz");
        user1.setRole(UserRole.REGULAR);
        user1.setCreatedAt(new Date());
        user1.setActive(true);
        users.add(user1);

        // Normal kullanıcı 2
        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setEmail("user2@nyp.com");
        user2.setPassword("123456");
        user2.setFullName("Ayşe Demir");
        user2.setRole(UserRole.REGULAR);
        user2.setCreatedAt(new Date());
        user2.setActive(true);
        users.add(user2);

        System.out.println("✅ Varsayılan kullanıcılar yüklendi:");
        System.out.println("   - Admin: admin@nyp.com / admin123");
        System.out.println("   - User1: user1@nyp.com / 123456");
        System.out.println("   - User2: user2@nyp.com / 123456");
    }

    public User login(String email, String password) {
        User user = users.stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (user != null) {
            currentUser = user;
            user.setLastLogin(new Date());
        }
        return user;
    }

    public User register(String email, String password, String fullName) {
        // Email zaten var mı kontrol et
        boolean exists = users.stream()
                .anyMatch(u -> u.getEmail().equals(email));

        if (exists) {
            return null;
        }

        User newUser = new User();
        newUser.setId(UUID.randomUUID().toString());
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        newUser.setRole(UserRole.REGULAR);
        newUser.setCreatedAt(new Date());
        newUser.setActive(true);
        users.add(newUser);
        saveUsers();

        return newUser;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public int getUserCount() {
        return users.size();
    }
}
