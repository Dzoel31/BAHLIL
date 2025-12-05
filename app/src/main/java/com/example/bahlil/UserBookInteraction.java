package com.example.bahlil;

import java.io.Serializable;

public class UserBookInteraction implements Serializable {
    private String bookId;
    private String title;
    private String lastReadDate; // String format dd/MM/yyyy
    private int lastPage;
    private String coverUrl;
    private Buku bukuAsli; // Menyimpan object buku asli untuk operan data

    public UserBookInteraction() { }

    public UserBookInteraction(String bookId, String title, String lastReadDate, int lastPage, String coverUrl, Buku bukuAsli) {
        this.bookId = bookId;
        this.title = title;
        this.lastReadDate = lastReadDate;
        this.lastPage = lastPage;
        this.coverUrl = coverUrl;
        this.bukuAsli = bukuAsli;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getLastReadDate() { return lastReadDate; }
    public int getLastPage() { return lastPage; }
    public String getCoverUrl() { return coverUrl; }
    public Buku getBukuAsli() { return bukuAsli; }
}