# 📧 JAVA E-POSTA SİSTEMİ - NYP PROJESİ RAPORU

## 📋 Proje Bilgileri

| Bilgi | Değer |
|-------|-------|
| **Proje Adı** | Java E-Posta Sistemi |
| **Ders** | Nesne Yönelimli Programlama (NYP/OOP) |
| **Programlama Dili** | Java |
| **Arayüz** | Terminal (Console) |
| **Tarih** | 22 Aralık 2025 |
| **Toplam Sınıf Sayısı** | 18 |
| **Toplam Satır Kodu** | ~1500+ |

---

## 📁 Proje Klasör Yapısı

```
NYP-JavaProjesi/
├── RAPOR.md                         # Bu rapor dosyası
├── src/                             # Kaynak kodlar (.java)
│   ├── Main.java                    # Ana uygulama sınıfı (Terminal UI)
│   │
│   ├── enums/                       # Enum (Sabit) sınıfları
│   │   ├── FolderType.java          # Klasör türleri (7 tür)
│   │   ├── Priority.java            # E-posta öncelikleri (4 seviye)
│   │   └── UserRole.java            # Kullanıcı rolleri (2 rol)
│   │
│   ├── exceptions/                  # Özel istisna sınıfları
│   │   ├── EmailException.java      # Temel e-posta istisnası (abstract)
│   │   ├── InvalidEmailException.java  # Geçersiz e-posta hatası
│   │   ├── MailSendException.java   # Gönderim hatası
│   │   └── AttachmentException.java # Dosya eki hatası
│   │
│   ├── interfaces/                  # Arayüz (Interface) tanımları
│   │   ├── IMailSender.java         # E-posta gönderme arayüzü
│   │   ├── IMailReceiver.java       # E-posta alma arayüzü
│   │   ├── ISearchable.java         # Arama arayüzü
│   │   └── IFolderManager.java      # Klasör yönetimi arayüzü
│   │
│   ├── models/                      # Model (Veri) sınıfları
│   │   ├── User.java                # Soyut kullanıcı sınıfı (abstract)
│   │   ├── AdminUser.java           # Yönetici kullanıcı (extends User)
│   │   ├── RegularUser.java         # Normal kullanıcı (extends User)
│   │   ├── Email.java               # E-posta modeli
│   │   ├── DraftEmail.java          # Taslak e-posta (extends Email)
│   │   ├── Folder.java              # Klasör modeli
│   │   └── Attachment.java          # Dosya eki modeli
│   │
│   ├── services/                    # Servis (İş mantığı) sınıfları
│   │   ├── Mailbox.java             # Posta kutusu servisi (4 interface impl.)
│   │   └── UserService.java         # Kullanıcı yönetim servisi (Singleton)
│   │
│   └── utils/                       # Yardımcı (Utility) sınıflar
│       ├── Constants.java           # Sabit değerler
│       ├── DateFormatter.java       # Tarih formatlama (static metodlar)
│       └── EmailValidator.java      # E-posta doğrulama (static metodlar)
│
└── out/                             # Derlenmiş dosyalar (.class)
    └── (javac ile otomatik oluşturulur)
```

---

## 🔢 Dosya ve Sınıf Özeti

| Paket | Dosya Sayısı | Sınıflar |
|-------|--------------|----------|
| `enums` | 3 | FolderType, Priority, UserRole |
| `exceptions` | 4 | EmailException, InvalidEmailException, MailSendException, AttachmentException |
| `interfaces` | 4 | IMailSender, IMailReceiver, ISearchable, IFolderManager |
| `models` | 7 | User, AdminUser, RegularUser, Email, DraftEmail, Folder, Attachment |
| `services` | 2 | Mailbox, UserService |
| `utils` | 3 | Constants, DateFormatter, EmailValidator |
| root | 1 | Main |
| **TOPLAM** | **24** | **18 sınıf + 4 interface + 3 enum** |

---

## 🎯 Kullanılan NYP (OOP) Kavramları

### 1️⃣ SOYUTLAMA (Abstraction)

#### Abstract Class: `User.java`
```java
public abstract class User implements Serializable {
    protected int id;
    protected String email;
    protected String password;
    protected String fullName;
    
    // Soyut metodlar - alt sınıflar implement etmek ZORUNDA
    public abstract UserRole getRole();
    public abstract List<String> getPermissions();
    public abstract String getWelcomeMessage();
    
    // Somut metodlar - tüm alt sınıflar kullanabilir
    public boolean login(String email, String password) { ... }
    public void logout() { ... }
}
```

**Açıklama:** `User` sınıfı soyut (abstract) olarak tanımlandı çünkü:
- Doğrudan bir "User" nesnesi oluşturmak mantıksız
- Her kullanıcı tipi farklı rol ve yetkilere sahip
- Ortak özellikler (email, password) bu sınıfta tanımlı
- Alt sınıflar abstract metodları implement etmek zorunda

---

### 2️⃣ KALITIM (Inheritance)

#### Kullanıcı Hiyerarşisi
```
User (abstract)
    ├── AdminUser extends User
    └── RegularUser extends User
```

#### E-posta Hiyerarşisi
```
Email
    └── DraftEmail extends Email
```

**AdminUser.java:**
```java
public class AdminUser extends User {
    private String department;
    private int accessLevel;
    
    @Override
    public UserRole getRole() {
        return UserRole.ADMIN;
    }
    
    @Override
    public List<String> getPermissions() {
        // Admin'e özel yetkiler
        return Arrays.asList("VIEW_ALL_USERS", "BAN_USER", ...);
    }
    
    // Admin'e özel metodlar
    public void banUser(User user) { ... }
    public void viewSystemStats() { ... }
}
```

**RegularUser.java:**
```java
public class RegularUser extends User {
    private long storageUsed;
    private boolean isPremium;
    
    @Override
    public UserRole getRole() {
        return UserRole.REGULAR;
    }
    
    // Normal kullanıcıya özel metodlar
    public void upgradeAccount() { ... }
    public double getStorageUsagePercentage() { ... }
}
```

**DraftEmail.java:**
```java
public class DraftEmail extends Email {
    private Date lastModified;
    private boolean autoSaved;
    
    // Email'in tüm özelliklerini miras alır
    // Ek olarak taslak özelliklerini ekler
    public void autoSave() { ... }
    public Email toEmail() { ... }  // Taslağı e-postaya dönüştür
}
```

---

### 3️⃣ ÇOK BİÇİMLİLİK (Polymorphism)

#### Örnek 1: Aynı metod farklı davranış
```java
User adminUser = new AdminUser("admin@mail.com", "123", "Ali");
User regularUser = new RegularUser("user@mail.com", "123", "Ayşe");

// Aynı metod çağrısı, farklı sonuçlar (Runtime Polymorphism)
System.out.println(adminUser.getRole());      // ADMIN
System.out.println(regularUser.getRole());    // REGULAR

System.out.println(adminUser.getWelcomeMessage());   
// "Hoş geldiniz Yönetici Ali! (Seviye: 3)"

System.out.println(regularUser.getWelcomeMessage()); 
// "Hoş geldiniz Ayşe!"
```

#### Örnek 2: Interface ile polymorphism
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

## 🚀 Derleme ve Çalıştırma

```bash
# Proje klasörüne git
cd /Users/ferzaer/Desktop/NYP-JavaProjesi

# Derleme (kaynak kodları out/ klasörüne derle)
javac -encoding UTF-8 -d out src/**/*.java src/*.java

# Çalıştırma
java -cp out Main
```

**Not:** `.class` dosyaları `out/` klasöründe oluşturulur. Bu dosyalar Java Virtual Machine (JVM) tarafından çalıştırılan bytecode dosyalarıdır.

---

## 🖥️ Uygulama Özellikleri

### Terminal Arayüzü
- ✅ Kullanıcı girişi / Kayıt
- ✅ E-posta gönderme
- ✅ Gelen kutusu görüntüleme
- ✅ E-posta okuma / yanıtlama / iletme
- ✅ Yıldızlama
- ✅ Silme (çöp kutusuna taşıma)
- ✅ Klasör yönetimi
- ✅ E-posta arama
- ✅ Profil görüntüleme
- ✅ Admin paneli (kullanıcı yönetimi)

---

## 👨‍💻 NYP Kavramları Özet Tablosu

| # | Kavram | Kullanım Yeri | Dosya |
|---|--------|---------------|-------|
| 1 | **Abstract Class** | Soyut kullanıcı sınıfı | `User.java` |
| 2 | **Inheritance** | Kullanıcı/E-posta hiyerarşisi | `AdminUser.java`, `RegularUser.java`, `DraftEmail.java` |
| 3 | **Polymorphism** | Farklı davranışlar | `getRole()`, `getPermissions()`, `getWelcomeMessage()` |
| 4 | **Encapsulation** | Private alanlar + getter/setter | Tüm model sınıfları |
| 5 | **Interface** | Çoklu arayüz implementasyonu | `IMailSender`, `IMailReceiver`, `ISearchable`, `IFolderManager` |
| 6 | **Enum** | Sabit değer kümeleri | `FolderType`, `Priority`, `UserRole` |
| 7 | **Composition** | HAS-A ilişkisi | `Mailbox`↔`User`, `Folder`↔`Email`, `Email`↔`Attachment` |
| 8 | **Custom Exception** | Özel hata sınıfları | `EmailException` hiyerarşisi |
| 9 | **Singleton Pattern** | Tek nesne deseni | `UserService` |
| 10 | **Static Methods** | Utility sınıfları | `EmailValidator`, `DateFormatter` |

---

## 📊 Proje İstatistikleri

| Metrik | Değer |
|--------|-------|
| Toplam Java Dosyası | 18 |
| Interface Sayısı | 4 |
| Enum Sayısı | 3 |
| Abstract Class | 1 |
| Concrete Class | 14 |
| Tahmini Kod Satırı | ~1500+ |

---

*Bu rapor NYP-JavaProjesi için 22 Aralık 2025 tarihinde oluşturulmuştur.*
