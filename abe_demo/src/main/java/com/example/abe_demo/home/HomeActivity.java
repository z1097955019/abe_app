package com.example.abe_demo.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.abe_demo.R;
import com.example.abe_demo.abe_tools.ABEFactory;
import com.example.abe_demo.show_mode.ShowModeFragment;
import com.example.abe_demo.sqlite3.DatabaseHelper;
import com.example.abe_demo.sqlite3.PlaceInitUtils;
import com.example.abe_demo.use_mode.UseModeFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.util.Objects;


public class HomeActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private DatabaseHelper dbHelper;
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if ((boolean) msg.obj) {
                    Toast.makeText(getApplicationContext(), "初始化地区数据成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "初始化地区数据失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public static final int CAMERA_REQ_CODE = 111;
    public static final int DEFINED_CODE = 222;
    public static final int DECODE = 1;
    public static final int GENERATE = 2;
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;
    private static final int REQUEST_CODE_DEFINE = 0X0111;
    public static final String RESULT = "SCAN_RESULT";
    private ShowModeFragment showModeFragment;
    private UseModeFragment useModeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.main_toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.main_ng);

        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);


        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // 设置标题栏不显示app名字
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

//        toolbar.set
        toolbar.setTitle("");
//        toolbar.setSubtitle("用于演示属性基加密的算法实现");
        showModeFragment = new ShowModeFragment();
        useModeFragment = new UseModeFragment();
        setShowPage(showModeFragment, R.id.menu_drawer_show_mode);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_drawer_show_mode:
                        setShowPage(showModeFragment, R.id.menu_drawer_show_mode);
                        Toast.makeText(getApplicationContext(), "menu_home_arrange", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_drawer_use_mode:
                        setShowPage(useModeFragment, R.id.menu_drawer_use_mode);
                        Toast.makeText(getApplicationContext(), "menu_home_keygen", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;

                }
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.close();
                return true;
            }
        });

        // 初始化未修改过的结构树
        SharedPreferences spRecord = getSharedPreferences("personal_mes", Context.MODE_PRIVATE);
        if (spRecord.getAll().isEmpty()) {
            System.out.println("log011: 进来了");
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor edit = spRecord.edit();
            edit.putString("nameAndPhoneAndId", "可爱小刺猬1320416416720220515");
            edit.putString("name", "可爱小刺猬");
            edit.putString("phone", "13204164167");
            edit.putString("id", "20220515");
            edit.apply();
        }

        // 初始化地点数据库
        SharedPreferences spState = getSharedPreferences("state", Context.MODE_PRIVATE);
        PlaceInitUtils placeInitUtils = new PlaceInitUtils(this);
        if (spState.getBoolean("dbIsNotInit", true)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dbHelper = new DatabaseHelper(getBaseContext(), 1);
                    Message message = new Message();
                    message.what = 0;
                    if (dbHelper.insertProvinceList(placeInitUtils.getProvinceListFromFile(R.raw.province)) &&
                            dbHelper.insertCityList(placeInitUtils.getCityListFromFile(R.raw.province_under)) &&
                            dbHelper.insertPlaceList(placeInitUtils.getPlaceCityListFromFile(R.raw.place))) {
                        spState.edit().putBoolean("dbIsNotInit", false).apply();
                        message.obj = true;
                    } else {
                        message.obj = false;
                    }
                    mHandler.sendMessage(message);

                }
            }).start();

        }

    }

    // toolbar 菜单按钮监听类
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_qr_code_btn:
                loadScanKitBtnClick(findViewById(com.huawei.hms.scankit.R.id.surfaceView));
                break;
            case R.id.access_tree_btn:
                Intent intent = new Intent(this, AccessTreeActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "?????????", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 加载页面的fragment
    private void setShowPage(Fragment fragment, int navigationViewId) {
//        ShowModeFragment showModeFragment = new ShowModeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.show_mode_fcv, fragment).commit();
        navigationView.setCheckedItem(navigationViewId);

    }

    // 加入菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void loadScanKitBtnClick(View view) {
        requestPermission(CAMERA_REQ_CODE, DECODE);
    }

    /**
     * Apply for permissions.
     */
    private void requestPermission(int requestCode, int mode) {
        if (mode == DECODE) {
            decodePermission(requestCode);
        } else if (mode == GENERATE) {
            generatePermission(requestCode);
        }
    }

    /**
     * Apply for permissions.
     */
    private void decodePermission(int requestCode) {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                requestCode);
    }

    /**
     * Apply for permissions.
     */
    private void generatePermission(int requestCode) {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                requestCode);
    }

    /**
     * Call back the permission application result. If the permission application is successful, the barcode scanning view will be displayed.
     *
     * @param requestCode   Permission application code.
     * @param permissions   Permission array.
     * @param grantResults: Permission application result array.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || grantResults == null) {
            return;
        }

        if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //Default View Mode
        if (requestCode == CAMERA_REQ_CODE) {
            ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, new HmsScanAnalyzerOptions.Creator().create());
        }
        //Customized View Mode
        if (requestCode == DEFINED_CODE) {
            Intent intent = new Intent(this, DefinedActivity.class);
            this.startActivityForResult(intent, REQUEST_CODE_DEFINE);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Event for receiving the activity result.
     *
     * @param requestCode Request code.
     * @param resultCode  Result code.
     * @param data        Result.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        System.out.println("log009:????????????????????"+ data.toString());
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        //Default View
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null) {
                String qrValue = obj.originalValue;
                ABEFactory abeFactory = new ABEFactory(this);
                String testStr;
                try {
                    testStr = abeFactory.decrypt(qrValue, true);
                    new MaterialAlertDialogBuilder(this).setTitle("解密结果：").setMessage("原始数据：\n" + qrValue + "\n\n" + "解密数据：\n" + testStr + "\n\n").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
                } catch (Exception e) {
                    Snackbar.make(navigationView, e.toString(), Snackbar.LENGTH_SHORT).show();
                }
            }
            //MultiProcessor & Bitmap
        } else if (requestCode == REQUEST_CODE_DEFINE) {
            HmsScan obj = data.getParcelableExtra(DefinedActivity.SCAN_RESULT);
            if (obj != null) {
                String qrValue = obj.originalValue;
                ABEFactory abeFactory = new ABEFactory(this);
                String testStr;
                try {
                    testStr = abeFactory.decrypt(qrValue, true);
                    new MaterialAlertDialogBuilder(this).setTitle("解密结果：").setMessage("原始数据：\n" + qrValue + "\n\n" + "解密数据：\n" + testStr + "\n\n").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    }).show();
                } catch (Exception e) {
                    Snackbar.make(navigationView, e.toString(), Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

}