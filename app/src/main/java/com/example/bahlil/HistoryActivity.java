package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity; // Atau BaseActivity jika sudah diubah
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

// Pastikan extends BaseActivity jika Anda mengikuti panduan sebelumnya,
// jika belum, tetap extends AppCompatActivity.
public class HistoryActivity extends BaseActivity { // Ganti BaseActivity/AppCompatActivity sesuai kondisi proyek

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<UserBookInteraction> fullList;
    private SearchView searchView;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }
        userId = currentUser.getUid();

        recyclerView = findViewById(R.id.rv_history);
        searchView = findViewById(R.id.search_view);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fullList = new ArrayList<>();
        adapter = new HistoryAdapter(this, fullList);
        recyclerView.setAdapter(adapter);

        loadHistory();

        // Fitur Search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        // --- PERBAIKAN NAVIGASI DI SINI ---
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_history);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_history) return true; // Sedang di sini

            Intent intent = null;
            if (itemId == R.id.nav_home) {
                // Gunakan HistoryActivity.this, BUKAN getApplicationContext()
                intent = new Intent(HistoryActivity.this, HomeActivity.class);
            } else if (itemId == R.id.nav_bookmark) {
                intent = new Intent(HistoryActivity.this, BookmarkActivity.class);
            } else if (itemId == R.id.nav_profile) {
                intent = new Intent(HistoryActivity.this, ProfilActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0); // Hilangkan animasi
                finish(); // Tutup activity ini agar tidak menumpuk
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    private void loadHistory() {
        // Pastikan Constants.COLLECTION_USERS dan HISTORY sudah sesuai (jika pakai Constants)
        // Jika belum pakai Constants, biarkan string "users" dan "history"
        db.collection(Constants.COLLECTION_USERS).document(userId).collection(Constants.COLLECTION_HISTORY)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fullList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            fullList.add(doc.toObject(UserBookInteraction.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void filter(String text) {
        List<UserBookInteraction> filtered = new ArrayList<>();
        if(fullList != null) {
            for (UserBookInteraction item : fullList) {
                if (item.getTitle() != null && item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    filtered.add(item);
                }
            }
        }
        adapter.updateList(filtered);
    }
}