package com.example.abe_demo.show_mode.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.abe_demo.R;
import com.example.abe_demo.abe_tools.AccessTree;
import com.example.abe_demo.abe_tools.CP_ABE;
import com.example.abe_demo.abe_tools.Node;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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
 * Use the {@link SetupActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupActionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView tv_pk;
    private TextView tv_msk;
    private Button btn_run;

    public SetupActionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetupActionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetupActionFragment newInstance(String param1, String param2) {
        SetupActionFragment fragment = new SetupActionFragment();
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
        return inflater.inflate(R.layout.fragment_setup_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_pk = view.findViewById(R.id.tv_show_setup_pk);
        tv_msk = view.findViewById(R.id.tv_show_setup_msk);
        btn_run = view.findViewById(R.id.btn_run_setup);
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setup();
            }
        });
    }


    private void writeFile(Properties prop, String FileName) {
        try {
            FileOutputStream fos = requireActivity().openFileOutput(FileName, Context.MODE_PRIVATE);
//            System.out.println("prop"+prop);
            prop.store(fos, null);
//            Toast.makeText(getActivity(), FileName + "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
//            Toast.makeText(getActivity(), FileName+"保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Properties readFile(String FileName, boolean needProp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
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

    private void setup() {

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
        String[] userAttList = {"nameAndPhoneAndId"};

        Node[] nodes = new Node[7];
        nodes[0] = new Node(0, new int[]{1, 2}, new int[]{1, 2}, 1);
        nodes[1] = new Node(1, "idForRoad");
        nodes[2] = new Node(2, new int[]{1, 2}, new int[]{3, 4}, 2);
        nodes[3] = new Node(3, "idForSender");
        nodes[4] = new Node(4, new int[]{2, 3}, new int[]{5, 6}, 3);
        nodes[5] = new Node(5, "InvitorId");
        nodes[6] = new Node(6, "nameAndPhoneAndId");



        AccessTree accessTree = new AccessTree(nodes, bp);
        Map<String, Properties> pkAndMsk = CP_ABE.setup(bp);

        tv_pk.setText(Objects.requireNonNull(pkAndMsk.get(pkFileName)).toString());
        tv_msk.setText(Objects.requireNonNull(pkAndMsk.get(mskFileName)).toString());

        // 初始化公共参数并写入文件
        for (String key : pkAndMsk.keySet()) {
//            !!!!!!!!!!!!!!!!
            writeFile(Objects.requireNonNull(pkAndMsk.get(key)), "show_"+key);
//            writeFile(Objects.requireNonNull(pkAndMsk.get(key)), key);
        }
        Toast.makeText(getActivity(),"初始化公共参数成功！",Toast.LENGTH_SHORT).show();
    }
}