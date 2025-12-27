# Spring Boot + JavaFX Email System

Bu proje, Spring Boot Backend ve JavaFX İstemci (Client) mimarisine dönüştürülmüştür.

## Gereksinimler
- JDK 17+
- Maven
- MongoDB (Yerel olarak çalışır durumda olmalı: localhost:27017)

## Kurulum ve Çalıştırma

### 1. Backend (Sunucu) Başlatma
Backend, veritabanı işlemlerini ve e-posta trafiğini yönetir via Spring Boot.

1. Terminal'i açın ve `backend` klasörüne gidin:
   ```cmd
   cd backend
   ```
2. Uygulamayı çalıştırın:
   ```cmd
   mvn spring-boot:run
   ```
   Backend 8080 portunda çalışacaktır.

### 2. Client (İstemci) Başlatma
JavaFX arayüzü kullanıcıların e-posta göndermesini ve okumasını sağlar.

1. Yeni bir terminal açın ve `client` klasörüne gidin:
   ```cmd
   cd client
   ```
2. Uygulamayı çalıştırın:
   ```cmd
   mvn javafx:run
   ```

## Yapılan Değişiklikler
- **Backend Modülü**: Spring Boot, Spring Data MongoDB ve REST API eklendi.
- **Client Modülü**: Mevcut JavaFX kodu Maven projesine dönüştürüldü.
- **Entegrasyon**: `UserService` ve `Mailbox` sınıfları Backend API ile konuşacak şekilde güncellendi.
- **Veritabanı**: Veriler artık MongoDB üzerinde (kullanıcılar ve e-postalar) kalıcı olarak saklanmaktadır.

## Notlar
- Varsayılan MongoDB veritabanı adı: `nyp-email`
- API Endpoint'leri: `http://localhost:8080/api/...`
