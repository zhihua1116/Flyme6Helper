package com.coderstory.FTool.module;

import android.util.Log;

import com.coderstory.FTool.BuildConfig;
import com.coderstory.FTool.plugins.IModule;
import com.coderstory.FTool.utils.file.FileHelper;

import java.util.Date;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.coderstory.FTool.utils.MyConfig.kLogPackName;
import static com.coderstory.FTool.utils.MyConfig.vMLogDir;
import static com.coderstory.FTool.utils.MyConfig.xsp;
import static com.coderstory.FTool.utils.MyConfig.XPOSED_TAG;
import static com.coderstory.FTool.utils.MyConfig.LOG_METHOD;
import static com.coderstory.FTool.utils.MyConfig.M_LOG_TAG;

/**
 * Created by zpp0196 on 2017/12/7.
 */

public class HookLog implements IModule{

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {

        final String packName1 = lpparam.packageName;
        xsp.reload();
        final String packName2 = xsp.getString(kLogPackName, "");

        // 仅拦截当前应用的 Log
        if(packName1.equals(BuildConfig.APPLICATION_ID) && packName2.equals("this")) {
            /**
             * 循环判断拦截 Log.x(String tag, String msg)
             * 不支持 Log.x(String tag, String msg, Throwable tr)
             */
            for (int i = 0; i < LOG_METHOD.length; i++) {
                final int j = i;
                findAndHookMethod(Log.class, LOG_METHOD[j], String.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        // Log 过滤
                        for(String tag : M_LOG_TAG ){
                            if(tag.equals(param.args[0])){
                                if(!param.args[0].equals(XPOSED_TAG)){
                                    write(param.args[0], param.args[1], LOG_METHOD[j].toUpperCase());
                                }
                            }
                        }
                    }
                });

                findAndHookMethod(Log.class, LOG_METHOD[j], String.class, String.class, Throwable.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        // Log 过滤
                        for(String tag : M_LOG_TAG ){
                            if(tag.equals(param.args[0])){
                                if(!param.args[0].equals(XPOSED_TAG)){
                                    write(param.args[0], String.valueOf(param.args[1]) + "\n" + Log.getStackTraceString((Throwable) param.args[2]), LOG_METHOD[j].toUpperCase());
                                }
                            }
                        }
                    }
                });
            }
        }
        // 拦截所有应用的 Log
        else if(packName1.equals(packName2)){
            /**
             * 循环判断拦截 Log.x(String tag, String msg)
             * 不支持 Log.x(String tag, String msg, Throwable tr)
             */
            for (int i = 0; i < LOG_METHOD.length; i++) {
                final int j = i;
                findAndHookMethod(Log.class, LOG_METHOD[j], String.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        // 排除 Xposed 的 Log
                        if(!param.args[0].equals(XPOSED_TAG))
                            write(param.args[0], param.args[1], getUid(lpparam), LOG_METHOD[j].toUpperCase(), packName1);
                    }
                });
                findAndHookMethod(Log.class, LOG_METHOD[j], String.class, String.class, Throwable.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        // Log 过滤
                        if(!param.args[0].equals(XPOSED_TAG))
                            write(param.args[0], String.valueOf(param.args[1]) + "\n" + Log.getStackTraceString((Throwable) param.args[2]), getUid(lpparam), LOG_METHOD[j].toUpperCase(), packName1);
                    }
                });
            }
        }

        /*
         XposedBridge.log(String text) 方法实际上是调用了 Log.i(String tag, String msg)
         所以直接拦截 Log.i(String tag, String msg) 和 Log.e(String tag, String msg)
         原理没问题但就是拦截不到

        findAndHookMethod(Log.class, "i", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                // 过滤 Xposed Log
                if(param.args[0].equals(XPOSED_TAG))
                    writeX(param.args[1], getUid(lpparam), "I");
            }
        });

        findAndHookMethod(Log.class, "e", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                // 过滤 Xposed Log
                if(param.args[0].equals(XPOSED_TAG))
                    writeX(param.args[1], getUid(lpparam), "E");
            }
        });
        */
    }

    /**
     * 获取日志前面的时间格式
     * @return
     */
    private String getDate(){
        Date date = new Date();
        StringBuilder sb = new StringBuilder();
        String month = String.format("%tm", date);
        String day = String.format("%td", date);
        String hour = String.format("%tH", date);
        String minute = String.format("%tM", date);
        String second = String.format("%tS", date);
        String millisecond = String.format("%tL", date);

        sb.append(month);
        sb.append("-");
        sb.append(day);
        sb.append(" ");
        sb.append(hour);
        sb.append(":");
        sb.append(minute);
        sb.append(":");
        sb.append(second);
        sb.append(".");
        sb.append(millisecond);
        return sb.toString();
    }

    /**
     * 将拦截到的本应用的 Log 写入文件中
     * 为了方便阅读所以去掉了 packagename
     * @param tag
     * @param msg
     * @param type
     */
    private void write(Object tag, Object msg, String type){
        for(String m : ((String)msg).split("\n")){
            StringBuilder sb = new StringBuilder();
            sb.append(getDate());
            sb.append(" ");
            sb.append(type);
            sb.append("/");
            sb.append(" ");
            sb.append(tag);
            sb.append(": ");
            sb.append(m);
            sb.append("\n");
            FileHelper.writeFileByWriter(vMLogDir, sb.toString(), true);
        }
    }

    /**
     * 将拦截到的所有应用的 Log 写入文件中
     * @param tag
     * @param msg
     * @param uid
     * @param type
     * @param packName
     */
    private void write(Object tag, Object msg, String uid, String type, String packName){
        for(String m : ((String)msg).split("\n")) {
            StringBuilder sb = new StringBuilder();
            sb.append(getDate());
            sb.append(" ");
            sb.append(uid);
            sb.append(" ");
            sb.append("/");
            sb.append(packName);
            sb.append(" ");
            sb.append(type);
            sb.append("/");
            sb.append(" ");
            sb.append(tag);
            sb.append(": ");
            sb.append(m);
            sb.append("\n");
            FileHelper.writeFileByWriter(vMLogDir, sb.toString(), true);
        }
    }

    /**
     * 将拦截到的 Xposed Log 写入到文件中
     * @param msg
     * @param uid
     * @param type
     */
    private void writeX(Object msg, String uid , String type){
        for(String m : ((String)msg).split("\n")) {
            StringBuilder sb = new StringBuilder();
            sb.append(getDate());
            sb.append(" ");
            sb.append(type);
            sb.append("/");
            sb.append("Xposed");
            sb.append(" ");
            sb.append("(");
            sb.append(uid);
            sb.append(")");
            sb.append(": ");
            sb.append(m);
            sb.append("\n");
            FileHelper.writeFileByWriter(vMLogDir, sb.toString(), true);
        }
    }

    private static void findAndHookMethod(Class p1, String p2, Object... parameterTypesAndCallback) {
        try {
            XposedHelpers.findAndHookMethod(p1, p2, parameterTypesAndCallback);
        } catch (Throwable localString3) {
            // 这里最好不要打印日志 同下面的获取进程 UID
        }
    }

    /**
     * 获取进程 UID
     * 如果不处理异常可能会开机时无限拦截 Log.e(String tag, String msg)
     * 导致无法开机
     * @param lpparam
     * @return
     */
    private String getUid(XC_LoadPackage.LoadPackageParam lpparam){
        try{
            return String.valueOf(lpparam.appInfo.uid);
        }catch (NullPointerException e){
            return "?";
        }
    }

}
