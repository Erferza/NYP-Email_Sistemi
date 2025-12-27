import enums.FolderType;
import enums.Priority;
import exceptions.MailSendException;
import models.*;
import services.Mailbox;
import services.UserService;
import utils.DateFormatter;
import utils.EmailValidator;

import java.util.List;
import java.util.Scanner;

/**
 * Ana uygulama sınıfı - Terminal tabanlı e-posta sistemi
 */
public class Main {
    
    private static Scanner scanner = new Scanner(System.in);
    private static UserService userService = UserService.getInstance();
    private static User currentUser = null;
    private static Mailbox mailbox = null;
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         JAVA E-POSTA SİSTEMİ - NYP PROJESİ               ║");
        System.out.println("║      Nesne Yönelimli Programlama Ders Projesi            ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        // Demo: NYP yapılarını göster
        demonstrateOOP();
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              İNTERAKTİF E-POSTA SİSTEMİ");
        System.out.println("=".repeat(60) + "\n");
        
        // Ana menü döngüsü
        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = showLoginMenu();
            } else {
                showMainMenu();
            }
        }
        
        System.out.println("\nProgram sonlandırıldı. Güle güle!");
        scanner.close();
    }
    
    /**
     * NYP yapılarını demonstre eder
     */
    private static void demonstrateOOP() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║           NYP YAPILARININ GÖSTERİMİ                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
        
        // 1. ABSTRACT CLASS & INHERITANCE (Soyut Sınıf & Kalıtım)
        System.out.println("1️⃣  ABSTRACT CLASS & INHERITANCE (Soyut Sınıf & Kalıtım)");
        System.out.println("-".repeat(55));
        
        User adminUser = new AdminUser("admin@mail.com", "admin123", "Ali Yönetici", "IT", 3);
        User regularUser = new RegularUser("kullanici@mail.com", "123456", "Ayşe Kullanıcı");
        
        System.out.println("Admin Kullanıcı: " + adminUser.getFullName());
        System.out.println("  → Rol: " + adminUser.getRole().getDisplayName());
        System.out.println("  → Mesaj: " + adminUser.getWelcomeMessage());
        System.out.println();
        System.out.println("Normal Kullanıcı: " + regularUser.getFullName());
        System.out.println("  → Rol: " + regularUser.getRole().getDisplayName());
        System.out.println("  → Mesaj: " + regularUser.getWelcomeMessage());
        System.out.println();
        
        // 2. POLYMORPHISM (Çok Biçimlilik)
        System.out.println("2️⃣  POLYMORPHISM (Çok Biçimlilik)");
        System.out.println("-".repeat(55));
        
        User[] users = {adminUser, regularUser};
        for (User user : users) {
            // Aynı metod farklı davranış - Polymorphism
            System.out.println(user.getFullName() + " yetkileri:");
            List<String> permissions = user.getPermissions();
            for (int i = 0; i < Math.min(3, permissions.size()); i++) {
                System.out.println("  • " + permissions.get(i));
            }
            if (permissions.size() > 3) {
                System.out.println("  ... ve " + (permissions.size() - 3) + " yetki daha");
            }
        }
        System.out.println();
        
        // 3. ENCAPSULATION (Kapsülleme)
        System.out.println("3️⃣  ENCAPSULATION (Kapsülleme)");
        System.out.println("-".repeat(55));
        
        Email email = new Email("gonder@mail.com", "alici@mail.com", "Test Konu", "Test içerik");
        System.out.println("E-posta oluşturuldu (private alanlar getter/setter ile erişilir):");
        System.out.println("  → Kimden: " + email.getFrom());
        System.out.println("  → Kime: " + email.getToAsString());
        System.out.println("  → Konu: " + email.getSubject());
        System.out.println("  → Okundu mu: " + (email.isRead() ? "Evet" : "Hayır"));
        
        email.markAsRead();  // Metod ile durum değişikliği
        System.out.println("  → markAsRead() sonrası: " + (email.isRead() ? "Evet" : "Hayır"));
        System.out.println();
        
        // 4. INTERFACE IMPLEMENTATION (Arayüz Uygulaması)
        System.out.println("4️⃣  INTERFACE IMPLEMENTATION (Arayüz Uygulaması)");
        System.out.println("-".repeat(55));
        
        Mailbox mailboxDemo = new Mailbox(regularUser);
        System.out.println("Mailbox sınıfı birden fazla interface implement eder:");
        System.out.println("  • IMailSender    → sendMail(), sendBulkMail()");
        System.out.println("  • IMailReceiver  → receiveMails(), getMailById()");
        System.out.println("  • ISearchable    → searchBySubject(), searchBySender()");
        System.out.println("  • IFolderManager → createFolder(), moveToFolder()");
        System.out.println();
        
        // 5. INHERITANCE (Email → DraftEmail)
        System.out.println("5️⃣  INHERITANCE (Kalıtım: Email → DraftEmail)");
        System.out.println("-".repeat(55));
        
        DraftEmail draft = new DraftEmail("ben@mail.com", "sen@mail.com", "Taslak Konu", "Taslak içerik...");
        System.out.println("DraftEmail, Email sınıfından kalıtım alır:");
        System.out.println("  → Email özellikleri: " + draft.getSubject());
        System.out.println("  → Ek özellik (lastModified): " + DateFormatter.formatFull(draft.getLastModified()));
        draft.autoSave();
        System.out.println("  → autoSave() sonrası: " + draft.isAutoSaved());
        System.out.println();
        
        // 6. ENUM Kullanımı
        System.out.println("6️⃣  ENUM KULLANIMI");
        System.out.println("-".repeat(55));
        
        System.out.println("Klasör Türleri (FolderType enum):");
        for (FolderType type : FolderType.values()) {
            System.out.println("  • " + type.name() + " → " + type.getDisplayName());
        }
        System.out.println();
        
        System.out.println("Öncelik Seviyeleri (Priority enum):");
        for (Priority p : Priority.values()) {
            System.out.println("  • " + p.name() + " → " + p.getDisplayName() + " (Seviye: " + p.getLevel() + ")");
        }
        System.out.println();
        
        // 7. COMPOSITION (Bileşim)
        System.out.println("7️⃣  COMPOSITION (Bileşim)");
        System.out.println("-".repeat(55));
        
        System.out.println("Mailbox HAS-A User (Mailbox bir User içerir):");
        System.out.println("  → Mailbox sahibi: " + mailboxDemo.getUser().getFullName());
        System.out.println();
        
        System.out.println("Folder HAS-A List<Email> (Folder e-postaları içerir):");
        Folder inbox = mailboxDemo.getInbox();
        System.out.println("  → " + inbox.getName() + ": " + inbox.getEmailCount() + " e-posta");
        System.out.println();
        
        System.out.println("Email HAS-A List<Attachment> (Email ekleri içerir):");
        Attachment att = new Attachment("belge.pdf", "/path/to/file", 1024 * 500, "application/pdf");
        email.addAttachment(att);
        System.out.println("  → Ek sayısı: " + email.getAttachments().size());
        System.out.println("  → Ek: " + att.toString());
        System.out.println();
        
        // 8. EXCEPTION HANDLING (İstisna Yönetimi)
        System.out.println("8️⃣  CUSTOM EXCEPTION (Özel İstisna Sınıfları)");
        System.out.println("-".repeat(55));
        
        System.out.println("Hiyerarşi: Exception → EmailException → InvalidEmailException");
        System.out.println("                                      → MailSendException");
        System.out.println("                                      → AttachmentException");
        
        // Email doğrulama demo
        String testEmail = "gecersiz-email";
        System.out.println("\nE-posta doğrulama testi:");
        System.out.println("  → '" + testEmail + "' geçerli mi? " + EmailValidator.isValidEmail(testEmail));
        testEmail = "gecerli@mail.com";
        System.out.println("  → '" + testEmail + "' geçerli mi? " + EmailValidator.isValidEmail(testEmail));
        System.out.println();
        
        // 9. SINGLETON PATTERN
        System.out.println("9️⃣  SINGLETON PATTERN (Tek Nesne Deseni)");
        System.out.println("-".repeat(55));
        
        UserService service1 = UserService.getInstance();
        UserService service2 = UserService.getInstance();
        System.out.println("UserService.getInstance() her zaman aynı nesneyi döner:");
        System.out.println("  → service1 == service2: " + (service1 == service2));
        System.out.println("  → Kayıtlı kullanıcı sayısı: " + service1.getAllUsers().size());
        System.out.println();
        
        // 10. Static Methods & Utility Classes
        System.out.println("🔟 STATIC METHODS & UTILITY CLASSES");
        System.out.println("-".repeat(55));
        
        System.out.println("DateFormatter utility metodları:");
        System.out.println("  → Şu an: " + DateFormatter.formatFull(new java.util.Date()));
        System.out.println("  → Akıllı format: " + DateFormatter.formatSmart(email.getSentDate()));
        System.out.println();
        
        System.out.println("EmailValidator utility metodları:");
        System.out.println("  → Domain: " + EmailValidator.getDomain("kullanici@ornek.com"));
        System.out.println("  → Username: " + EmailValidator.getUsername("kullanici@ornek.com"));
    }
    
    /**
     * Giriş menüsünü gösterir
     */
    private static boolean showLoginMenu() {
        System.out.println("\n┌─────────────────────────────────┐");
        System.out.println("│         GİRİŞ MENÜSÜ            │");
        System.out.println("├─────────────────────────────────┤");
        System.out.println("│  1. Giriş Yap                   │");
        System.out.println("│  2. Kayıt Ol                    │");
        System.out.println("│  3. Kayıtlı Kullanıcıları Gör   │");
        System.out.println("│  0. Çıkış                       │");
        System.out.println("└─────────────────────────────────┘");
        System.out.print("Seçiminiz: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                login();
                break;
            case "2":
                register();
                break;
            case "3":
                showRegisteredUsers();
                break;
            case "0":
                return false;
            default:
                System.out.println("❌ Geçersiz seçim!");
        }
        return true;
    }
    
    /**
     * Kayıtlı kullanıcıları gösterir
     */
    private static void showRegisteredUsers() {
        System.out.println("\n📋 Kayıtlı Kullanıcılar:");
        System.out.println("-".repeat(50));
        for (User user : userService.getAllUsers()) {
            String role = user.getRole().getDisplayName();
            System.out.println("  • " + user.getEmail() + " (" + role + ")");
        }
        System.out.println("-".repeat(50));
        System.out.println("💡 İpucu: Şifre olarak 'admin123' veya '123456' kullanabilirsiniz.");
    }
    
    /**
     * Giriş işlemi
     */
    private static void login() {
        System.out.println("\n═══ GİRİŞ YAP ═══");
        System.out.print("E-posta: ");
        String email = scanner.nextLine().trim();
        System.out.print("Şifre: ");
        String password = scanner.nextLine().trim();
        
        currentUser = userService.login(email, password);
        
        if (currentUser != null) {
            mailbox = new Mailbox(currentUser);
            addSampleEmails(); // Demo için örnek e-postalar ekle
            System.out.println("\n✅ " + currentUser.getWelcomeMessage());
        } else {
            System.out.println("\n❌ E-posta veya şifre hatalı!");
        }
    }
    
    /**
     * Kayıt işlemi
     */
    private static void register() {
        System.out.println("\n═══ KAYIT OL ═══");
        System.out.print("Ad Soyad: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("E-posta: ");
        String email = scanner.nextLine().trim();
        
        if (!EmailValidator.isValidEmail(email)) {
            System.out.println("❌ Geçersiz e-posta formatı!");
            return;
        }
        
        System.out.print("Şifre: ");
        String password = scanner.nextLine().trim();
        System.out.print("Şifre (Tekrar): ");
        String passwordConfirm = scanner.nextLine().trim();
        
        if (!password.equals(passwordConfirm)) {
            System.out.println("❌ Şifreler eşleşmiyor!");
            return;
        }
        
        User newUser = userService.register(email, password, fullName);
        if (newUser != null) {
            System.out.println("✅ Kayıt başarılı! Şimdi giriş yapabilirsiniz.");
        }
    }
    
    /**
     * Ana menüyü gösterir
     */
    private static void showMainMenu() {
        int unreadCount = mailbox.getInbox().getUnreadCount();
        
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│              ANA MENÜ                   │");
        System.out.println("│  Kullanıcı: " + padRight(currentUser.getFullName(), 26) + "│");
        System.out.println("├─────────────────────────────────────────┤");
        System.out.println("│  1. 📥 Gelen Kutusu (" + unreadCount + " okunmamış)" + padRight("", 12 - String.valueOf(unreadCount).length()) + "│");
        System.out.println("│  2. ✏️  Yeni E-posta Yaz                 │");
        System.out.println("│  3. 📤 Gönderilenler                    │");
        System.out.println("│  4. 📝 Taslaklar                        │");
        System.out.println("│  5. ⭐ Yıldızlı                         │");
        System.out.println("│  6. 🗑️  Çöp Kutusu                       │");
        System.out.println("│  7. 🔍 E-posta Ara                      │");
        System.out.println("│  8. 📁 Klasör Yönetimi                  │");
        System.out.println("│  9. 👤 Profil                           │");
        if (currentUser instanceof AdminUser) {
            System.out.println("│  10. ⚙️  Admin Paneli                   │");
        }
        System.out.println("│  0. 🚪 Çıkış Yap                        │");
        System.out.println("└─────────────────────────────────────────┘");
        System.out.print("Seçiminiz: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                showFolder(mailbox.getInbox());
                break;
            case "2":
                composeEmail();
                break;
            case "3":
                showFolder(mailbox.getSent());
                break;
            case "4":
                showFolder(mailbox.getDrafts());
                break;
            case "5":
                showFolder(mailbox.getStarred());
                break;
            case "6":
                showFolder(mailbox.getTrash());
                break;
            case "7":
                searchEmails();
                break;
            case "8":
                folderManagement();
                break;
            case "9":
                showProfile();
                break;
            case "10":
                if (currentUser instanceof AdminUser) {
                    showAdminPanel();
                } else {
                    System.out.println("❌ Geçersiz seçim!");
                }
                break;
            case "0":
                userService.logout();
                currentUser = null;
                mailbox = null;
                System.out.println("👋 Çıkış yapıldı!");
                break;
            default:
                System.out.println("❌ Geçersiz seçim!");
        }
    }
    
    /**
     * Klasör içeriğini gösterir
     */
    private static void showFolder(Folder folder) {
        System.out.println("\n═══ " + folder.getName().toUpperCase() + " ═══");
        System.out.println("Toplam: " + folder.getEmailCount() + " e-posta, " + folder.getUnreadCount() + " okunmamış\n");
        
        List<Email> emails = folder.getEmails();
        
        if (emails.isEmpty()) {
            System.out.println("📭 Bu klasör boş.");
            return;
        }
        
        System.out.println("┌────┬────────────────────┬──────────────────────────┬─────────────┐");
        System.out.println("│ ID │ Kimden             │ Konu                     │ Tarih       │");
        System.out.println("├────┼────────────────────┼──────────────────────────┼─────────────┤");
        
        for (Email email : emails) {
            String readMark = email.isRead() ? "  " : "● ";
            String starMark = email.isStarred() ? "⭐" : "  ";
            String from = truncate(email.getFrom(), 16);
            String subject = truncate(email.getSubject(), 22);
            String date = DateFormatter.formatSmart(email.getSentDate());
            
            System.out.printf("│%s%2d │ %-18s │ %s%-22s │ %-11s │%n", 
                    readMark, email.getId(), from, starMark, subject, date);
        }
        System.out.println("└────┴────────────────────┴──────────────────────────┴─────────────┘");
        
        System.out.println("\n[R] E-posta Oku  [D] Sil  [S] Yıldızla  [B] Geri");
        System.out.print("Seçim (örn: R 1): ");
        String input = scanner.nextLine().trim().toUpperCase();
        
        if (input.startsWith("R ")) {
            try {
                int id = Integer.parseInt(input.substring(2).trim());
                readEmail(id);
            } catch (NumberFormatException e) {
                System.out.println("❌ Geçersiz ID!");
            }
        } else if (input.startsWith("D ")) {
            try {
                int id = Integer.parseInt(input.substring(2).trim());
                deleteEmail(id, folder);
            } catch (NumberFormatException e) {
                System.out.println("❌ Geçersiz ID!");
            }
        } else if (input.startsWith("S ")) {
            try {
                int id = Integer.parseInt(input.substring(2).trim());
                starEmail(id);
            } catch (NumberFormatException e) {
                System.out.println("❌ Geçersiz ID!");
            }
        }
    }
    
    /**
     * E-posta okuma
     */
    private static void readEmail(int id) {
        Email email = mailbox.getMailById(id);
        if (email == null) {
            System.out.println("❌ E-posta bulunamadı!");
            return;
        }
        
        email.markAsRead();
        
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                    E-POSTA DETAYI                        ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║ Kimden : " + padRight(email.getFrom(), 48) + "║");
        System.out.println("║ Kime   : " + padRight(email.getToAsString(), 48) + "║");
        System.out.println("║ Tarih  : " + padRight(DateFormatter.formatFull(email.getSentDate()), 48) + "║");
        System.out.println("║ Konu   : " + padRight(email.getSubject(), 48) + "║");
        if (email.hasAttachments()) {
            System.out.println("║ Ekler  : " + padRight(email.getAttachments().size() + " dosya", 48) + "║");
        }
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║ İçerik:                                                  ║");
        System.out.println("╟──────────────────────────────────────────────────────────╢");
        
        // İçeriği satırlara böl
        String[] lines = email.getBody().split("\n");
        for (String line : lines) {
            while (line.length() > 56) {
                System.out.println("║ " + padRight(line.substring(0, 56), 56) + " ║");
                line = line.substring(56);
            }
            System.out.println("║ " + padRight(line, 56) + " ║");
        }
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        System.out.println("\n[Y] Yanıtla  [I] İlet  [D] Sil  [B] Geri");
        System.out.print("Seçim: ");
        String choice = scanner.nextLine().trim().toUpperCase();
        
        switch (choice) {
            case "Y":
                replyToEmail(email);
                break;
            case "I":
                forwardEmail(email);
                break;
            case "D":
                mailbox.moveToTrash(email);
                System.out.println("✅ E-posta çöp kutusuna taşındı.");
                break;
        }
    }
    
    /**
     * E-posta yazma
     */
    private static void composeEmail() {
        System.out.println("\n═══ YENİ E-POSTA ═══");
        
        System.out.print("Kime: ");
        String to = scanner.nextLine().trim();
        
        if (!EmailValidator.isValidEmail(to)) {
            System.out.println("❌ Geçersiz e-posta adresi!");
            return;
        }
        
        System.out.print("Konu: ");
        String subject = scanner.nextLine().trim();
        
        System.out.println("İçerik (bitirmek için boş satır girin):");
        StringBuilder body = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {
            body.append(line).append("\n");
        }
        
        Email email = new Email(currentUser.getEmail(), to, subject, body.toString().trim());
        
        System.out.print("\n[G] Gönder  [T] Taslak Kaydet  [I] İptal: ");
        String choice = scanner.nextLine().trim().toUpperCase();
        
        switch (choice) {
            case "G":
                try {
                    mailbox.sendMail(email);
                    System.out.println("✅ E-posta gönderildi!");
                } catch (MailSendException e) {
                    System.out.println("❌ Gönderim hatası: " + e.getMessage());
                }
                break;
            case "T":
                DraftEmail draft = new DraftEmail(email);
                mailbox.saveDraft(draft);
                System.out.println("✅ Taslak kaydedildi.");
                break;
            default:
                System.out.println("❌ İptal edildi.");
        }
    }
    
    /**
     * E-postayı yanıtla
     */
    private static void replyToEmail(Email original) {
        System.out.println("\n═══ YANITLA ═══");
        System.out.println("Kime: " + original.getFrom());
        System.out.println("Konu: Re: " + original.getSubject());
        
        System.out.println("Yanıtınız (bitirmek için boş satır girin):");
        StringBuilder body = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {
            body.append(line).append("\n");
        }
        
        Email reply = original.reply(body.toString().trim(), currentUser.getEmail());
        
        try {
            mailbox.sendMail(reply);
            System.out.println("✅ Yanıt gönderildi!");
        } catch (MailSendException e) {
            System.out.println("❌ Gönderim hatası: " + e.getMessage());
        }
    }
    
    /**
     * E-postayı ilet
     */
    private static void forwardEmail(Email original) {
        System.out.println("\n═══ İLET ═══");
        System.out.print("Kime: ");
        String to = scanner.nextLine().trim();
        
        if (!EmailValidator.isValidEmail(to)) {
            System.out.println("❌ Geçersiz e-posta adresi!");
            return;
        }
        
        Email forward = original.forward(currentUser.getEmail());
        forward.addTo(to);
        
        try {
            mailbox.sendMail(forward);
            System.out.println("✅ E-posta iletildi!");
        } catch (MailSendException e) {
            System.out.println("❌ Gönderim hatası: " + e.getMessage());
        }
    }
    
    /**
     * E-posta sil
     */
    private static void deleteEmail(int id, Folder folder) {
        Email email = folder.findEmailById(id);
        if (email != null) {
            mailbox.moveToTrash(email);
            System.out.println("✅ E-posta çöp kutusuna taşındı.");
        } else {
            System.out.println("❌ E-posta bulunamadı!");
        }
    }
    
    /**
     * E-posta yıldızla
     */
    private static void starEmail(int id) {
        Email email = mailbox.getMailById(id);
        if (email != null) {
            if (email.isStarred()) {
                mailbox.unstarEmail(email);
                System.out.println("✅ Yıldız kaldırıldı.");
            } else {
                mailbox.starEmail(email);
                System.out.println("✅ E-posta yıldızlandı.");
            }
        } else {
            System.out.println("❌ E-posta bulunamadı!");
        }
    }
    
    /**
     * E-posta arama
     */
    private static void searchEmails() {
        System.out.println("\n═══ E-POSTA ARA ═══");
        System.out.println("1. Konuya göre ara");
        System.out.println("2. Gönderene göre ara");
        System.out.println("3. İçeriğe göre ara");
        System.out.print("Seçim: ");
        
        String choice = scanner.nextLine().trim();
        System.out.print("Aranacak kelime: ");
        String keyword = scanner.nextLine().trim();
        
        List<Email> results;
        switch (choice) {
            case "1":
                results = mailbox.searchBySubject(keyword);
                break;
            case "2":
                results = mailbox.searchBySender(keyword);
                break;
            case "3":
                results = mailbox.searchByContent(keyword);
                break;
            default:
                System.out.println("❌ Geçersiz seçim!");
                return;
        }
        
        System.out.println("\n🔍 Bulunan: " + results.size() + " e-posta");
        for (Email email : results) {
            System.out.println("  [" + email.getId() + "] " + email.getFrom() + " - " + email.getSubject());
        }
    }
    
    /**
     * Klasör yönetimi
     */
    private static void folderManagement() {
        System.out.println("\n═══ KLASÖR YÖNETİMİ ═══");
        System.out.println("Mevcut Klasörler:");
        
        for (Folder folder : mailbox.getAllFolders()) {
            String type = folder.isSystemFolder() ? "(Sistem)" : "(Özel)";
            System.out.println("  • " + folder.getName() + " " + type + " - " + folder.getEmailCount() + " e-posta");
        }
        
        System.out.println("\n1. Yeni Klasör Oluştur");
        System.out.println("2. Klasör Sil");
        System.out.println("0. Geri");
        System.out.print("Seçim: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                System.out.print("Klasör adı: ");
                String name = scanner.nextLine().trim();
                Folder newFolder = mailbox.createFolder(name);
                System.out.println("✅ Klasör oluşturuldu: " + newFolder.getName());
                break;
            case "2":
                System.out.print("Silinecek klasör adı: ");
                String deleteName = scanner.nextLine().trim();
                for (Folder folder : mailbox.getAllFolders()) {
                    if (folder.getName().equalsIgnoreCase(deleteName)) {
                        if (mailbox.deleteFolder(folder)) {
                            System.out.println("✅ Klasör silindi.");
                        }
                        return;
                    }
                }
                System.out.println("❌ Klasör bulunamadı!");
                break;
        }
    }
    
    /**
     * Profil gösterimi
     */
    private static void showProfile() {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                      PROFİL                              ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║ Ad Soyad  : " + padRight(currentUser.getFullName(), 45) + "║");
        System.out.println("║ E-posta   : " + padRight(currentUser.getEmail(), 45) + "║");
        System.out.println("║ Rol       : " + padRight(currentUser.getRole().getDisplayName(), 45) + "║");
        System.out.println("║ Kayıt     : " + padRight(DateFormatter.formatDate(currentUser.getCreatedAt()), 45) + "║");
        
        if (currentUser instanceof RegularUser) {
            RegularUser ru = (RegularUser) currentUser;
            System.out.println("║ Hesap     : " + padRight(ru.isPremium() ? "Premium" : "Standart", 45) + "║");
            System.out.println("║ Depolama  : " + padRight(String.format("%.1f%%", ru.getStorageUsagePercentage()), 45) + "║");
        }
        
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        System.out.println("\n[S] Şifre Değiştir  [B] Geri");
        System.out.print("Seçim: ");
        String choice = scanner.nextLine().trim().toUpperCase();
        
        if (choice.equals("S")) {
            System.out.print("Mevcut şifre: ");
            String oldPass = scanner.nextLine();
            System.out.print("Yeni şifre: ");
            String newPass = scanner.nextLine();
            
            if (currentUser.changePassword(oldPass, newPass)) {
                System.out.println("✅ Şifre değiştirildi!");
            } else {
                System.out.println("❌ Mevcut şifre hatalı!");
            }
        }
    }
    
    /**
     * Admin paneli
     */
    private static void showAdminPanel() {
        AdminUser admin = (AdminUser) currentUser;
        
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                    ADMİN PANELİ                          ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║ 1. Tüm Kullanıcıları Listele                             ║");
        System.out.println("║ 2. Kullanıcı Yasakla/Yasak Kaldır                        ║");
        System.out.println("║ 3. Sistem İstatistikleri                                 ║");
        System.out.println("║ 0. Geri                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.print("Seçim: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                System.out.println("\n📋 Tüm Kullanıcılar:");
                System.out.println("-".repeat(60));
                for (User user : userService.getAllUsers()) {
                    String status = user.isActive() ? "✅ Aktif" : "❌ Yasaklı";
                    System.out.println("  [" + user.getId() + "] " + user.getEmail() + 
                            " (" + user.getRole().getDisplayName() + ") - " + status);
                }
                break;
            case "2":
                System.out.print("Kullanıcı e-posta: ");
                String email = scanner.nextLine().trim();
                User targetUser = userService.findByEmail(email);
                if (targetUser != null && !(targetUser instanceof AdminUser)) {
                    if (targetUser.isActive()) {
                        admin.banUser(targetUser);
                        System.out.println("✅ Kullanıcı yasaklandı.");
                    } else {
                        admin.unbanUser(targetUser);
                        System.out.println("✅ Yasak kaldırıldı.");
                    }
                } else {
                    System.out.println("❌ Kullanıcı bulunamadı veya admin yasaklanamaz!");
                }
                break;
            case "3":
                int totalUsers = userService.getAllUsers().size();
                int activeUsers = userService.getActiveUserCount();
                int totalEmails = mailbox.getTotalEmailCount();
                
                System.out.println("\n📊 Sistem İstatistikleri:");
                System.out.println("-".repeat(40));
                System.out.println("  Toplam Kullanıcı: " + totalUsers);
                System.out.println("  Aktif Kullanıcı: " + activeUsers);
                System.out.println("  Toplam E-posta: " + totalEmails);
                System.out.println("  Admin Seviyesi: " + admin.getAccessLevel());
                break;
        }
    }
    
    /**
     * Demo için örnek e-postalar ekler
     */
    private static void addSampleEmails() {
        Email email1 = new Email("sistem@mail.com", currentUser.getEmail(), 
                "Hoş Geldiniz!", 
                "Merhaba " + currentUser.getFullName() + ",\n\n" +
                "E-posta sistemimize hoş geldiniz!\n\n" +
                "Bu sistem NYP (Nesne Yönelimli Programlama) dersi kapsamında geliştirilmiştir.\n\n" +
                "İyi günler dileriz.");
        
        Email email2 = new Email("bilgi@mail.com", currentUser.getEmail(),
                "Önemli Duyuru",
                "Sayın kullanıcımız,\n\n" +
                "Sistemimizde yapılan güncellemeler hakkında sizi bilgilendirmek istiyoruz.\n\n" +
                "Yeni özellikler:\n" +
                "- Gelişmiş arama\n" +
                "- Klasör yönetimi\n" +
                "- Yıldızlı e-postalar\n\n" +
                "Teşekkürler.");
        
        Email email3 = new Email("destek@mail.com", currentUser.getEmail(),
                "Hesap Bilgileriniz",
                "Hesap bilgileriniz:\n\n" +
                "E-posta: " + currentUser.getEmail() + "\n" +
                "Rol: " + currentUser.getRole().getDisplayName() + "\n\n" +
                "Herhangi bir sorunuz varsa destek ekibimize ulaşabilirsiniz.");
        
        mailbox.receiveEmail(email1);
        mailbox.receiveEmail(email2);
        mailbox.receiveEmail(email3);
    }
    
    // Yardımcı metodlar
    
    private static String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() > n) s = s.substring(0, n);
        return String.format("%-" + n + "s", s);
    }
    
    private static String truncate(String s, int maxLength) {
        if (s == null) return "";
        if (s.length() <= maxLength) return s;
        return s.substring(0, maxLength - 2) + "..";
    }
}
