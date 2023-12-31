package com.gooddelivery.user.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gooddelivery.user.HomeActivity;
import com.gooddelivery.user.R;
import com.gooddelivery.user.build.api.ApiClient;
import com.gooddelivery.user.build.api.ApiInterface;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.helper.CustomDialog;
import com.gooddelivery.user.models.ResetPassword;
import com.gooddelivery.user.utils.LocaleUtils;
import com.gooddelivery.user.utils.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    Context context;
    @BindView(R.id.new_password)
    EditText newPassword;
    @BindView(R.id.confirm_password)
    EditText confirmPassword;
    @BindView(R.id.change_btn)
    Button changeBtn;
    @BindView(R.id.sigin_layout)
    LinearLayout siginLayout;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.sign_in_here)
    TextView signInHere;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    String strConfirmPassword, strNewPassword;

    CustomDialog customDialog;
    @BindView(R.id.password_eye_img)
    ImageView passwordEyeImg;
    @BindView(R.id.confirm_password_eye_img)
    ImageView confirmPasswordEyeImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        context = ResetPasswordActivity.this;
        customDialog = new CustomDialog(context);
        passwordEyeImg.setTag(1);
        confirmPasswordEyeImg.setTag(1);

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

    @OnClick({R.id.change_btn, R.id.sign_in_here, R.id.password_eye_img, R.id.confirm_password_eye_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.change_btn:
                initValues();
                break;
            case R.id.sign_in_here:
                break;
            case R.id.password_eye_img:
                if (passwordEyeImg.getTag().equals(1)) {
                    newPassword.setTransformationMethod(null);
                    passwordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_close));
                    passwordEyeImg.setTag(0);
                } else {
                    passwordEyeImg.setTag(1);
                    newPassword.setTransformationMethod(new PasswordTransformationMethod());
                    passwordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_open));
                }
                break;
            case R.id.confirm_password_eye_img:
                if (confirmPasswordEyeImg.getTag().equals(1)) {
                    confirmPassword.setTransformationMethod(null);
                    confirmPasswordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_close));
                    confirmPasswordEyeImg.setTag(0);
                } else {
                    confirmPasswordEyeImg.setTag(1);
                    confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    confirmPasswordEyeImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_open));
                }
                break;
        }
    }


    private void initValues() {

        strConfirmPassword = confirmPassword.getText().toString();
        strNewPassword = newPassword.getText().toString();
        if (TextUtils.isEmpty(strNewPassword)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(strConfirmPassword)) {
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show();

        } else if (!strConfirmPassword.equalsIgnoreCase(strNewPassword)) {
            Toast.makeText(this, "Password and confirm password doesn't match", Toast.LENGTH_SHORT).show();
        } else {

            HashMap<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(GlobalData.profileModel.getId()));
            map.put("password", strNewPassword);
            map.put("password_confirmation", strConfirmPassword);
            resetPassword(map);
        }

    }

    private void resetPassword(HashMap<String, String> map) {
        customDialog.show();
        Call<ResetPassword> call = apiInterface.resetPassword(map);
        call.enqueue(new Callback<ResetPassword>() {
            @Override
            public void onResponse(@NonNull Call<ResetPassword> call, @NonNull Response<ResetPassword> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(context, HomeActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.anim_nothing);
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
            public void onFailure(@NonNull Call<ResetPassword> call, @NonNull Throwable t) {

            }
        });

    }

}