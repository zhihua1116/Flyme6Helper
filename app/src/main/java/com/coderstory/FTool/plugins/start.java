package com.coderstory.FTool.plugins;

import com.coderstory.FTool.BuildConfig;
import com.coderstory.FTool.module.ThemePatch;
import com.coderstory.FTool.module.HookLog;
import com.coderstory.FTool.module.isEnable;
import com.coderstory.FTool.utils.MyConfig;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class start implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        new ThemePatch().handleLoadPackage(lpparam);
        new isEnable().handleLoadPackage(lpparam);
        new HookLog().handleLoadPackage(lpparam);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MyConfig.xsp = new XSharedPreferences(BuildConfig.APPLICATION_ID);
        MyConfig.xsp.makeWorldReadable();
    }
}
