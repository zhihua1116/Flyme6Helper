package com.coderstory.FTool.utils;

import com.coderstory.FTool.activity.MainActivity;
import com.coderstory.FTool.fragment.DisbaleAppFragment;
import com.coderstory.FTool.fragment.DonationFragment;
import com.coderstory.FTool.fragment.HostsFragment;
import com.coderstory.FTool.fragment.LogFragment;
import com.coderstory.FTool.fragment.ThemePatchFragment;
import com.coderstory.FTool.module.ThemePatch;
import com.coderstory.FTool.utils.root.ShellUtils;

import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by Baby Song on 2016/9/7.
 */

public class MyConfig {
    public static boolean isProcessing = false;
    public static String customizecenterPackageName ="com.meizu.customizecenter";
    public static String[] supportVersions = new String[]{"6.11.1", "6.12.1", "6.13.1", "6.14.2"};
    public static String dataDir = "/storage/emulated/0/FTool/";
    public static String logDir = dataDir + "logs/";
    public static String vMLogDir = logDir + "logs.txt";

    public static String kEnableThemePatch = "enableThemePatch";
    public static String kCVersionName = "cVersionName";
    public static String kMVersionCode = "mVersionCode";
    public static String kLogPackName= "hookPackName";
    public static String kIsRoot= "isRoot";
    public static String kIsSupport= "isSupport";

    public static String XPOSED_TAG = "Xposed";
    public static String[] LOG_METHOD = new String[]{"v", "d", "i", "w", "e"};
    public static String[] M_LOG_TAG = new String[]{MainActivity.TAG, DonationFragment.TAG, DisbaleAppFragment.TAG, HostsFragment.TAG, LogFragment.TAG, ThemePatchFragment.TAG, ThemePatch.TAG, ShellUtils.TAG};

    public static XSharedPreferences xsp;
}
