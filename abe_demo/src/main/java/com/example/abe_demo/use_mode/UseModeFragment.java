package com.example.abe_demo.use_mode;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.example.abe_demo.R;
import com.example.abe_demo.sqlite3.DatabaseHelper;
import com.example.abe_demo.use_mode.fragment.UserFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class UseModeFragment extends Fragment {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_use_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserFragment userFragment = new UserFragment();
        setShowPage(userFragment);
    }

    // 加载页面的fragment
    private void setShowPage(Fragment fragment) {
//        ShowModeFragment showModeFragment = new ShowModeFragment();
        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.use_mode_fcv, fragment).commit();

    }
}
