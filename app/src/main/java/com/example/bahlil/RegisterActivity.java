package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends BaseActivity {

    private EditText fullNameInput, emailInput, passwordInput;
    private Button registerButton, loginTextButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Binding View sesuai ID di XML activity_register.xml
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
        loginTextButton = findViewById(R.id.loginTextButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        loginTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser() {
        String nama = fullNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(nama)) {
            fullNameInput.setError("Nama lengkap wajib diisi");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email wajib diisi");
            return;
        }
        if (password.length() < 6) {
            passwordInput.setError("Kata sandi minimal 6 karakter");
            return;
        }

        // Buat User di Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Simpan data tambahan ke Firestore
                        saveUserToFirestore(nama, email);
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthUserCollisionException e) {
                            emailInput.setError("Email sudah terdaftar.");
                            emailInput.requestFocus();
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "Gagal Daftar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserToFirestore(String nama, String email) {
        String userId = mAuth.getCurrentUser().getUid();

        // Map data untuk disimpan
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", nama);
        userMap.put("email", email);
        userMap.put("role", "user"); // Default role

        db.collection(Constants.COLLECTION_USERS).document(userId)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Gagal simpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}