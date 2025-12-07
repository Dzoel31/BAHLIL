package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// PERUBAHAN PENTING: Menggunakan BaseActivity agar fitur cek internet aktif di sini
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // OPSI DEBUGGING (Hanya nyalakan jika ingin memaksa logout saat testing):
        // FirebaseAuth.getInstance().signOut();

        // Delay 2 detik (2000 ms) agar logo splash screen terlihat sejenak
        // Menggunakan Looper.getMainLooper() agar aman di Android versi baru
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLoginStatus();
            }
        }, 2000);
    }

    private void checkLoginStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // User terdeteksi login di cache HP.
            // Kita lakukan validasi ke server Firebase (reload) untuk memastikan akun belum dihapus/diblokir.
            currentUser.reload().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Sesi Valid & Akun Aman -> Lanjut ke Home
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                } else {
                    // Sesi Tidak Valid (misal: password diganti di device lain atau user dihapus admin)
                    // Paksa Logout dan kembali ke Login
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                // Tutup MainActivity agar tidak bisa kembali ke Splash Screen dengan tombol Back
                finish();
            });
        } else {
            // Belum login sama sekali -> Arahkan ke halaman Login
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
}