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
import android.widget.Toast;

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

    private void encrypt(){
        // 生成椭圆曲线群
        Pairing bp = initBp();

        // 文件存储路径
        String pkFileName = "pk.properties";

        // 结构化信息存储
        Map<Integer, String> structMes = new HashMap<>();
        structMes.put(1, "test1");
        structMes.put(2, "test2");
        structMes.put(3, "test3");

        // 访问树结构
        Node[] nodes = new Node[7];
        nodes[0] = new Node(0, new int[]{1, 2}, new int[]{1, 2}, 1);
        nodes[1] = new Node(1, "idForRoad");
        nodes[2] = new Node(2, new int[]{1, 2}, new int[]{3, 4}, 2);
        nodes[3] = new Node(3, "idForSender");
        nodes[4] = new Node(4, new int[]{1, 2}, new int[]{5, 6}, 3);
        nodes[5] = new Node(5, "InvitorId");
        nodes[6] = new Node(6, "name123id");

        AccessTree accessTree = new AccessTree(nodes, bp);

        // 获取公钥
        Properties pkProp = getData("show_" + pkFileName);

        // 展示公钥
        tv_show_encrypt_needed_pk.setText(pkProp.toString());

        // 生成加密密文
        Map<String, Properties> ct1AndCt2 = new HashMap<>();
        try {
            ct1AndCt2 = CP_ABE.encrypt(bp, structMes, accessTree, pkProp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // 写入sp并展示
        try {
            StringBuilder sb_in_encrypt = new StringBuilder();
            for (String key : ct1AndCt2.keySet()) {
                if( ct1AndCt2.get(key) !=null){
                    if(recordData(ct1AndCt2.get(key),"show_"+key)){
                        sb_in_encrypt.append("密文组件").append(key.substring(0,3)).append("：\n").append(ct1AndCt2.get(key).toString()).append("\n\n");
                    }
                }else{
                    break;
                }
            }
            // 设置展示
            tv_show_encrypt_ct.setText(sb_in_encrypt);
            Toast.makeText(getActivity(),"加密密文生成成功！",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getActivity(),"加密密文生成失败！",Toast.LENGTH_SHORT).show();
        }

    }

    private Properties getData(String SPName) {
        SharedPreferences SP = requireActivity().getSharedPreferences(SPName, Context.MODE_PRIVATE);
        Properties prop =new Properties() ;
        for(String key :SP.getAll().keySet()){
            if(!SP.getString(key, "").equals("")){
                prop.put(key, SP.getString(key, ""));
            }
        }
        return prop;
    }

    private boolean recordData(Properties temPro, String SPName) {
        try {
            SharedPreferences abe_show = requireActivity().getSharedPreferences(SPName, Context.MODE_PRIVATE);
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = abe_show.edit();
            for (String property_key : temPro.stringPropertyNames()){
                editor.putString(property_key, temPro.getProperty(property_key));
            }
            editor.apply();
            return true;
        }catch(Exception e){
            return false;
        }
    }

    private Pairing initBp() {
        // 生成椭圆曲线群
        InputStream raw = getResources().openRawResource(R.raw.a);
        PropertiesParameters curveParams = new PropertiesParameters();
        curveParams.load(raw);
//        Log.v("log004: curveParams: ", curveParams.toString());
        return PairingFactory.getPairing(curveParams);
    }
}