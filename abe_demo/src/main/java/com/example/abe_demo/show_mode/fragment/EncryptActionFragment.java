package com.example.abe_demo.show_mode.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.abe_demo.R;
import com.example.abe_demo.abe_tools.ABEFactory;
import com.example.abe_demo.abe_tools.AccessTree;
import com.example.abe_demo.abe_tools.CP_ABE;
import com.example.abe_demo.abe_tools.Node;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EncryptActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EncryptActionFragment extends Fragment {

    // 文件存储路径
    private final String pkFileName = "pk.properties";
    private final String mskFileName = "msk.properties";
    private final String skFileName = "sk.properties";
    private final String ctFileName1 = "ct1.properties";
    private final String ctFileName2 = "ct2.properties";
    private final String ming_before = "clearTB.properties";

    private Button btn_run_encrypt;
    private Button btn_run_encrypt_default;
    private TextView tv_show_encrypt_needed_pk;
    private TextView tv_show_encrypt_ct;
    private TextInputEditText clt1_edt;
    private TextInputEditText clt2_edt;
    private TextInputEditText clt3_edt;
    private Button btn_save_clear_text;
    private ImageView imageView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Properties pkProp;
    private ABEFactory abeFactory;

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

        abeFactory = new ABEFactory(requireActivity());

        btn_run_encrypt = view.findViewById(R.id.btn_run_encrypt);
        btn_run_encrypt_default = view.findViewById(R.id.btn_run_encrypt_default);
        tv_show_encrypt_needed_pk = view.findViewById(R.id.tv_show_encrypt_needed_pk);
        tv_show_encrypt_ct = view.findViewById(R.id.tv_show_encrypt_ct);
        clt1_edt = view.findViewById(R.id.clt1_edt);
        clt2_edt = view.findViewById(R.id.clt2_edt);
        clt3_edt = view.findViewById(R.id.clt3_edt);
        btn_save_clear_text = view.findViewById(R.id.btn_save_clear_text);
        imageView = view.findViewById(R.id.show_qr_code_img);

        btn_run_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abeFactory.encrypt();
                initData();
            }
        });

        btn_run_encrypt_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abeFactory.encrypt();
                initData();
            }
        });

        btn_save_clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = requireActivity().getSharedPreferences("show_" + ming_before, Context.MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sp.edit();
                editor.putString("1", clt1_edt.getText().toString());
                editor.putString("2", clt2_edt.getText().toString());
                editor.putString("3", clt3_edt.getText().toString());
                editor.apply();
                Toast.makeText(requireActivity().getBaseContext(), "所需加密明文更改成功，请重新运行加密算法", Toast.LENGTH_SHORT).show();
            }
        });

        initData();
    }


    private void initData() {
        System.out.println("log012: 这里初始化encrypt的数据了");
        // 初始化pk, msk
        pkProp = abeFactory.getData("show_" + pkFileName);
        if (!pkProp.isEmpty()) {
            tv_show_encrypt_needed_pk.setText(pkProp.toString());
        }


        Map<String, Properties> ct1AndCt2 = new HashMap<>();
        ct1AndCt2.put(ctFileName1, abeFactory.getData("show_" + ctFileName1));
        ct1AndCt2.put(ctFileName2, abeFactory.getData("show_" + ctFileName2));

//        System.out.println("log008: "+ct1AndCt2.toString());

        // 写入ct并展示
        StringBuilder ctb = new StringBuilder();
        List<String> ctList = abeFactory.dealCts(ct1AndCt2);
        for (String ctItem : ctList) {
            ctb.append(ctItem).append("\n\n");
        }
        tv_show_encrypt_ct.setText(ctb.toString());
        System.out.println("log008:ctList: " + ctList);
        if (ctList.toString().length() != 0) {
            try {
                imageView.setImageBitmap(zxing(ctList.get(1)));
            } catch (Exception e) {
//                Snackbar.make(getView(), "", Snackbar.LENGTH_SHORT).show();
            }
        }

        SharedPreferences spctb = requireActivity().getSharedPreferences("show_" + ming_before, Context.MODE_PRIVATE);
        String clearText1 = spctb.getString("1", "test1");
        String clearText2 = spctb.getString("2", "test2");
        String clearText3 = spctb.getString("3", "test3");

        clt1_edt.setText(clearText1);
        clt2_edt.setText(clearText2);
        clt3_edt.setText(clearText3);
    }


    private Bitmap zxing(String name) {
        int width = 900;
        int height = 900;

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
        return Bitmap.createBitmap(colors, width, height, Bitmap.Config.RGB_565);
    }
}