package xyz.deepwave.entity;


import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xyz.deepwave.DeepWeather.R;

public class LifeAdapter extends RecyclerView.Adapter<LifeAdapter.ViewHolder> {
    private List<String> life;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View lifeView;
        ImageView life_image;
        TextView life_info;

        public ViewHolder(View view) {
            super(view);
            lifeView = view;
            life_info = view.findViewById(R.id.suggestion_info);
            life_image = view.findViewById(R.id.suggestion_imageView);
        }
    }

    public LifeAdapter(List<String> list) {
        life = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int postion) {
        switch (postion) {
            case 0:
                holder.life_image.setImageResource(R.drawable.bg_comfort);
                holder.life_info.setText("舒适指数: "+life.get(postion));
                break;
            case 1:
                holder.life_image.setImageResource(R.drawable.bg_dress);
                holder.life_info.setText("穿衣指数: "+life.get(postion));
                break;
            case 2:
                holder.life_image.setImageResource(R.drawable.bg_flu);
                holder.life_info.setText("感冒指数: "+life.get(postion));
                break;
            case 3:
                holder.life_image.setImageResource(R.drawable.bg_sport);
                holder.life_info.setText("运动指数: "+life.get(postion));
                break;
            case 4:
                holder.life_image.setImageResource(R.drawable.bg_travel);
                holder.life_info.setText("旅游指数: "+life.get(postion));
                break;
            case 5:
                holder.life_image.setImageResource(R.drawable.bg_cv);
                holder.life_info.setText("紫外线指数："+life.get(postion));
                break;
            case 6:
                holder.life_image.setImageResource(R.drawable.bg_wash);
                holder.life_info.setText("洗车指数："+life.get(postion));
                break;
            case 7:
                holder.life_image.setImageResource(R.drawable.bg_air);
                holder.life_info.setText("空气指数："+life.get(postion));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return life.size();
    }
}

