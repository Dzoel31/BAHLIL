package com.example.bahlil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
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
        holder.tvDate.setText("Dibaca: " + item.getLastReadDate());
        holder.tvPage.setText("Halaman " + item.getLastPage());

        holder.itemView.setOnClickListener(v -> {
            if (item.getBukuAsli() != null) {
                Intent intent = new Intent(context, BacaBukuActivity.class);
                intent.putExtra("extra_buku", item.getBukuAsli());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvPage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_book_title);
            tvDate = itemView.findViewById(R.id.tv_last_read_date);
            tvPage = itemView.findViewById(R.id.tv_last_page);
        }
    }
}