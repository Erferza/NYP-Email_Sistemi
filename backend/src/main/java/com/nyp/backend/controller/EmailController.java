package com.nyp.backend.controller;

import com.nyp.backend.enums.FolderType;
import com.nyp.backend.model.Email;
import com.nyp.backend.service.EmailService;
import com.nyp.backend.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/emails/send")
    public ResponseEntity<?> sendEmail(@RequestBody Email email) {
        try {
            emailService.sendEmail(email);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending email: " + e.getMessage());
        }
    }

    @GetMapping("/emails")
    public ResponseEntity<List<Email>> getEmails(@RequestParam String email, @RequestParam FolderType folder) {
        System.out.println("Getting emails for: " + email + ", folder: " + folder);
        List<Email> emails = emailService.getEmails(email, folder);
        System.out.println("Found " + emails.size() + " emails");
        return ResponseEntity.ok(emails);
    }

    @GetMapping("/emails/{id}")
    public ResponseEntity<Email> getEmail(@PathVariable String id) {
        Email e = emailService.getEmail(id);
        if (e != null)
            return ResponseEntity.ok(e);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/emails/{id}")
    public ResponseEntity<?> deleteEmail(@PathVariable String id) {
        emailService.deleteEmail(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/emails/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        emailService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/emails/search")
    public ResponseEntity<List<Email>> searchEmails(@RequestParam String email,
            @RequestParam String query,
            @RequestParam(defaultValue = "SUBJECT") String type) {
        return ResponseEntity.ok(emailService.searchEmails(email, query, type));
    }

    @PostMapping("/emails/draft")
    public ResponseEntity<String> saveDraft(@RequestBody Email email) {
        try {
            // Draft olarak kaydet
            email.setFolder(FolderType.DRAFTS);
            email.setOwnerEmail(email.getFrom());
            email.setSentDate(new Date());
            email.setRead(true); // Drafts are always "read"

            String savedId = emailService.saveDraftAndReturnId(email);
            System.out.println("Draft saved with ID: " + savedId);

            return ResponseEntity.ok(savedId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving draft: " + e.getMessage());
        }
    }

    @PutMapping("/emails/{id}/star")
    public ResponseEntity<String> starEmail(@PathVariable String id) {
        try {
            boolean success = emailService.starEmail(id);
            if (success) {
                return ResponseEntity.ok("Email starred");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error starring email");
        }
    }

    @PutMapping("/emails/{id}/unstar")
    public ResponseEntity<String> unstarEmail(@PathVariable String id) {
        try {
            boolean success = emailService.unstarEmail(id);
            if (success) {
                return ResponseEntity.ok("Email unstarred");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error unstarring email");
        }
    }
}
