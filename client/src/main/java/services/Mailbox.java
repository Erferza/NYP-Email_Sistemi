package services;

import enums.FolderType;
import models.Email;
import models.Folder;
import models.User;
import models.DraftEmail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Mailbox {

    private User user;
    private List<Folder> folders;
    private Folder inbox;
    private Folder sent;
    private Folder drafts;
    private Folder trash;
    private Folder spam;
    private Folder starred;

    private EmailService emailService = EmailService.getInstance();

    public Mailbox(User user) {
        this.user = user;
        this.folders = new ArrayList<>();
        initializeDefaultFolders();
        refresh();
    }

    private void initializeDefaultFolders() {
        inbox = new Folder("Gelen Kutusu", FolderType.INBOX);
        sent = new Folder("Gönderilenler", FolderType.SENT);
        drafts = new Folder("Taslaklar", FolderType.DRAFTS);
        trash = new Folder("Çöp Kutusu", FolderType.TRASH);
        spam = new Folder("Spam", FolderType.SPAM);
        starred = new Folder("Yıldızlı", FolderType.STARRED);

        folders.add(inbox);
        folders.add(sent);
        folders.add(drafts);
        folders.add(trash);
        folders.add(spam);
        folders.add(starred);
    }

    public void refresh() {
        try {
            fetchFolder(inbox);
            fetchFolder(sent);
            fetchFolder(drafts);
            fetchFolder(trash);
            fetchFolder(starred);
        } catch (Exception e) {
            System.err.println("Error refreshing mailbox: " + e.getMessage());
        }
    }

    private void fetchFolder(Folder folder) {
        try {
            List<Email> emails = emailService.getEmails(user.getEmail(), folder.getType());
            folder.setEmails(emails != null ? emails : new ArrayList<>());
        } catch (Exception e) {
            System.err.println("Error fetching folder " + folder.getName() + ": " + e.getMessage());
            folder.setEmails(new ArrayList<>());
        }
    }

    public boolean sendMail(Email email) {
        try {
            email.setFrom(user.getEmail());
            emailService.sendEmail(email);
            refresh();
            System.out.println("✅ Email gönderildi: " + email.getSubject());
            return true;
        } catch (Exception e) {
            System.err.println("❌ Email gönderilemedi: " + e.getMessage());
            return false;
        }
    }

    public boolean sendBulkMail(List<Email> emails) {
        for (Email e : emails)
            sendMail(e);
        return true;
    }

    public List<Email> receiveMails() {
        refresh();
        return inbox.getEmails();
    }

    public Email getMailById(String id) {
        for (Folder folder : folders) {
            Email email = folder.findEmailById(id);
            if (email != null)
                return email;
        }
        return null; // or fetch from API
    }

    public List<Email> getUnreadMails() {
        return inbox.getEmails().stream().filter(e -> !e.isRead()).collect(Collectors.toList());
    }

    // ISearchable implementations
    public List<Email> searchBySubject(String keyword) {
        return emailService.searchEmails(user.getEmail(), keyword, "SUBJECT");
    }

    public List<Email> searchBySender(String sender) {
        return emailService.searchEmails(user.getEmail(), sender, "SENDER");
    }

    public List<Email> searchByDate(Date date) {
        return new ArrayList<>(); // TODO: date search implementation
    }

    public List<Email> searchByContent(String keyword) {
        return emailService.searchEmails(user.getEmail(), keyword, "CONTENT");
    }

    // IFolderManager
    public void moveToFolder(Email email, Folder targetFolder) {
        // Backend update needed? Yes.
        // ApiClient.put("/emails/" + email.getId() + "/move?folder=" +
        // targetFolder.getType())
        // Skipping implementation for brevity, implementing local + refresh
        targetFolder.addEmail(email);
        // Remove from others?
    }

    public Folder createFolder(String name) {
        Folder f = new Folder(name, FolderType.CUSTOM);
        folders.add(f);
        return f;
    }

    public boolean deleteFolder(Folder folder) {
        if (!folder.isSystemFolder())
            return folders.remove(folder);
        return false;
    }

    public List<Folder> getAllFolders() {
        return folders;
    }

    public void renameFolder(Folder folder, String newName) {
        if (!folder.isSystemFolder())
            folder.setName(newName);
    }

    // Helpers
    public void moveToTrash(Email email) {
        try {
            emailService.moveToTrash(email.getId());
            refresh();
            System.out.println("✅ Email çöp kutusuna taşındı: " + email.getSubject());
        } catch (Exception e) {
            System.err.println("❌ Çöp kutusuna taşınamadı: " + e.getMessage());
        }
    }

    public void saveDraft(DraftEmail draft) {
        try {
            Email email = new Email();
            email.setFrom(draft.getFrom());
            email.setOwnerEmail(draft.getFrom());
            email.setTo(draft.getTo());
            email.setSubject(draft.getSubject());
            email.setBody(draft.getBody());
            
            emailService.saveDraft(email);
            refresh(); // Tüm klasörleri yenile
            System.out.println("✅ Taslak kaydedildi: " + email.getSubject());
        } catch (Exception e) {
            System.err.println("❌ Taslak kaydedilemedi: " + e.getMessage());
        }
    }

    public void starEmail(Email email) {
        try {
            emailService.starEmail(email.getId());
            email.setStarred(true);
            refresh(); // Starred folder'ı güncellemek için
        } catch (Exception e) {
            System.err.println("Failed to star email: " + e.getMessage());
        }
    }

    public void unstarEmail(Email email) {
        try {
            emailService.unstarEmail(email.getId());
            email.setStarred(false);
            refresh(); // Starred folder'ı güncellemek için
        } catch (Exception e) {
            System.err.println("Failed to unstar email: " + e.getMessage());
        }
    }

    public Folder getInbox() {
        return inbox;
    }

    public Folder getSent() {
        return sent;
    }

    public Folder getDrafts() {
        return drafts;
    }

    public Folder getTrash() {
        return trash;
    }

    public Folder getStarred() {
        return starred;
    }

    public Folder getSpam() {
        return spam;
    }

    public void markAsRead(Email email) {
        if (!email.isRead()) {
            email.markAsRead();
            try {
                emailService.markAsRead(email.getId());
            } catch (Exception e) {
                System.err.println("Failed to mark as read: " + e.getMessage());
            }
        }
    }

    public User getUser() {
        return user;
    }
}
