package com.example.abe_demo.show_mode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.abe_demo.R;
import com.example.abe_demo.show_mode.adapter.FVPAdapter;
import com.example.abe_demo.show_mode.fragment.SetupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class ShowModeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ViewPager myViewPager;
    private BottomNavigationView myBottomNavigationView;
    private List<Fragment> myFragmentList;
    private  FVPAdapter fvpAdapter;

    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_mode);

        toolbar = findViewById(R.id.main_toolbar);
        myViewPager = findViewById(R.id.vp);
        myBottomNavigationView = findViewById(R.id.bottom_nav_menu);
        drawerLayout = findViewById(R.id.draw_layout);
        
        initData();

        setSupportActionBar(toolbar);


        fvpAdapter = new FVPAdapter(getSupportFragmentManager(),myFragmentList);
        myViewPager.setAdapter(fvpAdapter);

        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        myBottomNavigationView.setSelectedItemId(R.id.menu_arrange);
                        break;
                    case 1:
                        myBottomNavigationView.setSelectedItemId(R.id.menu_keygen);
                        break;
                    case 2:
                        myBottomNavigationView.setSelectedItemId(R.id.menu_encrypt);
                        break;
                    case 3:
                        myBottomNavigationView.setSelectedItemId(R.id.menu_decrypt);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        myBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_arrange:
                        myViewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_keygen:
                        myViewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_encrypt:
                        myViewPager.setCurrentItem(2);
                        break;
                    case R.id.menu_decrypt:
                        myViewPager.setCurrentItem(3);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        
    }


    private void initData() {
        myFragmentList = new ArrayList<>();
        SetupFragment setupFragment = SetupFragment.newInstance("这是部署的页面","");
        SetupFragment setupFragment1 = SetupFragment.newInstance("这是部署1的页面","");
        SetupFragment setupFragment2 = SetupFragment.newInstance("这是部署2的页面","");
        SetupFragment setupFragment3 = SetupFragment.newInstance("这是部署3的页面","");
        myFragmentList.add(setupFragment);
        myFragmentList.add(setupFragment1);
        myFragmentList.add(setupFragment2);
        myFragmentList.add(setupFragment3);
    }
}