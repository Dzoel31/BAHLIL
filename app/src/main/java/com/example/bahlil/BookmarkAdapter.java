package com.example.bahlil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private Context context;
    private List<UserBookInteraction> listData;

    public BookmarkAdapter(Context context, List<UserBookInteraction> listData) {
        this.context = context;
        this.listData = listData;
    }

    public void updateList(List<UserBookInteraction> newList) {
        this.listData = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bookmark, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserBookInteraction item = listData.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDate.setText("Dibaca: " + item.getLastReadDate());
        holder.tvPage.setText("Halaman " + item.getLastPage());

        if (item.getCoverUrl() != null && !item.getCoverUrl().isEmpty()) {
            Glide.with(context)
                 .load(item.getCoverUrl())
                 .error(R.drawable.ic_launcher_background) // Gambar jika terjadi error
                 .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.ic_launcher_background); // Gambar default
        }

        // Klik item -> Buka Buku
        holder.itemView.setOnClickListener(v -> {
            if (item.getBukuAsli() != null) {
                Intent intent = new Intent(context, BacaBukuActivity.class);
                intent.putExtra("extra_buku", item.getBukuAsli());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Detail buku tidak dapat dimuat.", Toast.LENGTH_SHORT).show();
            }
        });

        // Klik bintang untuk un-bookmark
        holder.ivStar.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(context, "Anda harus login untuk mengubah bookmark.", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = currentUser.getUid();
            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS).document(userId).collection(Constants.COLLECTION_BOOKMARKS).document(item.getBookId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        listData.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listData.size());
                        Toast.makeText(context, "Bookmark dihapus", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Gagal menghapus bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvPage;
        ImageView ivStar, ivCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_book_title);
            tvDate = itemView.findViewById(R.id.tv_last_read_date);
            tvPage = itemView.findViewById(R.id.tv_last_page);
            ivStar = itemView.findViewById(R.id.iv_bookmark_star);
            ivCover = itemView.findViewById(R.id.iv_book_cover);
        }
    }
}