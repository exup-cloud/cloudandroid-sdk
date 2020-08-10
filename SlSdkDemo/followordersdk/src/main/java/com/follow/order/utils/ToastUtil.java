package com.follow.order.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.follow.order.FollowOrderSDK;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class ToastUtil {
	private static Toast mToast = null;

	/**
	 * 在确定是主线程时调用
	 *
	 * @param text
	 */
	public static void updateUI(final String text) {
		if ("main".equals(Thread.currentThread().getName())) {
			showToast(text);
		} else {
			if (TextUtils.isEmpty(text)) {
				return;
			}
			Observable.just(text)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Consumer<String>() {
						@Override
						public void accept(String s) throws Exception {
							showToast(s);
						}
					});
		}
	}

	private static void showToast(String text) {
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toast.makeText(FollowOrderSDK.ins().getApplication(), text, Toast.LENGTH_SHORT);
		mToast.show();
	}

	public static void updateLongUI(final String text) {
		if ("main".equals(Thread.currentThread().getName())) {
			showLongToast(text);
		} else {
			if (TextUtils.isEmpty(text)) {
				return;
			}
			Observable.just(text)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Consumer<String>() {
						@Override
						public void accept(String s) throws Exception {
							showLongToast(s);
						}
					});
		}
	}

	private static void showLongToast(String text) {
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toast.makeText(FollowOrderSDK.ins().getApplication(), text, Toast.LENGTH_LONG);
		mToast.show();
	}

	public static void updateUI(final Activity context, final String text) {
		updateUI(text);
	}

	public static void updateUI(final Context context, final String text) {
		updateUI(text);
	}

	private static void updateUI(final Fragment context, final String text) {
		updateUI(text);
	}
}
