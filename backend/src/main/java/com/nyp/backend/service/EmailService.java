
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
    // Yıldızlı klasörü için özel mantık: isStarred=true olan tüm e-postaları getir
    if (folder == FolderType.STARRED) {
      return emailRepository.findByOwnerEmailAndIsStarredTrue(email);
    }
    return emailRepository.findByOwnerEmailAndFolder(email, folder);
  }

  public Email getEmail(String id) {
    return emailRepository.findById(id).orElse(null);
  }

  public void deleteEmail(String id) {
    Optional<Email> emailOpt = emailRepository.findById(id);
    if (emailOpt.isPresent()) {
      Email email = emailOpt.get();

      System.out.println("Deleting email: " + email.getSubject() + " from folder: " + email.getFolder());

      // Eğer e-posta zaten çöp kutusundaysa, kalıcı olarak sil
      if (email.getFolder() == FolderType.TRASH) {
        System.out.println("Permanently deleting email from trash");
        emailRepository.delete(email);
      }
      // Eğer e-posta taslak klasöründeyse, çöp kutusuna taşı
      else if (email.getFolder() == FolderType.DRAFTS) {
        System.out.println("Moving draft to trash");
        email.setFolder(FolderType.TRASH);
        emailRepository.save(email);
      }
      // Diğer klasörlerden çöp kutusuna taşı
      else {
        System.out.println("Moving email from " + email.getFolder() + " to trash");
        email.setFolder(FolderType.TRASH);
        emailRepository.save(email);
      }
    } else {
      System.out.println("Email with id " + id + " not found");
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

  // Taslak kaydet ve ID döndür
  public String saveDraftAndReturnId(Email email) {
    email.setFolder(FolderType.DRAFTS);
    Email saved = emailRepository.save(email);
    return saved.getId();
  }

  // E-postayı yıldızla
  public boolean starEmail(String id) {
    Optional<Email> emailOpt = emailRepository.findById(id);
    if (emailOpt.isPresent()) {
      Email email = emailOpt.get();
      email.setStarred(true);
      emailRepository.save(email);
      System.out.println("Email starred: " + email.getSubject());
      return true;
    }
    return false;
  }

  // E-postanın yıldızını kaldır
  public boolean unstarEmail(String id) {
    Optional<Email> emailOpt = emailRepository.findById(id);
    if (emailOpt.isPresent()) {
      Email email = emailOpt.get();
      email.setStarred(false);
      emailRepository.save(email);
      System.out.println("Email unstarred: " + email.getSubject());
      return true;
    }
    return false;
  }

  // E-postayı kalıcı olarak silmek için method
  public void permanentDelete(String id) {
    emailRepository.deleteById(id);
  }

  // Taslakları kaydetmek için yeni method
  public void saveDraft(Email email) {
    email.setFolder(FolderType.DRAFTS);
    emailRepository.save(email);
  }

  // Çöp kutusundan geri yüklemek için method
  public void restoreFromTrash(String id, FolderType targetFolder) {
    Optional<Email> emailOpt = emailRepository.findById(id);
    if (emailOpt.isPresent()) {
      Email email = emailOpt.get();
      if (email.getFolder() == FolderType.TRASH) {
        email.setFolder(targetFolder);
        emailRepository.save(email);
      }
    }
  }
}