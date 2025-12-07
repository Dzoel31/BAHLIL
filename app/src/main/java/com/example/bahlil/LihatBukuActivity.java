package com.example.bahlil;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class LihatBukuActivity extends BaseActivity {

    private EditText searchInput;
    private Spinner filterCategory;
    private LinearLayout bookListContainer; // Container untuk list buku
    private FirebaseFirestore db;
    private List<Buku> allBooks = new ArrayList<>(); // Simpan semua data untuk filter lokal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_buku);

        db = FirebaseFirestore.getInstance();

        searchInput = findViewById(R.id.searchInput);
        filterCategory = findViewById(R.id.filterCategory);
        bookListContainer = findViewById(R.id.bookListContainer);

        setupCategorySpinner();
        loadAllBooks();

        // Listener pencarian realtime
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBooks(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupCategorySpinner() {
        // Setup Filter Kategori Dummy
        String[] categories = {"Semua", "Fiksi", "Sains", "Teknologi", "Sejarah"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        filterCategory.setAdapter(adapter);
    }

    private void loadAllBooks() {
        db.collection("books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allBooks.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Buku buku = doc.toObject(Buku.class);
                            buku.setId(doc.getId());
                            allBooks.add(buku);
                        }
                        // Tampilkan semua buku saat awal
                        displayBooks(allBooks);
                    }
                });
    }

    private void filterBooks(String keyword) {
        List<Buku> filteredList = new ArrayList<>();
        for (Buku buku : allBooks) {
            if (buku.getJudul().toLowerCase().contains(keyword.toLowerCase()) ||
                    buku.getPenulis().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(buku);
            }
        }
        displayBooks(filteredList);
    }

    // Fungsi Kunci: Mengubah LinearLayout menjadi list buku secara manual
    private void displayBooks(List<Buku> books) {
        bookListContainer.removeAllViews(); // Bersihkan list lama

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Buku buku : books) {
            // Inflate item_buku.xml
            View itemView = inflater.inflate(R.layout.item_buku, bookListContainer, false);

            // Binding Data
            ImageView cover = itemView.findViewById(R.id.bookCover);
            TextView title = itemView.findViewById(R.id.bookTitle);
            TextView author = itemView.findViewById(R.id.bookAuthor);
            TextView category = itemView.findViewById(R.id.bookCategory);

            title.setText(buku.getJudul());
            author.setText(buku.getPenulis());
            category.setText(buku.getKategori());

            if (buku.getCoverUrl() != null && !buku.getCoverUrl().isEmpty()) {
                Glide.with(this).load(buku.getCoverUrl()).into(cover);
            }

            // Klik item -> Buka BacaBukuActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(LihatBukuActivity.this, BacaBukuActivity.class);
                intent.putExtra("extra_buku", buku);
                startActivity(intent);
            });

            // Tambahkan view ke container
            bookListContainer.addView(itemView);
        }
    }
}