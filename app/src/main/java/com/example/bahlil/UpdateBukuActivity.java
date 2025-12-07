package com.example.bahlil;

import android.os.Bundle;
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

public class UpdateBukuActivity extends BaseActivity {

    private EditText titleInput, authorInput, descInput, coverUrlInput, contentUrlInput;
    private Spinner categorySpinner;
    private Button updateButton, deleteButton;
    private FirebaseFirestore db;
    private Buku currentBuku; // Object buku yang sedang diedit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_buku);

        db = FirebaseFirestore.getInstance();

        // Terima data dari Intent
        currentBuku = (Buku) getIntent().getSerializableExtra("extra_buku");

        // Binding Views (Sesuaikan ID dengan XML Update)
        titleInput = findViewById(R.id.updateBookTitleInput);
        authorInput = findViewById(R.id.updateBookAuthorInput);
        descInput = findViewById(R.id.updateBookDescInput);
        coverUrlInput = findViewById(R.id.updateBookCoverUrlInput); // ID baru di XML Update
        contentUrlInput = findViewById(R.id.updateBookContentUrlInput); // ID baru di XML Update

        categorySpinner = findViewById(R.id.updateBookCategory);
        updateButton = findViewById(R.id.updateBookButton);
        deleteButton = findViewById(R.id.deleteBookButton);

        setupSpinner();
        populateData();

        updateButton.setOnClickListener(v -> updateBook());
        deleteButton.setOnClickListener(v -> deleteBook());
    }

    private void setupSpinner() {
        String[] categories = {"Fiksi", "Sains", "Teknologi", "Sejarah", "Biografi", "Lainnya"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(adapter);
    }

    private void populateData() {
        if (currentBuku != null) {
            titleInput.setText(currentBuku.getJudul());
            authorInput.setText(currentBuku.getPenulis());
            // contentUrlInput diisi dengan isiBuku (karena struktur data kita simpan URL di isiBuku)
            contentUrlInput.setText(currentBuku.getIsiBuku());
            coverUrlInput.setText(currentBuku.getCoverUrl());
            // Note: deskripsi perlu ditambahkan ke Class Buku.java jika ingin diambil.
            // Jika belum ada di Buku.java, bagian ini bisa diskip atau ambil manual dr Firestore.
        }
    }

    private void updateBook() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("judul", titleInput.getText().toString());
        updates.put("penulis", authorInput.getText().toString());
        updates.put("deskripsi", descInput.getText().toString());
        updates.put("isiBuku", contentUrlInput.getText().toString());
        updates.put("coverUrl", coverUrlInput.getText().toString());
        updates.put("kategori", categorySpinner.getSelectedItem().toString());

        db.collection(Constants.COLLECTION_BOOKS).document(currentBuku.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Data Berhasil Diupdate", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal Update", Toast.LENGTH_SHORT).show());
    }

    private void deleteBook() {
        db.collection("books").document(currentBuku.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Buku Dihapus", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal Hapus", Toast.LENGTH_SHORT).show());
    }
}