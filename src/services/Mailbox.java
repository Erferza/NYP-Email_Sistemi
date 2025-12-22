package services;

import enums.FolderType;
import exceptions.MailSendException;
import interfaces.IFolderManager;
import interfaces.IMailReceiver;
import interfaces.IMailSender;
import interfaces.ISearchable;
import models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Posta kutusu servis sınıfı
 * Tüm e-posta işlemlerini yönetir
 * Birden fazla interface'i implement eder (Çoklu kalıtım benzeri yapı)
 */
public class Mailbox implements IMailSender, IMailReceiver, ISearchable, IFolderManager {
    
    private User user;
    private List<Folder> folders;
    private Folder inbox;
    private Folder sent;
    private Folder drafts;
    private Folder trash;
    private Folder spam;
    private Folder starred;
    
    public Mailbox(User user) {
        this.user = user;
        this.folders = new ArrayList<>();
        initializeDefaultFolders();
    }
    
    /**
     * Varsayılan klasörleri oluşturur
     */
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
    
    // ==================== IMailSender Implementation ====================
    
    @Override
    public boolean sendMail(Email email) throws MailSendException {
        if (email == null) {
            throw new MailSendException("", "E-posta boş olamaz!");
        }
        
        if (email.getTo() == null || email.getTo().isEmpty()) {
            throw new MailSendException("", "Alıcı adresi belirtilmedi!");
        }
        
        // E-postayı gönder
        email.setSentDate(new Date());
        email.setFrom(user.getEmail());
        
        // Gönderilenlere ekle
        sent.addEmail(email);
        
        System.out.println("E-posta gönderildi: " + email.getSubject());
        return true;
    }
    
    @Override
    public boolean sendBulkMail(List<Email> emails) throws MailSendException {
        if (emails == null || emails.isEmpty()) {
            throw new MailSendException("", "Gönderilecek e-posta listesi boş!");
        }
        
        for (Email email : emails) {
            sendMail(email);
        }
        return true;
    }
    
    // ==================== IMailReceiver Implementation ====================
    
    @Override
    public List<Email> receiveMails() {
        return inbox.getEmails();
    }
    
    @Override
    public Email getMailById(int id) {
        for (Folder folder : folders) {
            Email email = folder.findEmailById(id);
            if (email != null) {
                return email;
            }
        }
        return null;
    }
    
    @Override
    public List<Email> getUnreadMails() {
        return inbox.getEmails().stream()
                .filter(e -> !e.isRead())
                .collect(Collectors.toList());
    }
    
    // ==================== ISearchable Implementation ====================
    
    @Override
    public List<Email> searchBySubject(String keyword) {
        List<Email> results = new ArrayList<>();
        for (Folder folder : folders) {
            results.addAll(folder.getEmails().stream()
                    .filter(e -> e.getSubject() != null && 
                            e.getSubject().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        return results;
    }
    
    @Override
    public List<Email> searchBySender(String sender) {
        List<Email> results = new ArrayList<>();
        for (Folder folder : folders) {
            results.addAll(folder.getEmails().stream()
                    .filter(e -> e.getFrom() != null && 
                            e.getFrom().toLowerCase().contains(sender.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        return results;
    }
    
    @Override
    public List<Email> searchByDate(Date date) {
        List<Email> results = new ArrayList<>();
        for (Folder folder : folders) {
            results.addAll(folder.getEmails().stream()
                    .filter(e -> e.getSentDate() != null && 
                            isSameDay(e.getSentDate(), date))
                    .collect(Collectors.toList()));
        }
        return results;
    }
    
    @Override
    public List<Email> searchByContent(String keyword) {
        List<Email> results = new ArrayList<>();
        for (Folder folder : folders) {
            results.addAll(folder.getEmails().stream()
                    .filter(e -> (e.getSubject() != null && 
                            e.getSubject().toLowerCase().contains(keyword.toLowerCase())) ||
                            (e.getBody() != null && 
                            e.getBody().toLowerCase().contains(keyword.toLowerCase())))
                    .collect(Collectors.toList()));
        }
        return results;
    }
    
    /**
     * İki tarihin aynı gün olup olmadığını kontrol eder
     */
    private boolean isSameDay(Date date1, Date date2) {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);
    }
    
    // ==================== IFolderManager Implementation ====================
    
    @Override
    public void moveToFolder(Email email, Folder targetFolder) {
        // Önce e-postayı mevcut klasörden kaldır
        for (Folder folder : folders) {
            if (folder.getEmails().contains(email)) {
                folder.removeEmail(email);
                break;
            }
        }
        // Hedef klasöre ekle
        targetFolder.addEmail(email);
    }
    
    @Override
    public Folder createFolder(String name) {
        Folder newFolder = new Folder(name, FolderType.CUSTOM);
        folders.add(newFolder);
        return newFolder;
    }
    
    @Override
    public boolean deleteFolder(Folder folder) {
        if (folder.isSystemFolder()) {
            System.out.println("Sistem klasörleri silinemez!");
            return false;
        }
        
        // Klasördeki e-postaları çöp kutusuna taşı
        for (Email email : folder.getEmails()) {
            trash.addEmail(email);
        }
        
        return folders.remove(folder);
    }
    
    @Override
    public List<Folder> getAllFolders() {
        return new ArrayList<>(folders);
    }
    
    @Override
    public void renameFolder(Folder folder, String newName) {
        if (folder.isSystemFolder()) {
            System.out.println("Sistem klasörlerinin adı değiştirilemez!");
            return;
        }
        folder.setName(newName);
    }
    
    // ==================== Ek Metodlar ====================
    
    /**
     * E-postayı çöp kutusuna taşır
     */
    public void moveToTrash(Email email) {
        moveToFolder(email, trash);
    }
    
    /**
     * E-postayı spam klasörüne taşır
     */
    public void markAsSpam(Email email) {
        moveToFolder(email, spam);
    }
    
    /**
     * E-postayı yıldızlar
     */
    public void starEmail(Email email) {
        email.setStarred(true);
        if (!starred.getEmails().contains(email)) {
            starred.addEmail(email);
        }
    }
    
    /**
     * E-postanın yıldızını kaldırır
     */
    public void unstarEmail(Email email) {
        email.setStarred(false);
        starred.removeEmail(email);
    }
    
    /**
     * Çöp kutusunu boşaltır
     */
    public void emptyTrash() {
        trash.clearFolder();
    }
    
    /**
     * Taslak olarak kaydeder
     */
    public void saveDraft(DraftEmail draft) {
        drafts.addEmail(draft);
    }
    
    /**
     * Gelen kutusuna e-posta ekler (simülasyon için)
     */
    public void receiveEmail(Email email) {
        email.setReceivedDate(new Date());
        inbox.addEmail(email);
    }
    
    /**
     * Toplam e-posta sayısını döndürür
     */
    public int getTotalEmailCount() {
        return folders.stream().mapToInt(Folder::getEmailCount).sum();
    }
    
    /**
     * Toplam okunmamış e-posta sayısını döndürür
     */
    public int getTotalUnreadCount() {
        return folders.stream().mapToInt(Folder::getUnreadCount).sum();
    }
    
    // Getters
    public User getUser() {
        return user;
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
    
    public Folder getSpam() {
        return spam;
    }
    
    public Folder getStarred() {
        return starred;
    }
}
