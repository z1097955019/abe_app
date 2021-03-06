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

    // ??????????????????
    private final DeliveryMessage senderMessage = new DeliveryMessage();
    private final DeliveryMessage receiverMessage = new DeliveryMessage();
    private final Map<String, DeliveryMessage> message = new HashMap<>();

    // ?????????????????????????????????
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

    // ???????????????????????????
    private final Map<String, Boolean> saveState = new HashMap<>();
    private MaterialButton use_mode_get_qr_code_btn;
    private MaterialButton use_mode_scan_qr_code_btn;
    private MaterialCardView control_card;

    // ?????????????????????
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

        // ?????????????????????
        use_mode_get_qr_code_btn = view.findViewById(R.id.use_mode_get_qr_code_btn);
        use_mode_scan_qr_code_btn = view.findViewById(R.id.use_mode_scan_qr_code_btn);
        control_card = view.findViewById(R.id.control_card);

        // ???????????????????????????
        use_mode_show_qr_code_img = view.findViewById(R.id.use_mode_show_qr_code_img);

        // ??????????????????????????????
        province_tv = view.findViewById(R.id.province_tv);
        city_tv = view.findViewById(R.id.city_tv);
        place_tv = view.findViewById(R.id.place_tv);

        // ?????????????????????
        use_mode_behind_address_edt = view.findViewById(R.id.use_mode_behind_address_edt);
        use_mode_name_edt = view.findViewById(R.id.use_mode_name_edt);
        use_mode_phone_edt = view.findViewById(R.id.use_mode_phone_edt);

        // ??????????????????
        user_message_save_btn = view.findViewById(R.id.user_message_save_btn);
        user_message_mod_btn = view.findViewById(R.id.user_message_mod_btn);
        // ?????????????????????????????????
        user_save_btn_view = view.findViewById(R.id.user_save_btn_view);
        user_mod_btn_view = view.findViewById(R.id.user_mod_btn_view);

        // ????????????????????????
        View user_message_mod_block = view.findViewById(R.id.user_message_mod_block);

        // ????????????????????????
        View user_message_show_block = view.findViewById(R.id.user_message_show_block);

        // ???????????????tv
        receiver_address_tv = view.findViewById(R.id.receiver_address_tv);
        receiver_name_tv = view.findViewById(R.id.receiver_name_tv);
        receiver_phone_tv = view.findViewById(R.id.receiver_phone_tv);

        // ???????????????????????????
        user_message_save_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                System.out.println("log015: " + user_message_save_btn.getText());
                if (user_message_save_btn.getText().toString().equals("??????")) {
                    System.out.println("log015: ?????????");
                    if (use_mode_behind_address_edt.getText() != null && use_mode_name_edt.getText() != null && use_mode_phone_edt.getText() != null) {
                        if (!(use_mode_behind_address_edt.getText().toString().equals("") &&
                                use_mode_name_edt.getText().toString().equals("") &&
                                use_mode_phone_edt.getText().toString().equals("")) &&
                                use_mode_phone_edt.getText().toString().length() == 11) {
                            receiverMessage.setBehindAddress(use_mode_behind_address_edt.getText().toString());
                            receiverMessage.setPersonName(use_mode_name_edt.getText().toString());
                            receiverMessage.setPhoneNumber(use_mode_phone_edt.getText().toString());

                            // ????????????????????????
                            receiver_address_tv.setText(selectedPlace + receiverMessage.getBehindAddress());
                            receiver_name_tv.setText(receiverMessage.getPersonName());
                            receiver_phone_tv.setText(receiverMessage.getPhoneNumber());

                            // ????????????
                            user_message_save_btn.setText("??????");

                            // ??????????????????
                            user_message_show_block.setVisibility(View.VISIBLE);
                            user_message_mod_block.setVisibility(View.GONE);

                            // ??????????????????
                            saveState.put("receiver", true);
                            show_qr_code();

                            // ??????
                            Toast.makeText(getContext(), "???????????????", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // ????????????
                    user_message_save_btn.setText("??????");

                    // ????????????
                    user_message_show_block.setVisibility(View.GONE);
                    user_message_mod_block.setVisibility(View.VISIBLE);

                    // ??????
                    Toast.makeText(getContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();

                }
                System.out.println("log015: " + receiverMessage);
            }
        });

        // ???????????????????????????

        // ??????????????????
        extended_fab = view.findViewById(R.id.extended_fab);
        initCharacterChanger();

        // ??????????????????????????????
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), 1);

        // ??????????????????????????????
        initAutoTv(province_tv, databaseHelper.retrieveAllProvinceNames(), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // ???????????????
                String selectedProvince = adapterView.getItemAtPosition(i).toString();
                // ????????????????????????
                selectedPlace.append(selectedProvince);
                // ????????????????????????
                initAutoTv(city_tv, databaseHelper.retrieveNeededCity(selectedProvince), new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedCity = adapterView.getItemAtPosition(i).toString();
                        // ?????????????????????
                        selectedPlace.append(selectedCity);
                        // ????????????????????????
                        initAutoTv(place_tv, databaseHelper.retrieveNeededPlace(selectedCity), new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String selectedPlaceItem = adapterView.getItemAtPosition(i).toString();
                                // ????????????????????????
                                selectedPlace.append(selectedPlaceItem);
                                // ??????????????????????????????????????????
                                receiverMessage.setAheadAddress(databaseHelper.retrieveSinglePlace(selectedPlaceItem));
                                System.out.println("log015: ~~~~~~~~~~~~~~~~" + senderMessage.getAheadAddress());
                            }
                        });
                    }
                });
            }
        });

        // ***********************************???????????????*****************************************

        // ??????????????????????????????
        sender_province_tv = view.findViewById(R.id.sender_province_tv);
        sender_city_tv = view.findViewById(R.id.sender_city_tv);
        sender_place_tv = view.findViewById(R.id.sender_place_tv);

        // ?????????????????????
        use_mode_sender_behind_address_edt = view.findViewById(R.id.use_mode_sender_behind_address_edt);
        use_mode_sender_name_edt = view.findViewById(R.id.use_mode_sender_name_edt);
        use_mode_sender_phone_edt = view.findViewById(R.id.use_mode_sender_phone_edt);

        // ??????????????????
        sender_message_save_btn = view.findViewById(R.id.sender_message_save_btn);
        sender_message_mod_btn = view.findViewById(R.id.sender_message_mod_btn);
        // ?????????????????????????????????
        sender_save_btn_view = view.findViewById(R.id.sender_save_btn_view);
        sender_mod_btn_view = view.findViewById(R.id.sender_mod_btn_view);

        // ????????????????????????
        sender_message_mod_block = view.findViewById(R.id.sender_message_mod_block);

        // ????????????????????????
        sender_message_show_block = view.findViewById(R.id.sender_message_show_block);

        // ???????????????tv
        sender_address_tv = view.findViewById(R.id.sender_address_tv);
        sender_name_tv = view.findViewById(R.id.sender_name_tv);
        sender_phone_tv = view.findViewById(R.id.sender_phone_tv);

        // ???????????????????????????
        sender_message_save_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                System.out.println("log015: " + sender_message_save_btn.getText());
                if (sender_message_save_btn.getText().toString().equals("??????")) {
                    System.out.println("log015: ?????????");
                    if (use_mode_sender_behind_address_edt.getText() != null && use_mode_sender_name_edt.getText() != null && use_mode_sender_phone_edt.getText() != null) {
                        if (!(use_mode_sender_behind_address_edt.getText().toString().equals("") &&
                                use_mode_sender_name_edt.getText().toString().equals("") &&
                                use_mode_sender_phone_edt.getText().toString().equals("")) &&
                                use_mode_sender_phone_edt.getText().toString().length() == 11) {

//                            System.out.println("log015: "+use_mode_sender_behind_address_edt.getText().toString()+use_mode_sender_behind_address_edt.getText().toString().equals(""));

                            senderMessage.setBehindAddress(use_mode_sender_behind_address_edt.getText().toString());
                            senderMessage.setPersonName(use_mode_sender_name_edt.getText().toString());
                            senderMessage.setPhoneNumber(use_mode_sender_phone_edt.getText().toString());

                            // ????????????????????????
                            sender_address_tv.setText(senderSelectedPlace + senderMessage.getBehindAddress());
                            sender_name_tv.setText(senderMessage.getPersonName());
                            sender_phone_tv.setText(senderMessage.getPhoneNumber());

                            // ????????????
                            sender_message_save_btn.setText("??????");

                            // ??????????????????
                            sender_message_show_block.setVisibility(View.VISIBLE);
                            sender_message_mod_block.setVisibility(View.GONE);

                            // ??????????????????
                            saveState.put("sender", true);
                            show_qr_code();

                            // ??????
                            Toast.makeText(getContext(), "???????????????", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // ????????????
                    sender_message_save_btn.setText("??????");

                    // ????????????
                    sender_message_show_block.setVisibility(View.GONE);
                    sender_message_mod_block.setVisibility(View.VISIBLE);

                    // ??????
                    Toast.makeText(getContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                }

                System.out.println("log015: " + senderMessage);
            }
        });

        // ???????????????????????????

        // ??????????????????
        extended_fab = view.findViewById(R.id.extended_fab);
        initCharacterChanger();

        // ??????????????????????????????
        initAutoTv(sender_province_tv, databaseHelper.retrieveAllProvinceNames(), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // ???????????????
                String selectedProvince = adapterView.getItemAtPosition(i).toString();
                // ????????????????????????
                senderSelectedPlace.append(selectedProvince);
                // ????????????????????????
                initAutoTv(sender_city_tv, databaseHelper.retrieveNeededCity(selectedProvince), new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String selectedCity = adapterView.getItemAtPosition(i).toString();
                        // ?????????????????????
                        senderSelectedPlace.append(selectedCity);
                        // ????????????????????????
                        initAutoTv(sender_place_tv, databaseHelper.retrieveNeededPlace(selectedCity), new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String selectedPlaceItem = adapterView.getItemAtPosition(i).toString();
                                // ????????????????????????
                                senderSelectedPlace.append(selectedPlaceItem);
                                // ??????????????????????????????????????????
                                senderMessage.setAheadAddress(databaseHelper.retrieveSinglePlace(selectedPlaceItem));
                                System.out.println("log015: ~~~~~~~~~~~~~~~~" + senderMessage.getAheadAddress());
                            }
                        });
                    }
                });
            }
        });


        // ??????????????????????????????
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
        adapterList.add("?????????");
        adapterList.add("??????????????????");
        adapterList.add("?????????");
        adapterList.add("????????????");
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
        // ??????????????????????????????
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, list);
        // ?????????????????????????????????
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

        // ??????????????????
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

        // ??????ct?????????
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
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); //????????????????????????
        BitMatrix encode = null;
        try {
            encode = qrCodeWriter.encode(name, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int[] colors = new int[width * height];
        //??????for????????????????????????????????????
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