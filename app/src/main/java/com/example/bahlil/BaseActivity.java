package com.example.bahlil;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    // Variabel untuk menyimpan receiver
    private NetworkChangeReceiver networkReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inisialisasi receiver
        networkReceiver = new NetworkChangeReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Mendaftarkan receiver secara dinamis saat Activity mulai terlihat
        // Ini adalah cara modern agar berfungsi di Android versi terbaru (Nougat ke atas)
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // SANGAT PENTING: Melepas receiver saat Activity tidak terlihat/berhenti
        // Jika lupa ini, aplikasi akan bocor memori (memory leak) dan lambat
        try {
            if (networkReceiver != null) {
                unregisterReceiver(networkReceiver);
            }
        } catch (IllegalArgumentException e) {
            // Menangani kasus jika receiver sudah terlepas
            e.printStackTrace();
        }
    }
}