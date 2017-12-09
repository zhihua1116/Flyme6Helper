package com.coderstory.FTool.utils.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;

import com.coderstory.FTool.BuildConfig;
import com.coderstory.FTool.utils.MyConfig;

import static com.coderstory.FTool.utils.MyConfig.customizecenterPackageName;
import static com.coderstory.FTool.utils.MyConfig.supportVersions;

import java.util.List;

/**
 * Created by coder on 2017/5/25.
 */

public class AppUtils {

    /**
     * 检测是否支持该版本的主题美化
     * @param context
     * @return
     */
    public static boolean isSupport(Context context) {
        String version = getVersionName(context, customizecenterPackageName);
        for (String s : supportVersions) {
            if (s.equals(version)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定应用的versionName
     * @param context
     * @param packName
     * @return
     */
    public static String getVersionName(Context context, String packName) {
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        if (packages != null) {
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                if (packageInfo.packageName.equals(packName)) {
                    return packageInfo.versionName;
                }
            }
        }
        return "null";
    }

    /**
     * 判断应用是否为新安装的
     * @return
     */
    public static boolean checkVersion(SharedPreferences sp){
        int v1 = sp.getInt(MyConfig.kMVersionCode, 0);
        int v2 = BuildConfig.VERSION_CODE;
        if(v2 > v1){
            sp.edit().putInt(MyConfig.kMVersionCode, v2).apply();
            return true;
        }
        return false;
    }
}
