package xyz.deepwave.entity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import xyz.deepwave.DeepWeather.R;


public class WeiboAdapter extends RecyclerView.Adapter<WeiboAdapter.ViewHolder>{
    private List<String> weibo;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View weiboView;
        TextView weibo_info;
        public ViewHolder(View view){
            super(view);
            weiboView = view;
            weibo_info = view.findViewById(R.id.weibo_hot_info_item);
        }
    }

    public WeiboAdapter(List<String> list){
        weibo = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_weibo_hot_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int postion){
        String s = weibo.get(postion);
        holder.weibo_info.setText(s);
        if( mOnItemClickListener!= null){
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(postion);
                }
            });
            holder.itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(postion);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return weibo.size();
    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }
    public OnItemClickListener mOnItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener=onItemClickListener;
    }

}