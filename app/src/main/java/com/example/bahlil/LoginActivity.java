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

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, registerTextButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Binding View sesuai ID di XML activity_login.xml
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerTextButton = findViewById(R.id.registerTextButton);

        // Aksi Tombol Login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Aksi Tombol Pindah ke Register
        registerTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email wajib diisi");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Kata sandi wajib diisi");
            return;
        }

        // Proses Login ke Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                        // Pindah ke HomeActivity (Nanti kita buat)
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}