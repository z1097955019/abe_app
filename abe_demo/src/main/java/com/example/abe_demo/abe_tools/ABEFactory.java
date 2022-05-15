package com.example.abe_demo.abe_tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.abe_demo.R;
import com.example.abe_demo.abe_tools.utils.CodeConvert;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;

public class ABEFactory {

    private final Context context;

    // 文件存储路径
    private final String pkFileName = "pk.properties";
    private final String mskFileName = "msk.properties";
    private final String skFileName = "sk.properties";
    private final String ctFileName1 = "ct1.properties";
    private final String ctFileName2 = "ct2.properties";
    private final String mingFileName = "ming.properties";
    private final String ming_before = "clearTB.properties";

    private final String defaultPkFileName = "show_default_" + pkFileName;
    private final String defaultMskFileName = "show_default_" + mskFileName;
    private final String defaultSkFileName = "show_default_" + mskFileName;
    private final String defaultCtFileName1 = "show_default_" + mskFileName;
    private final String defaultCtFileName2 = "show_default_" + mskFileName;
    private final String defaultMingFileName = "show_default_" + mskFileName;
    private final String defaultMing_before = "show_default_" + mskFileName;

    public ABEFactory(Context context) {
        this.context = context;
    }

    public void setup() {
        // 生成椭圆曲线群
        Pairing bp = initBp();

        // 生成参数
        Map<String, Properties> pkAndMsk = CP_ABE.setup(bp);

        // 写入文件
        try {
            for (String key : pkAndMsk.keySet()) {
                if (pkAndMsk.get(key) != null) {
                    if (recordData(pkAndMsk.get(key), "show_" + key)) {
                        Toast.makeText(context, "初始化公共参数成功！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "初始化公共参数失败！", Toast.LENGTH_SHORT).show();
                    break;
                }
            }

        } catch (Exception e) {
            Toast.makeText(context, "初始化公共参数失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public void keygen(boolean isDefault) {

        Properties defaultPk = readFile(R.raw.pk);
        Properties defaultMsk = readFile(R.raw.msk);
        System.out.println("log012: defaultPk " + defaultPk);
        System.out.println("log012: defaultMsk " + defaultMsk);
        if (recordData(defaultPk, "show_" + pkFileName) && recordData(defaultMsk, "show_" + mskFileName)) {
            try {
                keygen(pkFileName, mskFileName, skFileName);
            } catch (Exception e) {
                System.out.println("log012: e " + e);

            }
        }
    }

    public void keygen() {
        keygen(pkFileName, mskFileName, skFileName);
    }

    public void keygen(String pkFileName, String mskFileName, String skFileName) {
        // 生成椭圆曲线群
        Pairing bp = initBp();
        System.out.println("log012: 生成椭圆曲线群 ");

        // 初始化相关参数
        Properties pkProp = getData("show_" + pkFileName);
        Properties mskProp = getData("show_" + mskFileName);

        System.out.println("log012: 初始化相关参数 ");


        // 获取用户相关信息
        SharedPreferences personal_mes = context.getSharedPreferences("personal_mes", Context.MODE_PRIVATE);
        System.out.println("log012: 获取用户相关信息 ");

        // 用户拥有的属性表
        String[] userAttList = {personal_mes.getString("nameAndPhoneAndId", "")};

        System.out.println("log012: 用户拥有的属性表 ");

        // 根据用户属性生成sk
        Properties sk = new Properties();
        try {
            System.out.println("log012: " + Arrays.toString(userAttList) + pkProp.toString() + mskProp.toString());
            sk = CP_ABE.keygen(bp, userAttList, pkProp, mskProp);
        } catch (Exception e) {
            System.out.println("log012: e2 " + e);
        }

        // 展示
//        tv_show_keygen_sk.setText(sk.toString());

        // 存入sp
        if (recordData(sk, "show_" + skFileName)) {
            Toast.makeText(context, "生成密钥成功！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "生成密钥失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public void encrypt() {
        encrypt(pkFileName, ming_before);
    }

    public void encrypt(boolean isDefault) {
        encrypt(defaultPkFileName, defaultMing_before);
    }

    public void encrypt(String pkFileName, String ming_before) {
        // 生成椭圆曲线群
        Pairing bp = initBp();

        // 初始化pk
        Properties pkProp = getData("show_" + pkFileName);

        // 结构化信息存储
//        Map<Integer, String> structMes = new HashMap<>();
//        structMes.put(1, "test1");
//        structMes.put(2, "test2");
//        structMes.put(3, "test3");
        Map<Integer, String> structMes = getClearTextFromSP("show_" + ming_before);
        if (structMes.isEmpty()) {
            structMes.put(1, "test1");
            structMes.put(2, "test2");
            structMes.put(3, "test3");
        }
        System.out.println("log008" + structMes);

        // 访问树结构
        Node[] nodes = new Node[7];
        nodes[0] = new Node(0, new int[]{1, 2}, new int[]{1, 2}, 1);
        nodes[1] = new Node(1, "idForRoad");
        nodes[2] = new Node(2, new int[]{1, 2}, new int[]{3, 4}, 2);
        nodes[3] = new Node(3, "idForSender");
        nodes[4] = new Node(4, new int[]{1, 2}, new int[]{5, 6}, 3);
        nodes[5] = new Node(5, "InvitorId");
        nodes[6] = new Node(6, context.getSharedPreferences("personal_mes", Context.MODE_PRIVATE).getString("nameAndPhoneAndId", ""));

        AccessTree accessTree = new AccessTree(nodes, bp);

        // 生成加密密文
        Map<String, Properties> ct1AndCt2 = new HashMap<>();
        try {
            ct1AndCt2 = CP_ABE.encrypt(bp, structMes, accessTree, pkProp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // 写入ct并展示
        StringBuilder ctb = new StringBuilder();
        List<String> ctList = dealCts(ct1AndCt2);
        for (String ctItem : ctList) {
            ctb.append(ctItem).append("\n\n");
        }
//        imageView.setImageBitmap(zxing(ctList.get(1)));


        if (!ctb.toString().equals("")) {
//            tv_show_encrypt_ct.setText(ctb.toString());
            Toast.makeText(context, "加密密文生成成功！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "加密密文生成失败！", Toast.LENGTH_SHORT).show();
        }
    }

    public String decrypt(String ct2, boolean getCt2FromStr) {
        return decrypt(ctFileName1, ct2, skFileName, mingFileName, !getCt2FromStr);
    }

    public String decrypt() {
        return decrypt(ctFileName1, ctFileName2, skFileName, mingFileName, true);
    }

    public String decrypt(String ctFileName1, String ct2, String skFileName, String mingFileName, boolean ct2IsName) {
        Properties ct2Prop;
        if (ct2IsName) {
            ct2Prop = getData("show_" + ctFileName2);
        } else {
            ct2Prop = load(ct2);
        }
        return decrypt(ctFileName1, ct2Prop, skFileName, mingFileName);
    }

    public String decrypt(String ctFileName1, Properties ct2Prop, String skFileName, String mingFileName) {
        // 生成椭圆曲线群
        Pairing bp = initBp();

        // 初始化数据
        Properties ct1Prop = getData("show_" + ctFileName1);
//        Properties ct2Prop = getData("show_" + ctFileName2);
        Properties skProp = getData("show_" + skFileName);

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
        nodes[6] = new Node(6, context.getSharedPreferences("personal_mes", Context.MODE_PRIVATE).getString("nameAndPhoneAndId", ""));

        AccessTree accessTree = new AccessTree(nodes, bp);

        // 存储解密信息
        List<String> messageBigNumStringGroup = new LinkedList<>();

        System.out.println("log013: !!!" + ct1Prop.toString() + "-=-=-=-" + ct2Prop.toString() + "-=-=-=-" + skProp.toString());

        List<Element> res = null;
        // 解密部分
        try {
            res = CP_ABE.Decrypt(bp, accessTree, ct1Prop, ct2Prop, skProp, true);
        } catch (Exception e) {
            System.out.println("log013: e2" + e);
        }

        // 转译展示解密的明文
        if (!(res == null)) {
            for (Element bigNum : res) {
                messageBigNumStringGroup.add(bigNum.toString().substring(1, bigNum.toString().length() - 1).split(",")[0].substring(2));
            }
            String resString = CodeConvert.BigNumGroupToMes(messageBigNumStringGroup);

            // 存储
            Properties clearTextProp = new Properties();
            clearTextProp.put("clearText", resString);
            if (recordData(clearTextProp, "show_" + mingFileName)) {
                // 展示
//                tv_show_decrypt_ming.setText(resString);
                Toast.makeText(context, "解密成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "解密失败", Toast.LENGTH_SHORT).show();
            }
            return resString;
        }else {
            return "";
        }
    }

    // 从指定sp中获取结构化的明文
    private Map<Integer, String> getClearTextFromSP(String SPName) {
        SharedPreferences SP = context.getSharedPreferences(SPName, Context.MODE_PRIVATE);
        Map<Integer, String> clearText = new HashMap<>();
        for (String key : SP.getAll().keySet()) {
            if (!SP.getString(key, "").equals("")) {
                clearText.put(Integer.valueOf(key), SP.getString(key, ""));
            }
        }
        return clearText;
    }

    // 处理加密算法回传的ct属性对象，将其编码为字符串列表
    public List<String> dealCts(Map<String, Properties> ct1AndCt2) {
        List<String> cts = new LinkedList<>();
        try {
            StringBuilder sb_in_encrypt = new StringBuilder();
            for (String key : ct1AndCt2.keySet()) {
                if (ct1AndCt2.get(key) != null) {
                    if (recordData(ct1AndCt2.get(key), "show_" + key)) {
                        cts.add(propToString(ct1AndCt2.get(key)));
                        sb_in_encrypt.append("密文组件").append(key.substring(0, 3)).append(ct1AndCt2.get(key).toString());

                    }
                } else {
                    break;
                }
            }
            System.out.println("log013: " + sb_in_encrypt);
            return cts;
        } catch (Exception e) {
            return cts;
        }
    }

    // 将prop对象转换成指定格式字符串
    private String propToString(Properties properties) {
        StringBuilder sb = new StringBuilder();
        for (Object key : properties.keySet()) {
            sb.append(key.toString()).append("=").append(properties.get(key)).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

    // 把property中数据存入指定sp中
    public boolean recordData(Properties temPro, String SPName) {
        try {
            SharedPreferences abe_show = context.getSharedPreferences(SPName, Context.MODE_PRIVATE);
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

    // 从文件初始化椭圆曲线域参数
    public Pairing initBp() {
        // 生成椭圆曲线群
        InputStream raw = context.getResources().openRawResource(R.raw.a);
        PropertiesParameters curveParams = new PropertiesParameters();
        curveParams.load(raw);
        return PairingFactory.getPairing(curveParams);
    }

    // 从指定sp中获取全部数据，以property形式回传
    public Properties getData(String SPName) {
        SharedPreferences SP = context.getSharedPreferences(SPName, Context.MODE_PRIVATE);
        Properties prop = new Properties();
        for (String key : SP.getAll().keySet()) {
            if (!SP.getString(key, "").equals("")) {
                prop.put(key, SP.getString(key, ""));
            }
        }
        return prop;
    }

    private void writeFile(Properties prop, String FileName) {
        try {
            FileOutputStream fos = context.openFileOutput(FileName, Context.MODE_PRIVATE);
            prop.store(fos, null);
//            Toast.makeText(this, FileName + "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
//            Toast.makeText(this, FileName+"保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // 一堆文件存取操作
    private void writeFile(String mes, String FileName) {
        try {
            FileOutputStream fos = context.openFileOutput(FileName, Context.MODE_PRIVATE);
            fos.write(mes.getBytes(StandardCharsets.UTF_8));
//            Toast.makeText(this, FileName + "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
//            Toast.makeText(this, FileName+"保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Properties readFile(int id) {
        try {
            Properties prop = new Properties();
            InputStream raw = context.getResources().openRawResource(id);
            prop.load(raw);
//            Toast.makeText(this, FileName + "读取成功", Toast.LENGTH_SHORT).show();
            return prop;
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("log012: " + e);
//            Toast.makeText(this, FileName + "读取失败", Toast.LENGTH_SHORT).show();
            return new Properties();
        }
    }

    private String readFile(String FileName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            FileInputStream fis = context.openFileInput(FileName);
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

    // 从固定编码的字符串中加载prop属性对象
    public Properties load(String propertiesString) {
        Properties properties = new Properties();
        try {
            String[] split = propertiesString.split(",");
            System.out.println("log010: " + Arrays.toString(split));
            for (String stringItem : split) {
                String[] singleStringKeyValue = stringItem.split("=");
                System.out.println("log010: singleStringKeyValue: " + Arrays.toString(singleStringKeyValue) + "\n\n");
                if (singleStringKeyValue != null) {
                    properties.put(singleStringKeyValue[0], singleStringKeyValue[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("log010: " + "?????");
        }
        return properties;
    }
}
