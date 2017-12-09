package com.coderstory.FTool.module;

import com.coderstory.FTool.BuildConfig;
import com.coderstory.FTool.plugins.IModule;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class isEnable implements IModule {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            try {
                XposedHelpers.findAndHookMethod("com.coderstory.FTool.activity.MainActivity", lpparam.classLoader, "isEnable", XC_MethodReplacement.returnConstant(true));
            } catch (Throwable p1) {
                XposedBridge.log(p1);
            }
            try {
                XposedHelpers.findAndHookMethod("com.coderstory.FTool.fragment.ThemePatchFragment", lpparam.classLoader, "isEnable", XC_MethodReplacement.returnConstant(true));
            } catch (Throwable p1) {
                XposedBridge.log(p1);
            }
        }
    }
}
