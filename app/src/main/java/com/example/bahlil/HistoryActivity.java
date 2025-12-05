package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

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
            // Handle user not logged in case
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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_history);

        bottomNavigationView.setOnItemSelectedListener(item -> {
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
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
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
