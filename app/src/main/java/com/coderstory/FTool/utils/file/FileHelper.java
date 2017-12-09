package com.coderstory.FTool.utils.file;

import android.content.Context;
import android.util.Log;

import com.coderstory.FTool.utils.root.ShellUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.coderstory.FTool.utils.MyConfig.vMLogDir;

public class FileHelper {

    private static String TAG = "FileHelper";

    public static boolean rewriteFile(String path, String text) {
        if (path.equals(vMLogDir)) {
            return writeFileByWriter(new File(path), text, false);
        }
        return false;
    }

    public static boolean writeFileByWriter(String path, String text, boolean append) {
        if (path.equals(vMLogDir)) {
            return writeFileByWriter(new File(path), text, append);
        }
        return false;
    }

    public static boolean writeFileByWriter(File file, String text, boolean append){
        FileWriter writer;
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdir();
        }
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer = new FileWriter(file, append);
            if (append) {
                writer.append(text);
            } else {
                writer.write(text);
            }
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> readFile(File file){
        FileReader reader;
        List<String> fileText = new ArrayList<>();
        if(!file.exists()){
            return null;
        }
        try {
            reader = new FileReader(file);
            BufferedReader bufferReader = new BufferedReader(reader);
            String line;
            while ((line = bufferReader.readLine()) != null) {
                fileText.add(line);
            }
            bufferReader.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileText;
    }

    public static boolean setReadable(Context context){
        final File dataDir = new File(context.getApplicationInfo().dataDir);
        final File prefsDir = new File(dataDir, "shared_prefs");
        final File prefsFile = new File(prefsDir, "com.coderstory.FTool_preferences.xml");
        boolean result = false;
        if (prefsFile.exists()) {
            ShellUtils.CommandResult commandResult = ShellUtils.execCommand("chmod 777 " + prefsFile, true);
            result = commandResult.result == 0;
            Log.i(TAG, "setReadable:" + (result ? "success" : "failure"));
        }
        return result;
    }

    public static String getReadableFileSize(long size) {
        String[] units = new String[]{"K", "M", "G", "T", "P"};
        double nSize = size * 1L * 1.0f;
        double mod = 1024.0f;
        int i = 0;
        while (nSize >= mod) {
            nSize /= mod;
            i++;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        return String.format("%s %s", df.format(nSize), units[i]);
    }

    /**
     * 从Assets中读取文本
     *
     * @param FileName 文件名
     * @param mContext context
     * @return 读取到的文本
     */
    public String getFromAssets(String FileName, Context mContext) {
        try {
            InputStreamReader inputReader = new InputStreamReader(mContext.getAssets().open(FileName), "utf-8");
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line + "\n";
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "e:" + e);
            return "";
        }
    }
}
