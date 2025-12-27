package gui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Email;
import models.Folder;
import models.User;
import services.Mailbox;
import utils.DateFormatter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainView {

    private BorderPane root;
    private User user;
    private Mailbox mailbox;

    private ListView<Email> emailListView;
    private VBox detailView;
    private Label detailSubject;
    private Label detailFrom;
    private Label detailDate;
    private TextArea detailBody; // Read-only
    private VBox detailContainer;

    private Folder currentFolder;

    public MainView(User user, Mailbox mailbox) {
        this.user = user;
        this.mailbox = mailbox;
        this.currentFolder = mailbox.getInbox();
        createView();
    }

    public Parent getView() {
        return root;
    }

    private void createView() {
        root = new BorderPane();
        root.getStyleClass().add("root");

        // 1. Sidebar (Left)
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(20, 10, 20, 10));

        // Compose Button
        Button composeBtn = new Button("✏️  Oluştur");
        composeBtn.getStyleClass().add("primary-button");
        composeBtn.setMaxWidth(Double.MAX_VALUE);
        composeBtn.setPrefHeight(45);
        composeBtn.setOnAction(e -> openComposeWindow());

        // Navigation
        VBox navBox = new VBox(5);
        navBox.getChildren().addAll(
                createNavButton("📥 Gelen Kutusu", mailbox.getInbox()),
                createNavButton("📤 Gönderilenler", mailbox.getSent()),
                createNavButton("📝 Taslaklar", mailbox.getDrafts()),
                createNavButton("⭐ Yıldızlı", mailbox.getStarred()),
                createNavButton("🗑️  Çöp Kutusu", mailbox.getTrash()));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // User Profile Mini
        HBox userProfile = new HBox(10);
        userProfile.setAlignment(Pos.CENTER_LEFT);
        userProfile.setPadding(new Insets(10));

        Circle avatar = new Circle(16, Color.web("#89b4fa"));
        Label userName = new Label(user.getFullName());
        userName.setStyle("-fx-font-weight: bold;");

        Button logoutBtn = new Button("🚪");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #f38ba8; -fx-cursor: hand;");
        logoutBtn.setTooltip(new Tooltip("Çıkış Yap"));
        logoutBtn.setOnAction(e -> App.logout());

        userProfile.getChildren().addAll(avatar, userName, logoutBtn);

        sidebar.getChildren().addAll(composeBtn, new Separator(), navBox, spacer, new Separator(), userProfile);

        // 2. Email List (Center-Left)
        VBox listPane = new VBox();
        listPane.setPrefWidth(350);
        listPane.setStyle("-fx-border-color: #313244; -fx-border-width: 0 1 0 0;");

        // Search Bar
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 E-posta Ara...");
        searchField.setPrefHeight(35);
        VBox.setMargin(searchField, new Insets(10));

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                refreshEmailList();
            } else {
                List<Email> results = mailbox.searchBySubject(newVal); // Simple search
                emailListView.setItems(FXCollections.observableArrayList(results));
            }
        });

        emailListView = new ListView<>();
        emailListView.getStyleClass().add("list-view");

        // Custom Cell Factory
        emailListView.setCellFactory(param -> new ListCell<Email>() {
            @Override
            protected void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (empty || email == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox content = new VBox(5);

                    HBox topRow = new HBox();
                    Label sender = new Label(email.getFrom());
                    sender.setStyle("-fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
                    Region s = new Region();
                    HBox.setHgrow(s, Priority.ALWAYS);
                    Label date = new Label(DateFormatter.formatSmart(email.getSentDate()));
                    date.setStyle("-fx-font-size: 11px; -fx-text-fill: #a6adc8;");

                    topRow.getChildren().addAll(sender, s, date);

                    Label subject = new Label(email.getSubject()); // + (email.isRead() ? "" : " (Yeni)"));
                    if (!email.isRead()) {
                        subject.setStyle("-fx-text-fill: #89b4fa; -fx-font-weight: bold;");
                    } else {
                        subject.setStyle("-fx-text-fill: #bac2de;");
                    }

                    content.getChildren().addAll(topRow, subject);
                    setGraphic(content);
                }
            }
        });

        emailListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showEmailDetail(newVal);
            }
        });

        listPane.getChildren().addAll(searchField, emailListView);
        VBox.setVgrow(emailListView, Priority.ALWAYS);

        // 3. Email Detail (Center-Right/Fill)
        detailContainer = new VBox();
        detailContainer.getStyleClass().add("email-detail-view");
        detailContainer.setAlignment(Pos.CENTER);

        Label placeholder = new Label("Önizlemek için bir e-posta seçin");
        placeholder.setStyle("-fx-text-fill: #585b70; -fx-font-size: 16px;");
        detailContainer.getChildren().add(placeholder);

        // Build actual detail view structure (hidden initially)
        createDetailStructure();

        // Split Pane to hold List and Detail
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(listPane, detailContainer);
        splitPane.setDividerPositions(0.35); // List takes 35%

        // Adjust for dark mode split pane divider
        splitPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        root.setLeft(sidebar);
        root.setCenter(splitPane);

        refreshEmailList();
    }

    private void createDetailStructure() {
        detailView = new VBox(15);
        detailView.setPadding(new Insets(20));
        detailView.setVisible(false);

        HBox header = new HBox(10);
        detailSubject = new Label();
        detailSubject.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #cdd6f4;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        // Actions
        Button replyBtn = new Button("Yanıtla");
        Button deleteBtn = new Button("Sil");
        deleteBtn.getStyleClass().add("danger-button");

        replyBtn.setOnAction(e -> {
            Email selected = emailListView.getSelectionModel().getSelectedItem();
            if (selected != null)
                openComposeWindow(selected, false); // Reply
        });

        deleteBtn.setOnAction(e -> {
            Email selected = emailListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                mailbox.moveToTrash(selected);
                refreshEmailList();
                resetDetailView();
            }
        });

        header.getChildren().addAll(detailSubject, sp, replyBtn, deleteBtn);

        detailFrom = new Label();
        detailFrom.setStyle("-fx-text-fill: #89b4fa;");

        detailDate = new Label();
        detailDate.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 12px;");

        detailBody = new TextArea();
        detailBody.setEditable(false);
        detailBody.setWrapText(true);
        detailBody.setStyle("-fx-control-inner-background: #1e1e2e; -fx-background-color: transparent;");
        VBox.setVgrow(detailBody, Priority.ALWAYS);

        detailView.getChildren().addAll(header, detailFrom, detailDate, new Separator(), detailBody);
    }

    private Button createNavButton(String text, Folder folder) {
        Button btn = new Button(text);
        btn.getStyleClass().add("sidebar-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            currentFolder = folder;
            refreshEmailList();
        });
        return btn;
    }

    private void refreshEmailList() {
        if (currentFolder == null)
            return;
        List<Email> emails = currentFolder.getEmails();
        // Sort by date desc
        emails.sort(Comparator.comparing(Email::getSentDate).reversed());
        emailListView.setItems(FXCollections.observableArrayList(emails));
    }

    private void showEmailDetail(Email email) {
        if (detailView.getParent() == null) {
            detailContainer.getChildren().clear();
            detailContainer.getChildren().add(detailView);
            detailContainer.setAlignment(Pos.TOP_LEFT);
            detailView.setVisible(true);
        }

        detailSubject.setText(email.getSubject());
        detailFrom.setText("Kimden: " + email.getFrom());
        detailDate.setText(DateFormatter.formatFull(email.getSentDate()));
        detailBody.setText(email.getBody());

        if (!email.isRead()) {
            email.markAsRead();
            emailListView.refresh(); // Update read status visual
        }
    }

    private void resetDetailView() {
        detailContainer.getChildren().clear();
        Label placeholder = new Label("Önizlemek için bir e-posta seçin");
        placeholder.setStyle("-fx-text-fill: #585b70; -fx-font-size: 16px;");
        detailContainer.setAlignment(Pos.CENTER);
        detailContainer.getChildren().add(placeholder);
    }

    private void openComposeWindow() {
        openComposeWindow(null, false);
    }

    private void openComposeWindow(Email replyToInfo, boolean isForward) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(root.getScene().getWindow());

        ComposeView composeView = new ComposeView(mailbox, user, replyToInfo);
        Scene scene = new Scene(composeView.getView(), 600, 500);
        // Add style manually since it's a new stage
        String css = App.class.getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.setTitle(replyToInfo == null ? "Yeni E-posta" : "Yanıtla");
        stage.showAndWait();

        // Refresh after potential sent
        refreshEmailList();
    }
}
