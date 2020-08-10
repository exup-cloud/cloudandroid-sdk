package com.yjkj.chainup.extra_service.analytics;

import android.content.Context;

import com.yjkj.chainup.app.AppConfig;

import java.util.Map;

/*
 * umeng统计
 */
public class UmenStatisticsUtil {

	public static void init(Context context){
		if(AppConfig.needUmengStatistics){
			//UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, "5b7f9c0df29d983bc90000cd");
		}
	}
	public static void onResume(Context context){
		if(AppConfig.needUmengStatistics){
			//MobclickAgent.onResume(context);
		}
	}
	
	public static void onPause(Context context){
		if(AppConfig.needUmengStatistics){
			//MobclickAgent.onPause(context);
		}
	}
	
	/*
	 * 自定义事件
	 */
	public static void onEvent(Context context,String event){
		if(AppConfig.needUmengStatistics){
			//MobclickAgent.onEvent(context, event);
		}
	}

	public static void onEvent(Context context,String event,String label){
		if(AppConfig.needUmengStatistics){
			//MobclickAgent.onEvent(context, event,label);
		}
	}
	
	/*
	 * 自定义事件
	 */
	public static void onEvent(Context context,String event,Map<String,String> maps){
		if(AppConfig.needUmengStatistics){
			//MobclickAgent.onEvent(context, event, maps);
		}
	}

	/*
	 * 自定义异常错误上传
	 */
	public static void reportError(Context context,Object error){
		if(AppConfig.needUmengStatistics){
			/*if(NetworkUtil.isNetworkConnected(context)){
				if(error instanceof Throwable){
					//MobclickAgent.reportError(context, (Throwable)error);
				}else if(error instanceof String){
					//MobclickAgent.reportError(context, (String) error);
				}
			}*/
		}
	}
}
