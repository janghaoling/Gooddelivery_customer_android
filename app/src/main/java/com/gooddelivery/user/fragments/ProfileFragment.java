package com.gooddelivery.user.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.gooddelivery.user.BuildConfig;
import com.gooddelivery.user.HomeActivity;
import com.gooddelivery.user.R;
import com.gooddelivery.user.activities.AccountPaymentActivity;
import com.gooddelivery.user.activities.ChangePasswordActivity;
import com.gooddelivery.user.activities.EditAccountActivity;
import com.gooddelivery.user.activities.FavouritesActivity;
import com.gooddelivery.user.activities.LoginActivity;
import com.gooddelivery.user.activities.ManageAddressActivity;
import com.gooddelivery.user.activities.OrdersActivity;
import com.gooddelivery.user.activities.PromotionActivity;
import com.gooddelivery.user.activities.WelcomeScreenActivity;
import com.gooddelivery.user.adapter.ProfileSettingsAdapter;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.helper.SharedHelper;
import com.gooddelivery.user.utils.ListViewSizeHelper;
import com.facebook.login.LoginManager;
import com.gooddelivery.user.utils.LocaleUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by santhosh@appoets.com on 22-08-2017.
 */

public class ProfileFragment extends Fragment {
    @BindView(R.id.text_line)
    TextView textLine;
    @BindView(R.id.app_version)
    TextView appVersion;
    @BindView(R.id.view_line)
    View viewLine;
    @BindView(R.id.logout)
    Button logout;
    @BindView(R.id.arrow_image)
    ImageView arrowImage;
    @BindView(R.id.list_layout)
    RelativeLayout listLayout;
    @BindView(R.id.myaccount_layout)
    LinearLayout myaccountLayout;
    @BindView(R.id.error_layout)
    RelativeLayout errorLayout;
    @BindView(R.id.login_btn)
    Button loginBtn;
    private Activity activity;
    private Context context;
    @BindView(R.id.profile_setting_lv)
    ListView profileSettingLv;
    private ViewGroup toolbar;
    private View toolbarLayout;
    ImageView userImage;
    TextView userName, userPhone, userEmail;
    private static final int REQUEST_LOCATION = 1450;
    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        this.activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.updateNotificationCount(context, GlobalData.notificationCount);
        initView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (toolbar != null) {
            toolbar.removeView(toolbarLayout);
        }

    }

    private void openSettingPage(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(context, ManageAddressActivity.class));
                break;
            case 1:
                startActivity(new Intent(context, FavouritesActivity.class));
                break;
            case 2:
                startActivity(new Intent(context, AccountPaymentActivity.class).putExtra("is_show_wallet",true).putExtra("is_show_cash",false));
                break;
            case 3:
                startActivity(new Intent(context, OrdersActivity.class));
                break;
            case 4:
                startActivity(new Intent(context, PromotionActivity.class));
                break;
            case 5:
                changeLanguage();
                break;
            case 6:
                startActivity(new Intent(context, ChangePasswordActivity.class));
                break;
//            case 4:
//                startActivity(new Intent(context, NotificationActivity.class));
//                break;
//            case 5:
//                startActivity(new Intent(context, PromotionActivity.class));
//                break;
//            case 6:
//                startActivity(new Intent(context, ChangePasswordActivity.class));
//                break;
            default:
        }
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        System.out.println("ProfileFragment");
        toolbar = (ViewGroup) getActivity().findViewById(R.id.toolbar);
        if (GlobalData.profileModel != null) {
            toolbarLayout = LayoutInflater.from(context).inflate(R.layout.toolbar_profile, toolbar, false);
            userImage = (ImageView) toolbarLayout.findViewById(R.id.user_image);
            userName = (TextView) toolbarLayout.findViewById(R.id.user_name);
            userPhone = (TextView) toolbarLayout.findViewById(R.id.user_phone);
            userEmail = (TextView) toolbarLayout.findViewById(R.id.user_mail);
            initView();
            Button editBtn = (Button) toolbarLayout.findViewById(R.id.edit);
            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, EditAccountActivity.class));
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, EditAccountActivity.class));
                }
            });
            toolbar.addView(toolbarLayout);
            toolbar.setVisibility(View.VISIBLE);

            errorLayout.setVisibility(View.GONE);
            final List<String> list = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.profile_settings)));
            List<Integer> listIcons = new ArrayList<>();
            listIcons.add(R.drawable.home);
            listIcons.add(R.drawable.heart);
            listIcons.add(R.drawable.payment);
            listIcons.add(R.drawable.ic_myorders);
            listIcons.add(R.drawable.ic_promotion_details);
            listIcons.add(R.drawable.ic_translate);
            if (GlobalData.profileModel.getLoginBy().equalsIgnoreCase("manual")) {
                listIcons.add(R.drawable.padlock);
                list.add(getResources().getString(R.string.change_password));

            }
            ProfileSettingsAdapter adbPerson = new ProfileSettingsAdapter(context, list, listIcons);
            profileSettingLv.setAdapter(adbPerson);
            ListViewSizeHelper.getListViewSize(profileSettingLv);
            profileSettingLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    openSettingPage(position);
                }
            });
            arrowImage.setTag(true);
//            collapse(listLayout);
            HomeActivity.updateNotificationCount(context, GlobalData.notificationCount);

            String VERSION_NAME = BuildConfig.VERSION_NAME;
            int versionCode = BuildConfig.VERSION_CODE;
            appVersion.setText("App version " + VERSION_NAME + " ("+String.valueOf(versionCode)+ ")");

        } else {
            toolbar.setVisibility(View.GONE);
            //set Error Layout
            errorLayout.setVisibility(View.VISIBLE);
        }


    }

    private void changeLanguage() {
        List<String> languages = Arrays.asList(getResources().getStringArray(R.array.languages));
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.language_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setCancelable(true);
        alertDialog.setTitle(getResources().getString(R.string.change_language));
        final AlertDialog alert = alertDialog.create();

        final ListView lv = (ListView) convertView.findViewById(R.id.lv);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice, languages);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String item = lv.getItemAtPosition(position).toString();
                setLanguage(item);
                alert.dismiss();
            }
        });
        alert.show();
    }


    private void setLanguage(String value) {
        SharedHelper.putKey(getActivity(), "language", value);
        switch (value) {
            case "English":
                LocaleUtils.setLocale(getActivity(), "en");
                break;
            case "Spanish":
                LocaleUtils.setLocale(getActivity(), "es");
                break;
            default:
                LocaleUtils.setLocale(getActivity(), "es");
                break;
        }
        startActivity(new Intent(getActivity(),HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("change_language",true));
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }
    private void initView() {
        if (GlobalData.profileModel != null) {
            Glide.with(context)
                    .load(GlobalData.profileModel.getAvatar())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.man)
                            .error(R.drawable.man))
                    .into(userImage);
            userPhone.setText(GlobalData.profileModel.getPhone());
            userName.setText(GlobalData.profileModel.getName());
            userEmail.setText(" - " + GlobalData.profileModel.getEmail());
        }
    }

    @OnClick({R.id.arrow_image, R.id.logout, R.id.myaccount_layout, R.id.login_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.myaccount_layout:
                if (arrowImage.getTag().equals(true)) {
                    //rotate arrow image
                    arrowImage.animate().setDuration(500).rotation(180).start();
                    arrowImage.setTag(false);
                    //collapse animation
                    collapse(listLayout);
                    viewLine.setVisibility(View.VISIBLE);
                    textLine.setVisibility(View.GONE);
                } else {
                    //rotate arrow image
                    arrowImage.animate().setDuration(500).rotation(360).start();
                    arrowImage.setTag(true);
                    viewLine.setVisibility(View.GONE);
                    textLine.setVisibility(View.VISIBLE);
                    //expand animation
                    expand(listLayout);
                }
                break;
            case R.id.logout:
                alertDialog();
                LoginManager.getInstance().logOut();
                break;
            case R.id.login_btn:
                SharedHelper.putKey(context, "logged", "false");
                startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                getActivity().finish();
                break;
        }
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

//                FirebaseAuth.getInstance().signOut();
                if(mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("MainAct", "Google User Logged out");
                               /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();*/
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("MAin", "Google API Client Connection Suspended");
            }
        });
    }
    public void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.are_you_want_logout))
                .setPositiveButton(getResources().getString(R.string.logout), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        if (SharedHelper.getKey(context,"login_by").equals("facebook"))
                            LoginManager.getInstance().logOut();
                        if (SharedHelper.getKey(context,"login_by").equals("google"))
                            signOut();
                        SharedHelper.putKey(context, "logged", "false");
                        startActivity(new Intent(context, WelcomeScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        GlobalData.profileModel = null;
                        GlobalData.addCart = null;
                        GlobalData.notificationCount=0;
                        getActivity().finish();

                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
        nbutton.setTypeface(nbutton.getTypeface(), Typeface.BOLD);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(context, R.color.theme));
        pbutton.setTypeface(pbutton.getTypeface(), Typeface.BOLD);
    }

}

