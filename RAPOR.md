# 📧 JAVA E-POSTA SİSTEMİ - NYP PROJESİ DETAYLI RAPOR


## 🔢 Dosya ve Sınıf Özeti

### Backend (Spring Boot)

| Katman | Dosya Sayısı | Sınıflar | Açıklama |
|--------|--------------|----------|----------|
| `controller` | 2 | AuthController, EmailController | REST API endpoints |
| `model` | 3 | User, Email, Attachment | MongoDB document models |
| `repository` | 2 | UserRepository, EmailRepository | Spring Data MongoDB |
| `service` | 2 | AuthService, EmailService | Business logic |
| `enums` | 3 | FolderType, Priority, UserRole | Enum sabitleri |
| **TOPLAM** | **12** | **12 sınıf** | - |

### Client (JavaFX)

| Paket | Dosya Sayısı | Sınıflar | Açıklama |
|-------|--------------|----------|----------|
| `gui` | 4 | App, LoginView, MainView, ComposeView | JavaFX UI |
| `models` | 7 | User*, AdminUser, RegularUser, Email, DraftEmail, Folder, Attachment | Domain models |
| `services` | 3 | ApiClient, Mailbox, UserService | HTTP client & services |
| `interfaces` | 4 | IMailSender, IMailReceiver, ISearchable, IFolderManager | Interface definitions |
| `enums` | 3 | FolderType, Priority, UserRole | Enum sabitleri |
| `exceptions` | 4 | EmailException*, InvalidEmail, MailSend, Attachment | Custom exceptions |
| `utils` | 3 | Constants, DateFormatter, EmailValidator | Static utilities |
| **TOPLAM** | **28** | **28 sınıf (1 abstract*)** | - |

### Genel Toplam
- **Toplam Dosya**: 40+
- **Toplam Sınıf**: 40+
- **Interface**: 4
- **Enum**: 3 (her katmanda)
- **Abstract Class**: 2 (User, EmailException)
- **Tahmini Kod Satırı**: ~3000+

---

## 🎯 Kullanılan NYP (OOP) Kavramları

---

## 🎯 Kullanılan NYP (OOP) Kavramları

### 1️⃣ SOYUTLAMA (Abstraction)

#### Abstract Class: `User.java` (Client-Side)

**Dosya Yolu**: `client/src/main/java/models/User.java`

```java
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

    // Protected alanlar - alt sınıflar erişebilir
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

    // ============ ABSTRACT METODLAR - Alt sınıflar implement etmek ZORUNDA ============

    /**
     * Kullanıcının rolünü döndürür (ADMIN veya REGULAR)
     * Her alt sınıf kendi rolünü tanımlar
     */
    public abstract UserRole getRole();

    /**
     * Kullanıcının yetkilerini liste olarak döndürür
     * Admin daha fazla yetkiye sahiptir
     */
    public abstract List<String> getPermissions();

    /**
     * Kullanıcıya özel hoşgeldin mesajı
     * Her kullanıcı tipi farklı mesaj gösterir
     */
    public abstract String getWelcomeMessage();

    // ============ CONCRETE METODLAR - Tüm alt sınıflar kullanabilir ============

    /**
     * Kullanıcı girişi yapar
     * @return Giriş başarılıysa true
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

    // Getter ve Setter metodları...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    // ... diğer getter/setter'lar
}
```

**Açıklama:** 
- `User` sınıfı **abstract** olarak tanımlandı çünkü:
  - Doğrudan bir "User" nesnesi oluşturmak mantıksız (her kullanıcı Admin veya Regular olmalı)
  - Her kullanıcı tipi farklı rol ve yetkilere sahip
  - Ortak özellikler (email, password, fullName) bu sınıfta tanımlı
  - Alt sınıflar **abstract metodları implement etmek zorunda**
- Bu sınıf bir **şablon (template)** görevi görür

---

### 2️⃣ KALITIM (Inheritance)

#### Kullanıcı Hiyerarşisi
```
        User (abstract)
           ┌───────┴───────┐
           │               │
      AdminUser       RegularUser
    (extends User)   (extends User)
```

#### **AdminUser.java** - Yönetici Kullanıcı

**Dosya Yolu**: `client/src/main/java/models/AdminUser.java`

```java
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
    
    // AdminUser'a özel alanlar
    private String department;      // Bölüm (IT, HR, vb.)
    private int accessLevel;        // Erişim seviyesi (1-3)
    
    public AdminUser() {
        super();
        this.accessLevel = 1;  // Varsayılan seviye
    }
    
    public AdminUser(String email, String password, String fullName) {
        super(email, password, fullName);
        this.accessLevel = 1;
    }
    
    public AdminUser(String email, String password, String fullName, 
                     String department, int accessLevel) {
        super(email, password, fullName);
        this.department = department;
        this.accessLevel = accessLevel;
    }
    
    // ============ ABSTRACT METODLARIN IMPLEMENTASYONU ============
    
    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;  // Admin rolü döner
    }
    
    @Override
    public List<String> getPermissions() {
        // Admin'in temel yetkileri
        List<String> permissions = new ArrayList<>(Arrays.asList(
            "SEND_EMAIL",              // E-posta gönderme
            "RECEIVE_EMAIL",           // E-posta alma
            "CREATE_FOLDER",           // Klasör oluşturma
            "DELETE_OWN_EMAIL",        // Kendi e-postasını silme
            "MANAGE_OWN_PROFILE",      // Profil yönetimi
            "VIEW_ALL_USERS",          // Tüm kullanıcıları görüntüleme
            "BAN_USER",                // Kullanıcı yasaklama
            "UNBAN_USER",              // Yasak kaldırma
            "VIEW_SYSTEM_STATS",       // Sistem istatistikleri
            "MANAGE_SYSTEM_SETTINGS",  // Sistem ayarları
            "DELETE_ANY_EMAIL",        // Herhangi bir e-postayı silme
            "VIEW_USER_EMAILS"         // Kullanıcı e-postalarını görme
        ));
        
        // Erişim seviyesine göre ek yetkiler
        if (accessLevel >= 2) {
            permissions.add("CREATE_ADMIN");    // Yeni admin oluşturma
            permissions.add("DELETE_USER");     // Kullanıcı silme
        }
        
        if (accessLevel >= 3) {
            permissions.add("SYSTEM_MAINTENANCE");  // Sistem bakımı
            permissions.add("DATABASE_ACCESS");     // Veritabanı erişimi
        }
        
        return permissions;
    }
    
    @Override
    public String getWelcomeMessage() {
        return "Hoş geldiniz Yönetici " + getFullName() + 
               "! (Seviye: " + accessLevel + ")";
    }
    
    // ============ ADMIN'E ÖZEL METODLAR ============
    
    /**
     * Kullanıcıyı yasaklar (hesabı devre dışı bırakır)
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
     * Sistem istatistiklerini gösterir
     */
    public void viewSystemStats() {
        System.out.println("=== Sistem İstatistikleri ===");
        System.out.println("Bölüm: " + department);
        System.out.println("Erişim Seviyesi: " + accessLevel);
        // Gerçek projede daha detaylı istatistikler olurdu
    }
    
    // Getter ve Setter'lar
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }
}
```

#### **RegularUser.java** - Normal Kullanıcı

**Dosya Yolu**: `client/src/main/java/models/RegularUser.java`

```java
package models;

import enums.UserRole;
import java.util.Arrays;
import java.util.List;

/**
 * Normal kullanıcı sınıfı - User abstract sınıfından kalıtım alır
 */
public class RegularUser extends User {
    
    private static final long serialVersionUID = 1L;
    
    // RegularUser'a özel alanlar
    private long storageUsed;       // Kullanılan depolama (byte)
    private boolean isPremium;      // Premium hesap mı?
    
    private static final long FREE_STORAGE_LIMIT = 1_073_741_824L;  // 1 GB
    private static final long PREMIUM_STORAGE_LIMIT = 10_737_418_240L;  // 10 GB
    
    public RegularUser() {
        super();
        this.storageUsed = 0;
        this.isPremium = false;
    }
    
    public RegularUser(String email, String password, String fullName) {
        super(email, password, fullName);
        this.storageUsed = 0;
        this.isPremium = false;
    }
    
    // ============ ABSTRACT METODLARIN IMPLEMENTASYONU ============
    
    @Override
    public UserRole getRole() {
        return UserRole.REGULAR;  // Normal kullanıcı rolü
    }
    
    @Override
    public List<String> getPermissions() {
        // Normal kullanıcının temel yetkileri (Admin'den daha kısıtlı)
        return Arrays.asList(
            "SEND_EMAIL",           // E-posta gönderme
            "RECEIVE_EMAIL",        // E-posta alma
            "CREATE_FOLDER",        // Klasör oluşturma
            "DELETE_OWN_EMAIL",     // Sadece kendi e-postalarını silme
            "MANAGE_OWN_PROFILE"    // Sadece kendi profilini yönetme
        );
    }
    
    @Override
    public String getWelcomeMessage() {
        String accountType = isPremium ? " (Premium)" : "";
        return "Hoş geldiniz " + getFullName() + accountType + "!";
    }
    
    // ============ NORMAL KULLANICIYA ÖZEL METODLAR ============
    
    /**
     * Hesabı premium'a yükseltir
     */
    public void upgradeAccount() {
        this.isPremium = true;
        System.out.println("Hesabınız Premium'a yükseltildi!");
    }
    
    /**
     * Depolama kullanım yüzdesini hesaplar
     */
    public double getStorageUsagePercentage() {
        long limit = isPremium ? PREMIUM_STORAGE_LIMIT : FREE_STORAGE_LIMIT;
        return (storageUsed * 100.0) / limit;
    }
    
    /**
     * Depolama limiti kontrolü
     */
    public boolean hasStorageSpace(long fileSize) {
        long limit = isPremium ? PREMIUM_STORAGE_LIMIT : FREE_STORAGE_LIMIT;
        return (storageUsed + fileSize) <= limit;
    }
    
    /**
     * Depolama alanı ekle
     */
    public void addStorage(long bytes) {
        this.storageUsed += bytes;
    }
    
    // Getter ve Setter'lar
    public long getStorageUsed() { return storageUsed; }
    public void setStorageUsed(long storageUsed) { this.storageUsed = storageUsed; }
    public boolean isPremium() { return isPremium; }
    public void setPremium(boolean premium) { isPremium = premium; }
}
```

#### E-posta Hiyerarşisi
```
      Email
        │
        └─ DraftEmail
         (extends Email)
```

**DraftEmail.java** - Taslak e-posta'nın Email'den kalıtımı

```java
package models;

import java.util.Date;

/**
 * Taslak e-posta sınıfı - Email sınıfından kalıtım alır
 * Gönderilmemiş, kayıt edilmiş e-postalar için kullanılır
 */
public class DraftEmail extends Email {
    
    private static final long serialVersionUID = 1L;
    
    private Date lastModified;      // Son değiştirilme tarihi
    private boolean autoSaved;      // Otomatik kaydedildi mi?
    private int saveCount;          // Kaç kez kaydedildi
    
    public DraftEmail() {
        super();
        this.lastModified = new Date();
        this.autoSaved = false;
        this.saveCount = 0;
    }
    
    /**
     * Taslağı otomatik kaydet
     */
    public void autoSave() {
        this.lastModified = new Date();
        this.autoSaved = true;
        this.saveCount++;
        System.out.println("Taslak otomatik kaydedildi. (Kayıt #" + saveCount + ")");
    }
    
    /**
     * Taslağı manuel kaydet
     */
    public void manualSave() {
        this.lastModified = new Date();
        this.autoSaved = false;
        this.saveCount++;
        System.out.println("Taslak manuel olarak kaydedildi.");
    }
    
    /**
     * Taslağı normal e-postaya dönüştür (göndermeye hazır hale getir)
     */
    public Email toEmail() {
        Email email = new Email();
        email.setFrom(this.getFrom());
        email.setTo(this.getTo());
        email.setCc(this.getCc());
        email.setSubject(this.getSubject());
        email.setBody(this.getBody());
        email.setPriority(this.getPriority());
        email.setAttachments(this.getAttachments());
        email.setSentDate(new Date());  // Gönderim tarihi şimdi
        return email;
    }
    
    // Getter ve Setter'lar
    public Date getLastModified() { return lastModified; }
    public boolean isAutoSaved() { return autoSaved; }
    public int getSaveCount() { return saveCount; }
}
```

**Kalıtım Avantajları:**
1. **Kod Tekrarını Önler**: Email'in tüm özellikleri DraftEmail'de de vardır
2. **Genişletilebilir**: Yeni kullanıcı tipleri eklemek kolay
3. **Polimorfizm**: `User` referansı ile tüm alt sınıflar kullanılabilir

---

### 3️⃣ ÇOK BİÇİMLİLİK (Polymorphism)

Polymorphism, aynı metodun farklı sınıflarda farklı davranışlar sergilemesidir.

#### Örnek 1: Runtime Polymorphism (Çalışma Zamanı Çok Biçimliliği)

```java
// Ana metodda polymorphism kullanımı
public class TestPolymorphism {
    public static void main(String[] args) {
        // Parent referans, child obje - Bu polymorphism!
        User adminUser = new AdminUser("admin@mail.com", "123", "Ali Yılmaz");
        User regularUser = new RegularUser("user@mail.com", "123", "Ayşe Demir");
        
        // Aynı metod çağrısı, farklı sonuçlar (Runtime Polymorphism)
        System.out.println(adminUser.getRole());      
        // Çıktı: ADMIN
        
        System.out.println(regularUser.getRole());    
        // Çıktı: REGULAR
        
        System.out.println(adminUser.getWelcomeMessage());   
        // Çıktı: "Hoş geldiniz Yönetici Ali Yılmaz! (Seviye: 1)"
        
        System.out.println(regularUser.getWelcomeMessage()); 
        // Çıktı: "Hoş geldiniz Ayşe Demir!"
        
        // Yetkileri yazdır
        System.out.println("Admin Yetkileri: " + adminUser.getPermissions().size());
        // Çıktı: 12 yetki
        
        System.out.println("Normal Kullanıcı Yetkileri: " + regularUser.getPermissions().size());
        // Çıktı: 5 yetki
    }
}
```

#### Örnek 2: Interface ile Polymorphism

**Dosya Yolu**: `client/src/main/java/services/Mailbox.java`

```java
package services;

/**
 * Mailbox sınıfı 4 farklı interface'i implement eder
 * Bu, polymorphism'in güzel bir örneğidir
 */
public class Mailbox implements IMailSender, IMailReceiver, ISearchable, IFolderManager {
    
    private User user;
    private List<Folder> folders;
    private Folder inbox, sent, drafts, trash, starred;
    
    // ============ IMailSender INTERFACE IMPLEMENTASYONU ============
    
    @Override
    public boolean sendMail(Email email) throws MailSendException {
        try {
            email.setFrom(user.getEmail());
            String json = gson.toJson(email);
            ApiClient.post("/emails/send", json);
            return true;
        } catch (Exception e) {
            throw new MailSendException("E-posta gönderilemedi: " + e.getMessage());
        }
    }
    
    @Override
    public boolean replyMail(Email original, String replyBody) throws MailSendException {
        Email reply = new Email();
        reply.setFrom(user.getEmail());
        reply.setTo(Arrays.asList(original.getFrom()));
        reply.setSubject("Re: " + original.getSubject());
        reply.setBody(replyBody + "\n\n---Original Message---\n" + original.getBody());
        return sendMail(reply);
    }
    
    @Override
    public boolean forwardMail(Email original, List<String> recipients) throws MailSendException {
        Email forward = new Email();
        forward.setFrom(user.getEmail());
        forward.setTo(recipients);
        forward.setSubject("Fwd: " + original.getSubject());
        forward.setBody("---Forwarded Message---\n" + original.getBody());
        forward.setAttachments(original.getAttachments());
        return sendMail(forward);
    }
    
    // ============ IMailReceiver INTERFACE IMPLEMENTASYONU ============
    
    @Override
    public List<Email> receiveMails() {
        refresh();
        return inbox.getEmails();
    }
    
    @Override
    public Email readMail(String emailId) {
        try {
            String response = ApiClient.get("/emails/" + emailId);
            Email email = gson.fromJson(response, Email.class);
            
            // Okundu olarak işaretle
            ApiClient.put("/emails/" + emailId + "/read", "");
            
            return email;
        } catch (Exception e) {
            System.err.println("E-posta okunamadı: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public void markAsRead(String emailId) {
        try {
            ApiClient.put("/emails/" + emailId + "/read", "");
        } catch (Exception e) {
            System.err.println("Okundu işareti konulamadı: " + e.getMessage());
        }
    }
    
    @Override
    public void markAsUnread(String emailId) {
        // Backend'de endpoint eklenebilir
    }
    
    // ============ ISearchable INTERFACE IMPLEMENTASYONU ============
    
    @Override
    public List<Email> searchBySubject(String keyword) {
        try {
            String url = "/emails/search?email=" + user.getEmail() + 
                        "&query=" + keyword + "&type=SUBJECT";
            String response = ApiClient.get(url);
            Type listType = new TypeToken<ArrayList<Email>>(){}.getType();
            return gson.fromJson(response, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Email> searchBySender(String sender) {
        try {
            String url = "/emails/search?email=" + user.getEmail() + 
                        "&query=" + sender + "&type=SENDER";
            String response = ApiClient.get(url);
            Type listType = new TypeToken<ArrayList<Email>>(){}.getType();
            return gson.fromJson(response, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Email> searchByDateRange(Date startDate, Date endDate) {
        // Tüm e-postaları getir ve tarih filtrele
        return inbox.getEmails().stream()
                .filter(e -> e.getSentDate().after(startDate) && 
                            e.getSentDate().before(endDate))
                .collect(Collectors.toList());
    }
}
```

**Açıklama:** Mailbox sınıfı 4 farklı interface implement eder. Bu sayede:
- `IMailSender` referansı ile mail gönderme işlemleri
- `IMailReceiver` referansı ile mail alma işlemleri  
- `ISearchable` referansı ile arama işlemleri
- `IFolderManager` referansı ile klasör yönetimi

yapılabilir. Bu, polymorphism'in en güzel örneklerinden biridir.

---

#### Örnek 3: Login Metodunda Polymorphism (GERÇEK PROJE KODU) ⭐

**Dosya Yolu**: `client/src/main/java/services/UserService.java`

```java
public User login(String email, String password) {
    try {
        Map<String, String> creds = new HashMap<>();
        creds.put("email", email);
        creds.put("password", password);
        String jsonBody = gson.toJson(creds);

        String response = ApiClient.post("/auth/login", jsonBody);

        // ⭐ POLYMORPHİSM BURADA! - Role göre farklı obje döner
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        String role = jsonObject.get("role").getAsString();

        if ("ADMIN".equals(role)) {
            currentUser = gson.fromJson(response, AdminUser.class);
        } else {
            currentUser = gson.fromJson(response, RegularUser.class);
        }
        return currentUser;  // User tipinde dönüyor (Parent class)
    } catch (Exception e) {
        System.err.println("Login failed: " + e.getMessage());
        return null;
    }
}
```

**Açıklama:** 
- Method `User` tipinde return eder (parent class)
- Ama gerçekte `AdminUser` veya `RegularUser` objesi döner (child class)
- Bu sayede çağıran kod, kullanıcının tipini bilmeden çalışabilir
- Runtime'da doğru metodlar otomatik çağrılır (Dynamic Binding)

**📸 EKRAN GÖRÜNTÜSÜ İÇİN:** Bu metodu `UserService.java` dosyasından ekran görüntüsü alabilirsiniz.

---

#### Örnek 4: App.java'da Polymorphism Kullanımı (GERÇEK PROJE KODU) ⭐

**Dosya Yolu**: `client/src/main/java/gui/App.java`

```java
public class App extends Application {

    private static Stage primaryStage;
    private static UserService userService = UserService.getInstance();
    private static User currentUser;  // ⭐ Parent class referansı
    private static Mailbox userMailbox;
    
    // ...
    
    public static void showMainView(User user) {
        currentUser = user;  // AdminUser veya RegularUser olabilir
        userMailbox = new Mailbox(user);

        MainView mainView = new MainView(currentUser, userMailbox);
        Scene scene = new Scene(mainView.getView(), 1200, 800);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }
    
    public static void logout() {
        userService.logout();
        currentUser = null;
        userMailbox = null;
        showLoginView();
    }
}
```

**Açıklama:**
- `currentUser` değişkeni `User` tipinde tanımlanmış (abstract parent)
- Ama runtime'da `AdminUser` veya `RegularUser` objesi tutuyor
- `showMainView()` metodu her iki tip kullanıcı için de çalışıyor
- Kullanıcı tipine göre farklı davranışlar otomatik olarak çalışıyor

**📸 EKRAN GÖRÜNTÜSÜ İÇİN:** Bu kodu `App.java` dosyasından ekran görüntüsü alabilirsiniz.

---

#### Örnek 5: MainView Constructor'ında Polymorphism (GERÇEK PROJE KODU) ⭐

**Dosya Yolu**: `client/src/main/java/gui/MainView.java`

```java
public class MainView {

  private BorderPane root;
  private User user;  // ⭐ Parent class - AdminUser veya RegularUser olabilir
  private Mailbox mailbox;
  
  private ListView<Email> emailListView;
  private VBox detailView;
  // ...

  public MainView(User user, Mailbox mailbox) {
    this.user = user;  // Polymorphic assignment
    this.mailbox = mailbox;
    this.currentFolder = mailbox.getInbox();
    createView();
    setupAutoRefresh();
  }
  
  // User objesi üzerinden metodlar çağrılıyor
  // Runtime'da AdminUser ise admin metodları,
  // RegularUser ise normal user metodları çalışıyor
}
```

**Açıklama:**
- `MainView` constructor'ı `User` tipinde parametre alıyor
- Ama aslında `AdminUser` veya `RegularUser` nesnesi geliyor
- Arayüz her iki tip için de aynı şekilde çalışıyor
- Bu sayede kod tekrarı önleniyor ve esneklik sağlanıyor

**📸 EKRAN GÖRÜNTÜSÜ İÇİN:** Bu kodu `MainView.java` dosyasından ekran görüntüsü alabilirsiniz.

---

#### 🎯 Polymorphism'in Avantajları (Projemizde)

1. **Esneklik**: Yeni kullanıcı tipi eklemek kolay (örn: `ManagerUser`)
2. **Kod Tekrarını Önler**: `User` referansı ile her tip kullanıcı yönetilebilir
3. **Bakım Kolaylığı**: Tek bir arayüz üzerinden farklı davranışlar
4. **Genişletilebilirlik**: Interface'ler sayesinde yeni özellikler eklemek kolay
5. **Runtime Flexibility**: Kullanıcı tipi runtime'da belirlenir, kod esnek kalır

---

    
    // ============ IFolderManager INTERFACE IMPLEMENTASYONU ============
    
    @Override
    public Folder createFolder(String name) {
        Folder newFolder = new Folder(name, FolderType.CUSTOM);
        folders.add(newFolder);
        return newFolder;
    }
    
    @Override
    public boolean deleteFolder(String folderId) {
        return folders.removeIf(f -> f.getId().equals(folderId));
    }
    
    @Override
    public boolean moveEmail(String emailId, String targetFolderId) {
        // E-postayı bir klasörden diğerine taşı
        for (Folder folder : folders) {
            Email email = folder.getEmails().stream()
                    .filter(e -> e.getId().equals(emailId))
                    .findFirst()
                    .orElse(null);
            if (email != null) {
                folder.getEmails().remove(email);
                // Target folder'a ekle
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<Folder> getAllFolders() {
        return folders;
    }
}
```

**Polymorphism Kullanımı:**

```java
// Farklı interface referanslarıyla aynı nesneyi kullanma
Mailbox mailbox = new Mailbox(currentUser);

// IMailSender olarak kullan
IMailSender sender = mailbox;
sender.sendMail(newEmail);

// ISearchable olarak kullan
ISearchable searcher = mailbox;
List<Email> results = searcher.searchBySubject("toplantı");

// IFolderManager olarak kullan
IFolderManager folderMgr = mailbox;
folderMgr.createFolder("Önemli");

// Hepsi aynı nesne ama farklı davranışlar!
```

**Polymorphism Avantajları:**
1. **Esneklik**: Kodda değişiklik yapmadan yeni tipler eklenebilir
2. **Bakım Kolaylığı**: Ortak davranışlar tek yerden yönetilir
3. **Modülerlik**: Her sınıf kendi sorumluluğunu üstlenir

---

### 4️⃣ KAPSÜLLEME (Encapsulation)

Kapsülleme, verileri gizleyip sadece gerekli metodlarla erişim sağlamaktır.

#### Email Model Örneği

**Dosya Yolu**: `backend/src/main/java/com/nyp/backend/model/Email.java`

```java
package com.nyp.backend.model;

import com.nyp.backend.enums.FolderType;
import com.nyp.backend.enums.Priority;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "emails")  // MongoDB collection mapping
public class Email {

    // ============ PRIVATE ALANLAR (Encapsulation) ============
    // Bu alanlara doğrudan erişim YOK! Sadece getter/setter ile
    
    @Id
    private String id;                      // MongoDB ObjectId
    
    private String ownerEmail;              // E-postanın sahibi
    private FolderType folder;              // Hangi klasörde (INBOX, SENT, vb.)
    
    private String from;                    // Gönderen
    private List<String> to = new ArrayList<>();      // Alıcılar
    private List<String> cc = new ArrayList<>();      // Kopya alıcılar
    private List<String> bcc = new ArrayList<>();     // Gizli kopya
    
    private String subject;                 // Konu
    private String body;                    // İçerik
    private Date sentDate = new Date();     // Gönderim tarihi
    private boolean isRead = false;         // Okundu mu?
    private boolean isStarred = false;      // Yıldızlı mı?
    private Priority priority = Priority.NORMAL;  // Öncelik
    
    private List<Attachment> attachments = new ArrayList<>();  // Ekler
    
    // ============ CONSTRUCTOR'LAR ============
    
    public Email() {
        // Varsayılan constructor (Spring Data için gerekli)
    }
    
    public Email(String ownerEmail, FolderType folder, String from, 
                 List<String> to, String subject, String body) {
        this.ownerEmail = ownerEmail;
        this.folder = folder;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.sentDate = new Date();
    }
    
    // ============ GETTER VE SETTER METODLARI (Kontrollü Erişim) ============
    
    public String getId() { 
        return id; 
    }
    
    public void setId(String id) { 
        this.id = id; 
    }
    
    public String getOwnerEmail() { 
        return ownerEmail; 
    }
    
    public void setOwnerEmail(String ownerEmail) { 
        // Doğrulama eklenebilir
        if (ownerEmail != null && !ownerEmail.isEmpty()) {
            this.ownerEmail = ownerEmail; 
        }
    }
    
    public FolderType getFolder() { 
        return folder; 
    }
    
    public void setFolder(FolderType folder) { 
        this.folder = folder; 
    }
    
    public String getFrom() { 
        return from; 
    }
    
    public void setFrom(String from) { 
        this.from = from; 
    }
    
    public List<String> getTo() { 
        return to; 
    }
    
    public void setTo(List<String> to) { 
        this.to = to; 
    }
    
    public String getSubject() { 
        return subject; 
    }
    
    public void setSubject(String subject) { 
        this.subject = subject; 
    }
    
    public String getBody() { 
        return body; 
    }
    
    public void setBody(String body) { 
        this.body = body; 
    }
    
    public Date getSentDate() { 
        return sentDate; 
    }
    
    public void setSentDate(Date sentDate) { 
        this.sentDate = sentDate; 
    }
    
    public boolean isRead() { 
        return isRead; 
    }
    
    public void setRead(boolean read) { 
        isRead = read; 
    }
    
    public boolean isStarred() { 
        return isStarred; 
    }
    
    public void setStarred(boolean starred) { 
        isStarred = starred; 
    }
    
    public Priority getPriority() { 
        return priority; 
    }
    
    public void setPriority(Priority priority) { 
        this.priority = priority; 
    }
    
    public List<Attachment> getAttachments() { 
        return attachments; 
    }
    
    public void setAttachments(List<Attachment> attachments) { 
        this.attachments = attachments; 
    }
    
    // ============ İŞ MANTIĞI METODLARI ============
    
    /**
     * E-postaya ek ekler
     */
    public void addAttachment(Attachment attachment) {
        if (attachment != null) {
            this.attachments.add(attachment);
        }
    }
    
    /**
     * E-postayı yıldızla/yıldızdan çıkar
     */
    public void toggleStar() {
        this.isStarred = !this.isStarred;
    }
    
    /**
     * E-postayı okundu olarak işaretle
     */
    public void markAsRead() {
        this.isRead = true;
    }
    
    @Override
    public String toString() {
        return "Email{" +
                "id='" + id + '\'' +
                ", from='" + from + '\'' +
                ", subject='" + subject + '\'' +
                ", sentDate=" + sentDate +
                ", isRead=" + isRead +
                '}';
    }
}
```

**Encapsulation Avantajları:**
1. **Veri Gizleme**: Private alanlar dışarıdan erişilemez
2. **Doğrulama**: Setter'larda veri kontrolü yapılabilir
3. **Esneklik**: İç yapı değişse bile dış arayüz aynı kalır
4. **Güvenlik**: İstenmeyen değişiklikler önlenir

---

### 5️⃣ INTERFACE (Arayüz)

Interface'ler, sınıfların ne yapması gerektiğini tanımlar (nasıl yapılacağını değil).

#### IMailSender Interface

**Dosya Yolu**: `client/src/main/java/interfaces/IMailSender.java`

```java
package interfaces;

import exceptions.MailSendException;
import models.Email;
import java.util.List;

/**
 * E-posta gönderme yeteneklerini tanımlar
 * Bu interface'i implement eden her sınıf bu metodları sağlamak zorundadır
 */
public interface IMailSender {
    
    /**
     * E-posta gönderir
     * @param email Gönderilecek e-posta
     * @return Başarılıysa true
     * @throws MailSendException Gönderim hatası durumunda
     */
    boolean sendMail(Email email) throws MailSendException;
    
    /**
     * E-postayı yanıtlar
     * @param original Yanıtlanacak e-posta
     * @param replyBody Yanıt içeriği
     * @return Başarılıysa true
     * @throws MailSendException Gönderim hatası durumunda
     */
    boolean replyMail(Email original, String replyBody) throws MailSendException;
    
    /**
     * E-postayı iletir
     * @param original İletilecek e-posta
     * @param recipients Alıcılar
     * @return Başarılıysa true
     * @throws MailSendException Gönderim hatası durumunda
     */
    boolean forwardMail(Email original, List<String> recipients) throws MailSendException;
    
    /**
     * Taslak kaydeder
     * @param draft Taslak e-posta
     * @return Kaydedilen taslağın ID'si
     */
    String saveDraft(Email draft);
}
```

#### ISearchable Interface

**Dosya Yolu**: `client/src/main/java/interfaces/ISearchable.java`

```java
package interfaces;

import models.Email;
import java.util.Date;
import java.util.List;

/**
 * Arama yeteneklerini tanımlar
 * Bu interface'i implement eden sınıflar arama yapabilir
 */
public interface ISearchable {
    
    /**
     * Konu alanında arama yapar
     * @param keyword Aranacak kelime
     * @return Eşleşen e-postalar
     */
    List<Email> searchBySubject(String keyword);
    
    /**
     * Gönderen adresinde arama yapar
     * @param sender Gönderen e-posta adresi
     * @return Eşleşen e-postalar
     */
    List<Email> searchBySender(String sender);
    
    /**
     * Tarih aralığında arama yapar
     * @param startDate Başlangıç tarihi
     * @param endDate Bitiş tarihi
     * @return Eşleşen e-postalar
     */
    List<Email> searchByDateRange(Date startDate, Date endDate);
    
    /**
     * İçerikte arama yapar
     * @param keyword Aranacak kelime
     * @return Eşleşen e-postalar
     */
    List<Email> searchInContent(String keyword);
}
```

**Interface Avantajları:**
1. **Çoklu Kalıtım**: Java'da bir sınıf birden fazla interface implement edebilir
2. **Sözleşme**: Interface bir sözleşmedir, kim implement ederse o metodları sağlamak zorundadır
3. **Loose Coupling**: Sınıflar arası bağımlılık azalır
4. **Test Edilebilirlik**: Mock objeler kolayca oluşturulabilir

---

### 6️⃣ ENUM (Sabit Değerler)

Enum'lar, sabit değer kümelerini tanımlamak için kullanılır.

#### FolderType Enum

**Dosya Yolu**: `backend/src/main/java/com/nyp/backend/enums/FolderType.java`

```java
package com.nyp.backend.enums;

/**
 * E-posta klasör türlerini tanımlar
 * Her e-posta bir klasörde bulunur
 */
public enum FolderType {
    INBOX,      // Gelen Kutusu
    SENT,       // Gönderilenler
    DRAFTS,     // Taslaklar
    TRASH,      // Çöp Kutusu
    SPAM,       // Spam / İstenmeyen
    STARRED,    // Yıldızlı
    CUSTOM      // Kullanıcı tarafından oluşturulan özel klasör
}
```

#### Priority Enum

```java
package com.nyp.backend.enums;

/**
 * E-posta öncelik seviyelerini tanımlar
 */
public enum Priority {
    LOW,        // Düşük öncelik
    NORMAL,     // Normal öncelik (varsayılan)
    HIGH,       // Yüksek öncelik
    URGENT      // Acil
}
```

#### UserRole Enum

```java
package com.nyp.backend.enums;

/**
 * Kullanıcı rollerini tanımlar
 */
public enum UserRole {
    ADMIN,      // Yönetici (tüm yetkiler)
    REGULAR     // Normal kullanıcı (kısıtlı yetkiler)
}
```

**Enum Kullanımı:**

```java
// Email oluştururken
Email email = new Email();
email.setFolder(FolderType.INBOX);
email.setPriority(Priority.HIGH);

// Switch-case ile kullanım
switch (email.getFolder()) {
    case INBOX:
        System.out.println("Gelen kutusunda");
        break;
    case SENT:
        System.out.println("Gönderilenlerde");
        break;
    case TRASH:
        System.out.println("Çöp kutusunda");
        break;
    default:
        System.out.println("Diğer klasör");
}

// Kullanıcı rolü kontrolü
if (user.getRole() == UserRole.ADMIN) {
    // Admin işlemleri
}
```

**Enum Avantajları:**
1. **Tip Güvenliği**: Yanlış değer ataması yapılamaz
2. **Okunabilirlik**: Kod daha anlaşılır
3. **Bakım Kolaylığı**: Yeni değer eklemek kolay
4. **Otomatik Tamamlama**: IDE'de otomatik önerilir

---
```java
// Mailbox, IMailSender interface'ini implement eder
IMailSender sender = new Mailbox(user);
sender.sendMail(email);  // Mailbox'ın sendMail metodu çalışır
```

---

### 4️⃣ KAPSÜLLEME (Encapsulation)

#### Email.java Örneği:
```java
public class Email implements Serializable {
    // Private alanlar - dışarıdan doğrudan erişilemez
    private int id;
    private String from;
    private String subject;
    private String body;
    private boolean isRead;
    private boolean isStarred;
    
    // Getter metodları - kontrollü okuma
    public String getSubject() {
        return subject;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    // Setter metodları - kontrollü yazma
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    // İş mantığı metodları - davranışı kapsüller
    public void markAsRead() {
        this.isRead = true;  // Doğrudan erişim yerine metod
    }
    
    public void toggleStar() {
        this.isStarred = !this.isStarred;
    }
}
```

**Neden Encapsulation?**
- Veri bütünlüğü korunur
- Değişiklikler kontrollü yapılır
- İç yapı değişse bile dış arayüz aynı kalır

---

### 5️⃣ ARAYÜZLER (Interfaces)

#### IMailSender.java
```java
public interface IMailSender {
    boolean sendMail(Email email) throws MailSendException;
    boolean sendBulkMail(List<Email> emails) throws MailSendException;
}
```

#### IMailReceiver.java
```java
public interface IMailReceiver {
    List<Email> receiveMails();
    Email getMailById(int id);
    List<Email> getUnreadMails();
}
```

#### ISearchable.java
```java
public interface ISearchable {
    List<Email> searchBySubject(String keyword);
    List<Email> searchBySender(String sender);
    List<Email> searchByDate(Date date);
    List<Email> searchByContent(String keyword);
}
```

#### IFolderManager.java
```java
public interface IFolderManager {
    void moveToFolder(Email email, Folder folder);
    Folder createFolder(String name);
    boolean deleteFolder(Folder folder);
    List<Folder> getAllFolders();
    void renameFolder(Folder folder, String newName);
}
```

#### Mailbox - Çoklu Interface Implementasyonu
```java
public class Mailbox implements IMailSender, IMailReceiver, 
                                ISearchable, IFolderManager {
    
    // IMailSender metodları
    @Override
    public boolean sendMail(Email email) throws MailSendException { ... }
    
    // IMailReceiver metodları
    @Override
    public List<Email> receiveMails() { ... }
    
    // ISearchable metodları
    @Override
    public List<Email> searchBySubject(String keyword) { ... }
    
    // IFolderManager metodları
    @Override
    public Folder createFolder(String name) { ... }
}
```

**Neden Interface?**
- Java'da çoklu kalıtım yoktur, ama çoklu interface implementasyonu vardır
- Gevşek bağlılık (loose coupling) sağlar
- Test edilebilirliği artırır
- Farklı implementasyonlar aynı arayüzü kullanabilir

---

### 6️⃣ ENUM KULLANIMI

#### FolderType.java
```java
public enum FolderType {
    INBOX("Gelen Kutusu"),
    SENT("Gönderilenler"),
    DRAFTS("Taslaklar"),
    TRASH("Çöp Kutusu"),
    SPAM("Spam"),
    STARRED("Yıldızlı"),
    CUSTOM("Özel Klasör");

    private final String displayName;

    FolderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
```

#### Priority.java
```java
public enum Priority {
    LOW("Düşük", 1),
    NORMAL("Normal", 2),
    HIGH("Yüksek", 3),
    URGENT("Acil", 4);

    private final String displayName;
    private final int level;

    // Constructor ve getter'lar
}
```

#### UserRole.java
```java
public enum UserRole {
    ADMIN("Yönetici"),
    REGULAR("Normal Kullanıcı");
    
    // ...
}
```

**Neden Enum?**
- Sabit değerler tip güvenli olur
- IDE desteği (otomatik tamamlama)
- switch-case ile kullanım kolaylığı
- Ek özellikler eklenebilir (displayName gibi)

---

### 7️⃣ BİLEŞİM (Composition) - HAS-A İlişkisi

```
Mailbox HAS-A User
Mailbox HAS-A List<Folder>
Folder HAS-A List<Email>
Email HAS-A List<Attachment>
Email HAS-A Priority
User HAS-A UserRole
```

#### Mailbox.java
```java
public class Mailbox {
    private User user;           // HAS-A User
    private List<Folder> folders; // HAS-A Folders
    
    public Mailbox(User user) {
        this.user = user;
        this.folders = new ArrayList<>();
        initializeDefaultFolders();
    }
}
```

#### Folder.java
```java
public class Folder {
    private String name;
    private FolderType type;
    private List<Email> emails;  // HAS-A Emails
    
    public void addEmail(Email email) {
        emails.add(email);
    }
}
```

#### Email.java
```java
public class Email {
    private List<Attachment> attachments;  // HAS-A Attachments
    private Priority priority;             // HAS-A Priority
    
    public void addAttachment(Attachment att) {
        attachments.add(att);
    }
}
```

---

### 8️⃣ ÖZEL İSTİSNA SINIFLARI (Custom Exceptions)

#### Hiyerarşi:
```
Exception (Java built-in)
    └── EmailException (Temel)
            ├── InvalidEmailException
            ├── MailSendException
            └── AttachmentException
```

#### EmailException.java
```java
public class EmailException extends Exception {
    public EmailException(String message) {
        super(message);
    }
    
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

#### InvalidEmailException.java
```java
public class InvalidEmailException extends EmailException {
    private final String invalidEmail;
    
    public InvalidEmailException(String invalidEmail) {
        super("Geçersiz e-posta adresi: " + invalidEmail);
        this.invalidEmail = invalidEmail;
    }
    
    public String getInvalidEmail() {
        return invalidEmail;
    }
}
```

#### Kullanım:
```java
public boolean sendMail(Email email) throws MailSendException {
    if (email.getTo().isEmpty()) {
        throw new MailSendException("", "Alıcı adresi belirtilmedi!");
    }
    // ...
}
```

---

### 9️⃣ SINGLETON TASARIM DESENİ

#### UserService.java
```java
public class UserService {
    // Tek instance
    private static UserService instance;
    private List<User> users;
    private User currentUser;
    
    // Private constructor - dışarıdan new yapılamaz
    private UserService() {
        users = new ArrayList<>();
        initializeDefaultUsers();
    }
    
    // Tek erişim noktası
    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
}
```

#### Kullanım:
```java
// Her zaman aynı instance döner
UserService service1 = UserService.getInstance();
UserService service2 = UserService.getInstance();

System.out.println(service1 == service2);  // true
```

**Neden Singleton?**
- Tek kullanıcı yöneticisi gerekli
- Global erişim noktası sağlar
- Kaynak tasarrufu

---

### 🔟 STATIC METODLAR VE UTILITY SINIFLAR

#### EmailValidator.java
```java
public class EmailValidator {
    private static final String EMAIL_REGEX = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@...";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    // Static metodlar - nesne oluşturmadan çağrılır
    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static String getDomain(String email) {
        return email.substring(email.indexOf('@') + 1);
    }
}
```

#### DateFormatter.java
```java
public class DateFormatter {
    private static final SimpleDateFormat FULL_FORMAT = 
        new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    
    public static String formatFull(Date date) {
        return FULL_FORMAT.format(date);
    }
    
    public static String formatSmart(Date date) {
        // Bugün ise saat, bu hafta ise gün adı...
    }
    
    public static String getTimeDifference(Date date) {
        // "5 dakika önce", "2 saat önce" gibi
    }
}
```

#### Kullanım:
```java
// Nesne oluşturmadan direkt çağrı
boolean valid = EmailValidator.isValidEmail("test@mail.com");
String formatted = DateFormatter.formatFull(new Date());
```

---

## 📊 Sınıf İlişkileri Diyagramı

```
┌─────────────────────────────────────────────────────────────────────┐
│                           INTERFACES                                 │
├─────────────────────────────────────────────────────────────────────┤
│  IMailSender    IMailReceiver    ISearchable    IFolderManager      │
│       │              │               │               │              │
│       └──────────────┴───────────────┴───────────────┘              │
│                              │                                       │
│                       implements                                     │
│                              ▼                                       │
│  ┌───────────────────────────────────────────────────────────┐      │
│  │                        Mailbox                             │      │
│  │  - user: User                                              │      │
│  │  - folders: List<Folder>                                   │      │
│  │  + sendMail(), receiveMails(), searchBySubject()...        │      │
│  └───────────────────────────────────────────────────────────┘      │
│                              │                                       │
│                           has-a                                      │
│                              ▼                                       │
│  ┌───────────────────────────────────────────────────────────┐      │
│  │                    <<abstract>> User                       │      │
│  │  # id, email, password, fullName                           │      │
│  │  + login(), logout()                                       │      │
│  │  + abstract getRole(), getPermissions()                    │      │
│  └───────────────────────────────────────────────────────────┘      │
│                    ▲                    ▲                            │
│                    │                    │                            │
│              extends                extends                          │
│                    │                    │                            │
│  ┌─────────────────┴───┐    ┌─────────┴─────────────────┐           │
│  │     AdminUser       │    │      RegularUser          │           │
│  │  - department       │    │  - storageUsed            │           │
│  │  - accessLevel      │    │  - isPremium              │           │
│  │  + banUser()        │    │  + upgradeAccount()       │           │
│  └─────────────────────┘    └───────────────────────────┘           │
│                                                                      │
├─────────────────────────────────────────────────────────────────────┤
│  ┌──────────────┐         ┌─────────────────┐                       │
│  │    Email     │◄────────│   DraftEmail    │  (extends)            │
│  │  - subject   │         │  - lastModified │                       │
│  │  - body      │         │  - autoSaved    │                       │
│  │  - isRead    │         │  + autoSave()   │                       │
│  └──────────────┘         └─────────────────┘                       │
│         │                                                            │
│      has-a                                                           │
│         ▼                                                            │
│  ┌──────────────┐                                                   │
│  │  Attachment  │                                                   │
│  │  - fileName  │                                                   │
│  │  - fileSize  │                                                   │
│  └──────────────┘                                                   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🖥️ Terminal Arayüzü Özellikleri

### Giriş Menüsü
- Kullanıcı girişi
- Yeni kayıt
- Kayıtlı kullanıcıları görüntüleme

### Ana Menü
- 📥 Gelen Kutusu (okunmamış sayısı ile)
- ✏️ Yeni E-posta Yazma
- 📤 Gönderilenler
- 📝 Taslaklar
- ⭐ Yıldızlı
- 🗑️ Çöp Kutusu
- 🔍 E-posta Arama
- 📁 Klasör Yönetimi
- 👤 Profil
- ⚙️ Admin Paneli (sadece adminler için)

### E-posta İşlemleri
- E-posta okuma
- Yanıtlama
- İletme
- Yıldızlama
- Silme (çöp kutusuna taşıma)

### Admin Paneli
- Tüm kullanıcıları listeleme
- Kullanıcı yasaklama/yasak kaldırma
- Sistem istatistikleri

---

## 🧪 Örnek Çalışma Çıktısı

```
╔══════════════════════════════════════════════════════════╗
║         JAVA E-POSTA SİSTEMİ - NYP PROJESİ               ║
╚══════════════════════════════════════════════════════════╝

1️⃣  ABSTRACT CLASS & INHERITANCE
Admin Kullanıcı: Ali Yönetici
  → Rol: Yönetici
  → Mesaj: Hoş geldiniz Yönetici Ali Yönetici! (Seviye: 3)

2️⃣  POLYMORPHISM
Ali Yönetici yetkileri:
  • SEND_EMAIL
  • VIEW_ALL_USERS
  • BAN_USER
  ... ve 13 yetki daha

3️⃣  ENCAPSULATION
E-posta oluşturuldu:
  → Okundu mu: Hayır
  → markAsRead() sonrası: Evet

...
```

---

## 📝 Varsayılan Kullanıcılar

| E-posta | Şifre | Rol | Açıklama |
|---------|-------|-----|----------|
| admin@mail.com | admin123 | Admin | Seviye 3 yönetici |
| ahmet@mail.com | 123456 | Regular | Normal kullanıcı |
| ayse@mail.com | 123456 | Regular | Normal kullanıcı |
| mehmet@mail.com | 123456 | Regular | Normal kullanıcı |

---

### 7️⃣ COMPOSITION (Bileşim - HAS-A İlişkisi)

Composition, bir sınıfın başka sınıfları içermesi durumudur (has-a ilişkisi).

#### Örnek: Mailbox HAS-A User, HAS-A Folders

```java
public class Mailbox {
    // Mailbox bir User'a SAHİPTİR (HAS-A)
    private User user;
    
    // Mailbox birden fazla Folder'a SAHİPTİR (HAS-MANY)
    private List<Folder> folders;
    private Folder inbox;
    private Folder sent;
    private Folder drafts;
    private Folder trash;
    private Folder starred;
    
    public Mailbox(User user) {
        this.user = user;  // Composition
        this.folders = new ArrayList<>();
        initializeDefaultFolders();
    }
}
```

#### Örnek: Email HAS-A Attachment

```java
public class Email {
    // Email birden fazla Attachment'a SAHİPTİR (HAS-MANY)
    private List<Attachment> attachments = new ArrayList<>();
    
    // Ek ekleme metodu
    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
    }
}

public class Attachment {
    private String fileName;
    private long fileSize;
    private String contentType;
    private byte[] data;
}
```

**Composition vs Inheritance:**
- **Inheritance (IS-A)**: AdminUser **IS-A** User
- **Composition (HAS-A)**: Email **HAS-A** Attachment
- Composition daha esnek, loosely coupled

---

### 8️⃣ CUSTOM EXCEPTION (Özel İstisnalar)

Custom exception'lar, özel hata durumlarını ele almak için kullanılır.

#### Exception Hiyerarşisi

```
    Exception
        │
    EmailException (abstract)
        ├── InvalidEmailException
        ├── MailSendException
        └── AttachmentException
```

#### EmailException.java - Base Exception

**Dosya Yolu**: `client/src/main/java/exceptions/EmailException.java`

```java
package exceptions;

/**
 * Tüm e-posta ile ilgili hataların base sınıfı
 * Abstract olarak tanımlandı, doğrudan kullanılmaz
 */
public abstract class EmailException extends Exception {
    
    private int errorCode;
    private String userMessage;
    
    public EmailException(String message) {
        super(message);
    }
    
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EmailException(String message, int errorCode, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public String getUserMessage() {
        return userMessage != null ? userMessage : getMessage();
    }
    
    /**
     * Alt sınıflar bu metodu override edip özel log yazabilir
     */
    public abstract void logError();
}
```

#### MailSendException.java - Gönderim Hatası

```java
package exceptions;

import java.util.Date;

/**
 * E-posta gönderimi sırasında oluşan hatalar
 */
public class MailSendException extends EmailException {
    
    private String recipientEmail;
    private Date attemptTime;
    
    public MailSendException(String message) {
        super(message);
        this.attemptTime = new Date();
    }
    
    public MailSendException(String message, String recipientEmail) {
        super(message, 1001, "E-posta gönderilemedi. Lütfen tekrar deneyin.");
        this.recipientEmail = recipientEmail;
        this.attemptTime = new Date();
    }
    
    public MailSendException(String message, Throwable cause) {
        super(message, cause);
        this.attemptTime = new Date();
    }
    
    @Override
    public void logError() {
        System.err.println("[MAIL_SEND_ERROR] " + getMessage());
        System.err.println("Recipient: " + recipientEmail);
        System.err.println("Time: " + attemptTime);
    }
    
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public Date getAttemptTime() {
        return attemptTime;
    }
}
```

#### InvalidEmailException.java - Geçersiz E-posta

```java
package exceptions;

/**
 * Geçersiz e-posta adresi hatası
 */
public class InvalidEmailException extends EmailException {
    
    private String invalidEmail;
    
    public InvalidEmailException(String invalidEmail) {
        super("Geçersiz e-posta adresi: " + invalidEmail, 
              1002, 
              "E-posta adresi geçerli değil.");
        this.invalidEmail = invalidEmail;
    }
    
    @Override
    public void logError() {
        System.err.println("[INVALID_EMAIL_ERROR] " + getMessage());
        System.err.println("Invalid Email: " + invalidEmail);
    }
    
    public String getInvalidEmail() {
        return invalidEmail;
    }
}
```

#### Custom Exception Kullanımı

```java
// E-posta gönderirken
try {
    mailbox.sendMail(email);
    System.out.println("E-posta başarıyla gönderildi!");
    
} catch (MailSendException e) {
    e.logError();  // Hatayı logla
    
    // Kullanıcıya özel mesaj göster
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Gönderim Hatası");
    alert.setContentText(e.getUserMessage());
    alert.showAndWait();
    
    // Tekrar deneme seçeneği
    if (e.getErrorCode() == 1001) {
        // Retry logic
    }
    
} catch (InvalidEmailException e) {
    e.logError();
    System.out.println("Geçersiz e-posta: " + e.getInvalidEmail());
}
```

**Custom Exception Avantajları:**
1. **Özel Hata Mesajları**: Kullanıcıya anlamlı mesajlar
2. **Hata Kodu**: Kategorilendirme ve izleme
3. **Detaylı Loglama**: Özel log metodları
4. **Tip Güvenliği**: Farklı hata türlerini ayırt etme

---

### 9️⃣ SINGLETON PATTERN (Tekil Nesne Deseni)

Singleton pattern, bir sınıftan sadece bir nesne oluşturulmasını garanti eder.

#### UserService.java - Singleton Örneği

**Dosya Yolu**: `client/src/main/java/services/UserService.java`

```java
package services;

import models.User;
import models.AdminUser;
import models.RegularUser;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton pattern ile kullanıcı yönetim servisi
 * Tüm uygulama boyunca tek bir UserService nesnesi kullanılır
 */
public class UserService {
    
    // 1. PRIVATE STATIC INSTANCE (Tek nesne)
    private static UserService instance = null;
    
    // 2. PRIVATE CONSTRUCTOR (Dışarıdan new yapılamaz!)
    private UserService() {
        // Constructor private, dışarıdan nesne oluşturulamaz
        System.out.println("UserService instance created (Singleton)");
    }
    
    // 3. PUBLIC STATIC GET_INSTANCE METHOD (Tek giriş noktası)
    public static UserService getInstance() {
        // Lazy initialization: İlk kullanımda oluştur
        if (instance == null) {
            synchronized (UserService.class) {
                // Double-checked locking (Thread-safe)
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }
    
    // Kullanıcı veritabanı (gerçek projede DB olurdu)
    private List<User> users = new ArrayList<>();
    private User currentUser = null;
    
    /**
     * Kullanıcı kaydı
     */
    public User register(String email, String password, String fullName, String role) {
        User user;
        if ("ADMIN".equals(role)) {
            user = new AdminUser(email, password, fullName);
        } else {
            user = new RegularUser(email, password, fullName);
        }
        users.add(user);
        return user;
    }
    
    /**
     * Kullanıcı girişi
     */
    public boolean login(String email, String password) {
        for (User user : users) {
            if (user.login(email, password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }
    
    /**
     * Mevcut kullanıcıyı döndür
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Çıkış yap
     */
    public void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        }
    }
    
    /**
     * Tüm kullanıcıları listele (Admin only)
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
```

#### Singleton Kullanımı

```java
// Her yerden aynı instance'a erişim
public class Main {
    public static void main(String[] args) {
        // Tek UserService instance'ı al
        UserService userService = UserService.getInstance();
        
        // Kayıt ol
        userService.register("ali@mail.com", "123", "Ali", "REGULAR");
        
        // Giriş yap
        userService.login("ali@mail.com", "123");
        
        // Başka bir yerden aynı instance
        UserService sameService = UserService.getInstance();
        
        // İkisi de aynı nesne!
        System.out.println(userService == sameService);  // true
        
        User user = sameService.getCurrentUser();  // Ali
    }
}
```

**Singleton Avantajları:**
1. **Tek Nesne**: Gereksiz nesne oluşumu önlenir
2. **Global Erişim**: Her yerden erişilebilir
3. **Kaynak Yönetimi**: Veritabanı, dosya gibi kaynaklar için ideal
4. **Thread-Safe**: Synchronized kullanımıyla güvenli

---

### 🔟 STATIC METHODS (Statik Metodlar)

Static metodlar, nesne oluşturmadan çağrılabilen metodlardır.

#### EmailValidator.java - Static Utility

**Dosya Yolu**: `client/src/main/java/utils/EmailValidator.java`

```java
package utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * E-posta doğrulama için statik utility metodları
 * Nesne oluşturmaya gerek yok, doğrudan sınıf üzerinden çağrılır
 */
public class EmailValidator {
    
    // Private constructor - Bu sınıftan nesne oluşturulmayı engelle
    private EmailValidator() {
        throw new AssertionError("Utility class cannot be instantiated");
    }
    
    // E-posta regex pattern'i
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    /**
     * E-posta adresinin geçerli olup olmadığını kontrol eder
     * @param email Kontrol edilecek e-posta
     * @return Geçerliyse true
     */
    public static boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    /**
     * E-posta domain'ini döndürür
     * @param email E-posta adresi
     * @return Domain kısmı (örn: gmail.com)
     */
    public static String getDomain(String email) {
        if (!isValid(email)) {
            return null;
        }
        return email.substring(email.indexOf('@') + 1);
    }
    
    /**
     * E-posta username'ini döndürür
     * @param email E-posta adresi
     * @return Username kısmı (@ öncesi)
     */
    public static String getUsername(String email) {
        if (!isValid(email)) {
            return null;
        }
        return email.substring(0, email.indexOf('@'));
    }
    
    /**
     * Birden fazla e-posta adresini doğrular
     * @param emails E-posta listesi
     * @return Tümü geçerliyse true
     */
    public static boolean validateAll(String... emails) {
        for (String email : emails) {
            if (!isValid(email)) {
                return false;
            }
        }
        return true;
    }
}
```

#### DateFormatter.java - Static Utility

```java
package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tarih formatlama için statik utility metodları
 */
public class DateFormatter {
    
    private DateFormatter() {
        throw new AssertionError("Utility class");
    }
    
    private static final SimpleDateFormat FULL_FORMAT = 
        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    private static final SimpleDateFormat SHORT_FORMAT = 
        new SimpleDateFormat("dd/MM/yyyy");
    
    private static final SimpleDateFormat TIME_FORMAT = 
        new SimpleDateFormat("HH:mm");
    
    /**
     * Tam tarih formatı (gg/aa/yyyy ss:dd:ss)
     */
    public static String formatFull(Date date) {
        return date != null ? FULL_FORMAT.format(date) : "";
    }
    
    /**
     * Kısa tarih formatı (gg/aa/yyyy)
     */
    public static String formatShort(Date date) {
        return date != null ? SHORT_FORMAT.format(date) : "";
    }
    
    /**
     * Sadece saat formatı (ss:dd)
     */
    public static String formatTime(Date date) {
        return date != null ? TIME_FORMAT.format(date) : "";
    }
    
    /**
     * Bugün gelen e-postalar için özel format
     * Bugün ise: "HH:mm", değilse: "dd/MM/yyyy"
     */
    public static String formatSmart(Date date) {
        if (date == null) return "";
        
        Date now = new Date();
        if (isSameDay(date, now)) {
            return formatTime(date);
        } else {
            return formatShort(date);
        }
    }
    
    private static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
}
```

#### Static Method Kullanımı

```java
// Nesne oluşturmaya gerek yok, doğrudan sınıf adıyla çağır
String email = "test@mail.com";

// E-posta doğrulama
if (EmailValidator.isValid(email)) {
    System.out.println("Geçerli e-posta");
    
    // Domain ve username al
    String domain = EmailValidator.getDomain(email);    // "mail.com"
    String username = EmailValidator.getUsername(email); // "test"
}

// Tarih formatlama
Date now = new Date();
String full = DateFormatter.formatFull(now);      // "04/01/2026 14:30:25"
String short = DateFormatter.formatShort(now);    // "04/01/2026"
String smart = DateFormatter.formatSmart(now);    // "14:30" (bugün için)
```

**Static Method Avantajları:**
1. **Performans**: Nesne oluşturmaya gerek yok
2. **Kullanım Kolaylığı**: Doğrudan sınıf adıyla çağrılır
3. **Utility Fonksiyonlar**: Yardımcı metodlar için ideal
4. **Memory Efficient**: Tek kopya, tüm uygulama için

---

## 🌐 REST API ve Backend Servisleri

### Spring Boot Backend Mimarisi

Backend, **Spring Boot** framework'ü ile geliştirilmiş ve **3-Tier Architecture** kullanır:

1. **Controller Layer** - HTTP isteklerini karşılar
2. **Service Layer** - İş mantığını yönetir
3. **Repository Layer** - Veritabanı işlemlerini yapar

#### EmailController.java - REST API Endpoints

**Dosya Yolu**: `backend/src/main/java/com/nyp/backend/controller/EmailController.java`

```java
package com.nyp.backend.controller;

import com.nyp.backend.enums.FolderType;
import com.nyp.backend.model.Email;
import com.nyp.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")  // JavaFX client'tan erişime izin ver
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * E-posta gönderme endpoint'i
     * POST /api/emails/send
     */
    @PostMapping("/emails/send")
    public ResponseEntity<?> sendEmail(@RequestBody Email email) {
        try {
            emailService.sendEmail(email);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error sending email: " + e.getMessage());
        }
    }

    /**
     * Belirli klasördeki e-postaları getir
     * GET /api/emails?email=user@mail.com&folder=INBOX
     */
    @GetMapping("/emails")
    public ResponseEntity<List<Email>> getEmails(
            @RequestParam String email, 
            @RequestParam FolderType folder) {
        
        System.out.println("Getting emails for: " + email + ", folder: " + folder);
        List<Email> emails = emailService.getEmails(email, folder);
        System.out.println("Found " + emails.size() + " emails");
        return ResponseEntity.ok(emails);
    }

    /**
     * ID'ye göre tek e-posta getir
     * GET /api/emails/{id}
     */
    @GetMapping("/emails/{id}")
    public ResponseEntity<Email> getEmail(@PathVariable String id) {
        Email e = emailService.getEmail(id);
        if (e != null)
            return ResponseEntity.ok(e);
        return ResponseEntity.notFound().build();
    }

    /**
     * E-posta sil
     * DELETE /api/emails/{id}
     */
    @DeleteMapping("/emails/{id}")
    public ResponseEntity<?> deleteEmail(@PathVariable String id) {
        emailService.deleteEmail(id);
        return ResponseEntity.ok().build();
    }

    /**
     * E-postayı okundu olarak işaretle
     * PUT /api/emails/{id}/read
     */
    @PutMapping("/emails/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        emailService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * E-posta arama
     * GET /api/emails/search?email=user@mail.com&query=toplantı&type=SUBJECT
     */
    @GetMapping("/emails/search")
    public ResponseEntity<List<Email>> searchEmails(
            @RequestParam String email,
            @RequestParam String query,
            @RequestParam(defaultValue = "SUBJECT") String type) {
        return ResponseEntity.ok(emailService.searchEmails(email, query, type));
    }

    /**
     * Taslak kaydetme
     * POST /api/emails/draft
     */
    @PostMapping("/emails/draft")
    public ResponseEntity<String> saveDraft(@RequestBody Email email) {
        try {
            email.setFolder(FolderType.DRAFTS);
            email.setOwnerEmail(email.getFrom());
            email.setRead(true);  // Taslaklar her zaman "okundu"
            
            String savedId = emailService.saveDraftAndReturnId(email);
            System.out.println("Draft saved with ID: " + savedId);
            
            return ResponseEntity.ok(savedId);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Error saving draft: " + e.getMessage());
        }
    }

    /**
     * E-postayı yıldızla
     * PUT /api/emails/{id}/star
     */
    @PutMapping("/emails/{id}/star")
    public ResponseEntity<String> starEmail(@PathVariable String id) {
        try {
            boolean success = emailService.starEmail(id);
            if (success) {
                return ResponseEntity.ok("Email starred");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error starring email");
        }
    }

    /**
     * E-postayı yıldızdan çıkar
     * PUT /api/emails/{id}/unstar
     */
    @PutMapping("/emails/{id}/unstar")
    public ResponseEntity<String> unstarEmail(@PathVariable String id) {
        try {
            boolean success = emailService.unstarEmail(id);
            if (success) {
                return ResponseEntity.ok("Email unstarred");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error unstarring email");
        }
    }
}
```

#### EmailService.java - İş Mantığı

**Dosya Yolu**: `backend/src/main/java/com/nyp/backend/service/EmailService.java`

```java
package com.nyp.backend.service;

import com.nyp.backend.enums.FolderType;
import com.nyp.backend.model.Email;
import com.nyp.backend.repository.EmailRepository;
import com.nyp.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmailService {

  @Autowired
  private EmailRepository emailRepository;

  @Autowired
  private UserRepository userRepository;

  /**
   * E-posta gönderme iş mantığı
   * 1. Gönderene SENT klasöründe kaydet
   * 2. Her alıcıya INBOX klasöründe kopya oluştur
   * 3. CC ve BCC için aynısını yap
   */
  public void sendEmail(Email email) {
    // 1. Gönderenin SENT klasörüne kaydet
    email.setFolder(FolderType.SENT);
    email.setOwnerEmail(email.getFrom());
    email.setRead(true);  // Gönderen için "okundu"
    emailRepository.save(email);

    // 2. TO: Alıcılara dağıt
    for (String recipient : email.getTo()) {
      if (userRepository.existsByEmail(recipient)) {
        Email copy = copyEmailForRecipient(email, recipient, FolderType.INBOX);
        emailRepository.save(copy);
      }
    }

    // 3. CC: Kopya alıcılar
    for (String recipient : email.getCc()) {
      if (userRepository.existsByEmail(recipient)) {
        Email copy = copyEmailForRecipient(email, recipient, FolderType.INBOX);
        emailRepository.save(copy);
      }
    }

    // 4. BCC: Gizli kopya alıcılar
    for (String recipient : email.getBcc()) {
      if (userRepository.existsByEmail(recipient)) {
        Email copy = copyEmailForRecipient(email, recipient, FolderType.INBOX);
        emailRepository.save(copy);
      }
    }
  }

  /**
   * Alıcı için e-posta kopyası oluştur
   */
  private Email copyEmailForRecipient(Email original, String owner, FolderType folder) {
    Email copy = new Email();
    copy.setOwnerEmail(owner);           // Bu kopyanın sahibi
    copy.setFolder(folder);              // Klasör (INBOX vb.)
    copy.setFrom(original.getFrom());
    copy.setTo(original.getTo());
    copy.setCc(original.getCc());
    copy.setSubject(original.getSubject());
    copy.setBody(original.getBody());
    copy.setSentDate(original.getSentDate());
    copy.setAttachments(original.getAttachments());
    copy.setPriority(original.getPriority());
    copy.setRead(false);                 // Alıcı için "okunmadı"
    return copy;
  }

  /**
   * Kullanıcının belirli klasöründeki e-postaları getir
   */
  public List<Email> getEmails(String email, FolderType folder) {
    // Yıldızlı klasörü için özel mantık
    if (folder == FolderType.STARRED) {
      return emailRepository.findByOwnerEmailAndIsStarredTrue(email);
    }
    return emailRepository.findByOwnerEmailAndFolder(email, folder);
  }

  /**
   * ID'ye göre e-posta getir
   */
  public Email getEmail(String id) {
    return emailRepository.findById(id).orElse(null);
  }

  /**
   * E-posta silme mantığı
   * - Çöp kutusundaysa kalıcı sil
   * - Değilse çöp kutusuna taşı
   */
  public void deleteEmail(String id) {
    Optional<Email> emailOpt = emailRepository.findById(id);
    if (emailOpt.isPresent()) {
      Email email = emailOpt.get();
      
      if (email.getFolder() == FolderType.TRASH) {
        // Çöp kutusunda ise kalıcı sil
        emailRepository.delete(email);
      } else {
        // Değilse çöp kutusuna taşı
        email.setFolder(FolderType.TRASH);
        emailRepository.save(email);
      }
    }
  }

  /**
   * E-postayı okundu olarak işaretle
   */
  public void markAsRead(String id) {
    Optional<Email> emailOpt = emailRepository.findById(id);
    if (emailOpt.isPresent()) {
      Email email = emailOpt.get();
      email.setRead(true);
      emailRepository.save(email);
    }
  }

  /**
   * E-posta arama
   */
  public List<Email> searchEmails(String email, String query, String type) {
    switch (type.toUpperCase()) {
      case "SUBJECT":
        return emailRepository.findByOwnerEmailAndSubjectContainingIgnoreCase(email, query);
      case "SENDER":
        return emailRepository.findByOwnerEmailAndFromContainingIgnoreCase(email, query);
      case "BODY":
        return emailRepository.findByOwnerEmailAndBodyContainingIgnoreCase(email, query);
      default:
        return List.of();
    }
  }

  /**
   * E-postayı yıldızla
   */
  public boolean starEmail(String id) {
    Optional<Email> emailOpt = emailRepository.findById(id);
    if (emailOpt.isPresent()) {
      Email email = emailOpt.get();
      email.setStarred(true);
      emailRepository.save(email);
      return true;
    }
    return false;
  }

  /**
   * E-postayı yıldızdan çıkar
   */
  public boolean unstarEmail(String id) {
    Optional<Email> emailOpt = emailRepository.findById(id);
    if (emailOpt.isPresent()) {
      Email email = emailOpt.get();
      email.setStarred(false);
      emailRepository.save(email);
      return true;
    }
    return false;
  }

  /**
   * Taslak kaydet ve ID'sini döndür
   */
  public String saveDraftAndReturnId(Email draft) {
    Email saved = emailRepository.save(draft);
    return saved.getId();
  }
}
```

---

## 🖥️ JavaFX Client ve UI

### ApiClient.java - HTTP İstemcisi

**Dosya Yolu**: `client/src/main/java/services/ApiClient.java`

```java
package services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Backend REST API ile iletişim kuran HTTP client
 * Java 11'in HttpClient API'sini kullanır
 */
public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient client = HttpClient.newHttpClient();

    /**
     * POST isteği gönderir
     */
    public static String post(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();
        return send(request);
    }

    /**
     * GET isteği gönderir
     */
    public static String get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET()
                .build();
        return send(request);
    }

    /**
     * PUT isteği gönderir
     */
    public static String put(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();
        return send(request);
    }

    /**
     * DELETE isteği gönderir
     */
    public static String delete(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .DELETE()
                .build();
        return send(request);
    }

    /**
     * HTTP isteğini gönder ve yanıtı döndür
     */
    private static String send(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(
            request, 
            HttpResponse.BodyHandlers.ofString()
        );
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception("API Error: " + response.statusCode() + 
                              " " + response.body());
        }
    }
}
```

---

## 📊 NYP Kavramları Özet Tablosu

| # | Kavram | Kullanım Yeri | Dosya | Açıklama |
|---|--------|---------------|-------|----------|
| 1 | **Abstract Class** | Soyut kullanıcı, soyut exception | `User.java`, `EmailException.java` | IS-A hiyerarşisi için base class |
| 2 | **Inheritance** | Kullanıcı/E-posta/Exception hiyerarşisi | `AdminUser`, `RegularUser`, `DraftEmail` | Kod tekrarını önler, extends kullanımı |
| 3 | **Polymorphism** | Farklı davranışlar | `getRole()`, `getPermissions()`, Interface'ler | Runtime polymorphism |
| 4 | **Encapsulation** | Private alanlar + getter/setter | Tüm model sınıfları | Veri gizleme ve kontrollü erişim |
| 5 | **Interface** | Çoklu davranış tanımı | `IMailSender`, `IMailReceiver`, `ISearchable` | Sözleşme tanımı, çoklu kalıtım |
| 6 | **Enum** | Sabit değer kümeleri | `FolderType`, `Priority`, `UserRole` | Tip güvenliği |
| 7 | **Composition** | HAS-A ilişkisi | `Mailbox`-`User`, `Email`-`Attachment` | Loosely coupled yapı |
| 8 | **Custom Exception** | Özel hata yönetimi | `MailSendException`, `InvalidEmailException` | Anlamlı hata mesajları |
| 9 | **Singleton Pattern** | Tek nesne | `UserService` | Global state yönetimi |
| 10 | **Static Methods** | Utility fonksiyonlar | `EmailValidator`, `DateFormatter` | Nesnesiz kullanım |
| 11 | **REST API** | Client-Server iletişimi | `EmailController`, `AuthController` | HTTP endpoints |
| 12 | **Repository Pattern** | Veri erişimi | `EmailRepository`, `UserRepository` | Spring Data MongoDB |
| 13 | **Service Layer** | İş mantığı | `EmailService`, `AuthService` | Business logic |
| 14 | **MVC Pattern** | Mimari | Controller-Model-View ayrımı | 3-Tier Architecture |
| 15 | **Dependency Injection** | Bağımlılık yönetimi | `@Autowired` kullanımı | Spring IoC Container |

---

## 🚀 Derleme ve Çalıştırma

### Backend (Spring Boot)

```bash
# Backend dizinine git
cd backend

# Maven ile derle
mvn clean install

# Spring Boot uygulamasını çalıştır
mvn spring-boot:run

# Veya JAR dosyasından çalıştır
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

**Backend çalışınca:**
- Port: `8080`
- API Base URL: `http://localhost:8080/api`
- MongoDB Atlas'a bağlanır

### Client (JavaFX)

```bash
# Client dizinine git
cd client

# Maven ile derle
mvn clean install

# JavaFX uygulamasını çalıştır
mvn javafx:run
```

**Veya IntelliJ IDEA'dan:**
1. `client/src/main/java/gui/App.java` dosyasını aç
2. `Run` butonuna tıkla

---

## 🖥️ Uygulama Özellikleri

### Kullanıcı Özellikleri
- ✅ Kullanıcı kaydı (Register)
- ✅ Kullanıcı girişi (Login)
- ✅ Çıkış yapma (Logout)
- ✅ Admin ve Normal kullanıcı rolleri
- ✅ Profil yönetimi

### E-posta Özellikleri
- ✅ E-posta gönderme (Send)
- ✅ E-posta alma (Receive)
- ✅ E-posta okuma (Read)
- ✅ Yanıtlama (Reply)
- ✅ İletme (Forward)
- ✅ Taslak kaydetme (Draft)
- ✅ E-posta silme (çöp kutusuna taşıma)
- ✅ Kalıcı silme (çöp kutusundan)
- ✅ Yıldızlama (Star/Unstar)
- ✅ Okundu işaretleme
- ✅ Öncelik belirleme (LOW, NORMAL, HIGH, URGENT)
- ✅ Dosya ekleri (Attachments)
- ✅ CC ve BCC desteği

### Klasör Yönetimi
- ✅ Gelen Kutusu (Inbox)
- ✅ Gönderilenler (Sent)
- ✅ Taslaklar (Drafts)
- ✅ Yıldızlı (Starred)
- ✅ Çöp Kutusu (Trash)
- ✅ Spam
- ✅ Özel klasörler

### Arama Özellikleri
- ✅ Konu'da arama (Subject)
- ✅ Gönderen'de arama (Sender)
- ✅ İçerik'te arama (Body)
- ✅ Tarih aralığı ile arama

### UI/UX Özellikleri
- ✅ Modern, koyu tema (Dark mode)
- ✅ Otomatik yenileme (30 saniyede bir)
- ✅ Responsive tasarım
- ✅ WebView ile HTML e-posta içeriği
- ✅ ListView ile e-posta listesi
- ✅ Detay paneli
- ✅ Alert ve bildirimler

---

## 📦 Kullanılan Teknolojiler ve Kütüphaneler

### Backend
| Teknoloji | Versiyon | Kullanım Amacı |
|-----------|----------|----------------|
| Java | 21 | Programlama dili |
| Spring Boot | 3.3.0 | Backend framework |
| Spring Data MongoDB | - | MongoDB ORM |
| Maven | - | Build tool |
| MongoDB Atlas | - | Cloud veritabanı |

### Client
| Teknoloji | Versiyon | Kullanım Amacı |
|-----------|----------|----------------|
| Java | 21 | Programlama dili |
| JavaFX | 21 | GUI framework |
| Gson | 2.10.1 | JSON parsing |
| HttpClient | Java 11+ | REST API iletişimi |
| Maven | - | Build tool |

---

## 📊 Proje İstatistikleri

| Metrik | Backend | Client | Toplam |
|--------|---------|--------|--------|
| Java Dosyası | 12 | 28 | 40+ |
| Sınıf Sayısı | 12 | 28 | 40+ |
| Interface | - | 4 | 4 |
| Enum | 3 | 3 | 3 (her katman) |
| Abstract Class | - | 2 | 2 |
| Exception Sınıfı | - | 4 | 4 |
| Controller | 2 | - | 2 |
| Service | 2 | 3 | 5 |
| Repository | 2 | - | 2 |
| GUI Sınıfı | - | 4 | 4 |
| Tahmini Kod Satırı | ~1200 | ~1800 | ~3000+ |

---

## 🎓 Öğrenilen NYP Konuları

### Temel OOP Prensipleri
1. ✅ **Encapsulation** (Kapsülleme) - Veri gizleme
2. ✅ **Inheritance** (Kalıtım) - Kod tekrarını önleme
3. ✅ **Polymorphism** (Çok biçimlilik) - Esnek kod yapısı
4. ✅ **Abstraction** (Soyutlama) - Ortak davranışları tanımlama

### İleri Seviye Konular
5. ✅ **Interface** - Çoklu davranış tanımı
6. ✅ **Abstract Class** - Base class oluşturma
7. ✅ **Enum** - Sabit değer kümeleri
8. ✅ **Custom Exception** - Özel hata yönetimi
9. ✅ **Design Patterns** - Singleton, Repository, MVC
10. ✅ **Composition vs Inheritance** - HAS-A vs IS-A
11. ✅ **Static Methods** - Utility sınıfları
12. ✅ **Generic Types** - Tip güvenliği
13. ✅ **Collections Framework** - List, Set, Map
14. ✅ **Lambda Expressions** - Stream API kullanımı

### Framework ve Mimari
15. ✅ **Spring Boot** - Enterprise Java development
16. ✅ **REST API** - Web servisleri
17. ✅ **3-Tier Architecture** - Katmanlı mimari
18. ✅ **Dependency Injection** - IoC Container
19. ✅ **JPA/MongoDB** - Veritabanı işlemleri
20. ✅ **JavaFX** - Desktop GUI development

---

## 🌟 Projenin Güçlü Yönleri

1. **Tam Özellikli E-posta Sistemi**: Gerçek dünya uygulaması
2. **Modern Teknolojiler**: Spring Boot, JavaFX 21, MongoDB Atlas
3. **Temiz Mimari**: 3-Tier Architecture, separation of concerns
4. **OOP Prensiplerine Uygun**: Tüm NYP kavramları uygulanmış
5. **RESTful API**: Standart HTTP metodları
6. **Cloud Database**: MongoDB Atlas kullanımı
7. **Modern UI**: Dark theme, responsive tasarım
8. **Hata Yönetimi**: Custom exception'larla detaylı hata ele alma
9. **Kod Kalitesi**: Clean code, naming conventions
10. **Dokümantasyon**: Detaylı JavaDoc ve inline comments

---

## 🔮 Geliştirme Fırsatları

### Kısa Vadeli İyileştirmeler
- [ ] JWT Token ile authentication
- [ ] Şifreleme (password hashing - BCrypt)
- [ ] E-posta filtreleme ve kurallar
- [ ] Attachment upload/download
- [ ] Bildirim sistemi
- [ ] Kullanıcı ayarları sayfası

### Orta Vadeli Özellikler
- [ ] Gerçek SMTP entegrasyonu
- [ ] E-posta şablonları
- [ ] İmza özelliği
- [ ] Takvim entegrasyonu
- [ ] Kişiler yönetimi
- [ ] Toplu e-posta gönderimi

### Uzun Vadeli Vizyonlar
- [ ] Mobile uygulama (Android/iOS)
- [ ] Web arayüzü (React/Angular)
- [ ] Mikroservis mimarisi
- [ ] Gerçek zamanlı bildirimler (WebSocket)
- [ ] AI destekli spam filtreleme
- [ ] Çoklu dil desteği

---

## 📚 Referanslar ve Kaynaklar

### Resmi Dokümantasyonlar
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [JavaFX Documentation](https://openjfx.io/)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Java SE 21 Documentation](https://docs.oracle.com/en/java/javase/21/)

### Kullanılan Paternler
- **Singleton Pattern**: UserService
- **Repository Pattern**: EmailRepository, UserRepository
- **MVC Pattern**: Controller-Service-Repository
- **Factory Pattern**: User nesnesi oluşturma
- **Observer Pattern**: UI güncellemeleri

---

## 👨‍💻 Geliştirici Notları

### Veritabanı Yapısı (MongoDB)

**users collection:**
```json
{
  "_id": ObjectId,
  "email": "user@mail.com",
  "password": "hashed_password",
  "fullName": "User Name",
  "role": "REGULAR",
  "createdAt": ISODate,
  "lastLogin": ISODate,
  "isActive": true,
  "storageUsed": 0,
  "isPremium": false
}
```

**emails collection:**
```json
{
  "_id": ObjectId,
  "ownerEmail": "user@mail.com",
  "folder": "INBOX",
  "from": "sender@mail.com",
  "to": ["recipient@mail.com"],
  "cc": [],
  "bcc": [],
  "subject": "Email Subject",
  "body": "Email content",
  "sentDate": ISODate,
  "isRead": false,
  "isStarred": false,
  "priority": "NORMAL",
  "attachments": []
}
```

### API Endpoints Özeti

| Method | Endpoint | Açıklama |
|--------|----------|----------|
| POST | `/api/auth/register` | Kullanıcı kaydı |
| POST | `/api/auth/login` | Kullanıcı girişi |
| POST | `/api/emails/send` | E-posta gönder |
| GET | `/api/emails?email=&folder=` | Klasördeki e-postaları getir |
| GET | `/api/emails/{id}` | Tek e-posta getir |
| GET | `/api/emails/search?email=&query=&type=` | E-posta ara |
| POST | `/api/emails/draft` | Taslak kaydet |
| PUT | `/api/emails/{id}/read` | Okundu işaretle |
| PUT | `/api/emails/{id}/star` | Yıldızla |
| PUT | `/api/emails/{id}/unstar` | Yıldızdan çıkar |
| DELETE | `/api/emails/{id}` | E-posta sil |

---

## 📝 Sonuç

Bu proje, **Nesne Yönelimli Programlama** prensiplerini modern bir e-posta sistemi üzerinde uygulamalı olarak göstermektedir. 

**Proje Başarıları:**
- ✅ Tüm temel OOP kavramları (Encapsulation, Inheritance, Polymorphism, Abstraction) uygulandı
- ✅ İleri seviye konular (Interface, Abstract Class, Custom Exception, Design Patterns) kullanıldı
- ✅ Modern teknolojiler (Spring Boot, JavaFX, MongoDB) entegre edildi
- ✅ Clean Architecture ve best practices uygulandı
- ✅ Gerçek dünya senaryosuna uygun full-stack uygulama geliştirildi

**Öğrenilen Dersler:**
1. OOP prensipleri gerçek projelerde nasıl uygulanır
2. Backend ve Frontend nasıl ayrılır ve iletişim kurar
3. RESTful API tasarımı nasıl yapılır
4. Veritabanı modellemesi nasıl yapılır
5. Modern Java ekosistemi nasıl kullanılır

---

*Bu detaylı rapor NYP-EmailSistemi projesi için 4 Ocak 2026 tarihinde hazırlanmıştır.*

**Proje GitHub**: (Eklenebilir)  
**Geliştirici**: NYP Öğrencisi  
**Ders**: Nesne Yönelimli Programlama
