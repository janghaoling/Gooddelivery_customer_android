package com.gooddelivery.user.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gooddelivery.user.R;
import com.gooddelivery.user.models.Cuisine;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.MyViewHolder> {
    private static ClickListener clickListener;
    private List<Cuisine> list;
    private Context context;

    public DiscoverAdapter(List<Cuisine> list, Context con) {
        this.list = list;
        this.context = con;
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        DiscoverAdapter.clickListener = clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discover_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Cuisine obj = list.get(position);
        holder.title.setText(obj.getName());
        holder.optionCount.setText(String.valueOf(obj.getId()));
        Glide.with(context)
                .load(obj.getImage())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_banner)
                        .error(R.drawable.ic_banner))
                .into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView icon;
        public TextView title, optionCount;


        public MyViewHolder(View view) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.icon);
            title = (TextView) view.findViewById(R.id.title);
            optionCount = (TextView) view.findViewById(R.id.option_count);

        }

        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }


    }


}
