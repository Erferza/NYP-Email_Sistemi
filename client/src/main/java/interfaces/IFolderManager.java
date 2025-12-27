package interfaces;

import models.Email;
import models.Folder;
import java.util.List;

/**
 * Klasör yönetimi işlemleri için interface
 */
public interface IFolderManager {
    
    /**
     * E-postayı belirtilen klasöre taşır
     * @param email Taşınacak e-posta
     * @param folder Hedef klasör
     */
    void moveToFolder(Email email, Folder folder);
    
    /**
     * Yeni klasör oluşturur
     * @param name Klasör adı
     * @return Oluşturulan klasör
     */
    Folder createFolder(String name);
    
    /**
     * Klasörü siler
     * @param folder Silinecek klasör
     * @return Silme başarılı ise true
     */
    boolean deleteFolder(Folder folder);
    
    /**
     * Tüm klasörleri listeler
     * @return Klasör listesi
     */
    List<Folder> getAllFolders();
    
    /**
     * Klasörü yeniden adlandırır
     * @param folder Yeniden adlandırılacak klasör
     * @param newName Yeni ad
     */
    void renameFolder(Folder folder, String newName);
}
