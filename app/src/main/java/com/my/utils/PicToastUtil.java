package com.my.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gooddream.learningdriving.R;

public class PicToastUtil {
	/**
	 * 自定义toast
	 *
	 * @param text
	 *            文本
	 * @param drawableResId
	 *            drawable资源id
	 * @param gravity
	 *            toast要显示的位置Gravity.CENTER
	 * @param duration
	 *            toast要显示的时间3000ms
	 */
	private static void showPicToast(Context context, String text,
									 int drawableResId, int gravity, int duration, int xOffset,
									 int yOffset) {
		Toast toast = new Toast(context);
		View view = LayoutInflater.from(context).inflate(
				R.layout.inflater_toast, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_toast);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_toast);
		tv.setText(text);
		iv.setImageResource(drawableResId);
		toast.setView(view);
		toast.setGravity(gravity, xOffset, yOffset);
		toast.setDuration(duration);
		toast.show();
	}

	public static void showPicToast(Context context, String text,
									int drawableResId, int gravity, int duration) {
		showPicToast(context, text, drawableResId, gravity, duration, 0, 0);
	}

	public static void showPicToast(Context context, String text,
									int drawableResId, int gravity, int duration, int yOffset) {
		showPicToast(context, text, drawableResId, gravity, duration, 0,
				yOffset);
	}

	public static void showPicToast(Context context, String text) {
		showPicToast(context, text, R.mipmap.ic_launcher, Gravity.CENTER,
				3000, 0, 0);
	}

	public static void showPicToast(Context context, String text, int gravity,
									int yOffset) {
		showPicToast(context, text, R.mipmap.ic_launcher, Gravity.BOTTOM,
				3000, 0, yOffset);
	}
}
