package models;

import java.io.Serializable;

/**
 * E-posta eki sınıfı
 */
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final long MAX_SIZE = 25 * 1024 * 1024; // 25 MB

    private String id;
    private String fileName;
    private String filePath;
    private long fileSize;
    private String mimeType;

    public Attachment() {
    }

    public Attachment(String fileName, String filePath, long fileSize, String mimeType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Dosya boyutunu okunabilir formatta döndürür
     */
    public String getFormattedSize() {
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.2f KB", fileSize / 1024.0);
        } else {
            return String.format("%.2f MB", fileSize / (1024.0 * 1024));
        }
    }

    /**
     * Dosya boyutunun limiti aşıp aşmadığını kontrol eder
     */
    public boolean isValidSize() {
        return fileSize <= MAX_SIZE;
    }

    @Override
    public String toString() {
        return fileName + " (" + getFormattedSize() + ")";
    }
}
