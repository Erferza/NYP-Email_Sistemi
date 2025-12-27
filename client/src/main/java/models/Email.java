package models;

import enums.Priority;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * E-posta model sınıfı
 */
public class Email implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String from;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String body;
    private Date sentDate;
    private Date receivedDate;
    private boolean isRead;
    private boolean isStarred;
    private Priority priority;
    private List<Attachment> attachments;

    public Email() {
        this.to = new ArrayList<>();
        this.cc = new ArrayList<>();
        this.bcc = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.isRead = false;
        this.isStarred = false;
        this.priority = Priority.NORMAL;
        this.sentDate = new Date();
    }

    public Email(String from, String to, String subject, String body) {
        this();
        this.from = from;
        this.to.add(to);
        this.subject = subject;
        this.body = body;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void addTo(String recipient) {
        this.to.add(recipient);
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public void addCc(String recipient) {
        this.cc.add(recipient);
    }

    public List<String> getBcc() {
        return bcc;
    }

    public void setBcc(List<String> bcc) {
        this.bcc = bcc;
    }

    public void addBcc(String recipient) {
        this.bcc.add(recipient);
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

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
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

    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
    }

    // İş mantığı metodları

    /**
     * E-postayı okundu olarak işaretler
     */
    public void markAsRead() {
        this.isRead = true;
    }

    /**
     * E-postayı okunmadı olarak işaretler
     */
    public void markAsUnread() {
        this.isRead = false;
    }

    /**
     * E-postayı yıldızlar/yıldızı kaldırır
     */
    public void toggleStar() {
        this.isStarred = !this.isStarred;
    }

    /**
     * E-postanın eki var mı kontrol eder
     */
    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    /**
     * Yanıtla - yeni e-posta oluşturur
     */
    public Email reply(String replyBody, String replyFrom) {
        Email reply = new Email();
        reply.setFrom(replyFrom);
        reply.addTo(this.from);
        reply.setSubject("Re: " + this.subject);
        reply.setBody(replyBody + "\n\n--- Orijinal Mesaj ---\n" + this.body);
        return reply;
    }

    /**
     * Tümünü yanıtla
     */
    public Email replyAll(String replyBody, String replyFrom) {
        Email reply = reply(replyBody, replyFrom);
        for (String recipient : this.to) {
            if (!recipient.equals(replyFrom)) {
                reply.addCc(recipient);
            }
        }
        for (String ccRecipient : this.cc) {
            if (!ccRecipient.equals(replyFrom)) {
                reply.addCc(ccRecipient);
            }
        }
        return reply;
    }

    /**
     * İlet - yeni e-posta oluşturur
     */
    public Email forward(String forwardFrom) {
        Email forward = new Email();
        forward.setFrom(forwardFrom);
        forward.setSubject("Fwd: " + this.subject);
        forward.setBody("--- İletilen Mesaj ---\n" +
                "Kimden: " + this.from + "\n" +
                "Tarih: " + getFormattedDate() + "\n" +
                "Konu: " + this.subject + "\n\n" +
                this.body);
        forward.setAttachments(new ArrayList<>(this.attachments));
        return forward;
    }

    /**
     * Tarihi formatlanmış string olarak döndürür
     */
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sentDate != null ? sdf.format(sentDate) : "";
    }

    /**
     * Alıcıları virgülle ayrılmış string olarak döndürür
     */
    public String getToAsString() {
        return String.join(", ", to);
    }

    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", from='" + from + '\'' +
                ", to=" + to +
                ", subject='" + subject + '\'' +
                ", date=" + getFormattedDate() +
                ", isRead=" + isRead +
                '}';
    }
}
