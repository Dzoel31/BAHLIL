package com.example.bahlil;

import java.io.Serializable;

public class Buku implements Serializable {
    private String id;
    private String judul;
    private String penulis;
    private String kategori;
    private String deskripsi;
    private String isiBuku;
    private String coverUrl;

    public Buku() { }

    public Buku(String id, String judul, String penulis, String kategori, String deskripsi, String isiBuku, String coverUrl) {
        this.id = id;
        this.judul = judul;
        this.penulis = penulis;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.isiBuku = isiBuku;
        this.coverUrl = coverUrl;
    }

    // Getter dan Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getPenulis() { return penulis; }
    public void setPenulis(String penulis) { this.penulis = penulis; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    // Getter & Setter untuk Deskripsi (Baru)
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getIsiBuku() { return isiBuku; }
    public void setIsiBuku(String isiBuku) { this.isiBuku = isiBuku; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}