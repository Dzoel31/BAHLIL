package com.example.bahlil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class BukuGridAdapter extends RecyclerView.Adapter<BukuGridAdapter.ViewHolder> {

    private Context context;
    private List<Buku> listBuku;

    public BukuGridAdapter(Context context, List<Buku> listBuku) {
        this.context = context;
        this.listBuku = listBuku;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_buku_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Buku buku = listBuku.get(position);

        holder.tvJudul.setText(buku.getJudul());
        holder.tvPenulis.setText(buku.getPenulis());

        // Load Gambar menggunakan Glide
        if (buku.getCoverUrl() != null && !buku.getCoverUrl().isEmpty()) {
            Glide.with(context).load(buku.getCoverUrl()).into(holder.imgCover);
        } else {
            holder.imgCover.setImageResource(R.drawable.ic_launcher_background); // Gambar default
        }

        // Klik item untuk baca buku
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BacaBukuActivity.class);
            intent.putExtra("extra_buku", buku); // Mengirim object buku
            context.startActivity(intent);
        });

        // Long-press item to update/delete book for admins based on role
        holder.itemView.setOnLongClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists() && "admin".equals(documentSnapshot.getString("role"))) {
                                Intent intent = new Intent(context, UpdateBukuActivity.class);
                                intent.putExtra("extra_buku", buku);
                                context.startActivity(intent);
                            }
                        });
                return true; // Consume the long click event
            }
            return false; // Do not consume if user not logged in
        });
    }

    @Override
    public int getItemCount() {
        return listBuku.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvJudul, tvPenulis;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.bookCover);
            tvJudul = itemView.findViewById(R.id.itemBookTitle);
            tvPenulis = itemView.findViewById(R.id.itemBookAuthor);
        }
    }
}
