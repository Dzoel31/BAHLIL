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

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookmarkAdapter adapter;
    private List<UserBookInteraction> fullList;
    private ImageView btnHistory;
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
        btnHistory = findViewById(R.id.iv_history_icon_header);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fullList = new ArrayList<>();
        adapter = new BookmarkAdapter(this, fullList);
        recyclerView.setAdapter(adapter);

        loadBookmarks();

        // Navigasi ke History
        btnHistory.setOnClickListener(v -> {
            startActivity(new Intent(BookmarkActivity.this, HistoryActivity.class));
            finish(); // Tutup agar tidak menumpuk
        });

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
    }

    private void loadBookmarks() {
        db.collection("users").document(userId).collection("bookmarks")
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