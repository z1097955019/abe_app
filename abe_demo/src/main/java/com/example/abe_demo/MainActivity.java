package com.example.abe_demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.abe_demo.abe_tools.AccessTree;
import com.example.abe_demo.abe_tools.CP_ABE;
import com.example.abe_demo.abe_tools.Node;
import com.example.abe_demo.abe_tools.utils.CodeConvert;
import com.example.abe_demo.home.HomeActivity;
import com.example.abe_demo.show_mode.ShowModeActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;


public class MainActivity extends AppCompatActivity {

    private String ming = "在WEB测试中，表单输入框有两种，一种是数值型文本框，一种是字符型的普通文本输入框。";
    private String mi = "";
    private String ct = "";
    private TextView tv_show_str;
    private TextView tv_show_ct;
    private TextView tv_show_mi;
    private int width = 600;
    private int height = 600;
    private ImageView imageView;
    private Bitmap bit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_show_str = findViewById(R.id.tv_show_ming);
        tv_show_ct = findViewById(R.id.tv_show_ct);
        tv_show_mi = findViewById(R.id.tv_show_mi);

        tv_show_str.setText(ming);
        tv_show_ct.setText("暂无内容");
        tv_show_mi.setText("暂无内容");

        Button btn_run = findViewById(R.id.btn_run);
        imageView = (ImageView) findViewById(R.id.img_QRCode);
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
//                    writeFile("?????????????", "try1.properties");
                    betterTest();
                    tv_show_ct.setText(ct);
                    tv_show_mi.setText(mi);
                    zxing(ct);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button btn_jump = findViewById(R.id.btn_jump);
        btn_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(view.getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }
    private void writeFile(Properties prop, String FileName) {
        try {
            FileOutputStream fos = openFileOutput(FileName, Context.MODE_PRIVATE);
            prop.store(fos, null);
//            Toast.makeText(this, FileName + "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
//            Toast.makeText(this, FileName+"保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void writeFile(String mes, String FileName) {
        try {
            FileOutputStream fos = openFileOutput(FileName, Context.MODE_PRIVATE);
            fos.write(mes.getBytes(StandardCharsets.UTF_8));
//            Toast.makeText(this, FileName + "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
//            Toast.makeText(this, FileName+"保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Properties readFile(String FileName, boolean needProp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Properties prop = new Properties();
            FileInputStream fis = openFileInput(FileName);
            prop.load(fis);
//            Toast.makeText(this, FileName + "读取成功", Toast.LENGTH_SHORT).show();
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
//            Toast.makeText(this, FileName + "读取失败", Toast.LENGTH_SHORT).show();
            return new Properties();
        }
    }

    private String readFile(String FileName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            FileInputStream fis = openFileInput(FileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toString("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
    }

    private  void basicTest() throws Exception {
//        Toast.makeText(this, "进入程序", Toast.LENGTH_SHORT).show();
        int rBits = 20;
        int qBits = 512;
        TypeACurveGenerator pg = new TypeACurveGenerator(rBits, qBits);
//        TypeACurveGenerator pg = new TypeACurveGenerator();

        PairingParameters pp = pg.generate();
        Log.v("log", pp.toString());
        Pairing bp = PairingFactory.getPairing(pp);

        InputStream raw = getResources().openRawResource(R.raw.a);
        PropertiesParameters curveParams = new PropertiesParameters();
        curveParams.load(raw);
        Log.v("log004: curveParams: ", curveParams.toString());
        bp = PairingFactory.getPairing(curveParams);


        // 文件存储路径
        String pkFileName = "pk.properties";
        String mskFileName = "msk.properties";
        String skFileName = "sk.properties";
        String ctFileName1 = "ct1.properties";
        String ctFileName2 = "ct2.properties";

        // 生成椭圆曲线群
//        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);

        // 明文消息
        String mes = "在WEB测试中，表单输入框有两种，一种是数值型文本框，一种是字符型的普通文本输入框。\n";
        Log.v("log", "明文信息:" + mes);

        // 用户拥有的属性表
//        String[] userAttList = {"Hedgehog", "zshw@outlook.com", "13204163804"};
        String[] userAttList = {"121"};

        Node[] nodes = new Node[8];
        nodes[0] = new Node(0, new int[]{1, 2}, new int[]{1, 2});
        nodes[1] = new Node(1, new int[]{3, 4}, new int[]{3, 4, 5, 6});
        nodes[2] = new Node(2, new int[]{1, 1}, new int[]{7});
        nodes[3] = new Node(3, "user");
        nodes[4] = new Node(4, "Hedgehog");
        nodes[5] = new Node(5, "zshw@outlook.com");
        nodes[6] = new Node(6, "13204163804");
        nodes[7] = new Node(7, "121");

        // 初始化访问树
        AccessTree accessTree = new AccessTree(nodes, bp);
        Log.v("log", "初始化访问树成功！");


        // 初始化公共参数
        Map<String, Properties> pkAndMsk = CP_ABE.setup(bp);
        for (String key : pkAndMsk.keySet()) {
            writeFile(Objects.requireNonNull(pkAndMsk.get(key)), key);
        }
        Log.v("log", "初始化公共参数成功！");

        // 读取公共参数
        Properties pkProp =  readFile(pkFileName, true);
        Properties mskProp =  readFile(mskFileName, true);

        Properties sk = CP_ABE.keygen(bp, userAttList, pkProp, mskProp);
        writeFile(Objects.requireNonNull(sk), skFileName);
        Log.v("log", "生成密钥成功！");


        // 处理明文分组
        List<String> messageStringGroup;
        List<String> messageBigNumStringGroup = new LinkedList<>();
        List<Element> messageGroup = new LinkedList<>();
        // 将明文文本分组
        messageStringGroup = CodeConvert.mesToBigNumGroup(mes);
        // 将明文文本组装换为大数字组
        for (String mesBigNum : messageStringGroup) {
            messageGroup.add(bp.getGT().newElement(new BigInteger(mesBigNum)));
        }
        Log.v("log", "明文处理成功！");


        Map<String, Properties> ct1AndCt2 = CP_ABE.encrypt(bp, messageGroup, accessTree, pkProp);
        Log.v("log", "加密成功！");

        Properties test = ct1AndCt2.get(ctFileName2);
        StringBuilder sb = new StringBuilder();
        if (test != null) {
            for(Object key : test.keySet()){
                Object value = test.get(key);
                sb.append((key.toString() + value), 0, (key.toString() + value).length()).append("\n");
            }
            ct = sb.toString();
        }

        for (String key : ct1AndCt2.keySet()) {
            writeFile(Objects.requireNonNull(ct1AndCt2.get(key)), key);
        }
        Log.v("log", "加密密文写入文件成功！");

        Properties ct1Prop =  readFile(ctFileName1, true);
        Properties ct2Prop =  readFile(ctFileName2, true);
        Properties skProp =  readFile(skFileName, true);



        // 解密部分
        List<Element> res = CP_ABE.Decrypt(bp, accessTree, ct1Prop, ct2Prop, skProp);
        assert res != null;
        for (Element bigNum : res) {
//            messageBigNumStringGroup.add(bigNum.toString());
            messageBigNumStringGroup.add(bigNum.toString().substring(1, bigNum.toString().length() - 1).split(",")[0].substring(2));
        }
//        utils.util.BigNumGroupToMes()
        String resString = CodeConvert.BigNumGroupToMes(messageBigNumStringGroup);
        mi = resString;
        Log.v("log", messageBigNumStringGroup.toString());
        Log.v("log", "解密信息:" + resString);

        if (mes.equals(resString)) {
            Log.v("log", "解密成功");
        }
    }

    private  void betterTest() throws Exception{
        // 生成椭圆曲线群
        int rBits = 160;
        int qBits = 512;
        TypeACurveGenerator pg = new TypeACurveGenerator(rBits, qBits);
        PairingParameters pp = pg.generate();
        Log.v("log003", pp.toString());
        Pairing bp = PairingFactory.getPairing(pp);

        InputStream raw = getResources().openRawResource(R.raw.a);
        PropertiesParameters curveParams = new PropertiesParameters();
        curveParams.load(raw);
        Log.v("log004: curveParams: ", curveParams.toString());
        bp = PairingFactory.getPairing(curveParams);

        // 文件存储路径
//        String pkFileName = "pk.properties";
//        String mskFileName = "msk.properties";
//        String skFileName = "sk.properties";
//        String ctFileName1 = "ct1.properties";
//        String ctFileName2 = "ct2.properties";

        String pkFileName = "pk.properties";
        String mskFileName = "msk.properties";
        String skFileName = "sk.properties";
        String ctFileName1 = "ct1.properties";
        String ctFileName2 = "ct2.properties";


        // 明文消息
        String mes = "\tat it.unisa.dia.gas.plaf.jpbc.field.curve.ImmutableCurveElement)";
        System.out.println("明文:" + mes);

        Map<Integer, String> structMes = new HashMap<>();
        structMes.put(1, "test1");
        structMes.put(2, "test2");
        structMes.put(3, "test3");

        // 用户拥有的属性表
//        String[] userAttList = {"Hedgehog", "zshw@outlook.com", "13204163804"};
        String[] userAttList = {"nameAndPhoneAndId1"};

        Node[] nodes = new Node[7];
        nodes[0] = new Node(0, new int[]{1, 2}, new int[]{1, 2}, 1);
        nodes[1] = new Node(1, "idForRoad");
        nodes[2] = new Node(2, new int[]{1, 2}, new int[]{3, 4}, 2);
        nodes[3] = new Node(3, "idForSender");
        nodes[4] = new Node(4, new int[]{1, 2}, new int[]{5, 6}, 3);
        nodes[5] = new Node(5, "InvitorId");
        nodes[6] = new Node(6, "nameAndPhoneAndId");



        AccessTree accessTree = new AccessTree(nodes, bp);

        // 初始化公共参数并写入文件
        Map<String, Properties> pkAndMsk = CP_ABE.setup(bp);
        for (String key : pkAndMsk.keySet()) {
            writeFile(Objects.requireNonNull(pkAndMsk.get(key)), key);
        }
        Log.v("log", "初始化公共参数成功！");

        // 生成用户密钥
        Properties pkProp =  readFile(pkFileName, true);
        Properties mskProp =  readFile(mskFileName, true);

        Properties sk = CP_ABE.keygen(bp, userAttList, pkProp, mskProp);
        writeFile(Objects.requireNonNull(sk), skFileName);
        Log.v("log", "生成密钥成功！");

//        // 处理明文消息
//        List<String> messageStringGroup;
////        StringBuilder messageBigNumStringGroup = new StringBuilder();
        List<String> messageBigNumStringGroup = new LinkedList<>();
//        List<Element> messageGroup = new LinkedList<>();
//        messageStringGroup = CodeConvert.mesToBigNumGroup(mes);
//        for (String mesBigNum : messageStringGroup) {
//            messageGroup.add(bp.getGT().newElement(new BigInteger(mesBigNum)));
//        }


        Map<String, Properties> ct1AndCt2 = CP_ABE.encrypt(bp, structMes, accessTree, pkProp);
        Log.v("log", "加密成功！");

        Properties test = ct1AndCt2.get(ctFileName2);
        StringBuilder sb = new StringBuilder();
        if (test != null) {
            for(Object key : test.keySet()){
                Object value = test.get(key);
                sb.append((key.toString() + value), 0, (key.toString() + value).length()).append("\n");
            }
            ct = sb.toString();
        }

        for (String key : ct1AndCt2.keySet()) {
            writeFile(Objects.requireNonNull(ct1AndCt2.get(key)), key);
        }
        Log.v("log", "加密密文写入文件成功！");

        Properties ct1Prop =  readFile(ctFileName1, true);
        Properties ct2Prop =  readFile(ctFileName2, true);
        Properties skProp =  readFile(skFileName, true);

        System.out.println("ct2Prop"+ct2Prop);

        System.out.println("login_skProp"+skProp);
        System.out.println("login_ct1Prop"+ct1Prop);
        System.out.println("login_ct2Prop"+ct2Prop);
        // 解密部分
        List<Element> res = CP_ABE.Decrypt(bp, accessTree, ct1Prop, ct2Prop, skProp, true);

        System.out.println("login_res："+res);

        if (!(res == null)) {
            for (Element bigNum : res) {
//            messageBigNumStringGroup.add(bigNum.toString());
                messageBigNumStringGroup.add(bigNum.toString().substring(1, bigNum.toString().length() - 1).split(",")[0].substring(2));
            }
//        utils.util.BigNumGroupToMes()
            String resString = CodeConvert.BigNumGroupToMes(messageBigNumStringGroup);
            mi = resString;
            Log.v("log", messageBigNumStringGroup.toString());
            Log.v("log", "解密信息:" + resString);

            if (mes.equals(resString)) {
                Log.v("log", "解密成功");
            }
        }

    }

    private void zxing(String name){
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); //记得要自定义长宽
        BitMatrix encode = null;
        try {
            encode = qrCodeWriter.encode(name, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int[] colors = new int[width * height];
        //利用for循环将要表示的信息写出来
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (encode.get(i, j)) {
                    colors[i * width + j] = Color.BLACK;
                } else {
                    colors[i * width + j] = Color.WHITE;
                }
            }
        }

        bit = Bitmap.createBitmap(colors, width, height, Bitmap.Config.RGB_565);
        imageView.setImageBitmap(bit);
    }
}