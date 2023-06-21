package com.gooddelivery.user.fragments;


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
import com.gooddelivery.user.adapter.ProductsAdapter;
import com.gooddelivery.user.helper.GlobalData;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductSearchFragment extends Fragment {


    @BindView(R.id.product_rv)
    RecyclerView productRv;
    Unbinder unbinder;
    public  static ProductsAdapter productsAdapter;

    public ProductSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Set Categoery shopList adapter
        productsAdapter = new ProductsAdapter(getActivity(),getActivity(), GlobalData.searchProductList);
        productRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        productRv.setItemAnimator(new DefaultItemAnimator());
        productRv.setAdapter(productsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        productsAdapter.notifyDataSetChanged();
    }
}
