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

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.gooddelivery.user.R;
import com.gooddelivery.user.adapter.ManageAddressAdapter;
import com.gooddelivery.user.build.api.ApiClient;
import com.gooddelivery.user.build.api.ApiInterface;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.models.Address;
import com.gooddelivery.user.utils.LocaleUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageAddressActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.manage_address_rv)
    RecyclerView manageAddressRv;
    @BindView(R.id.add_new_address)
    Button addNewAddress;

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    List<Address> locations;
    ManageAddressAdapter adapter;
    boolean isSuccessDelete = false;
    @BindView(R.id.error_layout_description)
    TextView errorLayoutDescription;
    public static RelativeLayout errorLayout;
    @BindView(R.id.root_view)
    ScrollView rootView;

    private SkeletonScreen skeletonScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_address);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
        locations = new ArrayList<>();
        adapter = new ManageAddressAdapter(locations, ManageAddressActivity.this);
        manageAddressRv.setLayoutManager(new LinearLayoutManager(this));
        manageAddressRv.setItemAnimator(new DefaultItemAnimator());
        manageAddressRv.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        getAddress();
    }

    private void getAddress() {
        skeletonScreen = Skeleton.bind(rootView)
                .load(R.layout.skeloton_address_list_item)
                .color(R.color.shimmer_color)
                .angle(0)
                .show();

        Call<List<Address>> getres = apiInterface.getAddresses();
        getres.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(@NonNull Call<List<Address>> call, @NonNull Response<List<Address>> response) {
                skeletonScreen.hide();
                if (response.isSuccessful()) {


                    locations.clear();
                    locations.addAll(response.body());
                    GlobalData.profileModel.setAddresses(response.body());
                    if (locations.size() == 0) {
                        errorLayout.setVisibility(View.VISIBLE);
                    } else {
                        errorLayout.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Address>> call, @NonNull Throwable t) {
                skeletonScreen.hide();
                Toast.makeText(ManageAddressActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.onAttach(newBase));
    }

    @OnClick(R.id.add_new_address)
    public void onViewClicked() {
        startActivity(new Intent(ManageAddressActivity.this, SaveDeliveryLocationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

}
