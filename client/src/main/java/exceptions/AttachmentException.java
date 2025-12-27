package exceptions;

/**
 * Dosya eki ile ilgili hatalar için exception
 */
public class AttachmentException extends EmailException {
    
    private final String fileName;
    private final long fileSize;
    
    public AttachmentException(String fileName, long fileSize) {
        super("Dosya eki hatası: " + fileName + " (" + fileSize + " bytes)");
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
    
    public AttachmentException(String fileName, long fileSize, String message) {
        super(message);
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public long getFileSize() {
        return fileSize;
    }
}
