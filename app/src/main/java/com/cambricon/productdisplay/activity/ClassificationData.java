package com.cambricon.productdisplay.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cambricon.productdisplay.R;

/**
 * Created by dell on 18-2-8.
 */

public class ClassificationData extends Fragment {
    private View view;
    private Context context;
    private String content;
    public ClassificationData(){

    }

    @SuppressLint("ValidFragment")
    public ClassificationData(Context contexts, String content) {
        this.context = contexts;
        this.content = content;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.classification_data_layout, null);
        return view;
    }
}
