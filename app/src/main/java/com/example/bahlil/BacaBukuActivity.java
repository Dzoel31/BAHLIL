package com.example.bahlil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BacaBukuActivity extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener {

    private ImageView backButton;
    private TextView bookTitleToolbar;
    private ImageButton bookmarkButton;
    private PDFView pdfView;
    private ProgressBar progressBar;
    private TextView pageIndicator;

    private Buku currentBuku;
    private int currentPage = 0;

    // Executor untuk menggantikan AsyncTask
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baca_buku);

        // Mengambil data buku dari Intent
        currentBuku = (Buku) getIntent().getSerializableExtra("extra_buku");

        // Inisialisasi View
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
        // Membersihkan executor saat Activity dihancurkan untuk mencegah memory leak
        executor.shutdown();
    }

    private void setupData() {
        if (currentBuku != null) {
            bookTitleToolbar.setText(currentBuku.getJudul());
            fetchLastReadPageAndLoadPdf();
            checkBookmarkStatus(); // Cek apakah buku sudah dibookmark sebelumnya
        }
    }

    private void fetchLastReadPageAndLoadPdf() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && currentBuku != null) {
            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS).document(user.getUid())
                    .collection(Constants.COLLECTION_HISTORY).document(currentBuku.getId())
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

    // --- BAGIAN YANG DIPERBAIKI (Ganti AsyncTask) ---
    private void loadPdf() {
        if (currentBuku.getIsiBuku() != null && !currentBuku.getIsiBuku().isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);

            // Menjalankan proses download di Background Thread
            executor.execute(() -> {
                InputStream inputStream = null;
                try {
                    URL url = new URL(currentBuku.getIsiBuku());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    if (urlConnection.getResponseCode() == 200) {
                        inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Kembali ke Main UI Thread untuk menampilkan PDF
                InputStream finalInputStream = inputStream;
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (finalInputStream != null) {
                        pdfView.fromStream(finalInputStream)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .enableDoubletap(true)
                                .defaultPage(currentPage)
                                .onPageChange(BacaBukuActivity.this)
                                .onLoad(BacaBukuActivity.this)
                                .load();
                    } else {
                        Toast.makeText(BacaBukuActivity.this, "Gagal memuat PDF. Periksa koneksi internet.", Toast.LENGTH_SHORT).show();
                    }
                });
            });

        } else {
            Toast.makeText(this, "Link PDF tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }
    // ------------------------------------------------

    private void setupActions() {
        backButton.setOnClickListener(v -> finish());

        bookmarkButton.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            saveToBookmark(v.isSelected());
            updateBookmarkIcon(v.isSelected());
        });
    }

    private void updateBookmarkIcon(boolean isBookmarked) {
        bookmarkButton.setImageResource(isBookmarked ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
    }

    // Mengecek status bookmark saat awal load
    private void checkBookmarkStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && currentBuku != null) {
            FirebaseFirestore.getInstance().collection("users").document(user.getUid())
                    .collection("bookmarks").document(currentBuku.getId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        boolean isBookmarked = documentSnapshot.exists();
                        bookmarkButton.setSelected(isBookmarked);
                        updateBookmarkIcon(isBookmarked);
                    });
        }
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

    private void saveToHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || currentBuku == null) return;
        String userId = user.getUid();
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        UserBookInteraction interaction = new UserBookInteraction(
                currentBuku.getId(),
                currentBuku.getJudul(),
                date,
                currentPage,
                currentBuku.getCoverUrl(),
                currentBuku
        );

        FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS).document(userId)
                .collection(Constants.COLLECTION_HISTORY).document(currentBuku.getId())
                .set(interaction);
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

            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS).document(userId)
                    .collection(Constants.COLLECTION_HISTORY).document(bookId)
                    .update(updates);
        }
    }

    private void saveToBookmark(boolean isSaving) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || currentBuku == null) return;

        String userId = user.getUid();
        String bookId = currentBuku.getId();
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (isSaving) {
            UserBookInteraction interaction = new UserBookInteraction(
                    bookId,
                    currentBuku.getJudul(),
                    date,
                    currentPage,
                    currentBuku.getCoverUrl(),
                    currentBuku
            );
            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS).document(userId)
                    .collection(Constants.COLLECTION_BOOKMARKS).document(bookId)
                    .set(interaction);
            Toast.makeText(this, "Ditambahkan ke Bookmark", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS).document(userId)
                    .collection(Constants.COLLECTION_BOOKMARKS).document(bookId)
                    .delete();
            Toast.makeText(this, "Dihapus dari Bookmark", Toast.LENGTH_SHORT).show();
        }
    }
}