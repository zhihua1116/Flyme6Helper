package com.coderstory.FTool.plugins;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Baby Song on 2016/8/17.
 */

public interface IModule {
    void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam);
}
