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
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BacaBukuActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private ImageView backButton;
    private TextView bookTitleToolbar;
    private ImageButton bookmarkButton;
    private PDFView pdfView;
    private ProgressBar progressBar;
    private TextView pageIndicator;

    private Buku currentBuku;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baca_buku);

        currentBuku = (Buku) getIntent().getSerializableExtra("extra_buku");

        backButton = findViewById(R.id.backButton);
        bookTitleToolbar = findViewById(R.id.bookTitleToolbar);
        bookmarkButton = findViewById(R.id.bookmarkButton);
        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.pdfProgressBar);
        pageIndicator = findViewById(R.id.pageIndicator);

        setupData();
        setupActions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveLastReadPage();
    }

    private void setupData() {
        if (currentBuku != null) {
            bookTitleToolbar.setText(currentBuku.getJudul());
            fetchLastReadPageAndLoadPdf();
        }
    }

    private void fetchLastReadPageAndLoadPdf() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && currentBuku != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                    .collection("history").document(currentBuku.getId())
                    .get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Long lastPageLong = documentSnapshot.getLong("lastPage");
                            if (lastPageLong != null) {
                                currentPage = lastPageLong.intValue();
                            }
                        }
                        loadPdf();
                    }).addOnFailureListener(e -> loadPdf());
        } else {
            loadPdf();
        }
    }

    private void loadPdf() {
        if (currentBuku.getIsiBuku() != null && !currentBuku.getIsiBuku().isEmpty()) {
            new RetrievePDFfromUrl().execute(currentBuku.getIsiBuku());
        } else {
            Toast.makeText(this, "Link PDF tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());
        bookmarkButton.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            saveToBookmark(v.isSelected());
            ((ImageButton) v).setImageResource(v.isSelected() ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        });
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        currentPage = page;
        pageIndicator.setText(String.format(Locale.getDefault(), "%d / %d", page + 1, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        pageIndicator.setText(String.format(Locale.getDefault(), "%d / %d", currentPage + 1, nbPages));
        saveToHistory(); // Simpan ke riwayat setelah buku berhasil dimuat
    }

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
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .defaultPage(currentPage)
                        .onPageChange(BacaBukuActivity.this)
                        .onLoad(BacaBukuActivity.this)
                        .load();
            } else {
                Toast.makeText(BacaBukuActivity.this, "Gagal memuat PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveToHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || currentBuku == null) return;
        String userId = user.getUid();
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        UserBookInteraction interaction = new UserBookInteraction(currentBuku.getId(), currentBuku.getJudul(), date, currentPage, currentBuku.getCoverUrl(), currentBuku);
        FirebaseFirestore.getInstance().collection("users").document(userId).collection("history").document(currentBuku.getId()).set(interaction);
    }

    private void saveLastReadPage() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && currentBuku != null) {
            String userId = user.getUid();
            String bookId = currentBuku.getId();
            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            Map<String, Object> updates = new HashMap<>();
            updates.put("lastPage", currentPage);
            updates.put("lastReadDate", date);
            FirebaseFirestore.getInstance().collection("users").document(userId).collection("history").document(bookId).update(updates);
        }
    }

    private void saveToBookmark(boolean isSaving) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || currentBuku == null) return;
        String userId = user.getUid();
        String bookId = currentBuku.getId();
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        if (isSaving) {
            UserBookInteraction interaction = new UserBookInteraction(bookId, currentBuku.getJudul(), date, currentPage, currentBuku.getCoverUrl(), currentBuku);
            FirebaseFirestore.getInstance().collection("users").document(userId).collection("bookmarks").document(bookId).set(interaction);
            Toast.makeText(this, "Ditambahkan ke Bookmark", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseFirestore.getInstance().collection("users").document(userId).collection("bookmarks").document(bookId).delete();
            Toast.makeText(this, "Dihapus dari Bookmark", Toast.LENGTH_SHORT).show();
        }
    }
}