package com.yjkj.chainup.util;

import android.widget.EditText;

public class EdittextUtil {

	/**
	 * 设置EditText是否可编辑
	 */
	public static void setEditTextEditable(EditText editText, boolean isEdit) {
		if (isEdit) {
			if(!editText.isFocusable()){
				editText.requestFocus();
			}
		} else {
			editText.clearFocus();
		}
		editText.setCursorVisible(isEdit);
		editText.setFocusable(isEdit);
		editText.setFocusableInTouchMode(isEdit);
	}


	public static void setEditTextFocus(EditText editText, boolean isFocus) {
		if (isFocus) {
			editText.setFocusableInTouchMode(true);
			editText.setFocusable(true);
		} else {
			editText.setFocusableInTouchMode(false);
			editText.clearFocus();
		}
	}

}
