package com.coderstory.FTool.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coderstory.FTool.BuildConfig;
import com.coderstory.FTool.R;
import com.coderstory.FTool.utils.root.ShellUtils;

import java.util.ArrayList;
import java.util.List;

import ren.solid.library.utils.SnackBarUtils;
import ren.solid.library.utils.ToastUtils;
import ren.solid.library.utils.ViewUtils;

import static com.coderstory.FTool.utils.root.ShellUtils.execCommand;
import static com.coderstory.FTool.utils.root.SuHelper.canRunRootCommands;
import static com.coderstory.FTool.utils.app.AppUtils.isSupport;
import static com.coderstory.FTool.utils.app.AppUtils.getVersionName;
import static com.coderstory.FTool.utils.app.AppUtils.checkVersion;
import static com.coderstory.FTool.utils.MyConfig.kEnableThemePatch;
import static com.coderstory.FTool.utils.MyConfig.kCVersionName;
import static com.coderstory.FTool.utils.MyConfig.customizecenterPackageName;
import static com.coderstory.FTool.utils.MyConfig.supportVersions;

public class ThemePatchFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener,
        Preference.OnPreferenceClickListener{

    public static String TAG = "ThemePatchFragment";

    private SwitchPreference enableThemePatch;
    private SwitchPreference enableService;
    private Preference needToKnow;
    private Preference logsInfo;
    private SharedPreferences sp;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_theme);
        initPreferences();
    }

    private void initPreferences() {
        context = getActivity();
        sp = PreferenceManager.getDefaultSharedPreferences(getMContext());
        enableThemePatch = (SwitchPreference)findPreference("enableThemePatch");
        enableThemePatch.setChecked(sp.getBoolean(kEnableThemePatch, false));
        enableThemePatch.setOnPreferenceChangeListener(this);
        enableService = (SwitchPreference)findPreference("enableService");
        enableService.setOnPreferenceChangeListener(this);
        needToKnow = findPreference("needToKnow");
        needToKnow.setOnPreferenceClickListener(this);
        logsInfo = findPreference("logsInfo");
        logsInfo.setOnPreferenceClickListener(this);

        if(checkVersion(sp)){
            showDialog("使用须知", getDialogView(getActivity()));
        }
    }

    private static List<String> needDisableStr = new ArrayList<>();
    private static String packageName = "com.meizu.customizecenter/";
    private static String command;
    private static String disable = "pm disable " + packageName;
    private static String enable = "pm enable " + packageName;

    private void addList(){
        needDisableStr.clear();
        needDisableStr.add(command  + "com.meizu.customizecenter.common.helper.BootBroadcastReceiver");
        needDisableStr.add(command  + "com.meizu.customizecenter.common.font.FontTrialService");
        needDisableStr.add(command  + "com.meizu.customizecenter.common.theme.ThemeTrialService");
        needDisableStr.add(command  + "com.meizu.customizecenter.service.ThemeRestoreService");
        needDisableStr.add(command  + "com.meizu.customizecenter.service.FontRestoreService");
        //needDisableStr.add(command  + "com.meizu.gslb.push.GslbDataRefreshReceiver");
        //needDisableStr.add(command  + "com.meizu.customizecenter.common.push.CustomizePushReceiver");
        //needDisableStr.add("com.meizu.customizecenter.common.helper.ShopDemoReceiver");
        //needDisableStr.add("com.meizu.cloud.pushsdk.SystemReceiver");
        //needDisableStr.add("com.meizu.advertise.api.AppDownloadAndInstallReceiver");
    }

    /**
     * 禁用服务
     */
    private void disabled(){
        if (canRunRootCommands()) {
            if(enableService.isChecked()){
                command = enable;
            }else{
                command = disable;
            }
            addList();
            startTask();
        } else {
            Log.i("XposedSettingFragment", "can not get root！");
            SnackBarUtils.makeShort(getView(), "未获取到 Root 权限").danger();
        }
    }

    private ProgressDialog progressDialog;

    private void startTask(){
        ProgressAsyncTask progressAsyncTask = new ProgressAsyncTask();
        progressAsyncTask.setOnProgressListener(new ProgressAsyncTask.OnProgressListener() {
            @Override
            public void onFinish(String result) {
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                //SnackBarUtils.makeShort(getView(), (enableService.isChecked() ? "已禁用服务！" : "已恢复服务！")).info();
                new ToastUtils(getMContext()).showToast(result);
            }

            @Override
            public void onBegin(String msg) {
                if(progressDialog == null || !progressDialog.isShowing()){
                    progressDialog = ProgressDialog.show(getActivity(), "稍等", msg);
                }else{
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("稍等");
                    progressDialog.setMessage(msg);
                    progressDialog.setIcon(R.mipmap.ic_launcher);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.show();
                }
            }
        });
        progressAsyncTask.execute();
    }

    private void updateConfig(){
        sp.edit().putString(kCVersionName, getVersionName(getMContext(), customizecenterPackageName)).apply();
    }

    private void showDialog(String title, View view) {
        AlertDialog.Builder builder =new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setView(view);
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 获取日志信息
     * @param context
     * @return
     */
    private View getLogInfo(Context context){
        ScrollView scrollView = new ScrollView(context);
        int mp = ViewUtils.dp2px(context, 16);
        scrollView.setPadding(mp, mp, mp, mp);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView textView1 = new TextView(context);
        TextView textView2 = new TextView(context);
        TextView textView3 = new TextView(context);
        TextView textView4 = new TextView(context);
        TextView textView5 = new TextView(context);
        TextView textView6 = new TextView(context);
        TextView textView7 = new TextView(context);
        TextView textView8 = new TextView(context);
        TextView textView9 = new TextView(context);

        textView1.setText("手机品牌：" + Build.BRAND);
        textView2.setText("手机型号：" + Build.MODEL);
        textView3.setText("Android 版本：Android " + Build.VERSION.RELEASE + "(API " + Build.VERSION.SDK_INT + ")");
        textView4.setText("ROM 版本：" + execCommand("getprop ro.build.display.id", false).successMsg);
        textView5.setText("Xposed 状态：" + (isEnable() ? "已启用" : "未启用"));
        textView6.setText("Root 状态：" + (canRunRootCommands() ? "已Root" : "未Root"));
        textView7.setText("当前模块版本：" + BuildConfig.VERSION_NAME);
        textView8.setText("主题美化版本：" + getVersionName(getActivity(), customizecenterPackageName));
        textView9.setText("是否支持该版本：" + (isSupport(getMContext()) ? "支持" : "不支持"));

        Log.i(TAG, String.valueOf(textView1.getText()));
        Log.i(TAG, String.valueOf(textView2.getText()));
        Log.i(TAG, String.valueOf(textView3.getText()));
        Log.i(TAG, String.valueOf(textView4.getText()));
        Log.i(TAG, String.valueOf(textView5.getText()));
        Log.i(TAG, String.valueOf(textView6.getText()));
        Log.i(TAG, String.valueOf(textView7.getText()));
        Log.i(TAG, String.valueOf(textView8.getText()));
        Log.i(TAG, String.valueOf(textView9.getText()));

        linearLayout.addView(textView1);
        linearLayout.addView(textView2);
        linearLayout.addView(textView3);
        linearLayout.addView(textView4);
        linearLayout.addView(textView5);
        linearLayout.addView(textView6);
        linearLayout.addView(textView7);
        linearLayout.addView(textView8);
        linearLayout.addView(textView9);

        scrollView.addView(linearLayout);
        return scrollView;
    }

    private View getDialogView(Context context){
        ScrollView scrollView = new ScrollView(context);
        int mp = ViewUtils.dp2px(context, 16);
        scrollView.setPadding(mp, mp, mp, mp);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView textView1 = new TextView(context);
        TextView textView2 = new TextView(context);
        TextView textView3 = new TextView(context);
        TextView textView4 = new TextView(context);
        TextView textView5 = new TextView(context);
        TextView textView6 = new TextView(context);
        TextView textView7 = new TextView(context);

        textView1.setText("适配列表：");
        textView1.setTextSize(16);
        textView1.setTextColor(Color.BLACK);

        StringBuffer sb = new StringBuffer();
        for (String version : supportVersions){
            sb.append(version + "\n");
        }
        textView2.setText(sb.toString());

        updateConfig();

        String customizecenterVersionName = sp.getString(kCVersionName, "null");
        String isSupport = isSupport(getMContext()) ? "支持" : "不支持";

        String notSupport = "如果你的「主题美化」版本在 " + supportVersions[0] + " 之前，请安装以前的「Flyme6助手」。\n" +
                "如果你的「主题美化」版本在 " + supportVersions[supportVersions.length - 1] + " 之后，请联系作者适配。\n\n";
        textView3.setText("你的「主题美化」版本为「" + customizecenterVersionName + "」，该模块" + isSupport + "你的版本！\n\n" +
                (isSupport(getMContext()) ? "" : notSupport ));

        textView4.setText("模式介绍：");
        textView4.setTextSize(16);
        textView4.setTextColor(Color.BLACK);

        textView5.setText("Xposed 模式：开启 Xposed 模式后可以无需 Root 直接无限时长试用主题，该模式目前处于测试阶段。\n\n" +
                "Root 模式：开启 Root 模式后可以禁用「主题美化」的恢复服务，如果你的设备已经获取 Root 而且可以试用主题，开启后同样可以无限试用\n\n" +
                "上面两个模式互不冲突，可以同时开启，请根据你的情况选择使用。\n\n");

        textView6.setText("已知 Bug：");
        textView6.setTextSize(16);
        textView6.setTextColor(Color.BLACK);

        textView7.setText("1. 不能恢复默认字体\n" +
                "解决办法：关掉上面的开关再使用默认字体即可。\n\n" +
                "2. 混搭主题 1001 错误代码\n" +
                "解决办法：先使用默认主题再使用混搭主题或者将要混搭的主题重新下载一遍然后再应用混搭。\n\n" +
                "如果发现了其他 Bug 请加群：285880321 报告。\n");

        linearLayout.addView(textView1);
        linearLayout.addView(textView2);
        linearLayout.addView(textView3);
        linearLayout.addView(textView4);
        linearLayout.addView(textView5);
        linearLayout.addView(textView6);
        linearLayout.addView(textView7);

        scrollView.addView(linearLayout);
        return scrollView;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if(preference == enableThemePatch){
            updateConfig();
        }else if(preference == enableService){
            disabled();
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference == logsInfo){
            showDialog("日志信息", getLogInfo(getMContext()));
        }else if(preference == needToKnow){
            showDialog("使用须知", getDialogView(getMContext()));
        }
        return false;
    }

    private static class ProgressAsyncTask extends AsyncTask<String, Integer, String>{

        private String result;

        @Override
        protected String doInBackground(String... strings) {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            ShellUtils.CommandResult commandResult = execCommand(needDisableStr, true);
            result = commandResult.result == 0 ? "success" : "failure";
            Log.i("ProgressAsyncTask", commandResult.result == 0 ? "success" : commandResult.errorMsg);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mListener.onFinish(result);
        }

        @Override
        protected void onPreExecute() {
            mListener.onBegin(command.equals(disable) ? "正在禁用服务..." : "正在恢复服务...");
        }

        private OnProgressListener mListener;
        public void setOnProgressListener(OnProgressListener listener){
            mListener = listener;
        }

        public interface OnProgressListener{
            void onFinish(String result);
            void onBegin(String msg);
        }
    }

    private Context getMContext(){
        return this.context;
    }

    private boolean isEnable(){
        return false;
    }
}
