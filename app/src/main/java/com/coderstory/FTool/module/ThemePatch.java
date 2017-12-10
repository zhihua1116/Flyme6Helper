package com.coderstory.FTool.module;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.coderstory.FTool.BuildConfig;
import com.coderstory.FTool.plugins.IModule;
import com.coderstory.FTool.utils.MyConfig;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.coderstory.FTool.utils.MyConfig.xsp;

public class ThemePatch implements IModule {

    private static XC_LoadPackage.LoadPackageParam loadPackageParam;
    public static String TAG = "ThemePatch";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        loadPackageParam = lpparam;
        try{
            xsp.reload();
            patchcode();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, String.valueOf(e.getCause()));
            e.printStackTrace();
        }
    }

    private static void findAndHookMethod(String p1, ClassLoader lpparam, String p2, Object... parameterTypesAndCallback) {
        try {
            XposedHelpers.findAndHookMethod(p1, lpparam, p2, parameterTypesAndCallback);
        } catch (Throwable localString3) {
            //Log.e(TAG, localString3.getMessage());
            //Log.e(TAG, String.valueOf(localString3.getCause()));
            //XposedBridge.log(localString3);
        }
    }

    private void patchcode() throws ClassNotFoundException {

        boolean isEnableThemePatch = xsp.getBoolean(MyConfig.kEnableThemePatch, true);
        if(!isEnableThemePatch){
            return;
        }

        // 拦截开机自启广播
        findAndHookMethod("com.meizu.customizecenter.common.helper.BootBroadcastReceiver", loadPackageParam.classLoader, "onReceive", Context.class, Intent.class, XC_MethodReplacement.returnConstant(null));

        // 拦截试用服务
        findAndHookMethod("com.meizu.customizecenter.common.font.FontTrialService", loadPackageParam.classLoader, "onStartCommand", Intent.class, int.class, int.class, XC_MethodReplacement.returnConstant(0));
        findAndHookMethod("com.meizu.customizecenter.common.theme.ThemeTrialService", loadPackageParam.classLoader, "onStartCommand", Intent.class, int.class, int.class, XC_MethodReplacement.returnConstant(0));

        if (loadPackageParam.packageName.equals(MyConfig.customizecenterPackageName)) {

            Object activityThread = XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread");
            Context context = (Context) XposedHelpers.callMethod(activityThread, "getSystemContext");
            PackageInfo packageInfo = null;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(loadPackageParam.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            String cVersion = packageInfo.versionName;
            String mVersion = BuildConfig.VERSION_NAME;

            XposedBridge.log("------------------------");
            XposedBridge.log("crack by coderstory");
            XposedBridge.log("Powered By zpp0196");
            XposedBridge.log("模块版本：" + mVersion);
            XposedBridge.log("主题美化版本：" + cVersion);
            XposedBridge.log("------------------------");

            //device_states | doCheckState
            if(cVersion.equals("6.11.0") || cVersion.equals("6.13.1")){
                findAndHookMethod("com.meizu.customizecenter.h.al", loadPackageParam.classLoader, "h", Context.class, XC_MethodReplacement.returnConstant(0));
            }
            if(cVersion.equals("6.12.1")){
                findAndHookMethod("com.meizu.customizecenter.g.ak", loadPackageParam.classLoader, "h", Context.class, XC_MethodReplacement.returnConstant(0));
            }
            if(cVersion.equals("6.14.2")){
                findAndHookMethod("com.meizu.customizecenter.h.am", loadPackageParam.classLoader, "h", Context.class, XC_MethodReplacement.returnConstant(0));
            }

            //resetToSystemTheme
            // 6.11.1 & 6.12.1 & 6.13.1 & 6.14.2
            findAndHookMethod("com.meizu.customizecenter.common.theme.common.b", loadPackageParam.classLoader, "b", XC_MethodReplacement.returnConstant(false));
            findAndHookMethod("com.meizu.customizecenter.common.theme.common.b", loadPackageParam.classLoader, "b", boolean.class, XC_MethodReplacement.returnConstant(null));

            //data/data/com.meizu.customizecenter/font/   system_font
            // 6.11.1 & 6.12.1 & 6.13.1 & 6.14.2
            findAndHookMethod("com.meizu.customizecenter.common.font.c", loadPackageParam.classLoader, "b", XC_MethodReplacement.returnConstant(false));

            // notification
            if (cVersion.equals("6.11.0") || cVersion.equals("6.12.1")) {
                findAndHookMethod("com.meizu.customizecenter.common.f.e", loadPackageParam.classLoader, "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.f.e", loadPackageParam.classLoader, "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.f.c", loadPackageParam.classLoader, "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.f.c", loadPackageParam.classLoader, "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
            }
            if (cVersion.equals("6.13.1") || cVersion.equals("6.14.2")) {
                findAndHookMethod("com.meizu.customizecenter.common.g.f", loadPackageParam.classLoader, "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.g.f", loadPackageParam.classLoader, "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.g.c", loadPackageParam.classLoader, "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.meizu.customizecenter.common.g.c", loadPackageParam.classLoader, "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
            }

            //主题混搭 ThemeContentProvider query Unknown URI
            findAndHookMethod("com.meizu.customizecenter.common.dao.ThemeContentProvider", loadPackageParam.classLoader, "query", Uri.class, String[].class, String.class, String[].class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    Object[] objs = param.args;
                    String Tag = "(ITEMS LIKE";
                    String Tag2 = "%zklockscreen;%";
                    String Tag3 = "%com.meizu.flyme.weather;%";
                    //XposedBridge.log("开始");
                    boolean result = false;
                    for (Object obj : objs) {
                        //XposedBridge.log(obj == null ? "" : obj.toString());
                        if (obj instanceof String && (((String) obj).contains(Tag) || obj.equals(Tag2) || obj.equals(Tag3))) {
                            result = true;
                        }
                    }
                    //XposedBridge.log("结束");
                    if (result) {
                        for (Object obj : objs) {
                            if (obj instanceof String[]) {
                                for (int j = 0; j < ((String[]) obj).length; j++) {
                                    if (((String[]) obj)[j].contains("/storage/emulated/0/Customize/Themes")) {
                                        ((String[]) obj)[j] = "/storage/emulated/0/Customize%";
                                    } else if (((String[]) obj)[j].contains("/storage/emulated/0/Customize/TrialThemes")) {
                                        ((String[]) obj)[j] = "NONE";
                                    }
                                }
                            }
                        }
                    }
                    super.beforeHookedMethod(param);
                }
            });
        }
    }
}


