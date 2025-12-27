package utils;

import java.util.regex.Pattern;

/**
 * E-posta doğrulama utility sınıfı
 */
public class EmailValidator {
    
    // E-posta format regex pattern
    private static final String EMAIL_REGEX = 
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    /**
     * E-posta adresinin geçerli olup olmadığını kontrol eder
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * E-posta adresini temizler ve küçük harfe çevirir
     */
    public static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
    
    /**
     * E-posta adresinden domain kısmını alır
     */
    public static String getDomain(String email) {
        if (!isValidEmail(email)) {
            return null;
        }
        return email.substring(email.indexOf('@') + 1);
    }
    
    /**
     * E-posta adresinden kullanıcı adını alır
     */
    public static String getUsername(String email) {
        if (!isValidEmail(email)) {
            return null;
        }
        return email.substring(0, email.indexOf('@'));
    }
    
    /**
     * Birden fazla e-posta adresini doğrular (virgülle ayrılmış)
     */
    public static boolean validateMultipleEmails(String emails) {
        if (emails == null || emails.trim().isEmpty()) {
            return false;
        }
        
        String[] emailArray = emails.split(",");
        for (String email : emailArray) {
            if (!isValidEmail(email.trim())) {
                return false;
            }
        }
        return true;
    }
}
