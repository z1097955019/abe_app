package com.example.abe_demo.use_mode.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListPopupWindow;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.abe_demo.R;
import com.example.abe_demo.sqlite3.DatabaseHelper;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.LinkedList;
import java.util.List;

public class UserFragment extends Fragment {

    private AutoCompleteTextView esttest;
    private ExtendedFloatingActionButton extended_fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        esttest = view.findViewById(R.id.esttest);
        extended_fab =view.findViewById(R.id.extended_fab);
        initCharacterChanger();
        /*
         * 动态添显示下来菜单的选项，可以动态添加元素
         * 从数据库初始化数据
         */
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity(), 1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item, databaseHelper.retrieveAllProvinceNames());
        esttest.setAdapter(adapter2);

    }


    static class spinner2Listener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            String selected = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            System.out.println("nothingSelect");
        }
    }

    private void initCharacterChanger(){

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
}