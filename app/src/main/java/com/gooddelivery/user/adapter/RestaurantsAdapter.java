package com.gooddelivery.user.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gooddelivery.user.R;
import com.gooddelivery.user.activities.HotelViewActivity;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.models.Shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.MyViewHolder> {
    private List<Shop> list;
    private Context context;
    private Activity activity;

    public RestaurantsAdapter(List<Shop> list, Context con, Activity act) {
        this.list = list;
        this.context = con;
        this.activity = act;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public void add(Shop item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Shop item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Shop shops = list.get(position);
        Glide.with(context)
                .load(shops.getAvatar())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_restaurant_place_holder)
                        .error(R.drawable.ic_restaurant_place_holder))
                .into(holder.dishImg);
        holder.restaurantName.setText(shops.getName());
        holder.category.setText(shops.getDescription());
        if (shops.getOfferPercent() == null) {
            holder.offer.setVisibility(View.GONE);
        } else if (shops.getOfferPercent() == 0) {
            holder.offer.setVisibility(View.GONE);

        } else {
            holder.offer.setVisibility(View.VISIBLE);
//            holder.offer.setText("Flat " + shops.getOfferPercent().toString() + "% offer on all Orders");
            holder.offer.setText(shops.getOfferPercent().toString() + context.getString(R.string.discount_on_all_orders));
        }
        if (shops.getShopstatus() != null)
            holder.closedLay.setVisibility(shops.getShopstatus().equalsIgnoreCase("CLOSED") ? View.VISIBLE : View.GONE);

        if (shops.getRating() != null) {
            Double rating = new BigDecimal(shops.getRating()).setScale(1, RoundingMode.HALF_UP).doubleValue();
            holder.rating.setText("" + rating);
        } else
            holder.rating.setText("No Rating");

        if (shops.getEstimatedDeliveryTime() != null) {
            holder.distanceTime.setText(shops.getEstimatedDeliveryTime().toString() + " Mins");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout itemView;
        RelativeLayout closedLay;
        private ImageView dishImg;
        private TextView restaurantName, category, offer, rating, restaurantInfo, price, distanceTime;


        private MyViewHolder(View view) {
            super(view);
            itemView = view.findViewById(R.id.item_view);
            closedLay = view.findViewById(R.id.closed_lay);
            dishImg = view.findViewById(R.id.dish_img);
            restaurantName = view.findViewById(R.id.restaurant_name);
            category = view.findViewById(R.id.category);
            offer = view.findViewById(R.id.offer);
            rating = view.findViewById(R.id.rating);
            restaurantInfo = view.findViewById(R.id.restaurant_info);
            distanceTime = view.findViewById(R.id.distance_time);
            price = view.findViewById(R.id.price);
            itemView.setOnClickListener(this);
        }

        public void onClick(View v) {
            if (v.getId() == itemView.getId()) {
                GlobalData.selectedShop = list.get(getAdapterPosition());
                if (!GlobalData.selectedShop.getShopstatus().equalsIgnoreCase("CLOSED")) {
                    context.startActivity(new Intent(context, HotelViewActivity.class)
                            .putExtra("position", getAdapterPosition()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                    list.get(getAdapterPosition()).getCuisines();
                } else Toast.makeText(context, "The Shop is closed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
