package com.my.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.view.KeyEvent;

import com.gooddream.learningdriving.R;


public class CustomProgressDialogUtils {
	public static CustomProgressDialog customProgress;
	public static Handler h;

	public static void dismissProcessDialog() {
		try {
			h.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (customProgress != null) {
						customProgress.dismiss();
						customProgress = null;
					}
				}
			});
		} catch (Exception e) {

		}
	}

	public static ProgressDialog showProcessDialog(Context ac, String message) {
		try {
			if (customProgress != null) {
				try {
					customProgress.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				customProgress.setMessage(message);
				return customProgress;
			}
			customProgress = new CustomProgressDialog(ac, message,
					R.drawable.customprogressdialog_anim);
			// customProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			customProgress.setMessage(message);
			customProgress.setCancelable(false);
			customProgress.setIndeterminate(false);
			customProgress.setCancelable(false);

			try {
				// h.post(new Runnable() {
				//
				// @Override
				// public void run() {
				if (null != customProgress) {
					customProgress.show();
				}
				// }
				// });

			} catch (Exception e) {
				e.printStackTrace();
			}
			customProgress.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
									 KeyEvent event) {
					// h.post(new Runnable() {
					// @Override
					// public void run() {
					if (null != customProgress) {
						customProgress.dismiss();
					}
					customProgress = null;

					// }
					// });
					return false;
				}
			});
			return customProgress;
		} catch (Exception e) {
			return null;
		}
	}
}
