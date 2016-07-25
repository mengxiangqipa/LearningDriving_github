package com.my.utils;

import android.os.Environment;
import android.util.Log;

import com.my.configs.Configs;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * @author Administrator 简化的log输出
 */
public class Y {

	/**
	 * 记录日志到sdcard上
	 *
	 * @param context
	 */
	public static void writeFileToSD(String context) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
			Y.d("TestFile", "SD card is not avaiable/writeable right now.");
			return;
		}
		try {
			String pathName = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ File.separator
					+ "meapp"
					+ File.separator + "logs" + File.separator;
			String fileName = "log.txt";
			File path = new File(pathName);
			File file = new File(pathName + fileName);
			if (!path.exists()) {
				Y.d("TestFile", "Create the path:" + pathName);
				// mkdir() 只能在已经存在的目录中创建创建文件夹。
				// mkdirs() 可以在不存在的目录中创建文件夹。
				path.mkdirs();
			}
			if (!file.exists()) {
				Y.d("TestFile", "Create the file:" + fileName);
				file.createNewFile();
			}
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.write(context.getBytes());
			raf.close();
			// 注释的也是写文件..但是每次写入都会把之前的覆盖..
			/*
			 * String pathName = "/sdcard/"; String fileName = "log.txt"; File
			 * path = new File(pathName); File file = new File(pathName +
			 * fileName); if (!path.exists()) { Log.d("TestFile",
			 * "Create the path:" + pathName); path.mkdir(); } if
			 * (!file.exists()) { Log.d("TestFile", "Create the file:" +
			 * fileName); file.createNewFile(); } FileOutputStream stream = new
			 * FileOutputStream(file); String s = context; byte[] buf =
			 * s.getBytes(); stream.write(buf); stream.close();
			 */
		} catch (Exception e) {
			Y.e("TestFile", "Error on writeFilToSD.", e);
		}
	}

	/**
	 * debug
	 *
	 * @param tag
	 * @param msg
	 */
	public static void d(String tag, String msg) {
		if (Configs.allowLog) {
			Log.d(tag, msg);
		}
	}

	/**
	 * debug
	 *
	 * @param tag
	 * @param msg
	 * @param tr
	 */
	public static void d(String tag, String msg, Throwable tr) {
		if (Configs.allowLog) {
			Log.d(tag, msg, tr);
		}
	}

	/**
	 * error
	 *
	 * @param tag
	 * @param msg
	 */
	public static void e(String tag, String msg) {
		if (Configs.allowLog) {
			Log.e(tag, msg);
		}
	}

	/**
	 * error
	 *
	 * @param tag
	 * @param msg
	 * @param tr
	 */
	public static void e(String tag, String msg, Throwable tr) {
		if (Configs.allowLog) {
			Log.e(tag, msg, tr);
		}
	}

	/**
	 * verbose
	 *
	 * @param tag
	 * @param msg
	 */
	public static void v(String tag, String msg) {
		if (Configs.allowLog) {
			Log.v(tag, msg);
		}
	}

	/**
	 * verbose
	 *
	 * @param tag
	 * @param msg
	 * @param tr
	 */
	public static void v(String tag, String msg, Throwable tr) {
		if (Configs.allowLog) {
			Log.v(tag, msg, tr);
		}
	}

	public static void i(String msg) {
		if (Configs.allowLog) {
			Log.i("my11", msg);
		}
	}

	public static void i(int msg) {
		if (Configs.allowLog) {
			Log.i("my11", msg + "");
		}
	}

	public static void i(String tag, String msg) {
		if (Configs.allowLog) {
			Log.i(tag, msg);
		}
	}

	public static void y(String msg) {
		if (Configs.allowLog) {
			Log.i("yy", msg);
		}
	}

	public static void y(int msg) {
		if (Configs.allowLog) {
			Log.i("yy", msg + "");
		}
	}

}
