package com.coderstory.FTool.activity;

import android.app.Fragment;

import com.coderstory.FTool.fragment.AboutFragment;

import ren.solid.library.activity.ToolbarActivity;

public class AboutActivity extends ToolbarActivity {

    @Override
    protected Fragment setFragment() {
        return new AboutFragment();
    }

    @Override
    protected String getToolbarTitle() {
        return "关于";
    }
}
