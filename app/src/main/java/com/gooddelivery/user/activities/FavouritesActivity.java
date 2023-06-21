package com.gooddelivery.user.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.gooddelivery.user.R;
import com.gooddelivery.user.adapter.FavouritesAdapter;
import com.gooddelivery.user.build.api.ApiClient;
import com.gooddelivery.user.build.api.ApiInterface;
import com.gooddelivery.user.models.Available;
import com.gooddelivery.user.models.FavListModel;
import com.gooddelivery.user.models.FavoriteList;
import com.gooddelivery.user.models.UnAvailable;
import com.gooddelivery.user.utils.LocaleUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouritesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.favorites_Rv)
    RecyclerView favoritesRv;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    @BindView(R.id.root_view)
    RelativeLayout rootView;
    private FavouritesAdapter adapter;
    private List<FavListModel> modelListReference = new ArrayList<>();
    List<FavListModel> modelList = new ArrayList<>();

    private SkeletonScreen skeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        favoritesRv.setLayoutManager(manager);
        adapter = new FavouritesAdapter(this, modelListReference);
        favoritesRv.setAdapter(adapter);
        modelList.clear();
        modelListReference.clear();
        getFavorites();

    }

    private void getFavorites() {

        skeletonScreen = Skeleton.bind(rootView)
                .load(R.layout.skeleton_favorite_list_item)
                .color(R.color.shimmer_color)
                .angle(0)
                .show();

        Call<FavoriteList> call = apiInterface.getFavoriteList();
        call.enqueue(new Callback<FavoriteList>() {
            @Override
            public void onResponse(@NonNull Call<FavoriteList> call, @NonNull Response<FavoriteList> response) {
                skeletonScreen.hide();
                if (response.isSuccessful()) {
                    if (response.body().getAvailable().size() == 0 && response.body().getUnAvailable().size() == 0) {
                        errorLayout.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        errorLayout.setVisibility(View.GONE);
                    }

                    FavListModel model = new FavListModel();
                    model.setHeader(getString(R.string.available));
                    model.setFav(response.body().getAvailable());
                    modelList.clear();
                    modelList.add(model);

                    model = new FavListModel();
                    model.setHeader(getString(R.string.unavailable));

                    List<Available> list = new ArrayList<>();
                    for (UnAvailable obj : response.body().getUnAvailable()) {
                        Gson gson = new Gson();
                        String json = gson.toJson(obj);
                        Available cust = gson.fromJson(json, Available.class);
                        list.add(cust);
                    }
                    model.setFav(list);
                    modelList.add(model);

                    modelListReference.clear();
                    modelListReference.addAll(modelList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FavoriteList> call, @NonNull Throwable t) {
                skeletonScreen.hide();
                Toast.makeText(FavouritesActivity.this, "Something wrong - getFavorites", Toast.LENGTH_LONG).show();
            }
        });

        /*Call<FavoriteList> call = apiInterface.getFavoriteList();
        call.enqueue(new Callback<FavoriteList>() {
            @Override
            public void onResponse(@NonNull Call<FavoriteList> call, @NonNull Response<FavoriteList> response) {
                if (response.errorBody() != null) {

                } else if(response.isSuccessful()){
                    FavListModel model = new FavListModel();
                    model.setHeader("available");
                    model.setFav(response.body().getAvailable());
                    modelList.add(model);

                    *//*model = new FavListModel();
                    model.setHeader("un available");
                    List<Available> shopList = new ArrayList<>();
                    for (UnAvailable obj : response.body().getUnAvailable()) {
                        shopList.add(obj);
                    }
                    model.setFav(shopList);
                    modelList.add(model);*//*

                    modelListReference.clear();
                    modelListReference.addAll(modelList);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(@NonNull Call<FavoriteList> call, @NonNull Throwable t) {

            }
        });*/


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.onAttach(newBase));
    }
}
