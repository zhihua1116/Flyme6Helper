package com.coderstory.FTool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coderstory.FTool.R;
import com.coderstory.FTool.custview.ClearEditText;
import com.coderstory.FTool.utils.file.FileHelper;

import java.io.File;
import java.util.List;

import ren.solid.library.fragment.base.BaseFragment;
import ren.solid.library.utils.ToastUtils;
import ren.solid.library.utils.ViewUtils;

import static com.coderstory.FTool.utils.MyConfig.vMLogDir;
import static com.coderstory.FTool.utils.app.AppUtils.checkVersion;
import static com.coderstory.FTool.utils.MyConfig.kLogPackName;


/**
 * Created by zpp0196 on 2017/12/6.
 */

public class LogFragment extends BaseFragment{

    public static String TAG = "LogFragment";

    private ClearEditText packName;
    private ScrollView mSVLog;
    private HorizontalScrollView mHSVLog;
    private TextView logText;
    private Typeface typeface;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.fragment_log;
    }

    @Override
    protected void init() {
        super.init();
        packName = getContentView().findViewById(R.id.hook_package_name);
        packName.setText(getPrefs().getString(kLogPackName, ""));
        mSVLog = getContentView().findViewById(R.id.mSVLog);
        mHSVLog = getContentView().findViewById(R.id.mHSVLog);
        typeface=Typeface.createFromAsset(getMContext().getAssets(),"fonts/DroidSansMono.ttf");
        logText = getContentView().findViewById(R.id.logText);
        logText.setTypeface(typeface);
        loadLog();

        if(checkVersion(getPrefs())){
            showDialog();
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder =new android.support.v7.app.AlertDialog.Builder(getMContext());
        builder.setTitle("说明");
        TextView msg = new TextView(getMContext());
        int mp = ViewUtils.dp2px(getMContext(), 16);
        msg.setPadding(mp, mp, mp, mp);
        msg.setText("该功能需要 xposed 支持，可以按包名过滤拦截指定应用的日志，包名可以在冻结列表里长按应用复制，留空则不拦截，输入 this 拦截当前应用日志。\n\n" +
                "更改了包名以后需要重启相应的应用才会开始拦截日志。\n\n" +
                "为了避免出现应用无响应的情况，请使用拦截日志功能时再开启。\n\n" +
                "如果应用无响应把下面的文件手动删除即可。\n\n" +
                "日志文件目录：" + vMLogDir);
        builder.setView(msg);
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
     * 加载日志
     */
    private void loadLog(){
        logText.setText("");
        List<String> list = FileHelper.readFile(new File(vMLogDir));
        if(list == null){
            logText.setText("日志为空");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            logText.setText(logText.getText() + list.get(i) + "\n");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_log, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_set_readable:
                String pn = packName.getText().toString();
                getEditor().putString(kLogPackName, pn).apply();
                if(pn.equals("this")){
                    new ToastUtils(getMContext()).showToast("请重启该应用以开始拦截日志！");
                }else if(!pn.equals("")){
                    new ToastUtils(getMContext()).showToast("请重启 " + pn + " 以开始拦截日志！");
                }
                break;
            case R.id.action_reload:
                loadLog();
                break;
            case R.id.action_share:
                share();
                break;
            case R.id.action_about_log:
                showDialog();
                break;
            case R.id.action_clear:
                FileHelper.rewriteFile(vMLogDir, "");
                loadLog();
                break;
            case R.id.action_go_top:
                scrollTop();
                break;
            case R.id.action_go_bottom:
                scrollDown();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void scrollTop() {
        mSVLog.post(new Runnable() {
            @Override
            public void run() {
                mSVLog.scrollTo(0, 0);
            }
        });
        mHSVLog.post(new Runnable() {
            @Override
            public void run() {
                mHSVLog.scrollTo(0, 0);
            }
        });
    }

    private void scrollDown() {
        mSVLog.post(new Runnable() {
            @Override
            public void run() {
                mSVLog.scrollTo(0, logText.getHeight());
            }
        });
        mHSVLog.post(new Runnable() {
            @Override
            public void run() {
                mHSVLog.scrollTo(0, 0);
            }
        });
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(vMLogDir)));
        sendIntent.setType("application/html");
        startActivity(Intent.createChooser(sendIntent, "分享日志"));
    }

}
