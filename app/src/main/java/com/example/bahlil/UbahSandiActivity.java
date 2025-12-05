package com.example.bahlil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UbahSandiActivity extends AppCompatActivity {

    private EditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private Button changePasswordButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_sandi);

        mAuth = FirebaseAuth.getInstance();

        // Binding Views
        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(v -> prosesUbahSandi());
    }

    private void prosesUbahSandi() {
        String oldPass = currentPasswordInput.getText().toString().trim();
        String newPass = newPasswordInput.getText().toString().trim();
        String confirmPass = confirmPasswordInput.getText().toString().trim();

        // 1. Validasi Input Dasar
        if (TextUtils.isEmpty(oldPass)) {
            currentPasswordInput.setError("Sandi saat ini wajib diisi");
            return;
        }
        if (newPass.length() < 6) {
            newPasswordInput.setError("Sandi baru minimal 6 karakter");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            confirmPasswordInput.setError("Konfirmasi sandi tidak cocok");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // 2. Re-Autentikasi (Keamanan Firebase)
            // Kita harus membuktikan bahwa yang ganti sandi adalah pemilik akun asli
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // 3. Jika sandi lama benar, update sandi baru
                            user.updatePassword(newPass)
                                    .addOnCompleteListener(taskUpdate -> {
                                        if (taskUpdate.isSuccessful()) {
                                            Toast.makeText(UbahSandiActivity.this, "Sandi Berhasil Diubah!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(UbahSandiActivity.this, "Gagal Update: " + taskUpdate.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Jika sandi lama salah
                            currentPasswordInput.setError("Sandi saat ini salah");
                            Toast.makeText(UbahSandiActivity.this, "Autentikasi Gagal. Periksa sandi lama Anda.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}