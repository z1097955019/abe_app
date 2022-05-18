package com.example.abe_demo.show_mode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.abe_demo.R;
import com.example.abe_demo.show_mode.adapter.FVPAdapter;
import com.example.abe_demo.show_mode.fragment.DecryptFragment;
import com.example.abe_demo.show_mode.fragment.EncryptFragment;
import com.example.abe_demo.show_mode.fragment.KeygenFragment;
import com.example.abe_demo.show_mode.fragment.SetupFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowModeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowModeFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ViewPager myViewPager;
    private BottomNavigationView myBottomNavigationView;
    private List<Fragment> myFragmentList;
    private FVPAdapter fvpAdapter;
    private FloatingActionButton fab;


    public ShowModeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowModeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowModeFragment newInstance(String param1, String param2) {
        ShowModeFragment fragment = new ShowModeFragment();
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
        return inflater.inflate(R.layout.fragment_show_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab = view.findViewById(R.id.float_button);
        fab.setBottom(50);

        myViewPager = view.findViewById(R.id.vp);
        myViewPager.setSaveEnabled(false);
        myBottomNavigationView = view.findViewById(R.id.bottom_nav_menu);

        initData();

        fvpAdapter = new FVPAdapter(getChildFragmentManager(), myFragmentList);
        myViewPager.setAdapter(fvpAdapter);

        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
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
                switch (item.getItemId()) {
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

        // 浮动按钮的监听事件
        view.findViewById(R.id.float_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 文件存储路径
                String pkFileName = "pk.properties";
                String mskFileName = "msk.properties";
                String skFileName = "sk.properties";
                String ctFileName1 = "ct1.properties";
                String ctFileName2 = "ct2.properties";
                String mingFileName = "ming.properties";
                String ming_before = "clearTB.properties";

                clearSP(getContext(), "show_" + pkFileName);
                clearSP(getContext(), "show_" + mskFileName);
                clearSP(getContext(), "show_" + skFileName);
                clearSP(getContext(), "show_" + ctFileName1);
                clearSP(getContext(), "show_" + ctFileName2);
                clearSP(getContext(), "show_" + mingFileName);
                clearSP(getContext(), "show_" + ming_before);

                Toast.makeText(getContext(), "已清除数据缓存", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void initData() {
        myFragmentList = new ArrayList<>();
        SetupFragment setupFragment = SetupFragment.newInstance("这是部署的页面", "");
        KeygenFragment keygenFragment  = KeygenFragment.newInstance("这是部署1的页面", "");
        EncryptFragment encryptFragment = EncryptFragment.newInstance("这是部署2的页面", "");
        DecryptFragment decryptFragment = DecryptFragment.newInstance("这是部署3的页面", "");

        myFragmentList.add(setupFragment);
        myFragmentList.add(keygenFragment);
        myFragmentList.add(encryptFragment);
        myFragmentList.add(decryptFragment);


    }

    // 清空指定数据缓存
    private static void clearSP(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}