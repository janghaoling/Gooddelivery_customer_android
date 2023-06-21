package com.gooddelivery.user.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gooddelivery.user.HomeActivity;
import com.gooddelivery.user.R;
import com.gooddelivery.user.build.api.ApiClient;
import com.gooddelivery.user.build.api.ApiInterface;
import com.gooddelivery.user.helper.CustomDialog;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.helper.SharedHelper;
import com.gooddelivery.user.models.AddCart;
import com.gooddelivery.user.models.AddressList;
import com.gooddelivery.user.models.LoginModel;
import com.gooddelivery.user.models.Otp;
import com.gooddelivery.user.models.RegisterModel;
import com.gooddelivery.user.models.User;
import com.gooddelivery.user.utils.LocaleUtils;
import com.gooddelivery.user.utils.Utils;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.philio.pinentry.PinEntryView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {


    @BindView(R.id.otp_image)
    ImageView otpImage;
    @BindView(R.id.verification_code_txt)
    TextView verificationCodeTxt;
    @BindView(R.id.veri_txt1)
    TextView veriTxt1;
    @BindView(R.id.veri_txt2)
    TextView veriTxt2;
    @BindView(R.id.rel_verificatin_code)
    RelativeLayout relVerificatinCode;
    @BindView(R.id.otp_value1)
    PinEntryView otpValue1;
    @BindView(R.id.otp_continue)
    Button otpContinue;
    Context context;
    boolean isSignUp = true;
    @BindView(R.id.mobile_number_txt)
    TextView mobileNumberTxt;
    CustomDialog customDialog;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    String device_token, device_UDID;
    Utils utils = new Utils();
    String TAG = "OTPACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);
        context = OtpActivity.this;
        customDialog = new CustomDialog(context);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            isSignUp = bundle.getBoolean("signup", true);
        }
        mobileNumberTxt.setText(GlobalData.mobile);
//        otpValue1.setText(String.valueOf(GlobalData.otpValue));
        getDeviceToken();
    }

    public void getDeviceToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.d(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "" + FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token", "" + FirebaseInstanceId.getInstance().getToken());
                Log.d(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleUtils.onAttach(newBase));
    }

    public void signup(HashMap<String, String> map) {
        customDialog.show();
        Call<RegisterModel> call = apiInterface.postRegister(map);
        call.enqueue(new Callback<RegisterModel>() {
            @Override
            public void onResponse(@NonNull Call<RegisterModel> call, @NonNull Response<RegisterModel> response) {
                if (response.isSuccessful()) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("login_by", GlobalData.loginBy);
                    map.put("accessToken", GlobalData.access_token);
                    login(map);
                } else {
                    customDialog.dismiss();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("phone"))
                            Toast.makeText(context, jObjError.optString("phone"), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("email"))
                            Toast.makeText(context, jObjError.optString("email"), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RegisterModel> call, @NonNull Throwable t) {
                Toast.makeText(OtpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
            }
        });

    }

    private void verifyOtp(HashMap<String, String> map) {
        customDialog.show();
        Call<ResponseBody> response = apiInterface.verifyOtp(map);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "" + "Authenticated!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OtpActivity.this, SignUpActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                    finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("phone"))
                            Toast.makeText(context, jObjError.optString("phone"), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("email"))
                            Toast.makeText(context, jObjError.optString("email"), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
            }
        });
    }

    private void login(HashMap<String, String> map) {
        Call<LoginModel> call;
        if (GlobalData.loginBy.equals("manual"))
            call = apiInterface.postLogin(map);
        else
            call = apiInterface.postSocialLogin(map);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                if (response.isSuccessful()) {
                    SharedHelper.putKey(context, "access_token", response.body().getTokenType() + " " + response.body().getAccessToken());
                    GlobalData.accessToken = response.body().getTokenType() + " " + response.body().getAccessToken();
                    getProfile();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {

            }
        });
    }

    private void getProfile() {
        HashMap<String, String> map = new HashMap<>();
        map.put("device_type", "android");
        map.put("device_id", device_UDID);
        map.put("device_token", device_token);
        Call<User> getprofile = apiInterface.getProfile(map);
        getprofile.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    SharedHelper.putKey(context, "logged", "true");
                    GlobalData.profileModel = response.body();
                    GlobalData.addCart = new AddCart();
                    GlobalData.addCart.setProductList(response.body().getCart());
                    GlobalData.addressList = new AddressList();
                    GlobalData.addressList.setAddresses(response.body().getAddresses());
                    startActivity(new Intent(context, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                } else {
                    if (response.code() == 401) {
                        SharedHelper.putKey(context, "logged", "false");
                        startActivity(new Intent(context, LoginActivity.class));
                        finish();
                    }

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().toString());
                        Toast.makeText(context, jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

            }
        });
    }


    public void getOtpVerification(HashMap<String, String> map) {
        customDialog.show();
        Call<Otp> call = apiInterface.postOtp(map);
        call.enqueue(new Callback<Otp>() {
            @Override
            public void onResponse(@NonNull Call<Otp> call, @NonNull Response<Otp> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                    GlobalData.otpValue = response.body().getOtp();
//                    otpValue1.setText(String.valueOf(GlobalData.otpValue));
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
            public void onFailure(@NonNull Call<Otp> call, @NonNull Throwable t) {
                customDialog.dismiss();
            }
        });

    }

    @OnClick({R.id.otp_continue, R.id.resend_otp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.otp_continue:
                Log.d("OtpData", otpValue1.getText().toString() + " = " + GlobalData.otpValue);
                Log.d("OtpData", ":::::0" + GlobalData.loginBy + ":::" + isSignUp);
//                if (otpValue1.getText().toString().equals("" + GlobalData.otpValue)) {
                    if (!GlobalData.loginBy.equals("manual")) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("name", GlobalData.name);
                        map.put("email", GlobalData.email);
                        map.put("phone", GlobalData.mobile);
                        map.put("login_by", GlobalData.loginBy);
                        map.put("accessToken", GlobalData.access_token);
                        signup(map);
                    } else if (isSignUp) {
                        startActivity(new Intent(OtpActivity.this, SignUpActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                        finish();
                    } else {
                        startActivity(new Intent(OtpActivity.this, ResetPasswordActivity.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
                        finish();
                    }
//                } else {
////                    Toast.makeText(this, "Enter otp is incorrect", Toast.LENGTH_SHORT).show();
//                    HashMap<String, String> map = new HashMap<>();
//                    map.put("phone", GlobalData.mobile);
//                    map.put("otp", otpValue1.getText().toString());
//                    verifyOtp(map);
//                }
                break;
            case R.id.resend_otp:
                HashMap<String, String> map1 = new HashMap<>();
                map1.put("phone", GlobalData.mobileNumber);
                getOtpVerification(map1);
                break;
        }
    }

}