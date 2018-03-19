package com.cambricon.productdisplay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.cambricon.productdisplay.R;
import com.cambricon.productdisplay.adapter.GridViewAdapter;

/**
 * Created by dell on 18-2-3.
 */

public class TestFragment extends Fragment {

    private View view;
    private GridView mGridView;
    private GridViewAdapter mGridViewAdapter;
    final int CLASSIFICATION = 0;
    final int DETECTION = 1;
    final int FACE_DETECTION = 2;
    final int MOREFUNCTIONS = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.test_fragment, null);
        initView();
        setListener();
        return view;
    }

    private void initView() {
        mGridView = view.findViewById(R.id.functions_gv);
        mGridView.setOverScrollMode(View.OVER_SCROLL_NEVER|View.SCROLL_AXIS_NONE);
        mGridViewAdapter = new GridViewAdapter(getContext());
        mGridView.setAdapter(mGridViewAdapter);
    }

    private void setListener() {
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case CLASSIFICATION:
                        Log.i("huangyaling", "classification");
                        startActivity(new Intent(getActivity(), ClassificationActivity.class));
                        break;
                    case DETECTION:
                        Intent intent = new Intent(getActivity(), DetectionActivity2.class);
                        intent.putExtra("Detection", getActivity().getIntent().getSerializableExtra("caffeDetection"));
                        intent.putExtra("loadDTime", getActivity().getIntent().getSerializableExtra("loadDTime"));
                        startActivity(intent);
                        break;
                    case FACE_DETECTION:
                        intent = new Intent(getActivity(), FaceDetectorActivity.class);
                        intent.putExtra("loadDTime", getActivity().getIntent().getSerializableExtra("loadDTime"));
                        startActivity(intent);
                        break;
                    case MOREFUNCTIONS:
                        startActivity(new Intent(getActivity(), MoreFunctionsAct.class));
                        break;
                }
            }
        });
    }


}
