package gui;

import enums.FolderType;
import enums.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Email;
import models.User;
import services.AuthService;
import services.EmailService;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AdminDashboardController {

    @FXML private Label totalUsersLabel;
    @FXML private Label totalEmailsLabel;
    @FXML private Label unreadEmailsLabel;
    
    @FXML private PieChart emailDistributionChart;
    @FXML private BarChart<String, Number> folderDistributionChart;
    
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, String> lastLoginColumn;
    
    @FXML private Button refreshButton;
    @FXML private Button closeButton;

    private AuthService authService = AuthService.getInstance();
    private EmailService emailService = EmailService.getInstance();
    
    @FXML
    public void initialize() {
        System.out.println("🔧 AdminDashboardController initialize() called");
        
        // Placeholder ekle
        Label placeholder = new Label("Kullanıcı verisi yükleniyor...");
        placeholder.setStyle("-fx-text-fill: #a6adc8;");
        usersTable.setPlaceholder(placeholder);
        
        setupTable();
        loadData();
    }
    
    private void setupTable() {
        System.out.println("🔧 Setting up table columns...");
        
        // Email kolonu
        emailColumn.setCellValueFactory(cellData -> {
            String email = cellData.getValue().getEmail();
            System.out.println("  Email cell: " + email);
            return new javafx.beans.property.SimpleStringProperty(email);
        });
        
        // Ad Soyad kolonu
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        
        // Rol kolonu
        roleColumn.setCellValueFactory(cellData -> {
            String role = cellData.getValue().getRole() == UserRole.ADMIN ? "Admin" : "Kullanıcı";
            return new javafx.beans.property.SimpleStringProperty(role);
        });
        
        // Durum kolonu
        statusColumn.setCellValueFactory(cellData -> {
            String status = cellData.getValue().isActive() ? "✅ Aktif" : "❌ Pasif";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        
        // Son Giriş kolonu
        lastLoginColumn.setCellValueFactory(cellData -> {
            Date lastLogin = cellData.getValue().getLastLogin();
            if (lastLogin != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                return new javafx.beans.property.SimpleStringProperty(sdf.format(lastLogin));
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });
        
        System.out.println("✅ Table setup completed");
    }
    
    private void loadData() {
        // İstatistikler
        List<User> users = authService.getAllUsers();
        List<Email> emails = emailService.getAllEmails();
        
        System.out.println("📊 Loading dashboard data...");
        System.out.println("👥 Total users: " + users.size());
        System.out.println("📧 Total emails: " + emails.size());
        
        totalUsersLabel.setText(String.valueOf(users.size()));
        totalEmailsLabel.setText(String.valueOf(emails.size()));
        
        long unreadCount = emails.stream().filter(e -> !e.isRead()).count();
        unreadEmailsLabel.setText(String.valueOf(unreadCount));
        
        // Kullanıcılara göre email dağılımı
        loadEmailDistributionChart(emails, users);
        
        // Klasörlere göre email dağılımı
        loadFolderDistributionChart(emails);
        
        // Kullanıcı tablosu
        ObservableList<User> userList = FXCollections.observableArrayList(users);
        usersTable.setItems(userList);
        
        System.out.println("✅ Dashboard data loaded. Table items: " + userList.size());
        for (User user : users) {
            System.out.println("  - " + user.getEmail() + " (" + user.getFullName() + ")");
        }
    }
    
    private void loadEmailDistributionChart(List<Email> emails, List<User> users) {
        Map<String, Long> emailCountByUser = new HashMap<>();
        
        for (User user : users) {
            long count = emails.stream()
                    .filter(e -> e.getOwnerEmail() != null && e.getOwnerEmail().equals(user.getEmail()))
                    .count();
            emailCountByUser.put(user.getFullName(), count);
        }
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : emailCountByUser.entrySet()) {
            if (entry.getValue() > 0) {
                pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
            }
        }
        
        emailDistributionChart.setData(pieChartData);
        emailDistributionChart.setLegendVisible(true);
    }
    
    private void loadFolderDistributionChart(List<Email> emails) {
        Map<FolderType, Long> folderCounts = emails.stream()
                .filter(e -> e.getFolder() != null)
                .collect(Collectors.groupingBy(Email::getFolder, Collectors.counting()));
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Email Sayısı");
        
        Map<FolderType, String> folderNames = new HashMap<>();
        folderNames.put(FolderType.INBOX, "Gelen");
        folderNames.put(FolderType.SENT, "Gönderilen");
        folderNames.put(FolderType.DRAFTS, "Taslak");
        folderNames.put(FolderType.TRASH, "Çöp");
        folderNames.put(FolderType.SPAM, "Spam");
        folderNames.put(FolderType.STARRED, "Yıldızlı");
        
        for (FolderType folder : FolderType.values()) {
            if (folder != FolderType.CUSTOM) {
                long count = folderCounts.getOrDefault(folder, 0L);
                String folderName = folderNames.getOrDefault(folder, folder.name());
                series.getData().add(new XYChart.Data<>(folderName, count));
            }
        }
        
        folderDistributionChart.getData().clear();
        folderDistributionChart.getData().add(series);
        folderDistributionChart.setLegendVisible(false);
    }
    
    @FXML
    private void handleRefresh() {
        loadData();
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
