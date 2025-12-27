package exceptions;

/**
 * Geçersiz e-posta adresi için exception
 */
public class InvalidEmailException extends EmailException {
    
    private final String invalidEmail;
    
    public InvalidEmailException(String invalidEmail) {
        super("Geçersiz e-posta adresi: " + invalidEmail);
        this.invalidEmail = invalidEmail;
    }
    
    public InvalidEmailException(String invalidEmail, String message) {
        super(message);
        this.invalidEmail = invalidEmail;
    }
    
    public String getInvalidEmail() {
        return invalidEmail;
    }
}
