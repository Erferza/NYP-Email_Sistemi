# Test için Admin Dashboard'u Kontrol Et

## Adımlar:
1. Uygulamayı başlat: `cd client && mvn org.openjfx:javafx-maven-plugin:0.0.8:run`
2. admin@nyp.com / admin123 ile giriş yap
3. Sol menüden "🛡️ Admin" butonuna tıkla
4. Terminal/Console'da şu mesajları göreceksin:

```
🔧 AdminDashboardController initialize() called
🔧 Setting up table columns...
✅ Table setup completed
📊 Loading dashboard data...
👥 Total users: 3
📧 Total emails: 9
✅ Dashboard data loaded. Table items: 3
  - admin@nyp.com (Admin Kullanıcı)
  - user1@nyp.com (Ahmet Yılmaz)
  - user2@nyp.com (Ayşe Demir)
  Email cell: admin@nyp.com
  Email cell: user1@nyp.com
  Email cell: user2@nyp.com
```

## Eğer tablo hala boşsa:
- TableView'ın prefHeight değeri çok küçük olabilir
- FXML'de fx:id'ler yanlış eşleşmiş olabilir
- Controller FXML'e제대로 bağlanmamış olabilir
