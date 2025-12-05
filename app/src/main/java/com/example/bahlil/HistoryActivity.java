package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<UserBookInteraction> fullList;
    private ImageView btnBookmark;
    private SearchView searchView;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView = findViewById(R.id.rv_history);
        searchView = findViewById(R.id.search_view);
        // --- SETUP NAVBAR (4 Ikon) ---
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // SET ITEM AKTIF: HISTORY
        bottomNavigationView.setSelectedItemId(R.id.nav_history);
        // btnBookmark = findViewById(R.id.iv_bookmark_icon_header); // Icon Bookmark di layout history

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_bookmark) {
                    startActivity(new Intent(getApplicationContext(), BookmarkActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_history) {
                    return true; // Sedang di History, tidak perlu aksi
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(getApplicationContext(), ProfilActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    private void loadHistory() {
        db.collection("users").document(userId).collection("history")
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
        for (UserBookInteraction item : fullList) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filtered.add(item);
            }
        }
        adapter.updateList(filtered);
    }
}