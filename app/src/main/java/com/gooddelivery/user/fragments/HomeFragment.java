package com.gooddelivery.user.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.gooddelivery.user.HomeActivity;
import com.gooddelivery.user.R;
import com.gooddelivery.user.activities.FilterActivity;
import com.gooddelivery.user.activities.SetDeliveryLocationActivity;
import com.gooddelivery.user.adapter.BannerAdapter;
import com.gooddelivery.user.adapter.OfferRestaurantAdapter;
import com.gooddelivery.user.adapter.RestaurantsAdapter;
import com.gooddelivery.user.build.api.ApiClient;
import com.gooddelivery.user.build.api.ApiInterface;
import com.gooddelivery.user.helper.ConnectionHelper;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.models.Address;
import com.gooddelivery.user.models.Banner;
import com.gooddelivery.user.models.Cuisine;
import com.gooddelivery.user.models.Restaurant;
import com.gooddelivery.user.models.RestaurantsData;
import com.gooddelivery.user.models.Shop;
import com.gooddelivery.user.utils.Utils;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gooddelivery.user.helper.GlobalData.addressList;
import static com.gooddelivery.user.helper.GlobalData.cuisineIdArrayList;
import static com.gooddelivery.user.helper.GlobalData.cuisineList;
import static com.gooddelivery.user.helper.GlobalData.isOfferApplied;
import static com.gooddelivery.user.helper.GlobalData.isPureVegApplied;
import static com.gooddelivery.user.helper.GlobalData.latitude;
import static com.gooddelivery.user.helper.GlobalData.longitude;
import static com.gooddelivery.user.helper.GlobalData.selectedAddress;


public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.animation_line_image)
    ImageView animationLineImage;
    Context context;
    @BindView(R.id.catagoery_spinner)
    Spinner catagoerySpinner;
    @BindView(R.id.title)
    LinearLayout title;
    @BindView(R.id.restaurant_count_txt)
    TextView restaurantCountTxt;
    @BindView(R.id.offer_title_header)
    TextView offerTitleHeader;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.impressive_dishes_layout)
    LinearLayout impressiveDishesLayout;
    private SkeletonScreen skeletonScreen, skeletonScreen2, skeletonText1, skeletonText2, skeletonSpinner;
    private TextView addressLabel;
    private TextView addressTxt;
    private LinearLayout locationAddressLayout;
    private RelativeLayout errorLoadingLayout;

    private Button filterBtn;

    @BindView(R.id.restaurants_offer_rv)
    RecyclerView restaurantsOfferRv;
    @BindView(R.id.impressive_dishes_rv)
    RecyclerView bannerRv;
    @BindView(R.id.restaurants_rv)
    RecyclerView restaurantsRv;
    @BindView(R.id.discover_rv)
    RecyclerView discoverRv;
    int ADDRESS_SELECTION = 1;
    int FILTER_APPLIED_CHECK = 2;
    ImageView filterSelectionImage;

    private ViewGroup toolbar;
    private View toolbarLayout;
    AnimatedVectorDrawableCompat avdProgress;
    public static ArrayList<Integer> cuisineSelectedList = null;

    String[] catagoery = {"Relevance", "Cost for Two", "Delivery Time", "Rating"};

    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    List<Shop> restaurantList;
    RestaurantsAdapter adapterRestaurant;
    public static boolean isFilterApplied = false;
    BannerAdapter bannerAdapter;
    List<Banner> bannerList;
    ConnectionHelper connectionHelper;
    Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        activity = getActivity();

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");

            errorLoadingLayout.setVisibility(View.GONE);
            locationAddressLayout.setVisibility(View.VISIBLE);
            if (selectedAddress != null && GlobalData.profileModel != null) {
                GlobalData.addressHeader = selectedAddress.getType();
                addressLabel.setText(selectedAddress.getType());
                addressTxt.setText(selectedAddress.getMapAddress());
                latitude = selectedAddress.getLatitude();
                longitude = selectedAddress.getLongitude();
                GlobalData.addressHeader = selectedAddress.getMapAddress();
            } else if (addressList != null && addressList.getAddresses().size() != 0 && GlobalData.profileModel != null) {

                for (int i = 0; i < addressList.getAddresses().size(); i++) {
                    Address address1 = addressList.getAddresses().get(i);
                    if (getDoubleThreeDigits(latitude) == getDoubleThreeDigits(address1.getLatitude()) && getDoubleThreeDigits(longitude) == getDoubleThreeDigits(address1.getLongitude())) {
                        selectedAddress = address1;
                        addressLabel.setText(GlobalData.addressHeader);
                        addressTxt.setText(GlobalData.address);
                        addressLabel.setText(selectedAddress.getType());
                        addressTxt.setText(selectedAddress.getMapAddress());
                        latitude = selectedAddress.getLatitude();
                        longitude = selectedAddress.getLongitude();
                        break;
                    } else {
//                        addressLabel.setText(GlobalData.addressHeader);
//                        addressTxt.setText(GlobalData.address);
                        selectedAddress = address1;
                        addressLabel.setText(selectedAddress.getType());
                        addressTxt.setText(selectedAddress.getMapAddress());
                    }
                }
            } else {
                addressLabel.setText(GlobalData.addressHeader);
                addressTxt.setText(GlobalData.address);
            }
            findRestaurant();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        System.out.println("HomeFragment");
        connectionHelper = new ConnectionHelper(context);
        toolbar = (ViewGroup) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbarLayout = LayoutInflater.from(context).inflate(R.layout.toolbar_home, toolbar, false);
        addressLabel = (TextView) toolbarLayout.findViewById(R.id.address_label);
        addressTxt = (TextView) toolbarLayout.findViewById(R.id.address);
        locationAddressLayout = (LinearLayout) toolbarLayout.findViewById(R.id.location_ll);
        errorLoadingLayout = (RelativeLayout) toolbarLayout.findViewById(R.id.error_loading_layout);
        locationAddressLayout.setVisibility(View.INVISIBLE);
        errorLoadingLayout.setVisibility(View.VISIBLE);
        bannerList = new ArrayList<>();
        bannerAdapter = new BannerAdapter(bannerList, context, getActivity());
        bannerRv.setHasFixedSize(true);
        bannerRv.setItemViewCacheSize(20);
        bannerRv.setDrawingCacheEnabled(true);
        bannerRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        bannerRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        bannerRv.setItemAnimator(new DefaultItemAnimator());
        skeletonScreen2 = Skeleton.bind(bannerRv)
                .adapter(bannerAdapter)
                .load(R.layout.skeleton_impressive_list_item)
                .count(3)
                .show();
        skeletonText1 = Skeleton.bind(offerTitleHeader)
                .load(R.layout.skeleton_label)
                .show();
        skeletonText2 = Skeleton.bind(restaurantCountTxt)
                .load(R.layout.skeleton_label)
                .show();
        skeletonSpinner = Skeleton.bind(catagoerySpinner)
                .load(R.layout.skeleton_label)
                .show();
        HomeActivity.updateNotificationCount(context, GlobalData.notificationCount);

        //Spinner
        //Creating the ArrayAdapter instance having the country shopList
        ArrayAdapter aa = new ArrayAdapter(context, R.layout.spinner_layout, catagoery);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        catagoerySpinner.setAdapter(aa);
        catagoerySpinner.setOnItemSelectedListener(this);

        //Restaurant Adapter
        restaurantsRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        restaurantsRv.setItemAnimator(new DefaultItemAnimator());
        restaurantsRv.setHasFixedSize(true);
        restaurantList = new ArrayList<>();
        adapterRestaurant = new RestaurantsAdapter(restaurantList, context, getActivity());
        skeletonScreen = Skeleton.bind(restaurantsRv)
                .adapter(adapterRestaurant)
                .load(R.layout.skeleton_restaurant_list_item)
                .count(2)
                .show();

        final ArrayList<Restaurant> offerRestaurantList = new ArrayList<>();
        offerRestaurantList.add(new Restaurant("Madras Coffee House", "Cafe, South Indian", "", "3.8", "51 Mins", "$20", ""));
        offerRestaurantList.add(new Restaurant("Behrouz Biryani", "Biriyani", "", "3.7", "52 Mins", "$50", ""));
        offerRestaurantList.add(new Restaurant("SubWay", "American fast food", "Flat 20% offer on all orders", "4.3", "30 Mins", "$5", "Close soon"));
        offerRestaurantList.add(new Restaurant("Dominoz Pizza", "Pizza shop", "", "4.5", "25 Mins", "$5", ""));
        offerRestaurantList.add(new Restaurant("Pizza hut", "Cafe, Bakery", "", "4.1", "45 Mins", "$5", "Close soon"));
        offerRestaurantList.add(new Restaurant("McDonlad's", "Pizza Food, burger", "Flat 20% offer on all orders", "4.6", "20 Mins", "$5", ""));
        offerRestaurantList.add(new Restaurant("Chai Kings", "Cafe, Bakery", "", "3.3", "36 Mins", "$5", ""));
        offerRestaurantList.add(new Restaurant("sea sell", "Fish, Chicken, mutton", "Flat 30% offer on all orders", "4.3", "20 Mins", "$5", "Close soon"));
        //Offer Restaurant Adapter
        restaurantsOfferRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        restaurantsOfferRv.setItemAnimator(new DefaultItemAnimator());
        restaurantsOfferRv.setHasFixedSize(true);
        OfferRestaurantAdapter offerAdapter = new OfferRestaurantAdapter(offerRestaurantList, context);
        restaurantsOfferRv.setAdapter(offerAdapter);


        // Discover
//        final List<Discover> discoverList = new ArrayList<>();
//        discoverList.add(new Discover("Trending now ", "22 options", "1"));
//        discoverList.add(new Discover("Offers near you", "51 options", "1"));
//        discoverList.add(new Discover("Whats special", "7 options", "1"));
//        discoverList.add(new Discover("Pocket Friendly", "44 options", "1"));

//                    DiscoverAdapter adapterDiscover = new DiscoverAdapter(cuisineList, context);
//                    discoverRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
//                    discoverRv.setItemAnimator(new DefaultItemAnimator());
//                    discoverRv.setAdapter(adapterDiscover);
//                    adapterDiscover.setOnItemClickListener(new DiscoverAdapter.ClickListener() {
//                        @Override
//                        public void onItemClick(int position, View v) {
//                            Cuisine obj = cuisineList.get(position);
//                        }
//                    });

        locationAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.profileModel != null) {
                    startActivityForResult(new Intent(getActivity(), SetDeliveryLocationActivity.class).putExtra("get_address", true).putExtra("home_page", true), ADDRESS_SELECTION);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                } else {
                    Toast.makeText(context, "Please login", Toast.LENGTH_SHORT).show();
                }
            }
        });
        filterBtn = (Button) toolbarLayout.findViewById(R.id.filter);
        filterSelectionImage = (ImageView) toolbarLayout.findViewById(R.id.filter_selection_image);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, FilterActivity.class), FILTER_APPLIED_CHECK);
                getActivity().overridePendingTransition(R.anim.slide_up, R.anim.anim_nothing);
            }
        });
        toolbar.addView(toolbarLayout);
        //intialize image line
        initializeAvd();

        //Get cuisine values
        if (connectionHelper.isConnectingToInternet()) {
            getCuisines();
        } else {
            Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
        }
    }

    private void findRestaurant() {
        HashMap<String, String> map = new HashMap<>();
        map.put("latitude", String.valueOf(latitude));
        map.put("longitude", String.valueOf(longitude));
        //get User Profile Data
        if (GlobalData.profileModel != null) {
            map.put("user_id", String.valueOf(GlobalData.profileModel.getId()));
        }
        if (isFilterApplied) {
            filterSelectionImage.setVisibility(View.VISIBLE);
            if (isOfferApplied)
                map.put("offer", "1");
            if (isPureVegApplied)
                map.put("pure_veg", "1");
            if (cuisineIdArrayList != null && cuisineIdArrayList.size() != 0) {
                for (int i = 0; i < cuisineIdArrayList.size(); i++) {
                    map.put("cuisine[" + "" + i + "]", cuisineIdArrayList.get(i).toString());
                }
            }

        } else {
            filterSelectionImage.setVisibility(View.GONE);
        }

        if (connectionHelper.isConnectingToInternet()) {
            getRestaurant(map);
        } else {
            Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
        }
    }

    private void getRestaurant(HashMap<String, String> map) {
        Call<RestaurantsData> call = apiInterface.getshops(map);
        call.enqueue(new Callback<RestaurantsData>() {
            @Override
            public void onResponse(Call<RestaurantsData> call, Response<RestaurantsData> response) {
                skeletonScreen.hide();
                skeletonScreen2.hide();
                skeletonText1.hide();
                skeletonText2.hide();
                skeletonSpinner.hide();
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    //Check Restaurant list
                    if (response.body().getShops().size() == 0) {
                        title.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    } else {
                        title.setVisibility(View.VISIBLE);
                        errorLayout.setVisibility(View.GONE);
                    }

                    //Check Banner list
                    if (response.body().getBanners().size() == 0 || isFilterApplied)
                        impressiveDishesLayout.setVisibility(View.GONE);
                    else
                        impressiveDishesLayout.setVisibility(View.VISIBLE);
                    GlobalData.shopList = response.body().getShops();
                    restaurantList.clear();
                    restaurantList.addAll(GlobalData.shopList);
                    bannerList.clear();
                    bannerList.addAll(response.body().getBanners());
                    restaurantCountTxt.setText("" + restaurantList.size() + " Restaurants");
                    adapterRestaurant.notifyDataSetChanged();
                    bannerAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<RestaurantsData> call, Throwable t) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void getCuisines() {
        Call<List<Cuisine>> call = apiInterface.getcuCuisineCall();
        call.enqueue(new Callback<List<Cuisine>>() {
            @Override
            public void onResponse(Call<List<Cuisine>> call, Response<List<Cuisine>> response) {
                if (response != null && !response.isSuccessful() && response.errorBody() != null) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.isSuccessful()) {
                    cuisineList = new ArrayList<>();
                    cuisineList.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Cuisine>> call, Throwable t) {

            }
        });


    }

    public double getDoubleThreeDigits(Double value) {
        return new BigDecimal(value.toString()).setScale(3, RoundingMode.HALF_UP).doubleValue();

    }

    Runnable action = new Runnable() {
        @Override
        public void run() {
            avdProgress.stop();
            if (animationLineImage != null)
                animationLineImage.setVisibility(View.INVISIBLE);
        }
    };

    private void initializeAvd() {
        avdProgress = AnimatedVectorDrawableCompat.create(context, R.drawable.avd_line);
        animationLineImage.setBackground(avdProgress);
        repeatAnimation();
    }

    private void repeatAnimation() {
        avdProgress.start();
        animationLineImage.
                postDelayed(action, 3000); // Will repeat animation in every 1 second
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        errorLayout.setVisibility(View.GONE);
        HomeActivity.updateNotificationCount(context, GlobalData.notificationCount);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("location"));
        if (!GlobalData.addressHeader.equalsIgnoreCase("")) {
            errorLoadingLayout.setVisibility(View.GONE);
            locationAddressLayout.setVisibility(View.VISIBLE);
            if (selectedAddress != null && GlobalData.profileModel != null) {
                GlobalData.addressHeader = selectedAddress.getType();
                addressLabel.setText(selectedAddress.getType());
                addressTxt.setText(selectedAddress.getMapAddress());
                latitude = selectedAddress.getLatitude();
                longitude = selectedAddress.getLongitude();
                GlobalData.addressHeader = selectedAddress.getMapAddress();
            } else if (addressList != null && addressList.getAddresses().size() != 0 && GlobalData.profileModel != null) {
                for (int i = 0; i < addressList.getAddresses().size(); i++) {
                    Address address1 = addressList.getAddresses().get(i);
                    if (getDoubleThreeDigits(latitude) == getDoubleThreeDigits(address1.getLatitude()) && getDoubleThreeDigits(longitude) == getDoubleThreeDigits(address1.getLongitude())) {
                        selectedAddress = address1;
                        addressLabel.setText(GlobalData.addressHeader);
                        addressTxt.setText(GlobalData.address);
                        addressLabel.setText(selectedAddress.getType());
                        addressTxt.setText(selectedAddress.getMapAddress());
                        latitude = selectedAddress.getLatitude();
                        longitude = selectedAddress.getLongitude();
                        break;
                    } else {
                        addressLabel.setText(GlobalData.addressHeader);
                        addressTxt.setText(GlobalData.address);
                    }
                }
            } else {
                addressLabel.setText(GlobalData.addressHeader);
                addressTxt.setText(GlobalData.address);
            }
            findRestaurant();

        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = getActivity();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (toolbar != null) {
            toolbar.removeView(toolbarLayout);
        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_OK) {
            System.out.print("HomeFragment : Success");
            if (selectedAddress != null) {
                addressLabel.setText(GlobalData.addressHeader);
                addressTxt.setText(GlobalData.address);
                addressLabel.setText(selectedAddress.getType());
                addressTxt.setText(selectedAddress.getMapAddress());
                latitude = selectedAddress.getLatitude();
                longitude = selectedAddress.getLongitude();
                skeletonScreen.show();
                skeletonScreen2.show();
                skeletonText1.show();
                skeletonText2.show();
                skeletonSpinner.show();
                findRestaurant();

            }
        } else if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_CANCELED) {
            System.out.print("HomeFragment : Failure");

        }

        if (requestCode == FILTER_APPLIED_CHECK && resultCode == Activity.RESULT_OK) {
            System.out.print("HomeFragment : Filter Success");
            skeletonScreen.show();
            skeletonScreen2.show();
            skeletonText1.show();
            skeletonText2.show();
            skeletonSpinner.show();
            findRestaurant();

        } else if (requestCode == ADDRESS_SELECTION && resultCode == Activity.RESULT_CANCELED) {
            System.out.print("HomeFragment : Filter Failure");

        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(context, catagoery[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


}