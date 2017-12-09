package com.coderstory.FTool.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.coderstory.FTool.R;
import com.coderstory.FTool.adapter.AppInfo;
import com.coderstory.FTool.adapter.AppInfoAdapter;
import com.coderstory.FTool.utils.root.SuHelper;
import com.yalantis.phoenix.PullToRefreshView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import ren.solid.library.fragment.base.BaseFragment;
import ren.solid.library.utils.SnackBarUtils;
import ren.solid.library.utils.ToastUtils;

import static ren.solid.library.utils.FileUtils.readFile;
import static ren.solid.library.utils.ClipboardUtils.setText;

/**
 * A simple {@link Fragment} subclass.
 */
public class DisbaleAppFragment extends BaseFragment {

    // private Context mContext=null;
    List<PackageInfo> packages = new ArrayList<>();
    AppInfoAdapter adapter = null;
    RecyclerView recyclerView = null;
    AppInfo appInfo = null;
    int mposition = 0;
    View mview = null;
    com.yalantis.phoenix.PullToRefreshView mPullToRefreshView;
    AlertDialog mydialog;
    private List<AppInfo> appInfoList = new ArrayList<>();
    private List<AppInfo> appInfoList2 = new ArrayList<>();
    private Dialog dialog;
    public static String TAG = "DisbaleAppFragment";
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            ((ProgressDialog) dialog).setMessage("OK，正在刷新列表。");
            initData();
            adapter.notifyDataSetChanged();
            dialog.cancel();
            dialog = null;
            super.handleMessage(msg);
        }
    };

    private void initData() {
        packages = new ArrayList<>();
        packages = getActivity().getPackageManager().getInstalledPackages(0);
        initFruit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.menu_disableapp_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void initFruit() {
        //packageInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0 表示是系统应用
        appInfoList.clear();
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo appInfo = new AppInfo(packageInfo.applicationInfo.loadLabel(getActivity().getPackageManager()).toString(), packageInfo.applicationInfo.loadIcon(getActivity().getPackageManager()), packageInfo.packageName, !packageInfo.applicationInfo.enabled, String.valueOf(packageInfo.versionName));
            appInfoList.add(appInfo);
        }
    }

    private void showData() {
        adapter = new AppInfoAdapter(getActivity(), packages, appInfoList);
        adapter.setOnItemClickListener(new AppInfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mposition = position;
                mview = view;
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(R.string.Tips_Title);
                String tipsText;
                String BtnText = getString(R.string.Btn_Sure);
                appInfo = appInfoList.get(mposition);
                if (appInfo.getDisable()) {
                    tipsText = getString(R.string.sureAntiDisable) + "「" +appInfo.getName() + "」" + getString(R.string.sureAntiDisableAfter);
                } else {
                    tipsText = getString(R.string.sureDisable) + "「" +appInfo.getName() + "」" + getString(R.string.sureDisableAfter);
                }
                dialog.setMessage(tipsText);
                dialog.setPositiveButton(BtnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String commandText = (!appInfo.getDisable() ? "pm disable " : "pm enable ") + appInfo.getPackageName();
                        Log.d(TAG, commandText);
                        Process process = null;
                        DataOutputStream os = null;
                        try {
                            process = Runtime.getRuntime().exec("su"); //切换到root帐号
                            os = new DataOutputStream(process.getOutputStream());
                            os.writeBytes(commandText + "\n");
                            os.writeBytes("exit\n");
                            os.flush();
                            process.waitFor();
                            // View rootView = LayoutInflater.from(mContext).inflate(R.layout.app_info_item, null);

                            if (appInfo.getDisable()) {
                                appInfo.setDisable(false);
                                appInfoList.set(mposition, appInfo);
                                mview.setBackgroundColor(0xffffffff); //正常的颜色
                            } else {
                                appInfo.setDisable(true);
                                appInfoList.set(mposition, appInfo);
                                mview.setBackgroundColor(0xffc1e0f4); //冻结的颜色
                            }

                        } catch (Exception ignored) {

                        } finally {
                            try {
                                if (os != null) {
                                    os.close();
                                }
                                assert process != null;
                                process.destroy();
                            } catch (Exception ignored) {
                            }
                        }
                        adapter.notifyDataSetChanged();
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

        adapter.setOnItemLongClickListener(new AppInfoAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                appInfo = appInfoList.get(position);
                String packName = appInfo.getPackageName();
                setText(getMContext(), packName);
                new ToastUtils(getMContext()).showToast(packName);
            }
        });

        recyclerView = getContentView().findViewById(R.id.listView);
        assert recyclerView != null;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_disbale_app;
    }

    @Override
    protected void init() {
        super.init();
        Toast.makeText(getActivity(), R.string.disableapptips, Toast.LENGTH_LONG).show();

        new MyTask().execute();

        mPullToRefreshView = getContentView().findViewById(R.id.pull_to_refresh);

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

    protected void showProgress() {
        if (dialog == null) {
            dialog = ProgressDialog.show(getActivity(), getString(R.string.Tips_Title), getString(R.string.loadappinfo));
            dialog.show();
        }
    }

    protected void closeProgress() {

        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_backupList) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("备份列表");
            String tipsText = "你确定要备份当前系统应用的冻结列表吗?";
            dialog.setMessage(tipsText);
            dialog.setPositiveButton(getString(R.string.Btn_Sure), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    satrtBackuop();
                }
            });
            dialog.setCancelable(true);
            dialog.setNegativeButton(R.string.Btn_Cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            mydialog = dialog.create();
            mydialog.show();

        } else if (item.getItemId() == R.id.action_restoreList) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("还原设置");
            String tipsText = "你确定要还原当前系统应用的冻结状态吗?";
            dialog.setMessage(tipsText);
            dialog.setPositiveButton(getString(R.string.Btn_Sure), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    restoreList();
                    new MyTask().execute();
                }
            });
            dialog.setCancelable(true);
            dialog.setNegativeButton(R.string.Btn_Cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            mydialog = dialog.create();
            mydialog.show();
        }

        return false;
    }


    private void restoreList() {
        String CrashFilePath = Environment.getExternalStorageDirectory().getPath() + "/FTool/backup/";
        File dir = new File(CrashFilePath);
        String fileName = "userList";
        String content = "";
        if (!dir.exists()) {
            SnackBarUtils.makeShort($(R.id.listView), "未找到备份列表！").danger();
            return;
        }
        try {
            content = readFile(CrashFilePath + fileName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (content.isEmpty()) {
            SnackBarUtils.makeShort($(R.id.listView), "未找到备份列表！").danger();
            return;
        }

        final String[] list = content.split("\n");

        dialog = ProgressDialog.show(getActivity(), "提示", "OK,正在恢复配置！");
        dialog.show();

        new Thread(new Runnable() {
            public void run() {
                disableHelp dh = new disableHelp(list);
                dh.execute();
                myHandler.sendMessage(new Message());
            }
        }).start();


    }

    private void satrtBackuop() {
        StringBuilder SB = new StringBuilder("#已备份的系统APP冻结列表#\n");

        //遍历数据源
        for (AppInfo info : appInfoList) {
            if (info.getDisable()) { //判断是否被冻结
                SB.append(info.getPackageName() + "\n");
            }
        }
        String CrashFilePath = Environment.getExternalStorageDirectory().getPath() + "/FTool/backup/";
        File dir = new File(CrashFilePath);
        String fileName = "userList";
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileOutputStream fos = null;
        String result = "";
        try {
            fos = new FileOutputStream(CrashFilePath + fileName);
            fos.write(SB.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        if (result.equals("")) {
            SnackBarUtils.makeShort($(R.id.listView), "OK,列表备份完成！").show();
        } else {
            SnackBarUtils.makeShort($(R.id.listView), "备份失败,一般是因为APP没读写存储权限导致的！" + result).show();
        }
    }

    class MyTask extends AsyncTask<String, Integer, String> {

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

    class disableHelp extends SuHelper {
        String[] list;

        disableHelp(String[] list) {
            this.list = list;
        }

        @Override
        protected ArrayList<String> getCommandsToExecute() throws UnsupportedEncodingException {
            ArrayList<String> mylist = new ArrayList<>();

            for (String item : list) {
                mylist.add("pm disable " + item);
            }
            return mylist;
        }
    }
}

