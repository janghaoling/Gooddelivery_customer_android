package com.gooddelivery.user.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gooddelivery.user.utils.LocaleUtils;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.gooddelivery.user.R;
import com.gooddelivery.user.adapter.DeliveryLocationAdapter;
import com.gooddelivery.user.build.api.ApiClient;
import com.gooddelivery.user.build.api.ApiInterface;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.models.Address;
import com.gooddelivery.user.models.AddressList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetDeliveryLocationActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.delivery_location_rv)
    RecyclerView deliveryLocationRv;
    @BindView(R.id.current_location_ll)
    LinearLayout currentLocationLl;
    LinearLayoutManager manager;
    @BindView(R.id.animation_line_cart_add)
    ImageView animationLineCartAdd;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    @BindView(R.id.find_place_ll)
    LinearLayout findPlaceLl;
    private String TAG = "DeliveryLocationActi";
    private DeliveryLocationAdapter adapter;
    private List<AddressList> modelListReference = new ArrayList<>();
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    List<AddressList> modelList = new ArrayList<>();
    AnimatedVectorDrawableCompat avdProgress;

    public static boolean isAddressSelection = false;
    public static boolean isHomePage = false;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_delivery_location);
        ButterKnife.bind(this);
        activity = SetDeliveryLocationActivity.this;

        //Intialize Animation line
        initializeAvd();

        isAddressSelection = getIntent().getBooleanExtra("get_address", false);
        isHomePage = getIntent().getBooleanExtra("home_page", false);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        modelListReference.clear();
        AddressList addressList = new AddressList();
        if (GlobalData.profileModel != null) {
            addressList.setHeader(getResources().getString(R.string.saved_addresses));
            addressList.setAddresses(GlobalData.profileModel.getAddresses());
        }
        modelListReference.clear();
        modelListReference.add(addressList);

        if (modelListReference != null) {
            manager = new LinearLayoutManager(this);
            deliveryLocationRv.setLayoutManager(manager);
            adapter = new DeliveryLocationAdapter(this, activity, modelListReference);
            deliveryLocationRv.setAdapter(adapter);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getAddress();
    }


    private void initializeAvd() {
        avdProgress = AnimatedVectorDrawableCompat.create(getApplicationContext(), R.drawable.avd_line);
        animationLineCartAdd.setBackground(avdProgress);
        animationLineCartAdd.setVisibility(View.VISIBLE);
        avdProgress.start();
    }


    private void getAddress() {
        Call<List<Address>> call = apiInterface.getAddresses();
        call.enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(@NonNull Call<List<Address>> call, @NonNull Response<List<Address>> response) {
                if (response.isSuccessful()) {
                    modelList.clear();
                    animationLineCartAdd.setVisibility(View.GONE);
                    avdProgress.stop();
                    AddressList model = new AddressList();
                    model.setHeader(getResources().getString(R.string.saved_addresses));
                    model.setAddresses(response.body());
                    GlobalData.profileModel.setAddresses(response.body());
                    modelList.add(model);
                    modelListReference.clear();
                    modelListReference.addAll(modelList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Address>> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Intent intent = new Intent(SetDeliveryLocationActivity.this, SaveDeliveryLocationActivity.class);
                intent.putExtra("skip_visible", isHomePage);
                intent.putExtra("place_id", place.getId());
                startActivity(intent);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
               /* Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());*/

            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @OnClick({R.id.find_place_ll, R.id.current_location_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.find_place_ll:
                findPlace();
                break;
            case R.id.current_location_ll:
                startActivity(new Intent(SetDeliveryLocationActivity.this, SaveDeliveryLocationActivity.class).putExtra("skip_visible", isHomePage));
                overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                break;
        }
    }

    private void findPlace() {

        List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME);
        Intent autocompleteIntent =
                    new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields)
                            //.setInitialQuery(getQuery())
                           // .setCountry(getCountry())
                            .build(SetDeliveryLocationActivity.this);
            startActivityForResult(autocompleteIntent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.onAttach(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.anim_nothing, R.anim.slide_out_right);
    }

}
