package com.gooddelivery.user.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.gooddelivery.user.utils.LocaleUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.ViewSkeletonScreen;
import com.gooddelivery.user.HeaderView;
import com.gooddelivery.user.R;
import com.gooddelivery.user.adapter.HotelCatagoeryAdapter;
import com.gooddelivery.user.build.api.ApiClient;
import com.gooddelivery.user.build.api.ApiInterface;
import com.gooddelivery.user.helper.ConnectionHelper;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.models.AddCart;
import com.gooddelivery.user.models.Category;
import com.gooddelivery.user.models.Favorite;
import com.gooddelivery.user.models.Product;
import com.gooddelivery.user.models.Shop;
import com.gooddelivery.user.models.ShopDetail;
import com.gooddelivery.user.utils.Utils;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gooddelivery.user.adapter.HotelCatagoeryAdapter.bottomSheetDialogFragment;

public class HotelViewActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recommended_dishes_rv)
    RecyclerView recommendedDishesRv;
    @BindView(R.id.accompaniment_dishes_rv)
    RecyclerView accompanimentDishesRv;
    @BindView(R.id.heart_btn)
    ShineButton heartBtn;
    @BindView(R.id.view_line)
    View viewLine;
    @BindView(R.id.restaurant_title2)
    TextView restaurantTitle2;
    @BindView(R.id.restaurant_subtitle2)
    TextView restaurantSubtitle2;
    //    @BindView(R.id.scroll_view)
//    NestedScrollView scrollView;
    public static TextView itemText;
    public static TextView viewCartShopName;
    public static TextView viewCart;
    public static RelativeLayout viewCartLayout;
    @BindView(R.id.restaurant_image)
    ImageView restaurantImage;
    @BindView(R.id.header_view_title)
    TextView headerViewTitle;
    @BindView(R.id.header_view_sub_title)
    TextView headerViewSubTitle;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.offer)
    TextView offer;
    @BindView(R.id.rating)
    TextView rating;
    @BindView(R.id.delivery_time)
    TextView deliveryTime;
    @BindView(R.id.root_layout)
    CoordinatorLayout rootLayout;
    private boolean isHideToolbarView = false;
    @BindView(R.id.toolbar_header_view)
    HeaderView toolbarHeaderView;
    @BindView(R.id.float_header_view)
    HeaderView floatHeaderView;
    int restaurantPosition = 0;
    boolean isShopIsChanged = true;
    double priceAmount = 0;
    int itemCount = 0;
    int itemQuantity = 0;
    Animation slide_down, slide_up;


    Context context;
    ConnectionHelper connectionHelper;
    Activity activity;
    public static Shop shops;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    public static List<Category> categoryList;
    public static List<Product> featureProductList;
    public static HotelCatagoeryAdapter catagoeryAdapter;
    ViewSkeletonScreen skeleton;
    boolean isFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_view);
        ButterKnife.bind(this);
        context = HotelViewActivity.this;
        activity = HotelViewActivity.this;
        connectionHelper = new ConnectionHelper(context);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        appBarLayout.addOnOffsetChangedListener(this);
        categoryList = new ArrayList<>();
        shops = GlobalData.selectedShop;
        if (shops != null) {
            //Load animation
            slide_down = AnimationUtils.loadAnimation(context,
                    R.anim.slide_down);
            slide_up = AnimationUtils.loadAnimation(context,
                    R.anim.slide_up);

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                restaurantPosition = bundle.getInt("position");

            }
            isFavourite = getIntent().getBooleanExtra("is_fav", false);

            if (shops.getOfferPercent() == null) {
                offer.setVisibility(View.GONE);
            } else {
                offer.setVisibility(View.VISIBLE);
//                offer.setText("Flat " + shops.getOfferPercent().toString() + "% offer on all Orders");
                offer.setText(shops.getOfferPercent().toString() + getString(R.string.discount_on_all_orders));
            }
            if (shops.getRating() != null) {
                Double ratingValue = new BigDecimal(shops.getRating()).setScale(1, RoundingMode.HALF_UP).doubleValue();
                rating.setText("" + ratingValue);
            } else
                rating.setText("No Rating");

            deliveryTime.setText(shops.getEstimatedDeliveryTime().toString() + "Mins");

            itemText = findViewById(R.id.item_text);
            viewCartShopName = findViewById(R.id.view_cart_shop_name);
            viewCart = findViewById(R.id.view_cart);
            viewCartLayout = findViewById(R.id.view_cart_layout);

            viewCartLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HotelViewActivity.this, ViewCartActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                }
            });

            Glide.with(context)
                    .load(shops.getAvatar())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.ic_restaurant_place_holder)
                            .error(R.drawable.ic_restaurant_place_holder))
                    .into(restaurantImage);

            Picasso.get()
                    .load(shops.getAvatar())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            assert restaurantImage != null;
                            restaurantImage.setImageBitmap(bitmap);
                            Palette.from(bitmap)
                                    .generate(new Palette.PaletteAsyncListener() {
                                        @Override
                                        public void onGenerated(Palette palette) {
                                            Palette.Swatch textSwatch = palette.getDarkMutedSwatch();
                                            if (textSwatch == null) {
                                                textSwatch = palette.getMutedSwatch();
                                                if (textSwatch != null) {
                                                    collapsingToolbar.setContentScrimColor(textSwatch.getRgb());
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                        Window window = getWindow();
                                                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                                        window.setStatusBarColor(textSwatch.getRgb());
                                                    }
                                                    headerViewTitle.setTextColor(textSwatch.getTitleTextColor());
                                                    headerViewSubTitle.setTextColor(textSwatch.getBodyTextColor());
                                                }
                                            } else {
                                                collapsingToolbar.setContentScrimColor(textSwatch.getRgb());
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    Window window = getWindow();
                                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                                    window.setStatusBarColor(textSwatch.getRgb());
                                                }
                                                headerViewTitle.setTextColor(textSwatch.getTitleTextColor());
                                                headerViewSubTitle.setTextColor(textSwatch.getBodyTextColor());
                                            }

                                        }
                                    });

                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

            //Set title
            collapsingToolbar.setTitle(" ");
            toolbarHeaderView.bindTo(shops.getName(), shops.getDescription());
            floatHeaderView.bindTo(shops.getName(), shops.getDescription());

            //Set Categoery shopList adapter
            catagoeryAdapter = new HotelCatagoeryAdapter(this, activity, categoryList);
            accompanimentDishesRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            accompanimentDishesRv.setItemAnimator(new DefaultItemAnimator());
            accompanimentDishesRv.setAdapter(catagoeryAdapter);

            if (heartBtn != null)
                heartBtn.init(this);
            if (shops.getFavorite() != null || isFavourite) {
                heartBtn.setChecked(true);
                heartBtn.setTag(1);
            } else
                heartBtn.setTag(0);
            heartBtn.setShineDistanceMultiple(1.8f);
            heartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (heartBtn.getTag().equals(0)) {
                        heartBtn.setTag(1);
                        heartBtn.setShapeResource(R.raw.heart);
                    } else {
                        heartBtn.setTag(0);
                        heartBtn.setShapeResource(R.raw.icc_heart);
                    }

                }
            });
            heartBtn.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(View view, boolean checked) {
                    Log.e("HeartButton", "click " + checked);
                    if (connectionHelper.isConnectingToInternet()) {
                        if (checked) {
                            if (GlobalData.profileModel != null)
                                doFavorite(shops.getId());
                            else {
                                startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                overridePendingTransition(R.anim.slide_in_left, R.anim.anim_nothing);
                                finish();
                            }
                        } else {
                            deleteFavorite(shops.getId());
                        }
                    } else {
                        Utils.displayMessage(activity, context, getString(R.string.oops_connect_your_internet));
                    }
                }
            });
            skeleton = Skeleton.bind(rootLayout)
                    .load(R.layout.skeleton_hotel_view)
                    .show();

        } else {
            startActivity(new Intent(context, SplashActivity.class));
            finish();
        }
    }

    private void deleteFavorite(Integer id) {
        Call<Favorite> call = apiInterface.deleteFavorite(id);
        call.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(@NonNull Call<Favorite> call, @NonNull Response<Favorite> response) {
                Favorite favorite = response.body();
            }

            @Override
            public void onFailure(@NonNull Call<Favorite> call, @NonNull Throwable t) {

            }
        });

    }

    private void doFavorite(Integer id) {
        Call<Favorite> call = apiInterface.doFavorite(id);
        call.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(@NonNull Call<Favorite> call, @NonNull Response<Favorite> response) {
                Favorite favorite = response.body();
            }

            @Override
            public void onFailure(@NonNull Call<Favorite> call, @NonNull Throwable t) {

            }
        });

    }

    private void setViewcartBottomLayout(AddCart addCart) {
        priceAmount = 0;
        itemQuantity = 0;
        itemCount = 0;
        //get Item Count
        itemCount = addCart.getProductList().size();
        for (int i = 0; i < itemCount; i++) {
            //Get Total item Quantity
            itemQuantity = itemQuantity + addCart.getProductList().get(i).getQuantity();
            //Get product price
            if (addCart.getProductList().get(i).getProduct().getPrices().getPrice() != null)
                priceAmount = priceAmount + (addCart.getProductList().get(i).getQuantity() * addCart.getProductList().get(i).getProduct().getPrices().getPrice());
            if (addCart.getProductList().get(i).getCartAddons() != null && !addCart.getProductList().get(i).getCartAddons().isEmpty()) {
                for (int j = 0; j < addCart.getProductList().get(i).getCartAddons().size(); j++) {
                    priceAmount = priceAmount + (addCart.getProductList().get(i).getQuantity() * (addCart.getProductList().get(i).getCartAddons().get(j).getQuantity() *
                            addCart.getProductList().get(i).getCartAddons().get(j).getAddonProduct().getPrice()));
                }
            }
        }
        GlobalData.notificationCount = itemQuantity;
        if (itemQuantity == 0) {
            HotelViewActivity.viewCartLayout.setVisibility(View.GONE);
            // Start animation
            viewCartLayout.startAnimation(slide_down);
        } else if (itemQuantity == 1) {
            if (!shops.getId().equals(GlobalData.addCart.getProductList().get(0).getProduct().getShopId())) {
                HotelViewActivity.viewCartShopName.setVisibility(View.VISIBLE);
                HotelViewActivity.viewCartShopName.setText("From : " + GlobalData.addCart.getProductList().get(0).getProduct().getShop().getName());
            } else
                HotelViewActivity.viewCartShopName.setVisibility(View.GONE);
            String currency = addCart.getProductList().get(0).getProduct().getPrices().getCurrency();
//            HotelViewActivity.itemText.setText("" + itemQuantity + " Item | " + currency + "" + priceAmount);
            HotelViewActivity.itemText.setText("" + itemQuantity + " " + getString(R.string.product_item) + " | " + currency + "" + priceAmount);
            if (HotelViewActivity.viewCartLayout.getVisibility() == View.GONE) {
                // Start animation
                HotelViewActivity.viewCartLayout.setVisibility(View.VISIBLE);
                viewCartLayout.startAnimation(slide_up);
            }
        } else {
            if (!Objects.equals(shops.getId(), GlobalData.addCart.getProductList().get(0).getProduct().getShopId())) {
                HotelViewActivity.viewCartShopName.setVisibility(View.VISIBLE);
                HotelViewActivity.viewCartShopName.setText("From : " + GlobalData.addCart.getProductList().get(0).getProduct().getShop().getName());
            } else
                HotelViewActivity.viewCartShopName.setVisibility(View.GONE);

            String currency = addCart.getProductList().get(0).getProduct().getPrices().getCurrency();
//            HotelViewActivity.itemText.setText("" + itemQuantity + " Items | " + currency + "" + priceAmount);
            HotelViewActivity.itemText.setText("" + itemQuantity + " " + getString(R.string.product_item) + " | " + currency + "" + priceAmount);
            if (HotelViewActivity.viewCartLayout.getVisibility() == View.GONE) {
                // Start animation
                HotelViewActivity.viewCartLayout.setVisibility(View.VISIBLE);
                viewCartLayout.startAnimation(slide_up);
            }

        }
    }

    private void getCategories(HashMap<String, String> map) {
        skeleton.show();
        Call<ShopDetail> call = apiInterface.getCategories(map);
        call.enqueue(new Callback<ShopDetail>() {
            @Override
            public void onResponse(@NonNull Call<ShopDetail> call, @NonNull Response<ShopDetail> response) {
                skeleton.hide();
                if (response.isSuccessful()) {
                    categoryList = new ArrayList<>();
                    categoryList.clear();
                    Category category = new Category();
                    featureProductList = new ArrayList<>();
                    featureProductList = response.body().getFeaturedProducts();
                    category.setName(getResources().getString(R.string.featured_products));
                    category.setProducts(featureProductList);
                    categoryList.add(category);
                    categoryList.addAll(response.body().getCategories());
                    GlobalData.categoryList = categoryList;
                    GlobalData.selectedShop.setCategories(categoryList);
                    catagoeryAdapter = new HotelCatagoeryAdapter(context, activity, categoryList);
                    accompanimentDishesRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    accompanimentDishesRv.setItemAnimator(new DefaultItemAnimator());
                    accompanimentDishesRv.setAdapter(catagoeryAdapter);
                    if (GlobalData.addCart != null && GlobalData.addCart.getProductList().size() != 0) {
                        setViewcartBottomLayout(GlobalData.addCart);
                    } else {
                        viewCartLayout.setVisibility(View.GONE);
                    }
                    catagoeryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ShopDetail> call, @NonNull Throwable t) {

            }
        });
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.onAttach(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomSheetDialogFragment != null)
            bottomSheetDialogFragment.dismiss();

        if (connectionHelper.isConnectingToInternet()) {
            //get User Profile Data
            if (GlobalData.profileModel != null) {
                HashMap<String, String> map = new HashMap<>();
                map.put("shop", String.valueOf(shops.getId()));
                map.put("user_id", String.valueOf(GlobalData.profileModel.getId()));
                getCategories(map);
            } else {
                HashMap<String, String> map = new HashMap<>();
                map.put("shop", String.valueOf(shops.getId()));
                getCategories(map);
            }
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
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }
}


