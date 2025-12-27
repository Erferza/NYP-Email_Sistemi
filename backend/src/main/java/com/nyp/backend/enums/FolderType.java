package com.nyp.backend.enums;

public enum FolderType {
    INBOX("Gelen Kutusu"),
    SENT("Gönderilenler"),
    DRAFTS("Taslaklar"),
    TRASH("Çöp Kutusu"),
    SPAM("Spam"),
    STARRED("Yıldızlı"),
    CUSTOM("Özel Klasör");

    private final String displayName;

    FolderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
