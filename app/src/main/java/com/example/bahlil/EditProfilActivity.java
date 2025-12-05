package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfilActivity extends AppCompatActivity {

    private EditText etNamaLengkap, etEmail;
    private Button btnSimpan, btnUbahSandi;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Cek user login
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            finish(); // Tutup jika tidak login
            return;
        }
        userId = user.getUid();

        // Bind Views
        etNamaLengkap = findViewById(R.id.et_nama_lengkap);
        etEmail = findViewById(R.id.et_email);
        btnSimpan = findViewById(R.id.btn_simpan_perubahan);
        btnUbahSandi = findViewById(R.id.btn_ubah_sandi);

        // Load Data Awal
        etEmail.setText(user.getEmail()); // Email dari Auth (Read Only)
        loadCurrentProfile();

        // Aksi Simpan
        btnSimpan.setOnClickListener(v -> saveProfileChanges());

        // Aksi Ubah Sandi
        btnUbahSandi.setOnClickListener(v -> {
            // Arahkan ke Activity Ubah Sandi (Akan dibuat di tahap selanjutnya)
            Intent intent = new Intent(EditProfilActivity.this, UbahSandiActivity.class);
            startActivity(intent);
        });
    }

    private void loadCurrentProfile() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        etNamaLengkap.setText(name);
                    }
                });
    }

    private void saveProfileChanges() {
        String newName = etNamaLengkap.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            etNamaLengkap.setError("Nama tidak boleh kosong");
            return;
        }

        // Update data di Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", newName);

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfilActivity.this, "Profil Berhasil Diupdate!", Toast.LENGTH_SHORT).show();
                    finish(); // Kembali ke halaman profil
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfilActivity.this, "Gagal Update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}