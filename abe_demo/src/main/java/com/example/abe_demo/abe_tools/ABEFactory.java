package com.example.abe_demo.abe_tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.abe_demo.R;
import com.example.abe_demo.abe_tools.utils.CodeConvert;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
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

    public void keygen() {
        keygen(pkFileName, mskFileName, skFileName);
    }

    public void keygen(String pkFileName, String mskFileName, String skFileName) {
        // 生成椭圆曲线群
        Pairing bp = initBp();

        // 初始化相关参数
        Properties pkProp = getData("show_" + pkFileName);
        Properties mskProp = getData("show_" + mskFileName);

        // 获取用户相关信息
        SharedPreferences personal_mes = context.getSharedPreferences("personal_mes", Context.MODE_PRIVATE);

        // 用户拥有的属性表
        String[] userAttList = {personal_mes.getString("nameAndPhoneAndId", "")};


        // 根据用户属性生成sk
        Properties sk = new Properties();
        try {
            sk = CP_ABE.keygen(bp, userAttList, pkProp, mskProp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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

    public void decrypt() {
        decrypt(ctFileName1, ctFileName2, skFileName, mingFileName);
    }

    public void decrypt(String ctFileName1, String ctFileName2, String skFileName, String mingFileName) {
        // 生成椭圆曲线群
        Pairing bp = initBp();

        // 初始化数据
        Properties ct1Prop = getData("show_" + ctFileName1);
        Properties ct2Prop = getData("show_" + ctFileName2);
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

        // 解密部分
        List<Element> res = CP_ABE.Decrypt(bp, accessTree, ct1Prop, ct2Prop, skProp, true);

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
        }
    }

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

    public List<String> dealCts(Map<String, Properties> ct1AndCt2) {
        List<String> cts = new LinkedList<>();
        try {
            StringBuilder sb_in_encrypt = new StringBuilder();
            for (String key : ct1AndCt2.keySet()) {
                if (ct1AndCt2.get(key) != null) {
                    if (recordData(ct1AndCt2.get(key), "show_" + key)) {
                        cts.add(propToString(ct1AndCt2.get(key)));
                        sb_in_encrypt.append("密文组件").append(key.substring(0, 3)).append("：\n").append(ct1AndCt2.get(key).toString()).append("\n\n");
                    }
                } else {
                    break;
                }
            }
            return cts;
        } catch (Exception e) {
            return cts;
        }
    }

    private String propToString(Properties properties) {
        StringBuilder sb = new StringBuilder();
        for (Object key : properties.keySet()) {
            sb.append(key.toString()).append("=").append(properties.get(key)).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

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

    private Pairing initBp() {
        // 生成椭圆曲线群
        InputStream raw = context.getResources().openRawResource(R.raw.a);
        PropertiesParameters curveParams = new PropertiesParameters();
        curveParams.load(raw);
//        Log.v("log004: curveParams: ", curveParams.toString());
        return PairingFactory.getPairing(curveParams);
    }

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
}
