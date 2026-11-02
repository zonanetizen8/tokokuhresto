# TOKOKUH RESTO — Android Offline POS

Aplikasi kasir dan manajemen restoran Android berbasis Kotlin, Jetpack Compose, Room SQLite, dan berjalan lokal/offline. Data awal sengaja kosong; pemilik mengisi profil restoran, menu, kategori, meja, bahan baku, dan karyawan sendiri. Aplikasi dibuka langsung ke Dashboard tanpa login.

## Build di GitHub Codespaces

1. Buka repository ini di GitHub Codespaces.
2. Pastikan Java 17 atau lebih baru tersedia.
3. Pastikan Android SDK terpasang dan `ANDROID_HOME` mengarah ke SDK. Codespaces perlu Android SDK Platform 36 dan Build Tools yang sesuai.
4. Jalankan: `chmod +x gradlew`
5. Jalankan: `./gradlew :app:testDebugUnitTest :app:assembleDebug`
6. APK debug berada di `app/build/outputs/apk/debug/app-debug.apk`.

Script `gradlew` pada ZIP ini mengunduh Gradle 9.3.1 secara otomatis pada pemakaian pertama. Koneksi internet hanya dibutuhkan untuk mengunduh Gradle/dependensi saat build; aplikasi hasil build tidak memerlukan internet untuk operasionalnya.

## Catatan

- Database Room tidak diisi data demo pada instalasi baru.
- Foto menu dan logo restoran disimpan di penyimpanan internal aplikasi.
- Logo restoran dapat diatur dari Pengaturan Restoran dan digunakan pada preview/PDF struk.
- Nama pengembang pada halaman Tentang: **Logis Denis Prabowo**.
- Tidak ada Firebase atau Google Services yang diperlukan untuk menjalankan aplikasi.
