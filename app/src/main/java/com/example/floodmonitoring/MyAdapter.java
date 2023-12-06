package com.example.floodmonitoring;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<Model> mList;

    ArrayList<Model> originalList;
    Context context;

    public MyAdapter(Context context, ArrayList<Model> mList){
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model model = mList.get(position);
        if (model != null) {
            holder.date.setText(model.getDate());
            holder.time.setText(model.getTime());
            holder.level_one.setText(model.getLevel_1());
            holder.level_two.setText(model.getLevel_2());
            holder.level_three.setText(model.getLevel_3());
            holder.water_level.setText(model.getWater_level());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    public void setData(ArrayList<Model> newData) {
        this.mList = newData;
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView date, level_one, level_two, level_three, time, water_level;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_text);
            time = itemView.findViewById(R.id.time_text);
            level_one = itemView.findViewById(R.id.levelone_text);
            level_two = itemView.findViewById(R.id.leveltwo_text);
            level_three = itemView.findViewById(R.id.levelthree_text);
            water_level = itemView.findViewById(R.id.height_text);
        }
    }
}
