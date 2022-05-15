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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.abe_demo.R;
import com.example.abe_demo.abe_tools.AccessTree;
import com.example.abe_demo.abe_tools.CP_ABE;
import com.example.abe_demo.abe_tools.Node;
import com.example.abe_demo.abe_tools.utils.CodeConvert;
import com.example.abe_demo.show_mode.ShowModeFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;


public class HomeActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public static final int CAMERA_REQ_CODE = 111;
    public static final int DEFINED_CODE = 222;
    public static final int DECODE = 1;
    public static final int GENERATE = 2;
    private static final int REQUEST_CODE_SCAN_ONE = 0X01;
    private static final int REQUEST_CODE_DEFINE = 0X0111;
    public static final String RESULT = "SCAN_RESULT";

    // 文件存储路径
    private final String pkFileName = "pk.properties";
    private final String mskFileName = "msk.properties";
    private final String skFileName = "sk.properties";
    private final String ctFileName1 = "ct1.properties";
    private final String ctFileName2 = "ct2.properties";
    private final String mingFileName = "ming.properties";

    private Properties ct1Prop;
    private Properties ct2Prop;
    private Properties skProp;
    private Properties clearTextProp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.main_toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.main_ng);
        FloatingActionButton fab = findViewById(R.id.float_button);
        fab.setBottom(50);

        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);


        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // 设置标题栏不显示app名字
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

//        toolbar.set
        toolbar.setTitle("");
//        toolbar.setSubtitle("用于演示属性基加密的算法实现");
        setShowPage();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home_arrange:
                        Toast.makeText(getApplicationContext(), "menu_home_arrange", Toast.LENGTH_SHORT).show();
                        break;
//                    case R.id.menu_home_decrypt:
//                        Toast.makeText(getApplicationContext(), "menu_home_decrypt",Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.menu_home_encrypt:
//                        Toast.makeText(getApplicationContext(), "menu_home_encrypt",Toast.LENGTH_SHORT).show();
//                        break;
                    case R.id.menu_home_keygen:
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

        // 浮动按钮的监听事件
        findViewById(R.id.float_button).setOnClickListener(new View.OnClickListener() {
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

                clearSP(getBaseContext(), "show_" + pkFileName);
                clearSP(getBaseContext(), "show_" + mskFileName);
                clearSP(getBaseContext(), "show_" + skFileName);
                clearSP(getBaseContext(), "show_" + ctFileName1);
                clearSP(getBaseContext(), "show_" + ctFileName2);
                clearSP(getBaseContext(), "show_" + mingFileName);
                clearSP(getBaseContext(), "show_" + ming_before);

                Toast.makeText(getBaseContext(), "已清除数据缓存", Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences spRecord = getSharedPreferences("personal_mes", Context.MODE_PRIVATE);
        if(spRecord.getAll().isEmpty()){
            System.out.println("log011: 进来了");
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor edit = spRecord.edit();
            edit.putString("nameAndPhoneAndId", "可爱小刺猬1320416416720220515");
            edit.putString("name", "可爱小刺猬");
            edit.putString("phone", "13204164167");
            edit.putString("id", "20220515");
            edit.apply();
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
    private void setShowPage() {
        ShowModeFragment showModeFragment = new ShowModeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.show_mode_fcv, showModeFragment).commit();
        navigationView.setCheckedItem(R.id.menu_home_arrange);

    }


    // 清空指定数据缓存
    public static void clearSP(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
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
        System.out.println("log009:????????????????????"+ data.toString());
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        //Default View
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null) {
                String qrValue = obj.originalValue;
                new MaterialAlertDialogBuilder(this).setTitle("解密结果：").setMessage("原始数据：\n"+qrValue+"\n\n"+ "解密数据：\n"+decrypt(qrValue)+"\n\n").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();


                Intent intent = new Intent(this, DisPlayActivity.class);
//                startActivity(intent);
            }
            //MultiProcessor & Bitmap
        } else if (requestCode == REQUEST_CODE_DEFINE) {
            HmsScan obj = data.getParcelableExtra(DefinedActivity.SCAN_RESULT);
            if (obj != null) {
                String qrValue = obj.originalValue;
                new MaterialAlertDialogBuilder(this).setTitle("扫码结果：").setMessage(qrValue).show();
                Intent intent = new Intent(this, DisPlayActivity.class);
//                startActivity(intent);
            }
        }
    }

    private String  decrypt(String ct2) {
        // 生成椭圆曲线群
        Pairing bp = initBp();

        // 初始化数据
        ct1Prop = getData("show_" + ctFileName1);
        ct2Prop = load(ct2);
        skProp = getData("show_" + skFileName);

        Map<Integer, String> structMes = new HashMap<>();
        structMes.put(1, "test1");
        structMes.put(2, "test2");
        structMes.put(3, "test3");


        Node[] nodes = new Node[7];
        nodes[0] = new Node(0, new int[]{1, 2}, new int[]{1, 2}, 1);
        nodes[1] = new Node(1, "idForRoad");
        nodes[2] = new Node(2, new int[]{1, 2}, new int[]{3, 4}, 2);
        nodes[3] = new Node(3, "idForSender");
        nodes[4] = new Node(4, new int[]{1, 2}, new int[]{5, 6}, 3);
        nodes[5] = new Node(5, "InvitorId");
        nodes[6] = new Node(6, getSharedPreferences("personal_mes", Context.MODE_PRIVATE).getString("nameAndPhoneAndId", ""));

        AccessTree accessTree = new AccessTree(nodes, bp);

        // 存储解密信息
        List<String> messageBigNumStringGroup = new LinkedList<>();

        // 解密部分
        List<Element> res = null;
        try {
            res = CP_ABE.Decrypt(bp, accessTree, ct1Prop, ct2Prop, skProp, true);
        }catch (Exception e){
            System.out.println("log010: "+ e.toString());
            Snackbar.make(navigationView,e.toString(),Snackbar.LENGTH_SHORT).show();
        }


        // 转译展示解密的明文
        if (!(res == null)) {
            for (Element bigNum : res) {
                messageBigNumStringGroup.add(bigNum.toString().substring(1, bigNum.toString().length() - 1).split(",")[0].substring(2));
            }
            String resString = CodeConvert.BigNumGroupToMes(messageBigNumStringGroup);

            // 存储
            clearTextProp.put("clearText", resString);
            return resString;
        }else {
            return "无法解密成功！";
        }
    }


    private Properties getData(String SPName) {
        SharedPreferences SP = getSharedPreferences(SPName, Context.MODE_PRIVATE);
        Properties prop = new Properties();
        for (String key : SP.getAll().keySet()) {
            if (!SP.getString(key, "").equals("")) {
                prop.put(key, SP.getString(key, ""));
            }
        }
        return prop;
    }


    private Pairing initBp() {
        // 生成椭圆曲线群
        InputStream raw = getResources().openRawResource(R.raw.a);
        PropertiesParameters curveParams = new PropertiesParameters();
        curveParams.load(raw);
//        Log.v("log004: curveParams: ", curveParams.toString());
        return PairingFactory.getPairing(curveParams);
    }

    private boolean recordData(Properties temPro, String SPName) {
        try {
            SharedPreferences abe_show = getSharedPreferences(SPName, Context.MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = abe_show.edit();
            for (String property_key : temPro.stringPropertyNames()) {
                editor.putString(property_key, temPro.getProperty(property_key));
            }
            editor.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Properties load(String propertiesString) {
        Properties properties = new Properties();
        try {
            String[] split = propertiesString.split(",");
            System.out.println("log010: "+ Arrays.toString(split));
            for(String stringItem:split){
                String[] singleStringKeyValue = stringItem.split("=");
                System.out.println("log010: singleStringKeyValue: "+ Arrays.toString(singleStringKeyValue)+"\n\n");
                if(singleStringKeyValue!=null){
                    properties.put(singleStringKeyValue[0],singleStringKeyValue[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("log010: "+"?????");
        }
        return properties;
    }
}