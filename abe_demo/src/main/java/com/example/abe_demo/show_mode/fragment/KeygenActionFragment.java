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
import com.example.abe_demo.abe_tools.ABEFactory;
import com.example.abe_demo.abe_tools.CP_ABE;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KeygenActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeygenActionFragment extends Fragment {

    private Button btn_run_keygen;
    private Button btn_run_keygen_default;
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
    private Properties pkProp;
    private Properties mskProp;

    // 文件存储路径
    private final String pkFileName = "pk.properties";
    private final String mskFileName = "msk.properties";
    private final String skFileName = "sk.properties";
    private final String ctFileName1 = "ct1.properties";
    private final String ctFileName2 = "ct2.properties";
    private ABEFactory abeFactory;


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

        abeFactory = new ABEFactory(requireActivity());

        btn_run_keygen = view.findViewById(R.id.btn_run_keygen);
        btn_run_keygen_default = view.findViewById(R.id.btn_run_keygen_default);
        tv_show_keygen_needed_pk = view.findViewById(R.id.tv_show_keygen_needed_pk);
        tv_show_keygen_needed_msk = view.findViewById(R.id.tv_show_keygen_needed_msk);
        tv_show_keygen_sk = view.findViewById(R.id.tv_show_keygen_sk);

        btn_run_keygen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abeFactory.keygen();
                initData();
            }
        });

        btn_run_keygen_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abeFactory.keygen(true);
                initData();
            }
        });

         //初始化原始数据
        initData();
    }

    private void initData() {
        System.out.println("log012: 这里初始化keygen的数据了");
        // 初始化pk, msk
        pkProp = abeFactory.getData("show_" + pkFileName);
        mskProp = abeFactory.getData("show_" + mskFileName);
        Properties sk = abeFactory.getData("show_" + skFileName);
        if (!pkProp.isEmpty()) {
            tv_show_keygen_needed_pk.setText(pkProp.toString());
        }
        if (!mskProp.isEmpty()) {
            tv_show_keygen_needed_msk.setText(mskProp.toString());
        }
        if (!sk.isEmpty()) {
            tv_show_keygen_sk.setText(sk.toString());
        }
    }
}