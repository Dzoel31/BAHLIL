package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;
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
    private Spinner categorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.booksRecyclerView);
        searchIcon = findViewById(R.id.searchIcon);
        searchBar = findViewById(R.id.searchBar);
        fabAddBook = findViewById(R.id.fabAddBook);
        categorySpinner = findViewById(R.id.categorySpinner);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        listBuku = new ArrayList<>();
        allBukuList = new ArrayList<>();
        adapter = new BukuGridAdapter(this, listBuku);
        recyclerView.setAdapter(adapter);

        setupCategorySpinner();
        loadBooksFromServer();

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
                applyFilters();
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
            if (itemId == R.id.nav_home) return true;

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
                overridePendingTransition(0, 0);
            }
            return true;
        });
    }

    private void setupCategorySpinner() {
        String[] categories = {"Semua Kategori", "Fiksi", "Sains", "Teknologi", "Sejarah", "Biografi", "Lainnya"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Tidak melakukan apa-apa
            }
        });
    }

    private void loadBooksFromServer() {
        db.collection(Constants.COLLECTION_BOOKS)
                .get(Source.SERVER)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allBukuList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Buku buku = document.toObject(Buku.class);
                            buku.setId(document.getId());
                            allBukuList.add(buku);
                        }
                        applyFilters();
                    } else {
                        Toast.makeText(HomeActivity.this, "Gagal memuat data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyFilters() {
        String query = searchBar.getText().toString().toLowerCase(Locale.ROOT);
        String selectedCategory = "Semua Kategori";
        if (categorySpinner.getSelectedItem() != null) {
            selectedCategory = categorySpinner.getSelectedItem().toString();
        }

        listBuku.clear();

        for (Buku buku : allBukuList) {
            boolean categoryMatch = "Semua Kategori".equals(selectedCategory) ||
                    (buku.getKategori() != null && buku.getKategori().equalsIgnoreCase(selectedCategory));

            boolean queryMatch = query.isEmpty() ||
                    (buku.getJudul() != null && buku.getJudul().toLowerCase(Locale.ROOT).contains(query)) ||
                    (buku.getPenulis() != null && buku.getPenulis().toLowerCase(Locale.ROOT).contains(query));

            if (categoryMatch && queryMatch) {
                listBuku.add(buku);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooksFromServer();
    }
}
