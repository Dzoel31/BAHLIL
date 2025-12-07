package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private BukuGridAdapter adapter;
    private List<Buku> listBuku; // List yang ditampilkan di adapter
    private List<Buku> allBukuList; // List master untuk semua buku
    private FirebaseFirestore db;

    private ImageView searchIcon;
    private EditText searchBar;
    private FloatingActionButton fabAddBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.booksRecyclerView);
        searchIcon = findViewById(R.id.searchIcon);
        searchBar = findViewById(R.id.searchBar);
        fabAddBook = findViewById(R.id.fabAddBook);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        listBuku = new ArrayList<>();
        allBukuList = new ArrayList<>();
        adapter = new BukuGridAdapter(this, listBuku);
        recyclerView.setAdapter(adapter);

        loadBooks();

        searchIcon.setOnClickListener(v -> {
            if (searchBar.getVisibility() == View.GONE) {
                searchBar.setVisibility(View.VISIBLE);
                searchBar.requestFocus();
            } else {
                searchBar.setVisibility(View.GONE);
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooks(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        fabAddBook.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TambahBukuActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true; // Sedang di Home, tidak lakukan apa-apa

            Intent intent = null;
            if (itemId == R.id.nav_bookmark) {
                intent = new Intent(getApplicationContext(), BookmarkActivity.class);
            } else if (itemId == R.id.nav_history) {
                intent = new Intent(getApplicationContext(), HistoryActivity.class);
            } else if (itemId == R.id.nav_profile) {
                intent = new Intent(getApplicationContext(), ProfilActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0); // Hilangkan animasi agar terasa seperti tab
                // JANGAN panggil finish() di sini agar Home tetap menjadi 'parent'
            }
            return true;
        });
    }

    private void loadBooks() {
        db.collection(Constants.COLLECTION_BOOKS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allBukuList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Buku buku = document.toObject(Buku.class);
                            buku.setId(document.getId());
                            allBukuList.add(buku);
                        }
                        filterBooks(""); // Tampilkan semua buku pada awalnya
                    } else {
                        Toast.makeText(HomeActivity.this, "Gagal memuat data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterBooks(String query) {
        listBuku.clear();
        if (query.isEmpty()) {
            listBuku.addAll(allBukuList);
        } else {
            for (Buku buku : allBukuList) {
                if (buku.getJudul().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))) {
                    listBuku.add(buku);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }
}