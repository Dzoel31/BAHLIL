package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ManajemenAkunActivity extends AppCompatActivity {

    private Button btnEditProfile, btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manajemen_akun);

        btnEditProfile = findViewById(R.id.navigateToEditProfileButton);
        btnChangePassword = findViewById(R.id.navigateToChangePasswordButton);

        // Navigasi ke Edit Profil (Ganti Nama/Foto)
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ManajemenAkunActivity.this, EditProfilActivity.class);
            startActivity(intent);
        });

        // Navigasi ke Ubah Sandi
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(ManajemenAkunActivity.this, UbahSandiActivity.class);
            startActivity(intent);
        });

        // --- NAVBAR ---
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Profil Aktif

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            if (itemId == R.id.nav_bookmark) {
                startActivity(new Intent(getApplicationContext(), BookmarkActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            // LOGIKA PINDAH KE HISTORY
            if (itemId == R.id.nav_history) {
                startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            if (itemId == R.id.nav_profile) return true;
            return false;
        });
    }
}