package com.example.abe_demo.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.abe_demo.R;
import com.example.abe_demo.show_mode.ShowModeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.main_toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.main_ng);
        FloatingActionButton fab =  findViewById(R.id.float_button);
        fab.setBottom(50);

        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);


        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

//        toolbar.set
        toolbar.setTitle("");
//        toolbar.setSubtitle("用于演示属性基加密的算法实现");


        setShowPage();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_home_arrange:
                        Toast.makeText(getApplicationContext(), "menu_home_arrange",Toast.LENGTH_SHORT).show();
                        break;
//                    case R.id.menu_home_decrypt:
//                        Toast.makeText(getApplicationContext(), "menu_home_decrypt",Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.menu_home_encrypt:
//                        Toast.makeText(getApplicationContext(), "menu_home_encrypt",Toast.LENGTH_SHORT).show();
//                        break;
                    case R.id.menu_home_keygen:
                        Toast.makeText(getApplicationContext(), "menu_home_keygen",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;

                }
                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.close();
                return true;
            }
        });

    }

    private void setShowPage() {
        ShowModeFragment showModeFragment = new ShowModeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.show_mode_fcv, showModeFragment).commit();
        navigationView.setCheckedItem(R.id.menu_home_arrange);

    }


}