package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import services.Mailbox;
import services.UserService;
import utils.EmailValidator;

import java.io.IOException;

public class LoginController {

    @FXML private VBox loginPanel;
    @FXML private VBox registerPanel;
    @FXML private TextField loginEmailField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Label loginErrorLabel;
    @FXML private Button loginButton;
    
    @FXML private TextField registerNameField;
    @FXML private TextField registerEmailField;
    @FXML private PasswordField registerPasswordField;
    @FXML private PasswordField registerConfirmPasswordField;
    @FXML private Label registerErrorLabel;
    @FXML private Button registerButton;

    private UserService userService = UserService.getInstance();

    @FXML
    private void handleLogin() {
        String email = loginEmailField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            loginErrorLabel.setText("Lütfen tüm alanları doldurun.");
            return;
        }

        if (!EmailValidator.isValidEmail(email)) {
            loginErrorLabel.setText("Geçersiz e-posta formatı.");
            return;
        }

        try {
            User user = userService.login(email, password);
            if (user != null) {
                openMainView(user);
            } else {
                loginErrorLabel.setText("E-posta veya şifre hatalı.");
            }
        } catch (Exception e) {
            loginErrorLabel.setText("Giriş hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        String name = registerNameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String password = registerPasswordField.getText().trim();
        String confirmPassword = registerConfirmPasswordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            registerErrorLabel.setText("Lütfen tüm alanları doldurun.");
            return;
        }

        if (!EmailValidator.isValidEmail(email)) {
            registerErrorLabel.setText("Geçersiz e-posta formatı.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            registerErrorLabel.setText("Şifreler eşleşmiyor.");
            return;
        }

        if (password.length() < 6) {
            registerErrorLabel.setText("Şifre en az 6 karakter olmalı.");
            return;
        }

        try {
            User user = userService.register(email, password, name);
            if (user != null) {
                registerErrorLabel.setStyle("-fx-text-fill: #a6e3a1;");
                registerErrorLabel.setText("Kayıt başarılı! Giriş yapabilirsiniz.");
                
                // 2 saniye sonra login paneline geç
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        javafx.application.Platform.runLater(this::showLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                registerErrorLabel.setText("Kayıt başarısız. Bu e-posta zaten kullanımda olabilir.");
            }
        } catch (Exception e) {
            registerErrorLabel.setText("Kayıt hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showRegister() {
        loginPanel.setVisible(false);
        loginPanel.setManaged(false);
        registerPanel.setVisible(true);
        registerPanel.setManaged(true);
        clearLoginFields();
    }

    @FXML
    private void showLogin() {
        registerPanel.setVisible(false);
        registerPanel.setManaged(false);
        loginPanel.setVisible(true);
        loginPanel.setManaged(true);
        clearRegisterFields();
    }

    private void clearLoginFields() {
        loginEmailField.clear();
        loginPasswordField.clear();
        loginErrorLabel.setText("");
    }

    private void clearRegisterFields() {
        registerNameField.clear();
        registerEmailField.clear();
        registerPasswordField.clear();
        registerConfirmPasswordField.clear();
        registerErrorLabel.setText("");
        registerErrorLabel.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 12px;");
    }

    private void openMainView(User user) {
        try {
            Mailbox mailbox = new Mailbox(user);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            MainController mainController = new MainController(user, mailbox);
            loader.setController(mainController);
            
            Scene scene = new Scene(loader.load(), 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/gui/styles.css").toExternalForm());

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("NYP Email - " + user.getFullName());
        } catch (IOException e) {
            loginErrorLabel.setText("Uygulama yüklenemedi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
