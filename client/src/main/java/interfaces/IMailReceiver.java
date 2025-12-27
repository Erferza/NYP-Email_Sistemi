package interfaces;

import models.Email;
import java.util.List;

/**
 * E-posta alma işlemleri için interface
 */
public interface IMailReceiver {

    /**
     * Tüm e-postaları alır
     * 
     * @return E-posta listesi
     */
    List<Email> receiveMails();

    /**
     * Belirli bir e-postayı ID'ye göre getirir
     * 
     * @param id E-posta ID'si
     * @return Bulunan e-posta veya null
     */
    Email getMailById(String id);

    /**
     * Okunmamış e-postaları getirir
     * 
     * @return Okunmamış e-posta listesi
     */
    List<Email> getUnreadMails();
}
