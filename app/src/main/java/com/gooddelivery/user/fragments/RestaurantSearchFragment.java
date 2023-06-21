package com.gooddelivery.user.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gooddelivery.user.R;
import com.gooddelivery.user.adapter.RestaurantsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.gooddelivery.user.helper.GlobalData.searchShopList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantSearchFragment extends Fragment {

    public static RestaurantsAdapter restaurantsAdapter;

    Unbinder unbinder;
    Context context;
//    public static SkeletonScreen skeletonScreen;
    @BindView(R.id.restaurants_rv)
    RecyclerView restaurantsRv;

    public RestaurantSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        unbinder = ButterKnife.bind(this, view);
        context = getActivity();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Restaurant Adapter
        restaurantsRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        restaurantsRv.setItemAnimator(new DefaultItemAnimator());
        restaurantsRv.setHasFixedSize(true);
        restaurantsAdapter = new RestaurantsAdapter(searchShopList, context, getActivity());
        restaurantsRv.setAdapter(restaurantsAdapter);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
