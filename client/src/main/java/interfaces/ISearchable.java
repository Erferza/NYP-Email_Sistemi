package interfaces;

import models.Email;
import java.util.Date;
import java.util.List;

/**
 * E-posta arama işlemleri için interface
 */
public interface ISearchable {
    
    /**
     * Konuya göre arama yapar
     * @param keyword Aranacak kelime
     * @return Bulunan e-postalar
     */
    List<Email> searchBySubject(String keyword);
    
    /**
     * Gönderene göre arama yapar
     * @param sender Gönderen e-posta adresi
     * @return Bulunan e-postalar
     */
    List<Email> searchBySender(String sender);
    
    /**
     * Tarihe göre arama yapar
     * @param date Aranacak tarih
     * @return Bulunan e-postalar
     */
    List<Email> searchByDate(Date date);
    
    /**
     * İçeriğe göre arama yapar
     * @param keyword Aranacak kelime
     * @return Bulunan e-postalar
     */
    List<Email> searchByContent(String keyword);
}
