package com.example.abe_demo.use_mode.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.abe_demo.R;
import com.example.abe_demo.abe_tools.ABEFactory;
import com.example.abe_demo.abe_tools.utils.CodeConvert;
import com.example.abe_demo.abe_tools.utils.DeliveryMessage;
import com.example.abe_demo.sqlite3.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class UserFragment extends Fragment {

    private AutoCompleteTextView province_tv;
    private AutoCompleteTextView city_tv;
    private AutoCompleteTextView place_tv;
    private ExtendedFloatingActionButton extended_fab;

    private TextInputEditText use_mode_behind_address_edt;
    private TextInputEditText use_mode_name_edt;
    private TextInputEditText use_mode_phone_edt;

    private final StringBuilder selectedPlace = new StringBuilder();

    // 存储用户信息
    private final DeliveryMessage senderMessage = new DeliveryMessage();
    private final DeliveryMessage receiverMessage = new DeliveryMessage();
    private final Map<String, DeliveryMessage> message = new HashMap<>();

    // 控制用户信息保存和编辑
    private MaterialButton user_message_save_btn;
    private MaterialButton user_message_mod_btn;
    private View user_save_btn_view;
    private View user_mod_btn_view;
    private TextView receiver_address_tv;
    private TextView receiver_name_tv;
    private TextView receiver_phone_tv;

    private AutoCompleteTextView sender_province_tv;
    private AutoCompleteTextView sender_city_tv;
    private AutoCompleteTextView sender_place_tv;
    private TextInputEditText use_mode_sender_behind_address_edt;
    private TextInputEditText use_mode_sender_name_edt;
    private TextInputEditText use_mode_sender_phone_edt;
    private MaterialButton sender_message_save_btn;
    private MaterialButton sender_message_mod_btn;
    private View sender_save_btn_view;
    private View sender_mod_btn_view;
    private View sender_message_mod_block;
    private View sender_message_show_block;
    private TextView sender_address_tv;
    private TextView sender_name_tv;
    private TextView sender_phone_tv;
    private final StringBuilder senderSelectedPlace = new StringBuilder();

    // 存储两个信息的状态
    private final Map<String, Boolean> saveState = new HashMap<>();
    private MaterialButton use_mode_get_qr_code_btn;
    private MaterialButton use_mode_scan_qr_code_btn;
    private MaterialCardView control_card;

    // 二维码展示位置
    private ImageView use_mode_show_qr_code_img;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 控制生成和扫码
        use_mode_get_qr_code_btn = view.findViewById(R.id.use_mode_get_qr_code_btn);
        use_mode_scan_qr_code_btn = view.findViewById(R.id.use_mode_scan_qr_code_btn);
        control_card = view.findViewById(R.id.control_card);

        // 二维码图片展示位置
        use_mode_show_qr_code_img = view.findViewById(R.id.use_mode_show_qr_code_img);

        // 前置收件人的地址信息
        province_tv = view.findViewById(R.id.province_tv);
        city_tv = view.findViewById(R.id.city_tv);
        place_tv = view.findViewById(R.id.place_tv);

        // 其余收件人信息
        use_mode_behind_address_edt = view.findViewById(R.id.use_mode_behind_address_edt);
        use_mode_name_edt = view.findViewById(R.id.use_mode_name_edt);
        use_mode_phone_edt = view.findViewById(R.id.use_mode_phone_edt);

        // 两个操作按钮
        user_message_save_btn = view.findViewById(R.id.user_message_save_btn);
        user_message_mod_btn = view.findViewById(R.id.user_message_mod_btn);
        // 两个操作按钮所在的视图
        user_save_btn_view = view.findViewById(R.id.user_save_btn_view);
        user_mod_btn_view = view.findViewById(R.id.user_mod_btn_view);

        // 收件人信息编辑区
        View user_message_mod_block = view.findViewById(R.id.user_message_mod_block);

        // 收件人信息展示区
        View user_message_show_block = view.findViewById(R.id.user_message_show_block);

        // 展示区数据tv
        receiver_address_tv = view.findViewById(R.id.receiver_address_tv);
        receiver_name_tv = view.findViewById(R.id.receiver_name_tv);
        receiver_phone_tv = view.findViewById(R.id.receiver_phone_tv);

        // 设置保存按钮的功能
        user_message_save_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                System.out.println("log015: " + user_message_save_btn.getText());
                if (user_message_save_btn.getText().toString().equals("保存")) {
                    System.out.println("log015: 进来了");
                    if (use_mode_behind_address_edt.getText() != null && use_mode_name_edt.getText() != null && use_mode_phone_edt.getText() != null) {
                        if (!(use_mode_behind_address_edt.getText().toString().equals("") &&
                                use_mode_name_edt.getText().toString().equals("") &&
                                use_mode_phone_edt.getText().toString().equals("")) &&
                                use_mode_phone_edt.getText().toString().length() == 11) {
                            receiverMessage.setBehindAddress(use_mode_behind_address_edt.getText().toString());
                            receiverMessage.setPersonName(use_mode_name_edt.getText().toString());
                            receiverMessage.setPhoneNumber(use_mode_phone_edt.getText().toString());

                            // 更新展示区域数据
                            receiver_address_tv.setText(selectedPlace + receiverMessage.getBehindAddress());
                            receiver_name_tv.setText(receiverMessage.getPersonName());
                            receiver_phone_tv.setText(receiverMessage.getPhoneNumber());

                            // 按钮区域
                            user_message_save_btn.setText("编辑");

                            // 展示区域切换
                            user_message_show_block.setVisibility(View.VISIBLE);
                            user_message_mod_block.setVisibility(View.GONE);

                            // 记录成功状态
                            saveState.put("receiver", true);
                            show_qr_code();

                            // 交互
                            Toast.makeText(getContext(), "保存成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "信息格式有误，请对表单进行检查！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "请输入必要的信息！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 按钮区域
                    user_message_save_btn.setText("保存");

                    // 展示区域
                    user_message_show_block.setVisibility(View.GONE);
                    user_message_mod_block.setVisibility(View.VISIBLE);

                    // 交互
                    Toast.makeText(getContext(), "请输入合适的信息！", Toast.LENGTH_SHORT).show();

                }
                System.out.println("log015: " + receiverMessage);
            }
        });

        // 设置编辑按钮的功能

        // 切换角色按钮
        extended_fab = view.findViewById(R.id.extended_fab);
        initCharacterChanger();

        // 获得数据库操作类实例
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), 1);

        // 初始化最初的省份列表
        initAutoTv(province_tv, databaseHelper.retrieveAllProvinceNames(), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 给省份赋值
                String selectedProvince = adapterView.getItemAtPosition(i).toString();
                // 为总地址加入省份
                selectedPlace.append(selectedProvince);
                // 根据省份初始化市
                initAutoTv(city_tv, databaseHelper.retrieveNeededCity(selectedProvince), new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedCity = adapterView.getItemAtPosition(i).toString();
                        // 为总地址加入市
                        selectedPlace.append(selectedCity);
                        // 根据市初始化地区
                        initAutoTv(place_tv, databaseHelper.retrieveNeededPlace(selectedCity), new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String selectedPlaceItem = adapterView.getItemAtPosition(i).toString();
                                // 为总地址加入地区
                                selectedPlace.append(selectedPlaceItem);
                                // 查找并记录最终的前置地址编号
                                receiverMessage.setAheadAddress(databaseHelper.retrieveSinglePlace(selectedPlaceItem));
                                System.out.println("log015: ~~~~~~~~~~~~~~~~" + senderMessage.getAheadAddress());
                            }
                        });
                    }
                });
            }
        });

        // ***********************************寄件人部分*****************************************

        // 前置寄件人的地址信息
        sender_province_tv = view.findViewById(R.id.sender_province_tv);
        sender_city_tv = view.findViewById(R.id.sender_city_tv);
        sender_place_tv = view.findViewById(R.id.sender_place_tv);

        // 其余收件人信息
        use_mode_sender_behind_address_edt = view.findViewById(R.id.use_mode_sender_behind_address_edt);
        use_mode_sender_name_edt = view.findViewById(R.id.use_mode_sender_name_edt);
        use_mode_sender_phone_edt = view.findViewById(R.id.use_mode_sender_phone_edt);

        // 两个操作按钮
        sender_message_save_btn = view.findViewById(R.id.sender_message_save_btn);
        sender_message_mod_btn = view.findViewById(R.id.sender_message_mod_btn);
        // 两个操作按钮所在的视图
        sender_save_btn_view = view.findViewById(R.id.sender_save_btn_view);
        sender_mod_btn_view = view.findViewById(R.id.sender_mod_btn_view);

        // 收件人信息编辑区
        sender_message_mod_block = view.findViewById(R.id.sender_message_mod_block);

        // 收件人信息展示区
        sender_message_show_block = view.findViewById(R.id.sender_message_show_block);

        // 展示区数据tv
        sender_address_tv = view.findViewById(R.id.sender_address_tv);
        sender_name_tv = view.findViewById(R.id.sender_name_tv);
        sender_phone_tv = view.findViewById(R.id.sender_phone_tv);

        // 设置保存按钮的功能
        sender_message_save_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                System.out.println("log015: " + sender_message_save_btn.getText());
                if (sender_message_save_btn.getText().toString().equals("保存")) {
                    System.out.println("log015: 进来了");
                    if (use_mode_sender_behind_address_edt.getText() != null && use_mode_sender_name_edt.getText() != null && use_mode_sender_phone_edt.getText() != null) {
                        if (!(use_mode_sender_behind_address_edt.getText().toString().equals("") &&
                                use_mode_sender_name_edt.getText().toString().equals("") &&
                                use_mode_sender_phone_edt.getText().toString().equals("")) &&
                                use_mode_sender_phone_edt.getText().toString().length() == 11) {

//                            System.out.println("log015: "+use_mode_sender_behind_address_edt.getText().toString()+use_mode_sender_behind_address_edt.getText().toString().equals(""));

                            senderMessage.setBehindAddress(use_mode_sender_behind_address_edt.getText().toString());
                            senderMessage.setPersonName(use_mode_sender_name_edt.getText().toString());
                            senderMessage.setPhoneNumber(use_mode_sender_phone_edt.getText().toString());

                            // 更新展示区域数据
                            sender_address_tv.setText(senderSelectedPlace + senderMessage.getBehindAddress());
                            sender_name_tv.setText(senderMessage.getPersonName());
                            sender_phone_tv.setText(senderMessage.getPhoneNumber());

                            // 按钮区域
                            sender_message_save_btn.setText("编辑");

                            // 展示区域切换
                            sender_message_show_block.setVisibility(View.VISIBLE);
                            sender_message_mod_block.setVisibility(View.GONE);

                            // 记录成功状态
                            saveState.put("sender", true);
                            show_qr_code();

                            // 交互
                            Toast.makeText(getContext(), "保存成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "信息格式有误，请对表单进行检查！", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "请输入必要的信息！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 按钮区域
                    sender_message_save_btn.setText("保存");

                    // 展示区域
                    sender_message_show_block.setVisibility(View.GONE);
                    sender_message_mod_block.setVisibility(View.VISIBLE);

                    // 交互
                    Toast.makeText(getContext(), "请对信息进行编辑！", Toast.LENGTH_SHORT).show();
                }

                System.out.println("log015: " + senderMessage);
            }
        });

        // 设置编辑按钮的功能

        // 切换角色按钮
        extended_fab = view.findViewById(R.id.extended_fab);
        initCharacterChanger();

        // 初始化最初的省份列表
        initAutoTv(sender_province_tv, databaseHelper.retrieveAllProvinceNames(), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 给省份赋值
                String selectedProvince = adapterView.getItemAtPosition(i).toString();
                // 为总地址加入省份
                senderSelectedPlace.append(selectedProvince);
                // 根据省份初始化市
                initAutoTv(sender_city_tv, databaseHelper.retrieveNeededCity(selectedProvince), new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedCity = adapterView.getItemAtPosition(i).toString();
                        // 为总地址加入市
                        senderSelectedPlace.append(selectedCity);
                        // 根据市初始化地区
                        initAutoTv(sender_place_tv, databaseHelper.retrieveNeededPlace(selectedCity), new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String selectedPlaceItem = adapterView.getItemAtPosition(i).toString();
                                // 为总地址加入地区
                                senderSelectedPlace.append(selectedPlaceItem);
                                // 查找并记录最终的前置地址编号
                                senderMessage.setAheadAddress(databaseHelper.retrieveSinglePlace(selectedPlaceItem));
                                System.out.println("log015: ~~~~~~~~~~~~~~~~" + senderMessage.getAheadAddress());
                            }
                        });
                    }
                });
            }
        });


        // 控制生成二维码和扫码
        use_mode_get_qr_code_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ABEFactory abeFactory = new ABEFactory(getContext());
                message.put("sender", senderMessage);
                message.put("receiver", receiverMessage);
                CodeConvert ccHelper = new CodeConvert(message);
                Map<Integer, String> integerStringMap = ccHelper.abeCtEncoder.fromDataToBigNumGroup();
                abeFactory.encrypt("pk.properties", integerStringMap);
                initData(abeFactory);
            }
        });


    }


    private void initCharacterChanger() {

        ListPopupWindow listPopupWindow = new ListPopupWindow(getContext(), null, com.google.android.material.R.attr.listPopupWindowStyle);
        List<String> adapterList = new LinkedList<>();
        adapterList.add("收件人");
        adapterList.add("物流工作人员");
        adapterList.add("快递员");
        adapterList.add("代取件人");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, adapterList);
        listPopupWindow.setAdapter(arrayAdapter);
        listPopupWindow.setAnchorView(extended_fab);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listPopupWindow.dismiss();
            }
        });

        extended_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listPopupWindow.show();
            }
        });

    }

    private void initAutoTv(AutoCompleteTextView tv, List<String> list, AdapterView.OnItemClickListener listener) {
        // 初始化最初的省份列表
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, list);
        // 设置省份选项和事件监听
        tv.setAdapter(adapter);
        tv.setOnItemClickListener(listener);
    }

    private void show_qr_code() {
        if (saveState != null) {
            if (saveState.getOrDefault("sender", false) && saveState.getOrDefault("receiver", false)) {
                control_card.setVisibility(View.VISIBLE);
            }
        }

    }

    private void initData(ABEFactory abeFactory) {

        // 文件存储路径
        String pkFileName = "pk.properties";
        String mskFileName = "msk.properties";
        String skFileName = "sk.properties";
        String ctFileName1 = "ct1.properties";
        String ctFileName2 = "ct2.properties";
        String ming_before = "clearTB.properties";

        Properties pkProp = abeFactory.getData("show_" + pkFileName);


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

        System.out.println("log015:ctList: " + ctList);
        if (ctList.toString().length() != 0) {
            try {
                use_mode_show_qr_code_img.setImageBitmap(zxing(ctList.get(1)));
            } catch (Exception e) {
                Snackbar.make(getView(), "", Snackbar.LENGTH_SHORT).show();
            }
        }
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