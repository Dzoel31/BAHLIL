package com.example.bahlil;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class TambahBukuActivity extends BaseActivity {

    private EditText titleInput, authorInput, descInput, coverUrlInput, contentUrlInput;
    private Spinner categorySpinner;
    private Button addButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_buku);

        db = FirebaseFirestore.getInstance();

        // Binding Views
        titleInput = findViewById(R.id.bookTitleInput);
        authorInput = findViewById(R.id.bookAuthorInput);
        descInput = findViewById(R.id.bookDescInput);
        // Pastikan XML tambahannya sudah dicopy ya!
        coverUrlInput = findViewById(R.id.bookCoverUrlInput);
        contentUrlInput = findViewById(R.id.bookContentUrlInput);

        categorySpinner = findViewById(R.id.bookCategory);
        addButton = findViewById(R.id.addBookButton);

        setupSpinner();

        addButton.setOnClickListener(v -> saveBook());
    }

    private void setupSpinner() {
        String[] categories = {"Fiksi", "Sains", "Teknologi", "Sejarah", "Biografi", "Lainnya"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(adapter);
    }

    private void saveBook() {
        String title = titleInput.getText().toString().trim();
        String author = authorInput.getText().toString().trim();
        String desc = descInput.getText().toString().trim();
        String coverUrl = coverUrlInput.getText().toString().trim();
        String contentUrl = contentUrlInput.getText().toString().trim(); // Ini untuk PDF
        String category = categorySpinner.getSelectedItem().toString();

        // 1. Validasi Input Kosong Dasar
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author)) {
            Toast.makeText(this, "Judul dan Penulis wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Validasi Format URL (Kode Baru)
        if (!android.util.Patterns.WEB_URL.matcher(coverUrl).matches()) {
            coverUrlInput.setError("Link Cover tidak valid (harus http/https)");
            return;
        }

        if (!android.util.Patterns.WEB_URL.matcher(contentUrl).matches()) {
            contentUrlInput.setError("Link PDF tidak valid (harus http/https)");
            return;
        }

        // Map data untuk Firestore
        Map<String, Object> bookMap = new HashMap<>();
        bookMap.put("judul", title);
        bookMap.put("penulis", author);
        bookMap.put("deskripsi", desc); // Field baru deskripsi
        bookMap.put("kategori", category);
        bookMap.put("coverUrl", coverUrl);
        bookMap.put("isiBuku", contentUrl); // Kita simpan konten/URL di field isiBuku

        // Simpan ke Firestore
        db.collection(Constants.COLLECTION_BOOKS)
                .add(bookMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(TambahBukuActivity.this, "Buku Berhasil Ditambahkan!", Toast.LENGTH_SHORT).show();
                    finish(); // Kembali ke halaman sebelumnya
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TambahBukuActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}