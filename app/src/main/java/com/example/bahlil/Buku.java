package com.example.bahlil;

import com.google.firebase.firestore.Exclude;
import java.io.Serializable;

public class Buku implements Serializable {

    @Exclude
    private String id;

    private String judul;
    private String penulis;
    private String deskripsi;
    private String kategori;
    private String coverUrl;
    private String isiBuku;

    public Buku() {
    }

    public Buku(String judul, String penulis, String deskripsi, String kategori, String coverUrl, String isiBuku) {
        this.judul = judul;
        this.penulis = penulis;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.coverUrl = coverUrl;
        this.isiBuku = isiBuku;
    }

    // Getter dan Setter untuk semua variabel
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getPenulis() {
        return penulis;
    }

    public void setPenulis(String penulis) {
        this.penulis = penulis;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getIsiBuku() {
        return isiBuku;
    }

    public void setIsiBuku(String isiBuku) {
        this.isiBuku = isiBuku;
    }
}
