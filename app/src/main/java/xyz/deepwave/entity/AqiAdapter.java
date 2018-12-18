package xyz.deepwave.entity;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.deepwave.DeepWeather.R;

public class AqiAdapter extends RecyclerView.Adapter<AqiAdapter.ViewHolder>{
    private List<String> aqi;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View aqiView;
        TextView aqi_info;
        public ViewHolder(View view){
            super(view);
            aqiView = view;
            aqi_info = view.findViewById(R.id.aqi_item_info);
        }
    }

    public AqiAdapter(List<String> list){
        aqi = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.aqi_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int postion){
        String s = aqi.get(postion);
        holder.aqi_info.setText(s);
    }

    @Override
    public int getItemCount(){
        return aqi.size();
    }
}