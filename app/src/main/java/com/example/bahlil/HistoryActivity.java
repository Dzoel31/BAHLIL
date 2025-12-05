package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
        btnBookmark = findViewById(R.id.iv_bookmark_icon_header); // Icon Bookmark di layout history

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fullList = new ArrayList<>();
        adapter = new HistoryAdapter(this, fullList);
        recyclerView.setAdapter(adapter);

        loadHistory();

        // Navigasi ke Bookmark
        btnBookmark.setOnClickListener(v -> {
            startActivity(new Intent(HistoryActivity.this, BookmarkActivity.class));
            finish();
        });

        // Search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
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