package services;

import enums.FolderType;
import enums.Priority;
import models.Email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;

public class EmailService {

    private static EmailService instance;
    private static List<Email> emails = new ArrayList<>();
    private static boolean initialized = false;
    private static final String DATA_DIR = "data";
    private static final String EMAILS_FILE = DATA_DIR + "/emails.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static {
        // Veri klasörünü oluştur
        new File(DATA_DIR).mkdirs();
        
        // Dosyadan emailleri yükle
        loadEmails();
        
        // Eğer hiç email yoksa, örnek emailleri ekle
        if (emails.isEmpty()) {
            initializeSampleEmails();
            saveEmails();
        } else {
            System.out.println("✅ Email'ler dosyadan yüklendi: " + emails.size() + " email");
        }
    }

    private EmailService() {
    }

    public static synchronized EmailService getInstance() {
        if (instance == null) {
            instance = new EmailService();
        }
        return instance;
    }
    private static void saveEmails() {
        try (Writer writer = new FileWriter(EMAILS_FILE)) {
            gson.toJson(emails, writer);
            System.out.println("💾 Email'ler kaydedildi: " + emails.size() + " email");
        } catch (IOException e) {
            System.err.println("❌ Email'ler kaydedilemedi: " + e.getMessage());
        }
    }

    private static void loadEmails() {
        File file = new File(EMAILS_FILE);
        if (!file.exists()) {
            System.out.println("📂 Email dosyası bulunamadı, yeni oluşturulacak");
            return;
        }
        
        try (Reader reader = new FileReader(EMAILS_FILE)) {
            Type listType = new TypeToken<ArrayList<Email>>(){}.getType();
            List<Email> loadedEmails = gson.fromJson(reader, listType);
            if (loadedEmails != null) {
                emails.clear();
                emails.addAll(loadedEmails);
            }
        } catch (IOException e) {
            System.err.println("❌ Email'ler yüklenemedi: " + e.getMessage());
        }
    }
    public void sendEmail(Email email) {
        // Gönderenin kendi kopyası için ayarla
        email.setOwnerEmail(email.getFrom());
        email.setFolder(FolderType.SENT);
        email.setSentDate(new Date());
        emails.add(email);
        
        // Alıcılar için kopyalar oluştur
        if (email.getTo() != null) {
            for (String recipient : email.getTo()) {
                Email recipientCopy = copyEmailForRecipient(email, recipient, FolderType.INBOX);
                emails.add(recipientCopy);
            }
        }
        saveEmails();
    }

    private Email copyEmailForRecipient(Email original, String ownerEmail, FolderType folder) {
        Email copy = new Email();
        copy.setOwnerEmail(ownerEmail);
        copy.setFolder(folder);
        copy.setFrom(original.getFrom());
        copy.setTo(original.getTo());
        copy.setSubject(original.getSubject());
        copy.setBody(original.getBody());
        copy.setSentDate(original.getSentDate());
        copy.setRead(false);
        copy.setPriority(original.getPriority());
        copy.setAttachments(original.getAttachments());
        return copy;
    }

    public List<Email> getEmails(String userEmail, FolderType folder) {
        if (folder == FolderType.STARRED) {
            // Yıldızlı emailler - tüm folderlardan yıldızlıları getir
            return emails.stream()
                    .filter(e -> e.getOwnerEmail() != null && e.getOwnerEmail().equals(userEmail))
                    .filter(e -> e.isStarred())
                    .filter(e -> e.getFolder() != FolderType.TRASH) // Çöp kutusundakiler hariç
                    .collect(Collectors.toList());
        }
        return emails.stream()
                .filter(e -> e.getOwnerEmail() != null && e.getOwnerEmail().equals(userEmail))
                .filter(e -> e.getFolder() == folder)
                .collect(Collectors.toList());
    }

    public Email getEmail(String id) {
        return emails.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void deleteEmail(String id) {
        emails.removeIf(e -> e.getId().equals(id));
        saveEmails();
    }

    public void moveToTrash(String id) {
        Email email = getEmail(id);
        if (email != null) {
            email.setFolder(FolderType.TRASH);
            saveEmails();
        }
    }

    public void markAsRead(String id) {
        Email email = getEmail(id);
        if (email != null) {
            email.setRead(true);
            saveEmails();
        }
    }

    public List<Email> searchEmails(String userEmail, String query, String type) {
        return emails.stream()
                .filter(e -> e.getOwnerEmail().equals(userEmail))
                .filter(e -> {
                    if ("SUBJECT".equals(type)) {
                        return e.getSubject().toLowerCase().contains(query.toLowerCase());
                    } else if ("FROM".equals(type)) {
                        return e.getFrom().toLowerCase().contains(query.toLowerCase());
                    } else {
                        return e.getBody().toLowerCase().contains(query.toLowerCase());
                    }
                })
                .collect(Collectors.toList());
    }

    public String saveDraft(Email email) {
        if (email.getId() == null || email.getId().isEmpty()) {
            email.setId(UUID.randomUUID().toString());
        }
        email.setFolder(FolderType.DRAFTS);
        // OwnerEmail set edilmemişse from'dan al
        if (email.getOwnerEmail() == null || email.getOwnerEmail().isEmpty()) {
            email.setOwnerEmail(email.getFrom());
        }
        emails.add(email);
        saveEmails();
        return email.getId();
    }

    public boolean starEmail(String id) {
        Email email = getEmail(id);
        if (email != null) {
            email.setStarred(true);
            saveEmails();
            return true;
        }
        return false;
    }

    public boolean unstarEmail(String id) {
        Email email = getEmail(id);
        if (email != null) {
            email.setStarred(false);
            saveEmails();
            return true;
        }
        return false;
    }

    public int getTotalEmailCount() {
        return emails.size();
    }

    public List<Email> getAllEmails() {
        return new ArrayList<>(emails);
    }

    private static void initializeSampleEmails() {
        // user1'e gelen emailler
        addSampleEmail("admin@nyp.com", "user1@nyp.com", 
            "Hoşgeldiniz!", 
            "NYP Email Sistemine hoş geldiniz. Bu örnek bir mesajdır.",
            FolderType.INBOX, false, Priority.NORMAL, -2);

        addSampleEmail("user2@nyp.com", "user1@nyp.com",
            "Proje Toplantısı",
            "Yarın saat 14:00'te proje toplantımız var. Lütfen hazırlıklı gelin.",
            FolderType.INBOX, true, Priority.HIGH, -1);

        // user1'in gönderdiği emailler
        addSampleEmail("user1@nyp.com", "user2@nyp.com",
            "Re: Proje Toplantısı",
            "Tamam, hazırlıklarımı yapacağım.",
            FolderType.SENT, true, Priority.NORMAL, -1);

        // user1'in taslağı
        Email draft1 = new Email();
        draft1.setId(UUID.randomUUID().toString());
        draft1.setFrom("user1@nyp.com");
        draft1.setOwnerEmail("user1@nyp.com");
        draft1.addTo("admin@nyp.com");
        draft1.setSubject("Rapor Taslağı");
        draft1.setBody("Ay sonu raporunu hazırlıyorum...");
        draft1.setFolder(FolderType.DRAFTS);
        draft1.setSentDate(new Date());
        emails.add(draft1);

        // user2'ye gelen emailler
        addSampleEmail("admin@nyp.com", "user2@nyp.com",
            "Sistem Duyurusu",
            "Sistemde bakım çalışması yapılacaktır.",
            FolderType.INBOX, false, Priority.NORMAL, -3);

        addSampleEmail("user1@nyp.com", "user2@nyp.com",
            "Re: Proje Toplantısı",
            "Tamam, hazırlıklarımı yapacağım.",
            FolderType.INBOX, false, Priority.NORMAL, -1);

        // user2'nin gönderdiği
        addSampleEmail("user2@nyp.com", "user1@nyp.com",
            "Proje Toplantısı",
            "Yarın saat 14:00'te proje toplantımız var.",
            FolderType.SENT, true, Priority.HIGH, -1);

        // user2'nin çöp kutusunda
        Email trash1 = new Email();
        trash1.setId(UUID.randomUUID().toString());
        trash1.setFrom("spam@example.com");
        trash1.setOwnerEmail("user2@nyp.com");
        trash1.addTo("user2@nyp.com");
        trash1.setSubject("Spam Mesaj");
        trash1.setBody("Bu istenmeyen bir mesajdır.");
        trash1.setFolder(FolderType.TRASH);
        trash1.setSentDate(new Date(System.currentTimeMillis() - 86400000 * 5)); // 5 gün önce
        emails.add(trash1);

        // Admin'e gelen
        addSampleEmail("user1@nyp.com", "admin@nyp.com",
            "Yardım Talebi",
            "Sisteme erişimde sorun yaşıyorum. Yardımcı olabilir misiniz?",
            FolderType.INBOX, false, Priority.HIGH, -1);

        System.out.println("✅ " + emails.size() + " örnek email yüklendi");
    }

    private static void addSampleEmail(String from, String to, String subject, String body, 
                                FolderType folder, boolean read, Priority priority, int daysAgo) {
        Email email = new Email();
        email.setId(UUID.randomUUID().toString());
        email.setFrom(from);
        
        // OwnerEmail'i folder tipine göre ayarla
        if (folder == FolderType.SENT || folder == FolderType.DRAFTS) {
            email.setOwnerEmail(from); // Gönderen kişinin kendi maili
        } else {
            email.setOwnerEmail(to); // Alıcının maili
        }
        
        email.addTo(to);
        email.setSubject(subject);
        email.setBody(body);
        email.setFolder(folder);
        email.setRead(read);
        email.setPriority(priority);
        
        long daysInMillis = (long) daysAgo * 86400000L;
        email.setSentDate(new Date(System.currentTimeMillis() + daysInMillis));
        
        emails.add(email);
    }
}
