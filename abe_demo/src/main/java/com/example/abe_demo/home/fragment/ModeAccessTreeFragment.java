package com.example.abe_demo.home.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.abe_demo.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ModeAccessTreeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModeAccessTreeFragment extends Fragment {

    private TextInputEditText name_edt;
    private TextInputEditText phone_edt;
    private TextInputEditText id_edt;

    private Button save_mes_btn;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ModeAccessTreeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ModeAccessTreeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ModeAccessTreeFragment newInstance(String param1, String param2) {
        ModeAccessTreeFragment fragment = new ModeAccessTreeFragment();
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
        return inflater.inflate(R.layout.fragment_mode_access_tree, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name_edt = view.findViewById(R.id.name_edt);
        phone_edt = view.findViewById(R.id.phone_edt);
        id_edt = view.findViewById(R.id.id_edt);
        save_mes_btn = view.findViewById(R.id.save_mes_btn);

        save_mes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences spRecord = requireActivity().getSharedPreferences("personal_mes", Context.MODE_PRIVATE);
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor edit = spRecord.edit();
                if (!(name_edt.getText() == null) && !(phone_edt.getText() == null) && !(id_edt.getText() == null)) {
                    edit.putString("nameAndPhoneAndId", name_edt.getText().toString() + phone_edt.getText().toString()+id_edt.getText().toString());
                    edit.putString("name", name_edt.getText().toString());
                    edit.putString("phone", phone_edt.getText().toString());
                    edit.putString("id", id_edt.getText().toString());
                    edit.apply();
                    Snackbar.make(view,"保存成功"+spRecord.getString("nameAndPhoneAndId", ""),Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences spRecord = requireActivity().getSharedPreferences("personal_mes", Context.MODE_PRIVATE);
        String name = spRecord.getString("name", "name");
        String phone = spRecord.getString("phone", "phone");
        String id = spRecord.getString("id", "id");


        name_edt.setText(name);
        phone_edt.setText(phone);
        id_edt.setText(id);
    }
}