package com.coderstory.FTool.adapter;

import android.graphics.drawable.Drawable;

/**
 * Created by cc on 2015/12/11. 单个app info的模型
 */
public class AppInfo {

    private String name;

    public void setImageId(Drawable imageId) {
        this.imageId = imageId;
    }

    private Drawable imageId;

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    //private String AppDir;
    private boolean isDisable = true;
    private String packageName = "";
    private String Version = "";

    public void setName(String name) {
        this.name = name;
    }

    public AppInfo(){

    }

    public AppInfo(String name, Drawable imageId, String packageName, boolean Disable, String version) {
        this.name = name;
        this.imageId = imageId;
        this.packageName = packageName;
        this.isDisable = Disable;
        this.Version = version;
    }

    public Drawable getImageId() {
        return this.imageId;
    }

    public String getVersion() {
        return this.Version;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getName() {
        return this.name;
    }

    public boolean getDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        this.isDisable = disable;
    }
}
