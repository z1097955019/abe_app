package com.example.abe_demo.show_mode.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.abe_demo.R;
import com.example.abe_demo.show_mode.adapter.FVPAdapter;
import com.example.abe_demo.show_mode.adapter.FVPAdapterWithLabel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFragment extends Fragment {

    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private List<Fragment> fragmentList;
    private List<String> labelList;
    private FVPAdapterWithLabel fvpAdapterWithLabel;

    public SetupFragment() {
        // Required empty public constructor
    }


    public static SetupFragment newInstance(String param1, String param2) {
        SetupFragment fragment = new SetupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        TextView tv =  view.findViewById(R.id.tv_show);
//        tv.setText(mParam1);
        viewPager = view.findViewById(R.id.setup_vp);
        tabLayout = view.findViewById(R.id.setup_tab_layout);

        initData();

        fvpAdapterWithLabel = new FVPAdapterWithLabel(getChildFragmentManager(), fragmentList, labelList);

        viewPager.setAdapter(fvpAdapterWithLabel);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initData() {
        fragmentList = new ArrayList<>();
        labelList = new ArrayList<>();

        SetupActionFragment  setupActionFragment = new SetupActionFragment().newInstance("这是一个不能睡觉的页面","");
        SetupActionFragment  setupActionFragment1 = new SetupActionFragment().newInstance("这是一个能睡觉的页面","");
        SetupActionFragment  setupActionFragment2 = new SetupActionFragment().newInstance("这是一个超能睡觉的页面","");

        fragmentList.add(setupActionFragment);
        fragmentList.add(setupActionFragment1);
        fragmentList.add(setupActionFragment2);

        labelList.add("实时生成");
        labelList.add("默认配置");
        labelList.add("详细说明");
    }
}