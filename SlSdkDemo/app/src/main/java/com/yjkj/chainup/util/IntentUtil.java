package com.yjkj.chainup.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/*
 * 页面的跳转，Intent常量的设置
 */
public class IntentUtil {

	/*
	 * 无回调的
	 */
	public static void activityForward(Context activity, Class clazz,
			Bundle bundle, boolean isFinish) {
		Intent intent = new Intent(activity, clazz);
		if (null != bundle)
			intent.putExtras(bundle);
		activity.startActivity(intent);
		if (isFinish && activity instanceof Activity)
			((Activity) activity).finish();
	}
	
	/*
	 * 5.0以上页面跳转动画特性
	 */
	public static void avForwardAnima(Context activity, Class clazz,
			Bundle bundle, boolean isFinish) {
		Intent intent = new Intent(activity, clazz);  

		if (null != bundle)
			intent.putExtras(bundle);
		//activity.startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
		if (isFinish && activity instanceof Activity)
			((Activity) activity).finish();
	}

	/*
	 * 可回调的
	 */
	public static void startActivityForResult(Activity activity, Class clazz,
			int requestCode, Bundle bundle) {
		Intent intent = new Intent(activity, clazz);
		if (null != bundle) {
			intent.putExtras(bundle);
			activity.startActivityForResult(intent, requestCode);
		} else {
			activity.startActivityForResult(intent, requestCode);
		}
	}

	/*
	 * 启动一个服务
	 */
	public static void serviceForward(Context activity, Class clazz,
			Bundle bundle, boolean isFinish) {
		Intent intent = new Intent(activity, clazz);
		if (null != bundle)
			intent.putExtras(bundle);
		activity.startService(intent);
		if (isFinish && activity instanceof Activity)
			((Activity) activity).finish();
	}

	/*
	 * 启动Android默认浏览器
	 */
	public static void forwardBrowse(Context activity,String url){
		if(!StringUtil.isHttpUrl(url))
			return;
		Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse(url));
		activity.startActivity(intent);
	}

	/*
	 * 如果已经启动了四个Activity：A，B，C和D。在D Activity里，我们要跳到B Activity，同时希望C finish掉，
	 * 可以在startActivity(intent)里的intent里添加flags标记
	 */
	public static void activityForward(Context context, Class clazz) {
		Intent intent = new Intent(context, clazz);
		//intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); 不重建B，复用B
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}
}