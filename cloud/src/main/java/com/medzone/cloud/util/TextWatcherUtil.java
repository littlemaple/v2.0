package com.medzone.cloud.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextWatcherUtil implements TextWatcher {
	private CharSequence temp;
	private int editStart;
	private int editEnd;
	private EditText mEditText;
	private Context context;

	public TextWatcherUtil(Context context, EditText mEditText) {
		this.context = context;
		this.mEditText = mEditText;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		temp = s;

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		temp = s;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		// editStart = mEditText.getSelectionStart();
		// editEnd = mEditText.getSelectionEnd();
		// String inputStr = "";
		// try {
		// inputStr = new String(temp.toString().getBytes("gb2312"),
		// "iso-8859-1");
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// if (inputStr.length() > 80) {
		// ErrorDialogUtil.showErrorToast(context,
		// ProxyErrorCode.TYPE_MEASURE,
		// ProxyErrorCode.LocalError.CODE_10003);
		// s.delete(editStart - 1, editEnd);
		// int tempSelection = editStart;
		// mEditText.setText(s);
		// mEditText.setSelection(tempSelection);
		// }
	}

}
