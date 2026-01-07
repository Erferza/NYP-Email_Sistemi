package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import models.User;
import services.UserService;
import utils.EmailValidator;

public class LoginView {

    private BorderPane root;
    private UserService userService = UserService.getInstance();
    private VBox loginPanel;
    private VBox registerPanel;
    private StackPane contentArea;

    public LoginView() {
        createView();
    }

    public Parent getView() {
        return root;
    }

    private void createView() {
        root = new BorderPane();
        root.getStyleClass().add("root");


        contentArea = new StackPane();
        contentArea.setAlignment(Pos.CENTER);

        createLoginPanel();
        createRegisterPanel();

        contentArea.getChildren().add(loginPanel);

        root.setCenter(contentArea);
    }

    private void createLoginPanel() {
        loginPanel = new VBox(20);
        loginPanel.getStyleClass().add("card");
        loginPanel.setMaxWidth(400);
        loginPanel.setMaxHeight(450);
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setPadding(new Insets(40));

        Label title = new Label("Giriş Yap");
        title.getStyleClass().add("header-label");

        TextField emailField = new TextField();
        emailField.setPromptText("E-posta");
        emailField.setPrefHeight(40);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Şifre");
        passField.setPrefHeight(40);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 12px;");

        Button loginBtn = new Button("Giriş Yap");
        loginBtn.getStyleClass().add("primary-button");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setPrefHeight(40);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String pass = passField.getText().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Lütfen tüm alanları doldurun.");
                return;
            }

            User user = userService.login(email, pass);
            if (user != null) {
                App.showMainView(user);
            } else {
                errorLabel.setText("Hatalı e-posta veya şifre!");
            }
        });

        Hyperlink switchToReg = new Hyperlink("Hesabınız yok mu? Kayıt Olun");
        switchToReg.setStyle("-fx-text-fill: #89b4fa; -fx-border-color: transparent; -fx-cursor: hand;");
        switchToReg.setOnAction(e -> showRegister());

        loginPanel.getChildren().addAll(
                new Circle(30, javafx.scene.paint.Color.web("#89b4fa")), // Logo placeholder
                title,
                new Separator(),
                emailField,
                passField,
                loginBtn,
                errorLabel,
                switchToReg);
    }

    private void createRegisterPanel() {
        registerPanel = new VBox(20);
        registerPanel.getStyleClass().add("card");
        registerPanel.setMaxWidth(400);
        registerPanel.setMaxHeight(550);
        registerPanel.setAlignment(Pos.CENTER);
        registerPanel.setPadding(new Insets(40));

        Label title = new Label("Kayıt Ol");
        title.getStyleClass().add("header-label");

        TextField nameField = new TextField();
        nameField.setPromptText("Ad Soyad");
        nameField.setPrefHeight(40);

        TextField emailField = new TextField();
        emailField.setPromptText("E-posta");
        emailField.setPrefHeight(40);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Şifre");
        passField.setPrefHeight(40);

        PasswordField passConfirmField = new PasswordField();
        passConfirmField.setPromptText("Şifre (Tekrar)");
        passConfirmField.setPrefHeight(40);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #f38ba8; -fx-font-size: 12px;");

        Button regBtn = new Button("Kayıt Ol");
        regBtn.getStyleClass().add("primary-button");
        regBtn.setMaxWidth(Double.MAX_VALUE);
        regBtn.setPrefHeight(40);

        regBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = passField.getText().trim();
            String passConf = passConfirmField.getText().trim();

            System.out.println("DEBUG - Email: '" + email + "'");
            System.out.println("DEBUG - Email length: " + email.length());
            System.out.println("DEBUG - Valid: " + EmailValidator.isValidEmail(email));
            
            if (!EmailValidator.isValidEmail(email)) {
                errorLabel.setText("Geçersiz e-posta formatı!");
                return;
            }
            if (!pass.equals(passConf)) {
                errorLabel.setText("Şifreler eşleşmiyor!");
                return;
            }

            User user = userService.register(email, pass, name);
            if (user != null) {
                // Auto login after register
                App.showMainView(user);
            } else {
                errorLabel.setText("Kayıt başarısız (E-posta kullanılıyor olabilir).");
            }
        });

        Hyperlink switchToLogin = new Hyperlink("Zaten hesabınız var mı? Giriş Yap");
        switchToLogin.setStyle("-fx-text-fill: #89b4fa; -fx-border-color: transparent; -fx-cursor: hand;");
        switchToLogin.setOnAction(e -> showLogin());

        registerPanel.getChildren().addAll(
                title,
                new Separator(),
                nameField,
                emailField,
                passField,
                passConfirmField,
                regBtn,
                errorLabel,
                switchToLogin);
    }

    private void showRegister() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(registerPanel);
    }

    private void showLogin() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(loginPanel);
    }
}
