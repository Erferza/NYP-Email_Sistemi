package com.nyp.backend.model;

import com.nyp.backend.enums.FolderType;
import com.nyp.backend.enums.Priority;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "emails")
public class Email {

    @Id
    private String id;

    private String ownerEmail; // The user who sees this email in their mailbox
    private FolderType folder; // INBOX, SENT, TRASH, etc.

    private String from;
    private List<String> to = new ArrayList<>();
    private List<String> cc = new ArrayList<>();
    private List<String> bcc = new ArrayList<>();

    private String subject;
    private String body;
    private Date sentDate = new Date();
    private boolean isRead = false;
    private boolean isStarred = false;
    private Priority priority = Priority.NORMAL;

    private List<Attachment> attachments = new ArrayList<>();

    public Email(String ownerEmail, FolderType folder, String from, List<String> to, String subject, String body) {
        this.ownerEmail = ownerEmail;
        this.folder = folder;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.sentDate = new Date();
    }
}
