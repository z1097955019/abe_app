package com.example.abe_demo.show_mode.fragment;

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
import com.example.abe_demo.abe_tools.utils.CodeConvert;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DecryptActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DecryptActionFragment extends Fragment {

    private Button btn_run_decrypt;
    private TextView tv_show_decrypt_needed_sk;
    private TextView tv_show_decrypt_needed_ct;
    private TextView tv_show_decrypt_ming;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DecryptActionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DecryptActionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DecryptActionFragment newInstance(String param1, String param2) {
        DecryptActionFragment fragment = new DecryptActionFragment();
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
        return inflater.inflate(R.layout.fragment_decrypt_action, container, false);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_run_decrypt = view.findViewById(R.id.btn_run_decrypt);
        tv_show_decrypt_needed_sk = view.findViewById(R.id.tv_show_decrypt_needed_sk);
        tv_show_decrypt_needed_ct = view.findViewById(R.id.tv_show_decrypt_needed_ct);
        tv_show_decrypt_ming =view.findViewById(R.id.tv_show_decrypt_ming);

        btn_run_decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrypt();
            }
        });
    }

    private void decrypt(){

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
//!!!!!!!!!!!!!!!
//        Properties ct1Prop =  readFile("show_"+ctFileName1, true);
//        Properties ct2Prop =  readFile("show_"+ctFileName2, true);
//        Properties skProp =  readFile("show_"+skFileName, true);

        Properties ct1Prop =  new Properties();
        Properties ct2Prop = new Properties();
        Properties skProp =  new Properties();

        // 获取安卓内部存储
        SharedPreferences showCt1SP = requireActivity().getSharedPreferences("show_"+ctFileName1, Context.MODE_PRIVATE);
        SharedPreferences showCt2SP = requireActivity().getSharedPreferences("show_"+ctFileName2, Context.MODE_PRIVATE);
        SharedPreferences showSkSP = requireActivity().getSharedPreferences("show_"+skFileName, Context.MODE_PRIVATE);

        for(String key :showCt1SP.getAll().keySet()){
            if(!showCt1SP.getString(key, "").equals("")){
                ct1Prop.put(key, showCt1SP.getString(key, ""));
            }
        }
        for(String key :showCt2SP.getAll().keySet()){
            if(!showCt2SP.getString(key, "").equals("")){
                ct2Prop.put(key, showCt2SP.getString(key, ""));
            }
        }
        for(String key :showSkSP.getAll().keySet()){
            if(!showSkSP.getString(key, "").equals("")){
                skProp.put(key, showSkSP.getString(key, ""));
            }
        }
//        Properties ct1Prop =  readFile(ctFileName1, true);
//        Properties ct2Prop =  readFile(ctFileName2, true);
//        Properties skProp =  readFile(skFileName, true);

        tv_show_decrypt_needed_sk.setText(skProp.toString());
        StringBuilder sb = new StringBuilder();
        sb.append("密文组件ct1:\n").append(ct1Prop.toString()).append("\n\n").append("密文组件ct2:\n").append(ct2Prop);
        tv_show_decrypt_needed_ct.setText(sb);

        List<String> messageBigNumStringGroup = new LinkedList<>();

//         解密部分
        System.out.println("login_skProp"+skProp);
        System.out.println("login_ct1Prop"+ct1Prop);
        System.out.println("login_ct2Prop"+ct2Prop);
        List<Element> res = CP_ABE.Decrypt(bp, accessTree, ct1Prop, ct2Prop, skProp, true);
        System.out.println("res："+res);
        if (!(res == null)) {
            for (Element bigNum : res) {
//            messageBigNumStringGroup.add(bigNum.toString());
                messageBigNumStringGroup.add(bigNum.toString().substring(1, bigNum.toString().length() - 1).split(",")[0].substring(2));
            }
//        utils.util.BigNumGroupToMes()
            String resString = CodeConvert.BigNumGroupToMes(messageBigNumStringGroup);
            Log.v("log", messageBigNumStringGroup.toString());
            Log.v("log", "解密信息:" + resString);

            tv_show_decrypt_ming.setText(resString);

            if (mes.equals(resString)) {
                Toast.makeText(getActivity(), "解密成功",Toast.LENGTH_SHORT).show();
            }
        }

    }
}