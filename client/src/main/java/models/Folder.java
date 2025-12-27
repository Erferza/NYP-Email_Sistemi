package models;

import enums.FolderType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * E-posta klasörü model sınıfı
 */
public class Folder implements Serializable {

    private static final long serialVersionUID = 1L;
    private static int idCounter = 1;

    private int id;
    private String name;
    private FolderType type;
    private List<Email> emails;
    private boolean isSystemFolder;

    public Folder() {
        this.id = idCounter++;
        this.emails = new ArrayList<>();
        this.isSystemFolder = false;
    }

    public Folder(String name, FolderType type) {
        this();
        this.name = name;
        this.type = type;
        this.isSystemFolder = (type != FolderType.CUSTOM);
    }

    public Folder(String name) {
        this(name, FolderType.CUSTOM);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FolderType getType() {
        return type;
    }

    public void setType(FolderType type) {
        this.type = type;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public boolean isSystemFolder() {
        return isSystemFolder;
    }

    public void setSystemFolder(boolean systemFolder) {
        isSystemFolder = systemFolder;
    }

    // İş mantığı metodları

    /**
     * Klasöre e-posta ekler
     */
    public void addEmail(Email email) {
        if (!emails.contains(email)) {
            emails.add(email);
        }
    }

    /**
     * Klasörden e-posta kaldırır
     */
    public void removeEmail(Email email) {
        emails.remove(email);
    }

    /**
     * Klasördeki e-posta sayısını döndürür
     */
    public int getEmailCount() {
        return emails.size();
    }

    /**
     * Klasördeki okunmamış e-posta sayısını döndürür
     */
    public int getUnreadCount() {
        return (int) emails.stream().filter(e -> !e.isRead()).count();
    }

    /**
     * Klasörü boşaltır
     */
    public void clearFolder() {
        emails.clear();
    }

    /**
     * Belirli ID'ye sahip e-postayı bulur
     */
    public Email findEmailById(String emailId) {
        return emails.stream()
                .filter(e -> e.getId() != null && e.getId().equals(emailId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Klasör adını ve okunmamış sayısını döndürür (UI için)
     */
    public String getDisplayName() {
        int unread = getUnreadCount();
        if (unread > 0) {
            return name + " (" + unread + ")";
        }
        return name;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", emailCount=" + getEmailCount() +
                '}';
    }
}
