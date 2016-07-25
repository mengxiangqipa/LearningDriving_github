package com.my.broadcast;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.my.utils.PackageManagerUtil;
import com.my.utils.PreferencesHelper;
import com.my.utils.Y;

import java.io.File;


/**
 * @author Administrator 下载完成监听器
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {
	// private static final String DL_ID = "downloadId";
	private DownloadManager manager;

	@Override
	public void onReceive(Context context, Intent intent) {
		manager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		Query query = new Query();
		String action = intent.getAction();
		if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			query.setFilterById(id);
			Cursor cursor = manager.query(query);
			String path = null;
			while (cursor.moveToNext()) {
				path = cursor.getString(cursor
						.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				// MLog.i( "sdcard path = " + path);
			}
			cursor.close();
			// 如果sdcard不可用时下载下来的文件，那么这里将是一个内容提供者的路径，这里打印出来，有什么需求就怎么样处理
			if (null != path && path.startsWith("content:")) {
				cursor = context.getContentResolver().query(Uri.parse(path),
						null, null, null, null);
				while (cursor.moveToNext()) {
					path = cursor.getString(cursor.getColumnIndex("_data"));
					// MLog.i( "provider path = " + path);
				}
				cursor.close();
			}
			queryDownloadStatus(context, query, path);
		} else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
			// Toast.makeText(
			// context,
			// "点击 通知,"
			// + DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS,
			// Toast.LENGTH_LONG).show();
			queryDownloadStatus(context, query, "");
		}
	}

	/**
	 * 查询下载状态
	 *
	 * @param context
	 * @param query
	 * @param path
	 */
	private void queryDownloadStatus(Context context, Query query, String path) {
		query.setFilterById(PreferencesHelper.getLong(PreferencesHelper
				.getString("fileName")));
		Cursor cursor = manager.query(query);
		if (cursor.moveToFirst()) {
			int status = cursor.getInt(cursor
					.getColumnIndex(DownloadManager.COLUMN_STATUS));
			switch (status) {
			case DownloadManager.STATUS_PAUSED:
				// Log.v(TAG, "STATUS_PAUSED");
			case DownloadManager.STATUS_PENDING:
				Log.v("down", "STATUS_PENDING");
			case DownloadManager.STATUS_RUNNING:
				// 正在下载，不做任何事情
				// Log.v(C, "STATUS_RUNNING");
				Intent intent = new Intent(
						DownloadManager.ACTION_VIEW_DOWNLOADS);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				break;
			case DownloadManager.STATUS_SUCCESSFUL:
				// 完成
				// Log.v(TAG, "下载完成");
				if (!TextUtils.isEmpty(path))
					installAPK(context, path);
				break;
			case DownloadManager.STATUS_FAILED:
				// 清除已下载的内容，重新下载
				// Log.v(TAG, "STATUS_FAILED");
				manager.remove(PreferencesHelper.getLong(PreferencesHelper
						.getString("fileName")));
				PreferencesHelper.remove(PreferencesHelper
						.getString("fileName"));
				break;
			}
		}
	}

	private void installAPK(Context context, String path) {
		if (PreferencesHelper.getBoolean("SDAvailable")) {
			Y.y("installAPK:" + 1);
			Y.y("installAPK:" + path);
			PackageManagerUtil.installApk(context, path);// SD卡可用
		} else {
			Y.y("installAPK:" + 2);
			File file = new File(
					Environment.getExternalStoragePublicDirectory("Download"),
					DownloadManager.COLUMN_TITLE);
			if (file.exists()) {
				PackageManagerUtil.installApk(context, file.getPath());
			}
		}
	}
}
