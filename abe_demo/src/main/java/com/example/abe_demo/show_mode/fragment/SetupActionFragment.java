package com.example.abe_demo.show_mode.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.abe_demo.abe_tools.CP_ABE;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetupActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupActionFragment extends Fragment {

    // 文件存储路径
    private final String pkFileName = "pk.properties";
    private final String mskFileName = "msk.properties";
    private final String skFileName = "sk.properties";
    private final String ctFileName1 = "ct1.properties";
    private final String ctFileName2 = "ct2.properties";

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

        // 初始化原始数据
        initData();
    }

    private void initData() {
        // 初始化pk, msk
        Properties pkProp = getData("show_" + pkFileName);
        Properties mskProp = getData("show_" + mskFileName);

        if (!pkProp.isEmpty()) {
            tv_pk.setText(pkProp.toString());
        }
        if (!mskProp.isEmpty()) {
            tv_msk.setText(mskProp.toString());
        }

    }


    private void setup() {
        // 生成椭圆曲线群
        Pairing bp = initBp();

        // 生成参数
        Map<String, Properties> pkAndMsk = CP_ABE.setup(bp);

        // 写入文件
        try {
            for (String key : pkAndMsk.keySet()) {
                if (pkAndMsk.get(key) != null) {
                    if (recordData(pkAndMsk.get(key), "show_" + key)) {
                        Toast.makeText(getActivity(), "初始化公共参数成功！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "初始化公共参数失败！", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            initData();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "初始化公共参数失败！", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean recordData(Properties temPro, String SPName) {
        try {
            SharedPreferences abe_show = requireActivity().getSharedPreferences(SPName, Context.MODE_PRIVATE);
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
        InputStream raw = getResources().openRawResource(R.raw.a);
        PropertiesParameters curveParams = new PropertiesParameters();
        curveParams.load(raw);
//        Log.v("log004: curveParams: ", curveParams.toString());
        return PairingFactory.getPairing(curveParams);
    }

    private Properties getData(String SPName) {
        SharedPreferences SP = requireActivity().getSharedPreferences(SPName, Context.MODE_PRIVATE);
        Properties prop = new Properties();
        for (String key : SP.getAll().keySet()) {
            if (!SP.getString(key, "").equals("")) {
                prop.put(key, SP.getString(key, ""));
            }
        }
        return prop;
    }
}