package com.gooddelivery.user.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gooddelivery.user.R;
import com.gooddelivery.user.adapter.OrdersAdapter;
import com.gooddelivery.user.build.api.ApiClient;
import com.gooddelivery.user.build.api.ApiInterface;
import com.gooddelivery.user.helper.ConnectionHelper;
import com.gooddelivery.user.helper.CustomDialog;
import com.gooddelivery.user.models.Order;
import com.gooddelivery.user.models.OrderModel;
import com.gooddelivery.user.utils.LocaleUtils;
import com.gooddelivery.user.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gooddelivery.user.helper.GlobalData.onGoingOrderList;
import static com.gooddelivery.user.helper.GlobalData.pastOrderList;

public class OrdersActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.orders_rv)
    RecyclerView ordersRv;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    private OrdersAdapter adapter;
    Activity activity = OrdersActivity.this;
    private List<OrderModel> modelListReference = new ArrayList<>();

    Context context;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    List<OrderModel> modelList = new ArrayList<>();
    Intent orderIntent;
    ConnectionHelper connectionHelper;
    Runnable orderStatusRunnable;
    CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = OrdersActivity.this;
        customDialog = new CustomDialog(context);
        ButterKnife.bind(this);
        connectionHelper = new ConnectionHelper(context);
        setSupportActionBar(toolbar);
        onGoingOrderList = new ArrayList<>();
        pastOrderList = new ArrayList<>();
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setPadding(0, 0, 0, 0);//for tab otherwise give space in tab
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        ordersRv.setLayoutManager(manager);
        modelListReference.clear();

        ordersRv.setHasFixedSize(false);

    }


    private void getPastOrders() {
        Call<List<Order>> call = apiInterface.getPastOders();
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    pastOrderList.clear();
                    pastOrderList = response.body();
                    OrderModel model = new OrderModel();
                    model.setHeader(getString(R.string.past_orders));
                    model.setOrders(pastOrderList);
                    modelList.add(model);

                    if (modelList.size() > 0) {
                        modelListReference.clear();
                        modelListReference.addAll(modelList);
                        adapter.notifyDataSetChanged();
                    }
                    if (onGoingOrderList.size() == 0 && pastOrderList.size() == 0) {
                        errorLayout.setVisibility(View.VISIBLE);
                    } else
                        errorLayout.setVisibility(View.GONE);
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(OrdersActivity.this, "Some thing went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getOngoingOrders() {
        Call<List<Order>> call = apiInterface.getOngoingOrders();
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    onGoingOrderList.clear();
                    modelListReference.clear();
                    onGoingOrderList = response.body();
                    modelList.clear();
                    OrderModel model = new OrderModel();
                    model.setHeader("Current Orders");
                    model.setOrders(onGoingOrderList);
                    modelList.add(model);
                    modelListReference.addAll(modelList);
                    adapter = new OrdersAdapter(OrdersActivity.this, activity, modelListReference);
                    ordersRv.setAdapter(adapter);
                    getPastOrders();
                } else {
                    getPastOrders();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Toast.makeText(OrdersActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                getPastOrders();
                customDialog.dismiss();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
       // adapter.notifyDataSetChanged();
        modelList.clear();
        //Get Ongoing Order list
        if (connectionHelper.isConnectingToInternet()) {
            customDialog.show();
            getOngoingOrders();
        } else {
            Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.onAttach(newBase));
    }
}
