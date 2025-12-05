package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Delay 2 detik agar splash screen terlihat, lalu cek status login
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLoginStatus();
            }
        }, 2000); // 2000 ms = 2 detik
    }

    private void checkLoginStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User sudah login, arahkan ke HomeActivity
            // (Kita akan buat HomeActivity di tahap selanjutnya, ini akan merah dulu, abaikan saja)
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } else {
            // Belum login, arahkan ke LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        finish(); // Tutup MainActivity agar user tidak bisa back ke sini
    }
}