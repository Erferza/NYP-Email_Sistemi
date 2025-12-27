package com.nyp.backend.repository;

import com.nyp.backend.enums.FolderType;
import com.nyp.backend.model.Email;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends MongoRepository<Email, String> {
    List<Email> findByOwnerEmailAndFolder(String ownerEmail, FolderType folder);

    List<Email> findByOwnerEmail(String ownerEmail);

    List<Email> findByOwnerEmailAndIsStarredTrue(String ownerEmail);

    // Search methods
    List<Email> findByOwnerEmailAndSubjectContainingIgnoreCase(String ownerEmail, String subject);

    List<Email> findByOwnerEmailAndFromContainingIgnoreCase(String ownerEmail, String from);

    List<Email> findByOwnerEmailAndBodyContainingIgnoreCase(String ownerEmail, String body);
}
