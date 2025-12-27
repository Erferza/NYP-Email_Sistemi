package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Tarih formatlama utility sınıfı
 */
public class DateFormatter {
    
    private static final SimpleDateFormat FULL_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat DAY_MONTH_FORMAT = new SimpleDateFormat("dd MMM");
    
    /**
     * Tarihi tam format ile string'e çevirir
     */
    public static String formatFull(Date date) {
        if (date == null) return "";
        return FULL_FORMAT.format(date);
    }
    
    /**
     * Tarihi sadece tarih formatı ile string'e çevirir
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }
    
    /**
     * Tarihi sadece saat formatı ile string'e çevirir
     */
    public static String formatTime(Date date) {
        if (date == null) return "";
        return TIME_FORMAT.format(date);
    }
    
    /**
     * E-posta listesi için akıllı tarih formatı
     * Bugün: sadece saat
     * Bu yıl: gün ve ay
     * Önceki yıllar: tam tarih
     */
    public static String formatSmart(Date date) {
        if (date == null) return "";
        
        Date now = new Date();
        long diffInMillies = now.getTime() - date.getTime();
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        
        if (diffInDays == 0) {
            // Bugün - sadece saat göster
            return TIME_FORMAT.format(date);
        } else if (diffInDays < 7) {
            // Bu hafta - gün adı göster
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(date);
        } else if (isSameYear(date, now)) {
            // Bu yıl - gün ve ay göster
            return DAY_MONTH_FORMAT.format(date);
        } else {
            // Önceki yıllar - tam tarih göster
            return DATE_FORMAT.format(date);
        }
    }
    
    /**
     * İki tarihin aynı yıl olup olmadığını kontrol eder
     */
    private static boolean isSameYear(Date date1, Date date2) {
        java.util.Calendar cal1 = java.util.Calendar.getInstance();
        java.util.Calendar cal2 = java.util.Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR);
    }
    
    /**
     * String'i tarihe çevirir
     */
    public static Date parseDate(String dateString) {
        try {
            return DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
    
    /**
     * String'i tam tarih formatından tarihe çevirir
     */
    public static Date parseFull(String dateString) {
        try {
            return FULL_FORMAT.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }
    
    /**
     * İki tarih arasındaki farkı okunabilir string olarak döndürür
     */
    public static String getTimeDifference(Date date) {
        if (date == null) return "";
        
        long diffInMillies = new Date().getTime() - date.getTime();
        long diffInSeconds = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        long diffInMinutes = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
        long diffInHours = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        
        if (diffInSeconds < 60) {
            return "Az önce";
        } else if (diffInMinutes < 60) {
            return diffInMinutes + " dakika önce";
        } else if (diffInHours < 24) {
            return diffInHours + " saat önce";
        } else if (diffInDays < 7) {
            return diffInDays + " gün önce";
        } else if (diffInDays < 30) {
            return (diffInDays / 7) + " hafta önce";
        } else if (diffInDays < 365) {
            return (diffInDays / 30) + " ay önce";
        } else {
            return (diffInDays / 365) + " yıl önce";
        }
    }
}
