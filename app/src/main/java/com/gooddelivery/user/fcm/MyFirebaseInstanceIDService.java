package com.gooddelivery.user.fcm;

/**
 * Created by jayakumar on 16/02/17.
 */


import android.util.Log;

import com.gooddelivery.user.helper.SharedHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FCM";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedHelper.putKey(getApplicationContext(),"device_token",""+refreshedToken);
        Log.e(TAG,""+refreshedToken);
    }
}