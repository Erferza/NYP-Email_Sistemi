package gui;

import enums.UserRole;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Email;
import models.Folder;
import models.User;
import services.Mailbox;
import services.UserService;
import utils.DateFormatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainView {

  private BorderPane root;
  private User user;
  private Mailbox mailbox;
  private Stage stage;

  private ListView<Email> emailListView;
  private VBox detailView;
  private Label detailSubject;
  private Label detailFrom;
  private Label detailDate;
  private WebView detailBody;
  private VBox detailContainer;

  private Folder currentFolder;
  private Timeline refreshTimer;

  // Aktif buton takibi için eklenen fieldlar
  private Button activeNavButton = null;
  private List<Button> navButtons = new ArrayList<>();

  public MainView(User user, Mailbox mailbox) {
    this.user = user;
    this.mailbox = mailbox;
    this.currentFolder = mailbox.getInbox();
    createView();
    setupAutoRefresh();
  }

  public Parent getView() {
    return root;
  }

  private void setupAutoRefresh() {
    refreshTimer = new Timeline(new KeyFrame(Duration.seconds(30), e -> {
      mailbox.refresh();
      refreshEmailList();
    }));
    refreshTimer.setCycleCount(Timeline.INDEFINITE);
    refreshTimer.play();
  }

  private void createView() {
    root = new BorderPane();
    root.getStyleClass().add("root");

    VBox sidebar = new VBox(10);
    sidebar.getStyleClass().add("sidebar");
    sidebar.setPrefWidth(220);
    sidebar.setPadding(new Insets(20, 10, 20, 10));

    Button composeBtn = new Button("✏️  Oluştur");
    composeBtn.getStyleClass().add("primary-button");
    composeBtn.setMaxWidth(Double.MAX_VALUE);
    composeBtn.setPrefHeight(45);
    composeBtn.setOnAction(e -> openComposeWindow());

    VBox navBox = new VBox(5);

    // Navigation butonları oluşturup listeye ekliyoruz
    Button inboxBtn = createNavButton("📥 Gelen Kutusu", mailbox.getInbox());
    Button sentBtn = createNavButton("📤 Gönderilenler", mailbox.getSent());
    Button draftsBtn = createNavButton("📝 Taslaklar", mailbox.getDrafts());
    Button starredBtn = createNavButton("⭐ Yıldızlı", mailbox.getStarred());
    Button trashBtn = createNavButton("🗑️  Çöp Kutusu", mailbox.getTrash());

    navButtons.add(inboxBtn);
    navButtons.add(sentBtn);
    navButtons.add(draftsBtn);
    navButtons.add(starredBtn);
    navButtons.add(trashBtn);

    navBox.getChildren().addAll(inboxBtn, sentBtn, draftsBtn, starredBtn, trashBtn);

    // İlk başta Gelen Kutusu aktif olsun
    setActiveNavButton(inboxBtn);

    Region spacer = new Region();
    VBox.setVgrow(spacer, Priority.ALWAYS);

    // Admin için Dashboard butonu ekle
    VBox bottomSection = new VBox(10);
    if (user.getRole() == UserRole.ADMIN) {
      Button adminBtn = new Button("🛡️ Admin Dashboard");
      adminBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
      adminBtn.setMaxWidth(Double.MAX_VALUE);
      adminBtn.setPrefHeight(40);
      adminBtn.setOnAction(e -> openAdminDashboard());
      bottomSection.getChildren().add(adminBtn);
      bottomSection.getChildren().add(new Separator());
    }

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
    bottomSection.getChildren().add(userProfile);

    sidebar.getChildren().addAll(composeBtn, new Separator(), navBox, spacer, bottomSection);

    VBox listPane = new VBox();
    listPane.setPrefWidth(350);
    listPane.setStyle("-fx-border-color: #313244; -fx-border-width: 0 1 0 0;");

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

          // Yıldız ikonu ekle
          Label starIcon = new Label(email.isStarred() ? "⭐" : "");
          starIcon.setStyle("-fx-font-size: 12px; -fx-text-fill: #f9e2af;");
          starIcon.setPrefWidth(15);

          Label sender = new Label(email.getFrom());
          sender.setStyle("-fx-font-weight: bold; -fx-text-fill: #cdd6f4;");
          Region s = new Region();
          HBox.setHgrow(s, Priority.ALWAYS);
          Label date = new Label(DateFormatter.formatSmart(email.getSentDate()));
          date.setStyle("-fx-font-size: 11px; -fx-text-fill: #a6adc8;");

          topRow.getChildren().addAll(starIcon, sender, s, date);

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

    detailContainer = new VBox();
    detailContainer.getStyleClass().add("email-detail-view");
    detailContainer.setAlignment(Pos.CENTER);

    Label placeholder = new Label("Önizlemek için bir e-posta seçin");
    placeholder.setStyle("-fx-text-fill: #585b70; -fx-font-size: 16px;");
    detailContainer.getChildren().add(placeholder);

    createDetailStructure();

    SplitPane splitPane = new SplitPane();
    splitPane.getItems().addAll(listPane, detailContainer);
    splitPane.setDividerPositions(0.35); // List takes 35%

    splitPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

    root.setLeft(sidebar);
    root.setCenter(splitPane);

    refreshEmailList();

    trashBtn.setOnAction(e -> {
      currentFolder = mailbox.getTrash();
      setActiveNavButton(trashBtn);
      
      System.out.println("Switching to trash folder...");
      System.out.println("Trash folder emails before refresh: " + mailbox.getTrash().getEmails().size());
      
      mailbox.refresh();

      System.out.println("Trash folder emails after refresh: " + mailbox.getTrash().getEmails().size());

      refreshEmailList();
    });
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

    // Actions - Yıldız butonu eklendi
    Button starBtn = new Button("⭐");
    Button replyBtn = new Button("Yanıtla");
    Button deleteBtn = new Button("Sil");
    deleteBtn.getStyleClass().add("danger-button");

    starBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");
    starBtn.setTooltip(new Tooltip("Yıldızla/Yıldızı Kaldır"));

    starBtn.setOnAction(e -> {
      Email selected = emailListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        // Yıldızı değiştir
        boolean wasStarred = selected.isStarred();
        if (wasStarred) {
          mailbox.unstarEmail(selected);
          selected.setStarred(false);
          starBtn.setText("☆");
          starBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand; -fx-text-fill: #6c7086;");
        } else {
          mailbox.starEmail(selected);
          selected.setStarred(true);
          starBtn.setText("⭐");
          starBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand; -fx-text-fill: #f9e2af;");
        }

        // Backend'den tüm klasörleri yeniden çek ve listeyi güncelle
        mailbox.refresh();
        refreshEmailList();
        emailListView.refresh();

        System.out.println("Email star status changed: " + selected.getSubject() + " -> " + selected.isStarred());
      }
    });

    replyBtn.setOnAction(e -> {
      Email selected = emailListView.getSelectionModel().getSelectedItem();
      if (selected != null)
        openComposeWindow(selected, false);
    });

    deleteBtn.setOnAction(e -> {
      Email selected = emailListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        mailbox.moveToTrash(selected);
        refreshEmailList();
        resetDetailView();
        System.out.println("Email moved to trash: " + selected.getSubject());
      }
    });

    header.getChildren().addAll(detailSubject, sp, starBtn, replyBtn, deleteBtn);

    detailFrom = new Label();
    detailFrom.setStyle("-fx-text-fill: #89b4fa;");

    detailDate = new Label();
    detailDate.setStyle("-fx-text-fill: #a6adc8; -fx-font-size: 12px;");

    detailBody = new WebView();
    // detailBody.setContextMenuEnabled(false);
    VBox.setVgrow(detailBody, Priority.ALWAYS);

    detailView.getChildren().addAll(header, detailFrom, detailDate, new Separator(), detailBody);
  }

  private Button createNavButton(String text, Folder folder) {
    Button btn = new Button(text);
    btn.getStyleClass().add("sidebar-button");
    btn.setMaxWidth(Double.MAX_VALUE);
    btn.setOnAction(e -> {
      currentFolder = folder;
      setActiveNavButton(btn); // Aktif butonu ayarla
      refreshEmailList();
    });
    return btn;
  }

  // Aktif navigation butonunu ayarlayan yeni method
  private void setActiveNavButton(Button button) {
    // Önceki aktif butonu normal stile çevir
    if (activeNavButton != null) {
      activeNavButton.getStyleClass().remove("active");
    }

    // Yeni aktif butonu ayarla
    activeNavButton = button;
    if (activeNavButton != null && !activeNavButton.getStyleClass().contains("active")) {
      activeNavButton.getStyleClass().add("active");
    }
  }

  private void refreshEmailList() {
    if (currentFolder == null)
      return;
    List<Email> emails = currentFolder.getEmails();

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

    // Yıldız butonunu güncelle
    Button starBtn = (Button) ((HBox) detailView.getChildren().get(0)).getChildren().get(2);
    if (email.isStarred()) {
      starBtn.setText("⭐");
      starBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand; -fx-text-fill: #f9e2af;");
    } else {
      starBtn.setText("☆");
      starBtn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand; -fx-text-fill: #6c7086;");
    }

    // Load HTML content
    String darkThemeCss = "<style>body { background-color: #1e1e2e; color: #cdd6f4; font-family: sans-serif; }</style>";
    detailBody.getEngine().loadContent(darkThemeCss + email.getBody());

    if (!email.isRead()) {
      mailbox.markAsRead(email);
      emailListView.refresh();
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

    String css = App.class.getResource("styles.css").toExternalForm();
    scene.getStylesheets().add(css);

    stage.setScene(scene);
    stage.setTitle(replyToInfo == null ? "Yeni E-posta" : "Yanıtla");
    stage.showAndWait();

    refreshEmailList();
  }

  private void openAdminDashboard() {
    // Admin Dashboard - Basitleştirilmiş versiyon
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("🛡️ Admin Dashboard");
    alert.setHeaderText("Sitem İstatistikleri");
    
    try {
        int userCount = UserService.getInstance().getAllUsers().size();
        alert.setContentText(
            "Toplam Kullanıcı: " + userCount + "\n" +
            "Aktif Kullanıcı: " + user.getFullName() + "\n" +
            "Rol: " + user.getRole()
        );
    } catch (Exception e) {
        alert.setContentText("Admin paneli yüklenemedi: " + e.getMessage());
    }
    
    alert.showAndWait();
  }
}
