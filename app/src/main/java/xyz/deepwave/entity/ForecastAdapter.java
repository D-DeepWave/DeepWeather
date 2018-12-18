package xyz.deepwave.entity;

import android.support.v7.widget.RecyclerView;
import xyz.deepwave.entity.MyApplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import xyz.deepwave.DeepWeather.R;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder>{
    private List<ForecastBase> mForecast;
    static class ViewHolder extends RecyclerView.ViewHolder{
        View forecastView;
        TextView forecast_info;
        TextView forecast_date;
        TextView forecast_temp;
        TextView forecast_srss;
        TextView forecast_pop;
        public ViewHolder(View view){
            super(view);
            forecastView = view;
            forecast_info = view.findViewById(R.id.forecast_info_text);
            forecast_date = view.findViewById(R.id.forecast_date_text);
            forecast_temp = view.findViewById(R.id.forecast_temp);
            forecast_srss = view.findViewById(R.id.forecast_srss_text);
            forecast_pop = view.findViewById(R.id.forecast_pop);
        }
    }

    public ForecastAdapter(List<ForecastBase> list){
        mForecast = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        //Log.d("QQQQQ","has do this");
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int postion){

        holder.forecast_date.setText(mForecast.get(postion).getDate());
        holder.forecast_info.setText(mForecast.get(postion).getCond_txt_d());
        holder.forecast_temp.setText(mForecast.get(postion).getTmp_max()+"℃/\n"+mForecast.get(postion).getTmp_min()+"℃");
        holder.forecast_srss.setText(mForecast.get(postion).getSr()+"/\n"+mForecast.get(postion).getSs());
        holder.forecast_pop.setText("降水概率 "+mForecast.get(0).getPop()+'%');
    }

    @Override
    public int getItemCount(){
        return mForecast.size();
    }
}