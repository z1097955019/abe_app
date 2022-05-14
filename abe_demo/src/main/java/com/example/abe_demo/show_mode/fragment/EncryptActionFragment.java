package com.example.abe_demo.show_mode.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.abe_demo.R;
import com.example.abe_demo.abe_tools.AccessTree;
import com.example.abe_demo.abe_tools.CP_ABE;
import com.example.abe_demo.abe_tools.Node;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EncryptActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EncryptActionFragment extends Fragment {

    private Button btn_run_encrypt;
    private TextView tv_show_encrypt_needed_pk;
    private TextView tv_show_encrypt_ct;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EncryptActionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EncryptActionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EncryptActionFragment newInstance(String param1, String param2) {
        EncryptActionFragment fragment = new EncryptActionFragment();
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
        return inflater.inflate(R.layout.fragment_encrypt_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_run_encrypt = view.findViewById(R.id.btn_run_encrypt);
        tv_show_encrypt_needed_pk = view.findViewById(R.id.tv_show_encrypt_needed_pk);
        tv_show_encrypt_ct = view.findViewById(R.id.tv_show_encrypt_ct);

        btn_run_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encrypt();
            }
        });
    }

    private void writeFile(Properties prop, String FileName) {
        try {
            FileOutputStream fos = requireActivity().openFileOutput(FileName, Context.MODE_PRIVATE);
            prop.store(fos, null);
//            Toast.makeText(this, FileName + "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
//            Toast.makeText(this, FileName+"保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Properties readFile(String FileName, boolean needProp) {
        try {
            Properties prop = new Properties();
            FileInputStream fis = requireActivity().openFileInput(FileName);
            prop.load(fis);
//            Toast.makeText(this, FileName + "读取成功", Toast.LENGTH_SHORT).show();
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
//            Toast.makeText(this, FileName + "读取失败", Toast.LENGTH_SHORT).show();
            return new Properties();
        }
    }

    private void encrypt(){


        // 生成椭圆曲线群
        InputStream raw = getResources().openRawResource(R.raw.a);
        PropertiesParameters curveParams = new PropertiesParameters();
        curveParams.load(raw);
        Log.v("log004: curveParams: ", curveParams.toString());
        Pairing bp = PairingFactory.getPairing(curveParams);

        // 文件存储路径
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
        String[] userAttList = {"name123id"};

        Node[] nodes = new Node[7];
        nodes[0] = new Node(0, new int[]{1, 2}, new int[]{1, 2}, 1);
        nodes[1] = new Node(1, "idForRoad");
        nodes[2] = new Node(2, new int[]{1, 2}, new int[]{3, 4}, 2);
        nodes[3] = new Node(3, "idForSender");
        nodes[4] = new Node(4, new int[]{1, 2}, new int[]{5, 6}, 3);
        nodes[5] = new Node(5, "InvitorId");
        nodes[6] = new Node(6, "name123id");



        AccessTree accessTree = new AccessTree(nodes, bp);

        // 获取安卓内部存储
        SharedPreferences showPkSP = requireActivity().getSharedPreferences("show_" + pkFileName, Context.MODE_PRIVATE);



//!!!!!!!!!!!!!!!!!
        Properties pkProp =  new Properties();

        for(String key :showPkSP.getAll().keySet()){
            if(showPkSP.getAll().get(key) != null){
                pkProp.put(key, showPkSP.getString(key, ""));
            }
        }

        tv_show_encrypt_needed_pk.setText(pkProp.toString());


        //        // 处理明文消息
//        List<String> messageStringGroup;
////        StringBuilder messageBigNumStringGroup = new StringBuilder();
        List<String> messageBigNumStringGroup = new LinkedList<>();
//        List<Element> messageGroup = new LinkedList<>();
//        messageStringGroup = CodeConvert.mesToBigNumGroup(mes);
//        for (String mesBigNum : messageStringGroup) {
//            messageGroup.add(bp.getGT().newElement(new BigInteger(mesBigNum)));
//        }


        Map<String, Properties> ct1AndCt2 = null;
        try {
            ct1AndCt2 = CP_ABE.encrypt(bp, structMes, accessTree, pkProp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Log.v("log", "加密成功！");

        Properties test = ct1AndCt2.get(ctFileName2);
        StringBuilder sb = new StringBuilder();
        if (test != null) {
            for(Object key : test.keySet()){
                Object value = test.get(key);
                sb.append((key.toString() + value), 0, (key.toString() + value).length()).append("\n");
            }
        }

        StringBuilder sb_in_encrypt = new StringBuilder();
        for (String key : ct1AndCt2.keySet()) {
            if( ct1AndCt2.get(key) !=null){
                Properties temPro = ct1AndCt2.get(key);
                SharedPreferences abe_show = requireActivity().getSharedPreferences("show_"+key, Context.MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = abe_show.edit();
                for (String property_key : temPro.stringPropertyNames()){
                    editor.putString(property_key, temPro.getProperty(property_key));
                }
                editor.apply();
            }else{
                break;
            }

//            !!!!!!!!!!!!!!!
            writeFile(Objects.requireNonNull(ct1AndCt2.get(key)), "show_"+key);
//            writeFile(Objects.requireNonNull(ct1AndCt2.get(key)), key);
            sb_in_encrypt.append("密文组件").append(key.substring(0,3)).append("：\n").append(ct1AndCt2.get(key).toString()).append("\n\n");
        }

        tv_show_encrypt_ct.setText(sb_in_encrypt);
        Log.v("log", "加密密文写入文件成功！");
    }
}