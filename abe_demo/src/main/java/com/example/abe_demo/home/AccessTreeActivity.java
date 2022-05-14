package com.example.abe_demo.home;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.abe_demo.R;
import com.example.abe_demo.home.fragment.AccessTreeOnlyShowFragment;
import com.example.abe_demo.home.fragment.ModeAccessTreeFragment;
import com.example.abe_demo.show_mode.adapter.FVPAdapterWithLabel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccessTreeActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private List<Fragment> fragmentList;
    private List<String> labelList;
    private FVPAdapterWithLabel fvpAdapterWithLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_tree);

        toolbar = findViewById(R.id.accessTreeToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        // 设置标题栏不显示app名字
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        viewPager = findViewById(R.id.accessTree_vp);
        tabLayout = findViewById(R.id.accessTree_tab_layout);

        initData();

        fvpAdapterWithLabel = new FVPAdapterWithLabel(getSupportFragmentManager(), fragmentList, labelList);

        viewPager.setAdapter(fvpAdapterWithLabel);
        tabLayout.setupWithViewPager(viewPager);

    }



    private void initData() {
        fragmentList = new ArrayList<>();
        labelList = new ArrayList<>();

        AccessTreeOnlyShowFragment accessTreeOnlyShowFragment = new AccessTreeOnlyShowFragment().newInstance("这也是一个能睡觉的页面", "");
        ModeAccessTreeFragment modeAccessTreeFragment = new ModeAccessTreeFragment().newInstance("这是一个能睡觉的页面", "");

        fragmentList.add(modeAccessTreeFragment);
        fragmentList.add(accessTreeOnlyShowFragment);


        labelList.add("修改属性");
        labelList.add("访问树结构展示");
    }
}