package com.gooddelivery.user.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gooddelivery.user.R;
import com.gooddelivery.user.adapter.PromotionsAdapter;
import com.gooddelivery.user.build.api.ApiClient;
import com.gooddelivery.user.build.api.ApiInterface;
import com.gooddelivery.user.helper.CustomDialog;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.models.AddCart;
import com.gooddelivery.user.models.Promotions;
import com.gooddelivery.user.utils.LocaleUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromotionActivity extends AppCompatActivity implements PromotionsAdapter.PromotionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.promotions_rv)
    RecyclerView promotionsRv;

    ArrayList<Promotions> promotionsModelArrayList;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    Context context = PromotionActivity.this;
    CustomDialog customDialog;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        ButterKnife.bind(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        promotionsModelArrayList = new ArrayList<>();
        customDialog = new CustomDialog(context);

        //Offer Restaurant Adapter
        promotionsRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        promotionsRv.setItemAnimator(new DefaultItemAnimator());
        promotionsRv.setHasFixedSize(true);
        PromotionsAdapter orderItemListAdapter = new PromotionsAdapter(promotionsModelArrayList, this);
        promotionsRv.setAdapter(orderItemListAdapter);

        getPromoDetails();
    }

    private void getPromoDetails() {
        customDialog.show();
        Call<List<Promotions>> call = apiInterface.getWalletPromoCode();
        call.enqueue(new Callback<List<Promotions>>() {
            @Override
            public void onResponse(@NonNull Call<List<Promotions>> call, @NonNull Response<List<Promotions>> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    promotionsModelArrayList.clear();
                    Log.e("onResponse: ", response.toString());
                    promotionsModelArrayList.addAll(response.body());
                    if (promotionsModelArrayList.size() == 0) {
                        errorLayout.setVisibility(View.VISIBLE);
                    } else {
                        promotionsRv.getAdapter().notifyDataSetChanged();
                    }
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().toString());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Promotions>> call, @NonNull Throwable t) {
                customDialog.dismiss();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.onAttach(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String tag = null;
        try {
            tag = getIntent().getExtras().getString("tag");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tag != null && tag.equalsIgnoreCase(AddMoneyActivity.TAG)) {
            startActivity(new Intent(this, AddMoneyActivity.class));
        }
        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
        finish();
    }


    @Override
    public void onApplyBtnClick(final Promotions promotions) {
        customDialog.show();
        Call<AddCart> call = apiInterface.applyWalletPromoCode(String.valueOf(promotions.getId()));
        call.enqueue(new Callback<AddCart>() {
            @Override
            public void onResponse(@NonNull Call<AddCart> call, @NonNull Response<AddCart> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(PromotionActivity.this, getResources().getString(R.string.promo_code_apply_successfully), Toast.LENGTH_SHORT).show();
                    GlobalData.addCart = null;
                    GlobalData.addCart = response.body();
                    gotoFlow(String.valueOf(promotions.getId()));
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddCart> call, @NonNull Throwable t) {
                customDialog.dismiss();
            }
        });
    }

    private void gotoFlow(String promotionid) {
        Intent intent = new Intent();
        intent.putExtra("promotion", promotionid);
        setResult(201, intent);
        finish();
    }

}