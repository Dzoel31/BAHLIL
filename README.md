# BAHLIL (Book Archive & Highlight Library)

Proyek Mata Kuliah Pemrograman Platform


| Nama | NIM | Role |
| ---- | --- | ----|
| Yusuf Sheva Aimar | 2210511082 | Developer |
| Dzulfikri Adjmal | 2210511084 | Developer |
| Muhammad Rezka Al Maghribi | 2210511086 | Project Manager |
| Muhammad Ardhin Fakhrezi Hermawan | 2210511154 | Developer |
| Vincentius Gerd | 2310511146 | Developer |

## Fitur

*   **Autentikasi Pengguna:**
    *   Login dan registrasi pengguna menggunakan Firebase Authentication.
    *   Sistem role-based (admin dan pengguna biasa) untuk mengontrol akses ke fitur-fitur tertentu.

*   **Manajemen Buku:**
    *   Menampilkan daftar buku dalam format grid.
    *   Menambahkan, memperbarui, dan menghapus buku (fitur khusus admin).
    *   Mencari buku berdasarkan judul.

*   **Interaksi Pengguna:**
    *   Membaca buku.
    *   Memberi dan menghapus bookmark pada buku.
    *   Melihat riwayat buku yang telah dibaca.

*   **Manajemen Profil:**
    *   Melihat dan memperbarui informasi profil pengguna (nama dan email).
    *   Mengubah kata sandi.
    *   Logout dari aplikasi.

*   **Kustomisasi Tema:**
    *   Mengubah tema aplikasi antara mode terang, gelap, dan default sistem.

## Teknologi dan Library

*   **Firebase:**
    *   **Firestore:** Digunakan sebagai database NoSQL untuk menyimpan data buku, pengguna, bookmark, dan riwayat.
    *   **Firebase Authentication:** Mengelola autentikasi pengguna.

*   **Android Jetpack:**
    *   **RecyclerView:** Menampilkan daftar buku, bookmark, dan riwayat secara efisien.
    *   **CardView:** Menampilkan item dalam daftar dengan tampilan kartu yang modern.
    *   **ConstraintLayout:** Mendesain layout yang responsif dan fleksibel.
    *   **AppCompat:** Memastikan kompatibilitas aplikasi dengan berbagai versi Android.

*   **UI/UX:**
    *   **Material Design 3:** Mengimplementasikan prinsip-prinsip desain modern dari Google.
    *   **BottomNavigationView:** Menyediakan navigasi utama di bagian bawah layar.
    *   **Glide:** Memuat dan menampilkan gambar dari URL secara efisien.

*   **Bahasa Pemrograman:**
    *   **Java:** Bahasa utama yang digunakan untuk membangun logika aplikasi.
