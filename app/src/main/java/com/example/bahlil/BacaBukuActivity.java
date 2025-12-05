package com.example.bahlil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class BacaBukuActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView bookTitleToolbar;
    private ImageButton bookmarkButton;
    private PDFView pdfView;
    private ProgressBar progressBar;

    private Buku currentBuku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baca_buku);

        currentBuku = (Buku) getIntent().getSerializableExtra("extra_buku");

        // Init Views
        backButton = findViewById(R.id.backButton);
        bookTitleToolbar = findViewById(R.id.bookTitleToolbar);
        bookmarkButton = findViewById(R.id.bookmarkButton);
        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.pdfProgressBar);

        setupData();
        setupActions();
    }

    private void setupData() {
        if (currentBuku != null) {
            bookTitleToolbar.setText(currentBuku.getJudul());

            // Cek apakah ada URL PDF
            if (currentBuku.getIsiBuku() != null && !currentBuku.getIsiBuku().isEmpty()) {
                // Load PDF dari URL (Async)
                new RetrievePDFfromUrl().execute(currentBuku.getIsiBuku());
            } else {
                Toast.makeText(this, "Link PDF tidak ditemukan", Toast.LENGTH_SHORT).show();
            }

            // Simpan ke History saat dibuka
            saveToHistory();
        }
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        bookmarkButton.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            if (v.isSelected()) {
                saveToBookmark(true);
                ((ImageButton)v).setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                saveToBookmark(false);
                ((ImageButton)v).setImageResource(android.R.drawable.btn_star_big_off);
            }
        });
    }

    // Class Async untuk download Stream PDF dari URL
    class RetrievePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            progressBar.setVisibility(View.GONE);
            if (inputStream != null) {
                pdfView.fromStream(inputStream)
                        .enableSwipe(true)
                        .swipeHorizontal(false) // Scroll vertikal (seperti web)
                        .enableDoubletap(true)
                        .defaultPage(0)
                        .enableAnnotationRendering(false)
                        .password(null)
                        .scrollHandle(null)
                        .enableAntialiasing(true)
                        .spacing(10) // Jarak antar halaman
                        .load();
            } else {
                Toast.makeText(BacaBukuActivity.this, "Gagal memuat PDF. Cek koneksi internet.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // --- FITUR HISTORY & BOOKMARK (Sama seperti sebelumnya) ---

    private void saveToHistory() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String date = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new Date());

        UserBookInteraction interaction = new UserBookInteraction(
                currentBuku.getId(), currentBuku.getJudul(), date, 0, currentBuku.getCoverUrl(), currentBuku
        );

        FirebaseFirestore.getInstance().collection("users").document(userId)
                .collection("history").document(currentBuku.getId())
                .set(interaction);
    }

    private void saveToBookmark(boolean isSaving) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String date = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(new Date());

        if (isSaving) {
            UserBookInteraction interaction = new UserBookInteraction(
                    currentBuku.getId(), currentBuku.getJudul(), date, 0, currentBuku.getCoverUrl(), currentBuku
            );
            FirebaseFirestore.getInstance().collection("users").document(userId)
                    .collection("bookmarks").document(currentBuku.getId())
                    .set(interaction);
            Toast.makeText(this, "Ditambahkan ke Bookmark", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseFirestore.getInstance().collection("users").document(userId)
                    .collection("bookmarks").document(currentBuku.getId())
                    .delete();
            Toast.makeText(this, "Dihapus dari Bookmark", Toast.LENGTH_SHORT).show();
        }
    }
}