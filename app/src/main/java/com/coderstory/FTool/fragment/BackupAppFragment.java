package com.coderstory.FTool.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.coderstory.FTool.R;
import com.coderstory.FTool.utils.Adapter.Application.AppInfo;
import com.coderstory.FTool.utils.Adapter.Application.AppInfoAdapter;
import com.coderstory.FTool.utils.DirManager;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ren.solid.library.fragment.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BackupAppFragment extends BaseFragment {


    private static final String TAG = "backApp";
    final String path_backup = Environment.getExternalStorageDirectory().getPath() + "/FTool/backupAPP/";
    List<PackageInfo> packages = new ArrayList<>();
    AppInfoAdapter adapter = null;
    ListView listView = null;
    AppInfo appInfo = null;
    int mPosition = 0;
    View mView = null;
    com.yalantis.phoenix.PullToRefreshView mPullToRefreshView;
    private View view;
    private List<AppInfo> appInfoList = new ArrayList<>();
    private List<AppInfo> appInfoList2 = new ArrayList<>();
    private Dialog dialog;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_backupapp;
    }

    @Override
    protected void setUpView() {
        super.setUpView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_backupapp, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new MyTask().execute();
        mPullToRefreshView = (PullToRefreshView) getActivity().findViewById(R.id.pull_to_refresh);

        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        showData();
                        adapter.notifyDataSetChanged();
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void initFruit() {

        appInfoList = new ArrayList<>();
        PackageManager pm = getActivity().getPackageManager();
        DirManager.apkAll = DirManager.GetApkFileName(path_backup);
        packages = new ArrayList<>();

        appInfoList2.clear();
        for (String item : DirManager.apkAll) {
            PackageInfo packageInfo = DirManager.loadAppInfo(item, getActivity());
            if (packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                //必须设置apk的路径 否则无法读取app的图标和名称
                appInfo.sourceDir = path_backup + item;
                appInfo.publicSourceDir = path_backup + item;
                AppInfo appInfos = new AppInfo(pm.getApplicationLabel(appInfo).toString(), pm.getApplicationIcon(appInfo), packageInfo.packageName, false, packageInfo.applicationInfo.sourceDir, packageInfo.versionName, packageInfo.versionCode);
                appInfoList2.add(appInfos);
            }
        }

        appInfoList.clear();
        packages = getContext().getPackageManager().getInstalledPackages(0);
        //packageInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0 表示是系统应用
        if (packages != null) {
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                if (packageInfo.applicationInfo.sourceDir.startsWith("/data/")) {
                    switch (isBackuped(packageInfo)) {
                        case 0:
                            AppInfo appInfo = new AppInfo(packageInfo.applicationInfo.loadLabel(getActivity().getPackageManager()).toString(), packageInfo.applicationInfo.loadIcon(getActivity().getPackageManager()), packageInfo.packageName, false, packageInfo.applicationInfo.sourceDir, packageInfo.versionName, packageInfo.versionCode);
                            appInfoList.add(appInfo);
                            break;
                        case 1:
                            break;
                        default:
                            AppInfo appInfo2 = new AppInfo(packageInfo.applicationInfo.loadLabel(getActivity().getPackageManager()).toString(), packageInfo.applicationInfo.loadIcon(getActivity().getPackageManager()), packageInfo.packageName, false, packageInfo.applicationInfo.sourceDir, packageInfo.versionName + "  有新版本未备份", packageInfo.versionCode);
                            appInfoList.add(appInfo2);
                            break;

                    }
                }
            }
        }
    }

    //1 已备份  0未备份 2 有新的版本未备份
    private int isBackuped(PackageInfo packageInfo) {
        int result = 0;
        if (DirManager.apkAll != null) {
            for (AppInfo element : appInfoList2) {
                if ((packageInfo.packageName).equals(element.getPackageName())) {
                    if (packageInfo.versionCode > element.getVersionCode()) {
                        result = 2;
                    } else {
                        result = 1;
                    }
                    break;
                }
            }
        }
        return result;
    }

    private void initData() {
        packages.clear();
        packages = getActivity().getPackageManager().getInstalledPackages(0);
        initFruit();
    }

    private void showData() {
        adapter = new AppInfoAdapter(getActivity(), R.layout.app_info_item, R.color.disableApp, appInfoList);
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                File dir = new File(path_backup);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                mPosition = position;
                mView = view;
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(R.string.Tips_Title);
                String tipsText;
                String BtnText = getString(R.string.Btn_Sure);
                appInfo = appInfoList.get(mPosition);
                tipsText = "你确定要备份" + appInfo.getName() + "吗？";

                dialog.setMessage(tipsText);
                dialog.setPositiveButton(BtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DirManager.needReload = true;
                        String commandText = "cp -f " + appInfo.getappdir() + " \"" + path_backup + appInfo.getPackageName() + ".apk\"";
                        Log.e(TAG, "onClick: " + commandText);
                        Process process = null;
                        DataOutputStream os = null;
                        try {
                            process = Runtime.getRuntime().exec("su"); //切换到root帐号
                            os = new DataOutputStream(process.getOutputStream());
                            os.writeBytes(commandText + "\n");
                            os.writeBytes("exit\n");
                            os.flush();
                            process.waitFor();
                            //View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.app_info_item, null);
                            appInfoList.remove(mPosition);
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e(TAG, "onClick: " + e.getMessage());
                        } finally {
                            try {
                                if (os != null) {
                                    os.close();
                                }
                                assert process != null;
                                process.destroy();
                            } catch (Exception e) {
                                Log.e(TAG, "onClick: " + e.getMessage());
                            }
                        }
                        DirManager.needReload = true;
                    }
                });
                dialog.setCancelable(true);
                dialog.setNegativeButton(R.string.Btn_Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    protected void showProgress() {
        if (dialog == null) {
            dialog = ProgressDialog.show(getActivity(), getString(R.string.Tips_Title), getString(R.string.loadappinfo));
            dialog.show();
        }
    }

    //
    protected void closeProgress() {

        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    private class MyTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected void onPostExecute(String param) {
            showData();
            adapter.notifyDataSetChanged();
            closeProgress();
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... params) {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            initData();
            return null;
        }
    }
/*
    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }*/

}
