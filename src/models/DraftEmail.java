package models;

import java.util.Date;

/**
 * Taslak e-posta sınıfı - Email sınıfından kalıtım alır
 */
public class DraftEmail extends Email {
    
    private static final long serialVersionUID = 1L;
    
    private Date lastModified;
    private boolean autoSaved;
    
    public DraftEmail() {
        super();
        this.lastModified = new Date();
        this.autoSaved = false;
    }
    
    public DraftEmail(String from, String to, String subject, String body) {
        super(from, to, subject, body);
        this.lastModified = new Date();
        this.autoSaved = false;
    }
    
    /**
     * Email nesnesinden DraftEmail oluşturur
     */
    public DraftEmail(Email email) {
        super();
        this.setFrom(email.getFrom());
        this.setTo(email.getTo());
        this.setCc(email.getCc());
        this.setBcc(email.getBcc());
        this.setSubject(email.getSubject());
        this.setBody(email.getBody());
        this.setPriority(email.getPriority());
        this.setAttachments(email.getAttachments());
        this.lastModified = new Date();
        this.autoSaved = false;
    }
    
    // Getters and Setters
    public Date getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    public boolean isAutoSaved() {
        return autoSaved;
    }
    
    public void setAutoSaved(boolean autoSaved) {
        this.autoSaved = autoSaved;
    }
    
    /**
     * Taslağı günceller ve son değişiklik tarihini ayarlar
     */
    public void updateDraft(String subject, String body) {
        this.setSubject(subject);
        this.setBody(body);
        this.lastModified = new Date();
    }
    
    /**
     * Taslağı otomatik kaydeder
     */
    public void autoSave() {
        this.lastModified = new Date();
        this.autoSaved = true;
    }
    
    /**
     * Taslağı normal e-postaya dönüştürür
     */
    public Email toEmail() {
        Email email = new Email();
        email.setFrom(this.getFrom());
        email.setTo(this.getTo());
        email.setCc(this.getCc());
        email.setBcc(this.getBcc());
        email.setSubject(this.getSubject());
        email.setBody(this.getBody());
        email.setPriority(this.getPriority());
        email.setAttachments(this.getAttachments());
        email.setSentDate(new Date());
        return email;
    }
    
    @Override
    public String toString() {
        return "DraftEmail{" +
                "id=" + getId() +
                ", subject='" + getSubject() + '\'' +
                ", lastModified=" + lastModified +
                ", autoSaved=" + autoSaved +
                '}';
    }
}
