package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.User;
import services.Mailbox;
import services.UserService;

public class App extends Application {

    private static Stage primaryStage;
    private static UserService userService = UserService.getInstance();
    private static User currentUser;
    private static Mailbox userMailbox;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("NYP Email Sistemi");

        showLoginView();

        primaryStage.show();
    }

    public static void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            applyTheme(scene);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Login view yüklenemedi: " + e.getMessage());
        }
    }

    public static void showMainView(User user) {
        currentUser = user;
        userMailbox = new Mailbox(user);

        MainView mainView = new MainView(currentUser, userMailbox);
        Scene scene = new Scene(mainView.getView(), 1200, 800);
        applyTheme(scene);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
    }

    public static void logout() {
        userService.logout();
        currentUser = null;
        userMailbox = null;
        showLoginView();
    }

    private static void applyTheme(Scene scene) {
        String css = App.class.getResource("styles.css").toExternalForm();
        scene.getStylesheets().add(css);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Mailbox getMailbox() {
        return userMailbox;
    }
}
