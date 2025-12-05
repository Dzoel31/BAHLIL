package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BukuGridAdapter adapter;
    private List<Buku> listBuku;
    private FirebaseFirestore db;

    // UI Components
    private ImageView searchIcon;
    private EditText searchBar;
    private FloatingActionButton fabAddBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        // Init UI
        recyclerView = findViewById(R.id.booksRecyclerView);
        searchIcon = findViewById(R.id.searchIcon);
        searchBar = findViewById(R.id.searchBar);
        fabAddBook = findViewById(R.id.fabAddBook);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        listBuku = new ArrayList<>();
        adapter = new BukuGridAdapter(this, listBuku);
        recyclerView.setAdapter(adapter);

        // Load Data
        loadBooks();

        // Tombol Search (Toggle Visibility)
        searchIcon.setOnClickListener(v -> {
            if (searchBar.getVisibility() == View.GONE) {
                searchBar.setVisibility(View.VISIBLE);
            } else {
                searchBar.setVisibility(View.GONE);
            }
        });

        // Tombol Tambah Buku (FAB)
        fabAddBook.setOnClickListener(v -> {
            // Arahkan ke Activity Tambah Buku (Akan dibuat di Tahap 3)
//            Intent intent = new Intent(HomeActivity.this, TambahBukuActivity.class);
//            startActivity(intent);
        });

        // --- NAVBAR ---
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home); // Home Aktif

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true;

            if (itemId == R.id.nav_bookmark) {
                startActivity(new Intent(getApplicationContext(), BookmarkActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            // LOGIKA PINDAH KE HISTORY
            if (itemId == R.id.nav_history) {
                startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfilActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void loadBooks() {
        // Mengambil data dari koleksi "books" di Firestore
        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listBuku.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Konversi dokumen Firestore ke object Buku
                            Buku buku = document.toObject(Buku.class);
                            buku.setId(document.getId()); // Simpan ID dokumen
                            listBuku.add(buku);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(HomeActivity.this, "Gagal memuat data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Refresh data saat kembali ke halaman ini (misal setelah tambah buku)
    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
    }
}