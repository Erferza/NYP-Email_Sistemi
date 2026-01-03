package services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import enums.FolderType;
import exceptions.MailSendException;
import interfaces.IFolderManager;
import interfaces.IMailReceiver;
import interfaces.IMailSender;
import interfaces.ISearchable;
import models.Email;
import models.Folder;
import models.User;
import models.DraftEmail;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Mailbox implements IMailSender, IMailReceiver, ISearchable, IFolderManager {

    private User user;
    private List<Folder> folders;
    private Folder inbox;
    private Folder sent;
    private Folder drafts;
    private Folder trash;
    private Folder spam;
    private Folder starred;

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

    public Mailbox(User user) {
        this.user = user;
        this.folders = new ArrayList<>();
        initializeDefaultFolders();
        refresh();
    }

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

    public void refresh() {
        try {
            fetchFolder(inbox);
            fetchFolder(sent);
            fetchFolder(drafts);
            fetchFolder(trash);
            fetchFolder(starred); // Special case in backend? Or query?
        } catch (Exception e) {
            System.err.println("Error refreshing mailbox: " + e.getMessage());
        }
    }


  private void fetchFolder(Folder folder) {
    try {
      String url = "/emails?email=" + user.getEmail() + "&folder=" + folder.getType().toString();

      System.out.println("Fetching folder: " + folder.getName() + " with URL: " + url);

      String response = ApiClient.get(url);
      Type listType = new TypeToken<ArrayList<Email>>() {}.getType();
      List<Email> emails = gson.fromJson(response, listType);

      if (emails == null) {
        emails = new ArrayList<>();
      }

      System.out.println("Fetched " + emails.size() + " emails for folder: " + folder.getName());
      folder.setEmails(emails);

    } catch (Exception e) {
      System.err.println("Error fetching folder " + folder.getName() + ": " + e.getMessage());
      e.printStackTrace();
      folder.setEmails(new ArrayList<>());
    }
  }

    @Override
    public boolean sendMail(Email email) throws MailSendException {
        try {
            email.setFrom(user.getEmail());
            String json = gson.toJson(email);
            ApiClient.post("/emails/send", json);
            refresh(); // Get updated boxes
            return true;
        } catch (Exception e) {
            throw new MailSendException("", "API Error: " + e.getMessage());
        }
    }

    @Override
    public boolean sendBulkMail(List<Email> emails) throws MailSendException {
        for (Email e : emails)
            sendMail(e);
        return true;
    }

    @Override
    public List<Email> receiveMails() {
        refresh();
        return inbox.getEmails();
    }

    @Override
    public Email getMailById(String id) {
        for (Folder folder : folders) {
            Email email = folder.findEmailById(id);
            if (email != null)
                return email;
        }
        return null; // or fetch from API
    }

    @Override
    public List<Email> getUnreadMails() {
        return inbox.getEmails().stream().filter(e -> !e.isRead()).collect(Collectors.toList());
    }

    // ISearchable implementations (Memory based on fetched data)
    // ISearchable implementations - Now using Backend API
    @Override
    public List<Email> searchBySubject(String keyword) {
        return searchBackend(keyword, "SUBJECT");
    }

    @Override
    public List<Email> searchBySender(String sender) {
        return searchBackend(sender, "SENDER");
    }

    @Override
    public List<Email> searchByDate(Date date) {
        return new ArrayList<>(); // TODO date search
    }

    @Override
    public List<Email> searchByContent(String keyword) {
        return searchBackend(keyword, "CONTENT");
    }

    private List<Email> searchBackend(String query, String type) {
        try {
            // Encode query?
            String url = "/emails/search?email=" + user.getEmail() + "&query=" + query + "&type=" + type;
            // Simple encoding - better use URLEncoder in real app
            url = url.replace(" ", "%20");
            String response = ApiClient.get(url);
            Type listType = new TypeToken<ArrayList<Email>>() {
            }.getType();
            return gson.fromJson(response, listType);
        } catch (Exception e) {
            System.err.println("Search failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // IFolderManager
    @Override
    public void moveToFolder(Email email, Folder targetFolder) {
        // Backend update needed? Yes.
        // ApiClient.put("/emails/" + email.getId() + "/move?folder=" +
        // targetFolder.getType())
        // Skipping implementation for brevity, implementing local + refresh
        targetFolder.addEmail(email);
        // Remove from others?
    }

    @Override
    public Folder createFolder(String name) {
        Folder f = new Folder(name, FolderType.CUSTOM);
        folders.add(f);
        return f;
    }

    @Override
    public boolean deleteFolder(Folder folder) {
        if (!folder.isSystemFolder())
            return folders.remove(folder);
        return false;
    }

    @Override
    public List<Folder> getAllFolders() {
        return folders;
    }

    @Override
    public void renameFolder(Folder folder, String newName) {
        if (!folder.isSystemFolder())
            folder.setName(newName);
    }

    // Helpers
    public void moveToTrash(Email email) {
      try {
        System.out.println("Attempting to delete email ID: " + email.getId());
        System.out.println("Email subject: " + email.getSubject());

        ApiClient.delete("/emails/" + email.getId());
        System.out.println("Delete API call completed");
        refresh();

        System.out.println("Refresh completed. Trash count: " + trash.getEmails().size());
      } catch (Exception e) {
        System.err.println("Failed to move to trash: " + e.getMessage());
        e.printStackTrace();
      }
    }

  public void saveDraft(DraftEmail draft) {
    try {
      // DraftEmail'i normal Email'e dönüştür
      Email email = new Email();
      email.setFrom(draft.getFrom());
      email.setTo(draft.getTo());
      email.setSubject(draft.getSubject());
      email.setBody(draft.getBody());

      // Backend'e gönder
      String json = gson.toJson(email);
      String response = ApiClient.post("/emails/draft", json);

      System.out.println("Draft saved, response: " + response);

      // Taslaklar klasörünü yenile
      fetchFolder(drafts);

    } catch (Exception e) {
      System.err.println("Failed to save draft: " + e.getMessage());
      e.printStackTrace();
      // Fallback: sadece local kaydet
      drafts.addEmail(draft);
    }
  }

    public void starEmail(Email email) {
        try {
            ApiClient.put("/emails/" + email.getId() + "/star", "");
            email.setStarred(true);
            // Not: refresh() çağrısı MainView'da yapılıyor
        } catch (Exception e) {
            System.err.println("Failed to star email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void unstarEmail(Email email) {
        try {
            ApiClient.put("/emails/" + email.getId() + "/unstar", "");
            email.setStarred(false);
            // Not: refresh() çağrısı MainView'da yapılıyor
        } catch (Exception e) {
            System.err.println("Failed to unstar email: " + e.getMessage());
            e.printStackTrace();
        }
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

    public Folder getStarred() {
        return starred;
    }

    public Folder getSpam() {
        return spam;
    }

    public void markAsRead(Email email) {
        if (!email.isRead()) {
            email.markAsRead();
            try {
                ApiClient.put("/emails/" + email.getId() + "/read", "");
            } catch (Exception e) {
                System.err.println("Failed to mark as read: " + e.getMessage());
            }
        }
    }

    public User getUser() {
        return user;
    }
}
