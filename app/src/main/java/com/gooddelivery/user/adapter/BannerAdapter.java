package com.gooddelivery.user.adapter;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gooddelivery.user.R;
import com.gooddelivery.user.activities.HotelViewActivity;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.models.Banner;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.MyViewHolder> {
    private List<Banner> list;
    Context context;
    private Activity activity;

    public BannerAdapter(List<Banner> list, Context con, Activity activity) {
        this.list = list;
        this.context = con;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.impressive_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Banner banner = list.get(position);
        Glide.with(context)
                .load(banner.getUrl())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_banner)
                        .error(R.drawable.ic_banner))
                .into(holder.bannerImg);
        holder.bannerImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Banner banner = list.get(position);
                context.startActivity(new Intent(context, HotelViewActivity.class));
                GlobalData.selectedShop = banner.getShop();
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                Log.d("Hello", "onItemClick position: " + banner.getShop().getName());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImg;
        public MyViewHolder(View view) {
            super(view);
            bannerImg = view.findViewById(R.id.banner_img);
        }
    }

}
