package com.example.abe_demo.show_mode.fragment;

import android.content.Context;
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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
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
 * Use the {@link KeygenActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeygenActionFragment extends Fragment {

    private Button btn_run_keygen;
    private TextView tv_show_keygen_needed_pk;
    private TextView tv_show_keygen_needed_msk;
    private TextView tv_show_keygen_sk;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public KeygenActionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KeygenActionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KeygenActionFragment newInstance(String param1, String param2) {
        KeygenActionFragment fragment = new KeygenActionFragment();
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
        return inflater.inflate(R.layout.fragment_keygen_action, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_run_keygen = view.findViewById(R.id.btn_run_keygen);
        tv_show_keygen_needed_pk = view.findViewById(R.id.tv_show_keygen_needed_pk);
        tv_show_keygen_needed_msk = view.findViewById(R.id.tv_show_keygen_needed_msk);
        tv_show_keygen_sk =view.findViewById(R.id.tv_show_keygen_sk);

        btn_run_keygen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keygen();
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

    private void keygen(){
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

        // 用户拥有的属性表
        String[] userAttList = {"nameAndPhoneAndId"};


        // 生成用户密钥
//        !!!!!!!!!!
        Properties pkProp =  readFile("show_"+pkFileName, true);
        Properties mskProp =  readFile("show_"+mskFileName, true);
//        Properties pkProp =  readFile(pkFileName, true);
//        Properties mskProp =  readFile(mskFileName, true);
        tv_show_keygen_needed_pk.setText(pkProp.toString());
        tv_show_keygen_needed_msk.setText(mskProp.toString());

        Properties sk = null;
        try {
            sk = CP_ABE.keygen(bp, userAttList, pkProp, mskProp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//        !!!!!!!!!!!!!!!
        writeFile(Objects.requireNonNull(sk), "show_"+skFileName);
//        writeFile(Objects.requireNonNull(sk), skFileName);

        tv_show_keygen_sk.setText(sk.toString());

        Log.v("log", "生成密钥成功！");
        Toast.makeText(getActivity(), "生成密钥成功！", Toast.LENGTH_SHORT).show();
    }
}