package com.coderstory.FTool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ThemePatch implements IXposedHookLoadPackage{
	
	private void findAndHookMethod(String p1, ClassLoader lpparam, String p2, Object... parameterTypesAndCallback){
		try{
			XposedHelpers.findAndHookMethod(p1, lpparam, p2, parameterTypesAndCallback);
		}catch(Throwable throwable){
			//
		}
	}
	
	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam){
		// 拦截开机自启广播
		findAndHookMethod("com.meizu.customizecenter.common.helper.BootBroadcastReceiver", lpparam.classLoader, "onReceive", Context.class, Intent.class, XC_MethodReplacement.returnConstant(null));
		
		// 拦截试用服务
		findAndHookMethod("com.meizu.customizecenter.common.font.FontTrialService", lpparam.classLoader, "onStartCommand", Intent.class, int.class, int.class, XC_MethodReplacement.returnConstant(0));
		findAndHookMethod("com.meizu.customizecenter.common.theme.ThemeTrialService", lpparam.classLoader, "onStartCommand", Intent.class, int.class, int.class, XC_MethodReplacement.returnConstant(0));
		
		if(lpparam.packageName.equals("com.meizu.customizecenter")){
			
			//device_states | doCheckState
			// 6.11.0 & 6.13.1
			findAndHookMethod("com.meizu.customizecenter.h.al", lpparam.classLoader, "h", Context.class, XC_MethodReplacement.returnConstant(0));
			// 6.12.1
			findAndHookMethod("com.meizu.customizecenter.g.ak", lpparam.classLoader, "h", Context.class, XC_MethodReplacement.returnConstant(0));
			// 6.14.2 & 6.14.3 & 6.14.4 & 6.17.0 & 6.17.3
			findAndHookMethod("com.meizu.customizecenter.h.am", lpparam.classLoader, "h", Context.class, XC_MethodReplacement.returnConstant(0));
			// 7.0.4
			findAndHookMethod("com.meizu.customizecenter.i.am", lpparam.classLoader, "h", Context.class, XC_MethodReplacement.returnConstant(0));
			
			//resetToSystemTheme
			// 6.11.1 & 6.12.1 & 6.13.1 & 6.14.2 & 6.14.3 & 6.14.4 & 6.17.0 & 6.17.3 & 7.0.4
			findAndHookMethod("com.meizu.customizecenter.common.theme.common.b", lpparam.classLoader, "b", XC_MethodReplacement.returnConstant(false));
			findAndHookMethod("com.meizu.customizecenter.common.theme.common.b", lpparam.classLoader, "b", boolean.class, XC_MethodReplacement.returnConstant(null));
			
			//data/data/com.meizu.customizecenter/font/   system_font
			// 6.11.1 & 6.12.1 & 6.13.1 & 6.14.2 & 6.14.3 & 6.14.4 & 6.17.0 & 6.17.3 & 7.0.4
			findAndHookMethod("com.meizu.customizecenter.common.font.c", lpparam.classLoader, "b", XC_MethodReplacement.returnConstant(false));
			
			// notification
			// 6.11.0 & 6.12.1
			findAndHookMethod("com.meizu.customizecenter.common.f.e", lpparam.classLoader, "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
			findAndHookMethod("com.meizu.customizecenter.common.f.e", lpparam.classLoader, "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
			findAndHookMethod("com.meizu.customizecenter.common.f.c", lpparam.classLoader, "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
			findAndHookMethod("com.meizu.customizecenter.common.f.c", lpparam.classLoader, "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
			// 6.13.1 & 6.14.2 & 6.14.3 & 6.14.4 & 6.17.0 & 6.17.3
			findAndHookMethod("com.meizu.customizecenter.common.g.f", lpparam.classLoader, "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
			findAndHookMethod("com.meizu.customizecenter.common.g.f", lpparam.classLoader, "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
			findAndHookMethod("com.meizu.customizecenter.common.g.c", lpparam.classLoader, "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
			findAndHookMethod("com.meizu.customizecenter.common.g.c", lpparam.classLoader, "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
			// 7.0.4
			findAndHookMethod("com.meizu.customizecenter.common.h.g", lpparam.classLoader, "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
			findAndHookMethod("com.meizu.customizecenter.common.h.g", lpparam.classLoader, "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
			findAndHookMethod("com.meizu.customizecenter.common.h.c", lpparam.classLoader, "a", String.class, String.class, int.class, int.class, int.class, XC_MethodReplacement.returnConstant(null));
			findAndHookMethod("com.meizu.customizecenter.common.h.c", lpparam.classLoader, "a", String.class, String.class, int.class, XC_MethodReplacement.returnConstant(null));
			
			//主题混搭 ThemeContentProvider query Unknown URI
			findAndHookMethod("com.meizu.customizecenter.common.dao.ThemeContentProvider", lpparam.classLoader, "query", Uri.class, String[].class, String.class, String[].class, String.class, new XC_MethodHook(){
				@Override
				protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable{
					Object[] objs = param.args;
					String Tag = "(ITEMS LIKE";
					String Tag2 = "%zklockscreen;%";
					String Tag3 = "%com.meizu.flyme.weather;%";
					//XposedBridge.log("开始");
					boolean result = false;
					for(Object obj : objs){
						//XposedBridge.log(obj == null ? "" : obj.toString());
						if(obj instanceof String && (((String)obj).contains(Tag) || obj.equals(Tag2) || obj.equals(Tag3))){
							result = true;
						}
					}
					//XposedBridge.log("结束");
					if(result){
						for(Object obj : objs){
							if(obj instanceof String[]){
								for(int j = 0; j < ((String[])obj).length; j++){
									if(((String[])obj)[j].contains("/storage/emulated/0/Customize/Themes")){
										((String[])obj)[j] = "/storage/emulated/0/Customize%";
									}else if(((String[])obj)[j].contains("/storage/emulated/0/Customize/TrialThemes")){
										((String[])obj)[j] = "NONE";
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


