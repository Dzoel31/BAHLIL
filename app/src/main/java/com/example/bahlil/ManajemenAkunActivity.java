package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

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
    }
}