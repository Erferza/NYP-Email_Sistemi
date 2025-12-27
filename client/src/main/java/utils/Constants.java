package utils;

/**
 * Uygulama sabitleri
 */
public class Constants {
    
    // Uygulama bilgileri
    public static final String APP_NAME = "Java Mail Sistemi";
    public static final String APP_VERSION = "1.0.0";
    
    // Pencere boyutları
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 700;
    public static final int LOGIN_WIDTH = 400;
    public static final int LOGIN_HEIGHT = 500;
    
    // Renkler
    public static final String PRIMARY_COLOR = "#3498db";
    public static final String SECONDARY_COLOR = "#2ecc71";
    public static final String DANGER_COLOR = "#e74c3c";
    public static final String WARNING_COLOR = "#f39c12";
    public static final String DARK_COLOR = "#2c3e50";
    public static final String LIGHT_COLOR = "#ecf0f1";
    public static final String SIDEBAR_COLOR = "#34495e";
    
    // Dosya boyutu limitleri
    public static final long MAX_ATTACHMENT_SIZE = 25 * 1024 * 1024; // 25 MB
    public static final long DEFAULT_STORAGE_LIMIT = 1024 * 1024 * 1024; // 1 GB
    public static final long PREMIUM_STORAGE_LIMIT = 15L * 1024 * 1024 * 1024; // 15 GB
    
    // Mesajlar
    public static final String LOGIN_SUCCESS = "Giriş başarılı!";
    public static final String LOGIN_FAILED = "E-posta veya şifre hatalı!";
    public static final String REGISTER_SUCCESS = "Kayıt başarılı! Giriş yapabilirsiniz.";
    public static final String REGISTER_FAILED = "Kayıt başarısız!";
    public static final String EMAIL_SENT = "E-posta gönderildi!";
    public static final String EMAIL_SEND_FAILED = "E-posta gönderilemedi!";
    public static final String INVALID_EMAIL = "Geçersiz e-posta adresi!";
    public static final String EMPTY_FIELD = "Lütfen tüm alanları doldurun!";
    public static final String PASSWORD_MISMATCH = "Şifreler eşleşmiyor!";
    
    // Klasör isimleri
    public static final String FOLDER_INBOX = "Gelen Kutusu";
    public static final String FOLDER_SENT = "Gönderilenler";
    public static final String FOLDER_DRAFTS = "Taslaklar";
    public static final String FOLDER_TRASH = "Çöp Kutusu";
    public static final String FOLDER_SPAM = "Spam";
    public static final String FOLDER_STARRED = "Yıldızlı";
    
    // Font boyutları
    public static final int FONT_SIZE_SMALL = 11;
    public static final int FONT_SIZE_NORMAL = 13;
    public static final int FONT_SIZE_LARGE = 16;
    public static final int FONT_SIZE_TITLE = 20;
    public static final int FONT_SIZE_HEADER = 24;
    
    private Constants() {
        // Utility class - instance oluşturulamaz
    }
}
