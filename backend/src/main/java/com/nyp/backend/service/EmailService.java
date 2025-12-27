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

    public void sendEmail(Email email) {
        // 1. Save to Sender's SENT box
        email.setFolder(FolderType.SENT);
        email.setOwnerEmail(email.getFrom());
        email.setRead(true);
        emailRepository.save(email);

        // 2. Distribute to Receivers
        for (String recipient : email.getTo()) {
            if (userRepository.existsByEmail(recipient)) {
                Email copy = copyEmailForRecipient(email, recipient, FolderType.INBOX);
                emailRepository.save(copy);
            }
        }

        // Handle CC
        for (String recipient : email.getCc()) {
            if (userRepository.existsByEmail(recipient)) {
                Email copy = copyEmailForRecipient(email, recipient, FolderType.INBOX);
                emailRepository.save(copy);
            }
        }

        // Handle BCC (Recipient sees it, but others don't know - basic impl)
        for (String recipient : email.getBcc()) {
            if (userRepository.existsByEmail(recipient)) {
                Email copy = copyEmailForRecipient(email, recipient, FolderType.INBOX);
                emailRepository.save(copy);
            }
        }
    }

    private Email copyEmailForRecipient(Email original, String owner, FolderType folder) {
        Email copy = new Email();
        copy.setOwnerEmail(owner);
        copy.setFolder(folder);
        copy.setFrom(original.getFrom());
        copy.setTo(original.getTo());
        copy.setCc(original.getCc());
        // BCC is usually stripped or hidden, but for simplicity we keep usage minimal
        copy.setSubject(original.getSubject());
        copy.setBody(original.getBody());
        copy.setSentDate(original.getSentDate());
        copy.setAttachments(original.getAttachments());
        copy.setPriority(original.getPriority());
        copy.setRead(false);
        return copy;
    }

    public List<Email> getEmails(String email, FolderType folder) {
        return emailRepository.findByOwnerEmailAndFolder(email, folder);
    }

    public Email getEmail(String id) {
        return emailRepository.findById(id).orElse(null);
    }

    public void deleteEmail(String id) {
        Optional<Email> email = emailRepository.findById(id);
        if (email.isPresent()) {
            Email e = email.get();
            e.setFolder(FolderType.TRASH);
            emailRepository.save(e);
        }
    }

    public void markAsRead(String id) {
        Optional<Email> email = emailRepository.findById(id);
        if (email.isPresent()) {
            Email e = email.get();
            e.setRead(true);
            emailRepository.save(e);
        }
    }

    public List<Email> searchEmails(String email, String query, String type) {
        switch (type.toUpperCase()) {
            case "SUBJECT":
                return emailRepository.findByOwnerEmailAndSubjectContainingIgnoreCase(email, query);
            case "SENDER":
                return emailRepository.findByOwnerEmailAndFromContainingIgnoreCase(email, query);
            case "CONTENT":
                return emailRepository.findByOwnerEmailAndBodyContainingIgnoreCase(email, query);
            default:
                return List.of();
        }
    }
}
