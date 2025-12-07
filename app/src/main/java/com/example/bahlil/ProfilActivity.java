package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfilActivity extends BaseActivity {

    private TextView profileName, profileEmail;
    private Button logoutButton, editAccountButton;
    private ImageView profileImage;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Init View
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        logoutButton = findViewById(R.id.logoutButton);
        editAccountButton = findViewById(R.id.editAccountButton);
        profileImage = findViewById(R.id.profileImage);

        loadUserData();

        // Tombol Edit Profil
        editAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, EditProfilActivity.class);
            startActivity(intent);
        });

        // Tombol Logout
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(ProfilActivity.this, "Berhasil Logout", Toast.LENGTH_SHORT).show();

            // Kembali ke halaman Login dan hapus stack history agar tidak bisa di-back
            Intent intent = new Intent(ProfilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // --- NAVBAR ---
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Profil Aktif

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) return true; // Sedang di sini

            if (itemId == R.id.nav_home) {
                // Jika ke Home, gunakan FLAG_ACTIVITY_CLEAR_TOP untuk menghapus tumpukan di atas Home
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish(); // Hapus activity saat ini
                return true;
            }

            // Logika pindah ke menu lain (selain Home dan Diri Sendiri)
            Intent intent = null;
            if (itemId == R.id.nav_bookmark) {
                intent = new Intent(getApplicationContext(), BookmarkActivity.class);
            } else if (itemId == R.id.nav_history) {
                intent = new Intent(getApplicationContext(), HistoryActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish(); // PENTING: Hapus activity saat ini agar tidak menumpuk
            }
            return true;
        });
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Set Email langsung dari Auth
            profileEmail.setText(user.getEmail());

            // Ambil Nama Lengkap dari Firestore
            db.collection(Constants.COLLECTION_USERS).document(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("fullName");
                                profileName.setText(name != null ? name : "User");
                            }
                        } else {
                            Toast.makeText(ProfilActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Refresh data saat kembali dari Edit Profil
    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }
}