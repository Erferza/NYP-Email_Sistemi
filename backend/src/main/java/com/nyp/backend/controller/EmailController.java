package com.nyp.backend.controller;

import com.nyp.backend.enums.FolderType;
import com.nyp.backend.model.Email;
import com.nyp.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody Email email) {
        try {
            emailService.sendEmail(email);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error sending email: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Email>> getEmails(@RequestParam String email, @RequestParam FolderType folder) {
        return ResponseEntity.ok(emailService.getEmails(email, folder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Email> getEmail(@PathVariable String id) {
        Email e = emailService.getEmail(id);
        if (e != null)
            return ResponseEntity.ok(e);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmail(@PathVariable String id) {
        emailService.deleteEmail(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String id) {
        emailService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Email>> searchEmails(@RequestParam String email,
            @RequestParam String query,
            @RequestParam(defaultValue = "SUBJECT") String type) {
        return ResponseEntity.ok(emailService.searchEmails(email, query, type));
    }
}
