package com.example.keepwalking;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    Context context;
    List<RecyclerItem> resultsList;
    int itemLayout;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date, time, result;
        public CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.text_date);
            time = itemView.findViewById(R.id.text_time);
            result = itemView.findViewById(R.id.text_result);
            cardview = itemView.findViewById(R.id.record_view);
        }
    }

    public CalendarAdapter(Context context, List<RecyclerItem> resultsList, int itemLayout) {
        this.context = context;
        this.resultsList = resultsList;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_adapter_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RecyclerItem item = resultsList.get(position);
//        Drawable drawable = context.getResources().getDrawable(item.getImage());
//        holder.image.setBackground(drawable);
//        holder.title.setText(item.getTitle());
//        holder.cardview.setOnClickListener(v -> Toast.makeText(context, item.getTitle(), Toast.LENGTH_SHORT).show());

        holder.date.setText(resultsList.get(position).getDate());
        holder.time.setText(resultsList.get(position).getTime());
        holder.result.setText(resultsList.get(position).getResult());
        // 새 창으로 넘어가기 할 때 사용하기
//         holder.cardview.setOnClickListener(v ->);
    }

    @Override
    public int getItemCount() {
        return this.resultsList.size();
    }
}