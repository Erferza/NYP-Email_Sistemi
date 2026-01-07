package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.DraftEmail;
import models.Email;
import models.User;
import services.Mailbox;

public class ComposeView {

    private BorderPane root;
    private Mailbox mailbox;
    private User currentUser;
    private Email replyContext;

    private TextField toField;
    private TextField subjectField;
    private TextArea bodyArea;

    public ComposeView(Mailbox mailbox, User user, Email replyContext) {
        this.mailbox = mailbox;
        this.currentUser = user;
        this.replyContext = replyContext;
        createView();
    }

    public Parent getView() {
        return root;
    }

    private void createView() {
        root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root"); // Inherit dark theme background

        // Header
        Label title = new Label(replyContext == null ? "Yeni E-posta" : "Yanıtla");
        title.getStyleClass().add("header-label");

        // Form
        VBox form = new VBox(15);
        form.setPadding(new Insets(20, 0, 0, 0));

        toField = new TextField();
        toField.setPromptText("Kime (örn: alici@mail.com)");

        subjectField = new TextField();
        subjectField.setPromptText("Konu");

        bodyArea = new TextArea();
        bodyArea.setPromptText("Mesajınızı buraya yazın...");
        bodyArea.setWrapText(true);
        VBox.setVgrow(bodyArea, Priority.ALWAYS);

        // Prefill if reply
        if (replyContext != null) {
            toField.setText(replyContext.getFrom());
            subjectField.setText("Re: " + replyContext.getSubject());
            bodyArea.setText(
                    "\n\n------------------\n" + replyContext.getFrom() + " yazdı:\n" + replyContext.getBody());
        }

        form.getChildren().addAll(
                new Label("Kime:"), toField,
                new Label("Konu:"), subjectField,
                new Label("Mesaj:"), bodyArea);

        // Buttons
        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(15, 0, 0, 0));

        Button sendBtn = new Button("Gönder");
        sendBtn.getStyleClass().add("primary-button");

        Button cancelBtn = new Button("İptal");
        cancelBtn.getStyleClass().add("button"); // Normal

        Button draftBtn = new Button("Taslak Kaydet");

        sendBtn.setOnAction(e -> sendEmail());
        cancelBtn.setOnAction(e -> closeWindow());
        draftBtn.setOnAction(e -> saveDraft());

        buttonBar.getChildren().addAll(draftBtn, cancelBtn, sendBtn);

        root.setTop(title);
        root.setCenter(form);
        root.setBottom(buttonBar);
    }

    private void sendEmail() {
        String to = toField.getText().trim();
        String subject = subjectField.getText().trim();
        String body = bodyArea.getText();

        if (to.isEmpty()) {
            showAlert("Hata", "Alıcı adresi boş olamaz.");
            return;
        }

        Email email = new Email(currentUser.getEmail(), to, subject, body);

        try {
            boolean success = mailbox.sendMail(email);
            if (success) {
                closeWindow();
            } else {
                showAlert("Gönderim Hatası", "Email gönderilemedi");
            }
        } catch (Exception e) {
            showAlert("Gönderim Hatası", e.getMessage());
        }
    }

    private void saveDraft() {
        String to = toField.getText().trim();
        String subject = subjectField.getText().trim();
        String body = bodyArea.getText();

        Email email = new Email(currentUser.getEmail(), to, subject, body);
        DraftEmail draft = new DraftEmail(email);

        mailbox.saveDraft(draft);
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) root.getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
