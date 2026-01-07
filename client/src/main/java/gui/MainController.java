package gui;

import enums.FolderType;
import enums.UserRole;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Email;
import models.Folder;
import models.User;
import services.Mailbox;
import services.UserService;

import java.io.IOException;

public class MainController {

    @FXML private VBox sidebar;
    @FXML private VBox adminSection;
    @FXML private Button composeButton;
    @FXML private Button inboxButton;
    @FXML private Button sentButton;
    @FXML private Button draftsButton;
    @FXML private Button starredButton;
    @FXML private Button trashButton;
    @FXML private Button adminButton;
    @FXML private Button logoutButton;
    @FXML private Label userNameLabel;
    @FXML private Label folderNameLabel;
    @FXML private Label emailCountLabel;
    @FXML private ListView<Email> emailListView;
    @FXML private VBox detailView;
    @FXML private VBox detailContainer;

    private User user;
    private Mailbox mailbox;
    private Folder currentFolder;

    public MainController(User user, Mailbox mailbox) {
        this.user = user;
        this.mailbox = mailbox;
        this.currentFolder = mailbox.getInbox();
    }

    @FXML
    private void initialize() {
        userNameLabel.setText(user.getFullName());
        
        // Admin butonunu göster
        if (user.getRole() == UserRole.ADMIN) {
            adminSection.setVisible(true);
            adminSection.setManaged(true);
        }
        
        // Email listesini ayarla
        emailListView.setCellFactory(lv -> new EmailListCell());
        emailListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Email selected = emailListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showEmailDetail(selected);
                }
            }
        });
        
        // İlk folder'ı yükle
        showInbox();
    }

    @FXML
    private void handleCompose() {
        openComposeWindow(null);
    }

    @FXML
    private void showInbox() {
        loadFolder(mailbox.getInbox(), "Gelen Kutusu");
    }

    @FXML
    private void showSent() {
        loadFolder(mailbox.getSent(), "Gönderilenler");
    }

    @FXML
    private void showDrafts() {
        loadFolder(mailbox.getDrafts(), "Taslaklar");
    }

    @FXML
    private void showStarred() {
        loadFolder(mailbox.getStarred(), "Yıldızlı");
    }

    @FXML
    private void showTrash() {
        loadFolder(mailbox.getTrash(), "Çöp Kutusu");
    }

    @FXML
    private void handleAdminDashboard() {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(adminButton.getScene().getWindow());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin_dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 950, 700);
            scene.getStylesheets().add(getClass().getResource("/gui/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Admin Dashboard açılamadı: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        UserService.getInstance().logout();
        App.logout();
    }

    private void loadFolder(Folder folder, String name) {
        currentFolder = folder;
        folderNameLabel.setText(name);
        emailListView.getItems().clear();
        emailListView.getItems().addAll(folder.getEmails());
        emailCountLabel.setText(folder.getEmails().size() + " e-posta");
        
        // Detail'i temizle
        detailContainer.getChildren().clear();
        Label placeholder = new Label("Önizlemek için bir e-posta seçin");
        placeholder.setStyle("-fx-text-fill: #585b70; -fx-font-size: 16px;");
        detailContainer.getChildren().add(placeholder);
    }

    private void showEmailDetail(Email email) {
        detailContainer.getChildren().clear();
        
        VBox detail = new VBox(15);
        detail.setStyle("-fx-padding: 20; -fx-background-color: #1e1e2e;");
        
        // Action buttons
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button replyBtn = new Button("↩️ Yanıtla");
        replyBtn.getStyleClass().add("secondary-button");
        replyBtn.setOnAction(e -> openComposeWindow(email));
        
        Button deleteBtn = new Button("🗑️ Sil");
        deleteBtn.getStyleClass().add("danger-button");
        deleteBtn.setOnAction(e -> {
            mailbox.moveToTrash(email);
            // Mevcut folder'ı yeniden yükle ve detail view'i temizle
            loadFolder(currentFolder, folderNameLabel.getText());
            clearDetailView();
        });
        
        Button starBtn = new Button(email.isStarred() ? "⭐ Yıldızlı" : "☆ Yıldızla");
        starBtn.getStyleClass().add("secondary-button");
        starBtn.setOnAction(e -> {
            if (email.isStarred()) {
                mailbox.unstarEmail(email);
            } else {
                mailbox.starEmail(email);
            }
            // Mevcut folder'ı yeniden yükle
            mailbox.refresh();
            loadFolder(currentFolder, folderNameLabel.getText());
            // Email nesnesinin starred durumunu güncelle ve detail view'i yeniden göster
            Email updatedEmail = mailbox.getMailById(email.getId());
            if (updatedEmail != null) {
                showEmailDetail(updatedEmail);
            }
        });
        
        actionBar.getChildren().addAll(replyBtn, starBtn, deleteBtn);
        
        Label subject = new Label(email.getSubject());
        subject.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
        
        HBox metaBox = new HBox(20);
        Label from = new Label("📨 Kimden: " + email.getFrom());
        from.setStyle("-fx-text-fill: #89b4fa;");
        Label date = new Label("📅 Tarih: " + email.getFormattedDate());
        date.setStyle("-fx-text-fill: #a6adc8;");
        metaBox.getChildren().addAll(from, date);
        
        Separator sep = new Separator();
        
        javafx.scene.control.ScrollPane bodyScroll = new javafx.scene.control.ScrollPane();
        bodyScroll.setFitToWidth(true);
        bodyScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        Label body = new Label(email.getBody());
        body.setWrapText(true);
        body.setStyle("-fx-text-fill: #cdd6f4; -fx-font-size: 14px; -fx-line-spacing: 5px;");
        bodyScroll.setContent(body);
        
        detail.getChildren().addAll(actionBar, subject, metaBox, sep, bodyScroll);
        detailContainer.getChildren().add(detail);
        
        // Mark as read
        if (!email.isRead()) {
            mailbox.markAsRead(email);
            emailListView.refresh();
        }
    }

    private void openComposeWindow(Email replyTo) {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(composeButton.getScene().getWindow());

            ComposeView composeView = new ComposeView(mailbox, user, replyTo);
            Scene scene = new Scene(composeView.getView(), 600, 500);
            scene.getStylesheets().add(getClass().getResource("/gui/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle(replyTo == null ? "Yeni E-posta" : "Yanıtla");
            stage.showAndWait();

            // Refresh
            loadFolder(currentFolder, folderNameLabel.getText());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Compose penceresi açılamadı: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearDetailView() {
        detailContainer.getChildren().clear();
        Label placeholder = new Label("Önizlemek için bir e-posta seçin");
        placeholder.setStyle("-fx-text-fill: #585b70; -fx-font-size: 16px;");
        detailContainer.getChildren().add(placeholder);
    }

    // Email list cell
    private static class EmailListCell extends ListCell<Email> {
        @Override
        protected void updateItem(Email email, boolean empty) {
            super.updateItem(email, empty);
            if (empty || email == null) {
                setText(null);
                setGraphic(null);
                setStyle("");
            } else {
                VBox content = new VBox(5);
                content.setStyle("-fx-padding: 8;");
                
                HBox headerBox = new HBox(10);
                headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                
                Label from = new Label(email.getFrom());
                from.setStyle("-fx-font-weight: " + (email.isRead() ? "normal" : "bold") + "; -fx-text-fill: #cdd6f4;");
                
                Label date = new Label(email.getFormattedDate());
                date.setStyle("-fx-text-fill: #6c7086; -fx-font-size: 11px;");
                
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                headerBox.getChildren().addAll(from, spacer, date);
                
                Label subject = new Label(email.getSubject());
                subject.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (email.isRead() ? "#a6adc8" : "#cdd6f4") + ";");
                
                String preview = email.getBody().length() > 60 ? 
                    email.getBody().substring(0, 60) + "..." : email.getBody();
                Label bodyPreview = new Label(preview);
                bodyPreview.setStyle("-fx-text-fill: #6c7086; -fx-font-size: 12px;");
                
                content.getChildren().addAll(headerBox, subject, bodyPreview);
                
                if (email.isStarred()) {
                    Label star = new Label("⭐");
                    star.setStyle("-fx-font-size: 10px;");
                    headerBox.getChildren().add(1, star);
                }
                
                setGraphic(content);
                setText(null);
                
                String bgColor = email.isRead() ? "transparent" : "#313244";
                setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 5;");
            }
        }
    }
}
