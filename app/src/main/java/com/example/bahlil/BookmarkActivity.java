package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity; // Atau BaseActivity
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class BookmarkActivity extends BaseActivity { // Ganti sesuai kondisi proyek

    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    private List<UserBookInteraction> fullList;
    private SearchView searchView;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = findViewById(R.id.rv_bookmark);
        searchView = findViewById(R.id.search_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fullList = new ArrayList<>();
        adapter = new BookmarkAdapter(this, fullList);
        recyclerView.setAdapter(adapter);

        loadBookmarks();

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
        bottomNavigationView.setSelectedItemId(R.id.nav_bookmark);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_bookmark) return true; // Sedang di sini

            Intent intent = null;
            if (itemId == R.id.nav_home) {
                // Gunakan BookmarkActivity.this
                intent = new Intent(BookmarkActivity.this, HomeActivity.class);
            } else if (itemId == R.id.nav_history) {
                intent = new Intent(BookmarkActivity.this, HistoryActivity.class);
            } else if (itemId == R.id.nav_profile) {
                intent = new Intent(BookmarkActivity.this, ProfilActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish(); // Tutup activity
                return true;
            }
            return false;
        });
    }

    private void loadBookmarks() {
        // Gunakan Constants jika sudah diterapkan
        db.collection(Constants.COLLECTION_USERS).document(userId).collection(Constants.COLLECTION_BOOKMARKS)
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
        if (fullList != null) {
            for (UserBookInteraction item : fullList) {
                if (item.getTitle() != null && item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    filtered.add(item);
                }
            }
        }
        adapter.updateList(filtered);
    }
}