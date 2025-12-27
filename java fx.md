# NYP Email Sistemi - JavaFX Arayüzü

Bu proje için modern, "Premium" görünümlü bir JavaFX arayüzü (GUI) eklenmiştir.

## Dosya Yapısı

Aşağıdaki grafiksel arayüz dosyaları `src/gui` paketine eklenmiştir:
- **App.java**: Ana uygulama başlatıcısı.
- **LoginView.java**: Modern giriş ve kayıt ekranı.
- **MainView.java**: Üç sütunlu (Kenar çubuğu, Liste, Detay) ana kontrol paneli.
- **ComposeView.java**: E-posta yazma penceresi.
- **styles.css**: Koyu (Dark Mode) modern tema stilleri.

## Nasıl Çalıştırılır?

Bu proje standart Java kütüphanelerini kullanmaktadır. Eğer JDK sürümünüz (JDK 8 gibi) JavaFX içeriyorsa doğrudan çalıştırabilirsiniz.

### 1. Derleme (Compile)
Terminalde proje ana dizininde (Mevcut dizin) şu komutu çalıştırın:
```powershell
javac -d out -sourcepath src src/gui/App.java
```

### 2. Çalıştırma (Run)
Derleme başarılı olduktan sonra arayüzü başlatmak için:
```powershell
java -cp out gui.App
```

### JavaFX Hatası Alırsanız (JDK 11+)
Eğer "package javafx... does not exist" hatası alıyorsanız, sisteminizde JavaFX kütüphanesi eksik veya yapılandırılmamış demektir.
Bu durumda JavaFX SDK'sını indirip `--module-path` ekleyerek çalıştırmanız gerekir veya JavaFX destekli bir JDK (örn. Azul Zulu JDK FX veya BellSoft Liberica Full) kullanmanız önerilir.

**Alternatif:** IDE (IntelliJ, Eclipse) kullanıyorsanız, `gui.App` sınıfını açıp "Run" diyerek IDE'nin kütüphaneleri yönetmesini sağlayabilirsiniz.
