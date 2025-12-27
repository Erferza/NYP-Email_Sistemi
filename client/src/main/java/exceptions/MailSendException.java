package exceptions;

/**
 * E-posta gönderme hatası için exception
 */
public class MailSendException extends EmailException {
    
    private final String recipient;
    
    public MailSendException(String recipient) {
        super("E-posta gönderilemedi: " + recipient);
        this.recipient = recipient;
    }
    
    public MailSendException(String recipient, String message) {
        super(message);
        this.recipient = recipient;
    }
    
    public MailSendException(String recipient, String message, Throwable cause) {
        super(message, cause);
        this.recipient = recipient;
    }
    
    public String getRecipient() {
        return recipient;
    }
}
