package com.example.abe_demo.show_mode.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abe_demo.R;
import com.example.abe_demo.show_mode.adapter.FVPAdapterWithLabel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DecryptFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DecryptFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private List<Fragment> fragmentList;
    private List<String> labelList;
    private FVPAdapterWithLabel fvpAdapterWithLabel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DecryptFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DecryptFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DecryptFragment newInstance(String param1, String param2) {
        DecryptFragment fragment = new DecryptFragment();
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
        return inflater.inflate(R.layout.fragment_decrypt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        TextView tv =  view.findViewById(R.id.tv_show);
//        tv.setText(mParam1);
        viewPager = view.findViewById(R.id.decrypt_vp);
        tabLayout = view.findViewById(R.id.decrypt_tab_layout);

        initData();

        fvpAdapterWithLabel = new FVPAdapterWithLabel(getChildFragmentManager(), fragmentList, labelList);

        viewPager.setAdapter(fvpAdapterWithLabel);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initData() {
        fragmentList = new ArrayList<>();
        labelList = new ArrayList<>();

        DecryptActionFragment  decryptActionFragment = new DecryptActionFragment().newInstance("这是一个不能睡觉的页面","");
        DecryptActionFragment  decryptActionFragment1 = new DecryptActionFragment().newInstance("这是一个能睡觉的页面","");
        DecryptActionFragment  decryptActionFragment2 = new DecryptActionFragment().newInstance("这是一个超能睡觉的页面","");

        fragmentList.add(decryptActionFragment);
        fragmentList.add(decryptActionFragment1);
        fragmentList.add(decryptActionFragment2);

        labelList.add("实时生成");
        labelList.add("默认配置");
        labelList.add("详细说明");
    }
}