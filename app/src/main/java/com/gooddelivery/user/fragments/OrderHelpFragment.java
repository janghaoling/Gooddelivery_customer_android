package com.gooddelivery.user.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.gooddelivery.user.R;
import com.gooddelivery.user.activities.OtherHelpActivity;
import com.gooddelivery.user.activities.SplashActivity;
import com.gooddelivery.user.adapter.DisputeMessageAdapter;
import com.gooddelivery.user.build.api.ApiClient;
import com.gooddelivery.user.build.api.ApiInterface;
import com.gooddelivery.user.helper.CustomDialog;
import com.gooddelivery.user.helper.GlobalData;
import com.gooddelivery.user.models.DisputeMessage;
import com.gooddelivery.user.models.Order;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.gooddelivery.user.helper.GlobalData.disputeMessageList;
import static com.gooddelivery.user.helper.GlobalData.isSelectedOrder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderHelpFragment extends Fragment {


    Unbinder unbinder;
    Context context;
    DisputeMessageAdapter disputeMessageAdapter;
    @BindView(R.id.help_rv)
    RecyclerView helpRv;
    @BindView(R.id.other_help_layout)
    LinearLayout otherHelpLayout;
    @BindView(R.id.dispute)
    Button dispute;
    @BindView(R.id.chat_us)
    Button chatUs;


    Double priceAmount = 0.0;
    int DISPUTE_ID = 0;
    int itemQuantity = 0;
    String currency = "";
    CustomDialog customDialog;
    String disputeType;
    Integer DISPUTE_HELP_ID = 0;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);

    public OrderHelpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_help, container, false);
        unbinder = ButterKnife.bind(this, view);
        customDialog = new CustomDialog(context);


        getDisputeMessage();


        return view;
    }

    private void getDisputeMessage() {
        Call<List<DisputeMessage>> call = apiInterface.getDisputeList();
        call.enqueue(new Callback<List<DisputeMessage>>() {
            @Override
            public void onResponse(Call<List<DisputeMessage>> call, Response<List<DisputeMessage>> response) {
                if (response.isSuccessful()) {
                    Log.e("Dispute List : ", response.toString());
                    disputeMessageList = new ArrayList<>();
                    disputeMessageList.addAll(response.body());
                    updateDiputeLayout();

                } else {
                    updateDiputeLayout();

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().toString());
                        Toast.makeText(context, jObjError.optString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<List<DisputeMessage>> call, Throwable t) {
                updateDiputeLayout();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void updateDiputeLayout() {
        if (disputeMessageList != null) {
            helpRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            helpRv.setItemAnimator(new DefaultItemAnimator());
            helpRv.setHasFixedSize(true);
            disputeMessageAdapter = new DisputeMessageAdapter(disputeMessageList, context, getActivity());
            helpRv.setAdapter(disputeMessageAdapter);
            if (disputeMessageList.size() > 0) {
                otherHelpLayout.setVisibility(View.GONE);
            } else {
                otherHelpLayout.setVisibility(View.VISIBLE);
            }
        } else {
            startActivity(new Intent(context, SplashActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showDialog() {
        final String[] disputeArrayList = {getString(R.string.complained), getString(R.string.cancelled), getString(R.string.refund)};
        disputeType = "COMPLAINED";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dispute_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.reason_edit);
        final Spinner disputeTypeSpinner = (Spinner) dialogView.findViewById(R.id.dispute_type);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, disputeArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        disputeTypeSpinner.setAdapter(arrayAdapter);
        disputeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = disputeArrayList[position];
                if (str.equalsIgnoreCase(getString(R.string.complained))) {
                    disputeType = "COMPLAINED";
                } else if (str.equalsIgnoreCase(getString(R.string.cancelled))) {
                    disputeType = "CANCELLED";
                } else {
                    disputeType = "REFUND";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialogBuilder.setTitle(getString(R.string.order) + " #000" + isSelectedOrder.getId().toString());
        dialogBuilder.setMessage(getString(R.string.others));
        dialogBuilder.setPositiveButton(getString(R.string.submit), null);
        dialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edt.getText().toString().equalsIgnoreCase("")) {
                            Toast.makeText(context, getString(R.string.enter_your_description), Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            HashMap<String, String> map = new HashMap<>();
                            map.put("order_id", GlobalData.isSelectedOrder.getId().toString());
                            map.put("status", "CREATED");
                            map.put("description", edt.getText().toString());
                            map.put("dispute_type", disputeType);
                            map.put("created_by", "user");
                            map.put("created_to", "user");
                            postDispute(map);
                        }
                    }
                });
            }
        });
        alertDialog.show();

    }

    private void postDispute(HashMap<String, String> map) {
        customDialog.show();
        Call<Order> call = apiInterface.postDispute(map);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(context, getString(R.string.dispute_created_successfully), Toast.LENGTH_SHORT).show();
                    getActivity().finish();
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
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @OnClick({R.id.chat_us, R.id.dispute})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.chat_us:
                startActivity(new Intent(getActivity(), OtherHelpActivity.class).putExtra("is_chat", true));
                break;
            case R.id.dispute:
                showDialog();
                break;
        }
    }
}
