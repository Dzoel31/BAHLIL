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

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final Context context;
    private List<UserBookInteraction> listData;

    public HistoryAdapter(Context context, List<UserBookInteraction> listData) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserBookInteraction item = listData.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDate.setText(context.getString(R.string.last_read_date_format, item.getLastReadDate()));
        holder.tvPage.setText(context.getString(R.string.page_number_format, item.getLastPage()));

        if (item.getCoverUrl() != null && !item.getCoverUrl().isEmpty()) {
            Glide.with(context)
                 .load(item.getCoverUrl())
                 .error(R.drawable.ic_launcher_background) // Gambar jika terjadi error
                 .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.ic_launcher_background); // Gambar default
        }

        holder.itemView.setOnClickListener(v -> {
            if (item.getBukuAsli() != null) {
                Intent intent = new Intent(context, BacaBukuActivity.class);
                intent.putExtra("extra_buku", item.getBukuAsli());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Detail buku tidak dapat dimuat.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvPage;
        ImageView ivCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_book_title);
            tvDate = itemView.findViewById(R.id.tv_last_read_date);
            tvPage = itemView.findViewById(R.id.tv_last_page);
            ivCover = itemView.findViewById(R.id.iv_book_cover);
        }
    }
}