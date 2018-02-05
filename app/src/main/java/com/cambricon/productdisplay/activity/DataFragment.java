package com.cambricon.productdisplay.activity;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.adapter.GridViewAdapter;

/**
 * Created by dell on 18-2-3.
 */

public class DataFragment extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_fragment, null);
        return view;
    }
}
