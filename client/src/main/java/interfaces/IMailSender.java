package interfaces;

import models.Email;
import exceptions.MailSendException;
import java.util.List;

/**
 * E-posta gönderme işlemleri için interface
 */
public interface IMailSender {
    
    /**
     * Tek bir e-posta gönderir
     * @param email Gönderilecek e-posta
     * @return Gönderim başarılı ise true
     * @throws MailSendException Gönderim hatası durumunda
     */
    boolean sendMail(Email email) throws MailSendException;
    
    /**
     * Birden fazla e-posta gönderir
     * @param emails Gönderilecek e-posta listesi
     * @return Gönderim başarılı ise true
     * @throws MailSendException Gönderim hatası durumunda
     */
    boolean sendBulkMail(List<Email> emails) throws MailSendException;
}
