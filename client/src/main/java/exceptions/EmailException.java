package exceptions;

/**
 * E-posta işlemleri için temel exception sınıfı
 */
public class EmailException extends Exception {
    
    public EmailException(String message) {
        super(message);
    }
    
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
