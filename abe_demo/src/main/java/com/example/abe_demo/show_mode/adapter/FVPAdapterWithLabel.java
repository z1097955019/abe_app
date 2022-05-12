package com.example.abe_demo.show_mode.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

public class FVPAdapterWithLabel extends FVPAdapter {

    private final List<String> titleList;

    public FVPAdapterWithLabel(@NonNull FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
        super(fm, fragmentList);
        this.titleList = titleList;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList == null ? null : titleList.get(position);
    }
}
