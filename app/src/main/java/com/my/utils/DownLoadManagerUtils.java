package com.my.utils;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;


import com.gooddream.learningdriving.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Administrator 下载器工具类
 */
@SuppressLint("InlinedApi")
public class DownLoadManagerUtils {
    private static final String DownLoad_ID = "downloadId";
    private static DownloadManager downloadManager;
    private static DownLoadManagerUtils newInstance;
    private static Context mContext;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
            Y.y("intent" + ""
                    + intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
            queryDownloadStatus(PreferencesHelper.getLong(DownLoad_ID));
        }
    };

    public static DownLoadManagerUtils newInstance(Context context) {
        mContext = context;
        if (null == downloadManager || null == newInstance) {
            synchronized (DownLoadManagerUtils.class) {
                if (null == downloadManager) {
                    downloadManager = (DownloadManager) context
                            .getSystemService(Context.DOWNLOAD_SERVICE);
                }
                if (null == newInstance) {
                    newInstance = new DownLoadManagerUtils();
                }
            }
        }
        return newInstance;
    }

    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     *
     * @param string
     * @return
     */
    private static String encodeGB(String string) {
        // 转换中文编码
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "GB2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0] + "/" + split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");// 处理空格
        split[0] = split[0].replaceAll("%3A", ":");// 处理空格
        return split[0];
    }

    /**
     * @param ids 移除正在下载的
     */
    public void removeTask(long... ids) {
        downloadManager.remove(ids);
    }

    /**
     * <pre>
     * return  -1 未知错误   0:开始正常下载   4:下载暂停
     *    1:STATUS_PENDING 2:STATUS_RUNNING 8:STATUS_SUCCESSFUL
     *
     * @param url      请求的下载url
     * @param fileName 下载的文件名  //例如:"我的歌声里.mp3"
     * @ (destinationInExternalFilesDir)设置下载后文件存放的位置，不设置会存在data/data/com.android.provider.downloads/cache/下面，
     * 设置后存在sd上的Android/data/<包名>/files/下面。
     * 第2个参数是files下再建目录的目录名，第3个参数是文件名，如果第3个参数带路径，要确保路径存在，第2个参数路径随便写，会自己创建
     * 路径不存在要报错  应该是eg:Environment.DIRECTORY_DOWNLOADS
     *
     * <pre/>
     */
    public int requestDownLoad(String url, String fileName, String notifyTitle) {
        Y.y("下载地址：" + url);
        return requestDownLoad(url, 0, true, true, 1,
                Environment.DIRECTORY_DOWNLOADS, "", fileName, notifyTitle);
    }

    public int requestDownLoad(String url, boolean hideNotification,
                               String fileName, String notifyTitle) {
        if (hideNotification) {
            return requestDownLoad(url, 0, true, true, 0,
                    Environment.DIRECTORY_DOWNLOADS, "", fileName, notifyTitle);
        }
        return requestDownLoad(url, 0, true, true, 1,
                Environment.DIRECTORY_DOWNLOADS, "", fileName, notifyTitle);
    }

    /**
     * <pre>
     * return  -1 未知错误   0:开始正常下载   4:下载暂停
     *    1:STATUS_PENDING 2:STATUS_RUNNING 8:STATUS_SUCCESSFUL  100:已存在直接安装
     *
     * @param url                            请求的下载url
     * @param fileName                       下载的文件名  //例如:"我的歌声里.mp3"
     * @param netWorkType                    网络下载环境  0both wifi and mobile 1wifi 2mobile 默认为0
     * @param setVisibleInDownloadsUi        是否希望下载的文件可以被系统的Downloads应用扫描到并管理
     * @param setAllowedOverRoaming          移动网络情况下是否允许漫游
     * @param notificationVisibility         在通知栏显示下载详情，比如百分比。
     *                                       0HIDDEN 1VISIBLE 2VISIBLE_NOTIFY_COMPLETED
     *                                       3VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION default VISIBLE
     * @param destinationInExternalFilesDir  设置下载后文件存放的位置，不设置会存在data/data/com.android.provider.downloads/cache/下面，
     *                                       设置后存在sd上的Android/data/<包名>/files/下面。
     *                                       第2个参数是files下再建目录的目录名，第3个参数是文件名，如果第3个参数带路径，要确保路径存在，第2个参数路径随便写，会自己创建
     * @param destinationInExternalPublicDir 以sd卡路径为根路径,与上方法只有一个有效。第一个参数创建文件夹用的是mkdir,
     *                                       路径不存在要报错  应该是eg:Environment.DIRECTORY_DOWNLOADS
     *
     *                                       <pre/>
     */
    public int requestDownLoad(String url, int netWorkType,
                               boolean setVisibleInDownloadsUi, boolean setAllowedOverRoaming,
                               int notificationVisibility, String destinationInExternalPublicDir,
                               String destinationInExternalFilesDir, String fileName,
                               String notifyTitle) {
        try {
            int queryDownloadStatus = queryDownloadStatus(
                    PreferencesHelper.getLong(fileName), fileName);
            Y.y("queryDownloadStatus:" + queryDownloadStatus);
            switch (queryDownloadStatus) {
                case -1:
                    PreferencesHelper.putLong("downState", 0l);
                    break;
                // 1
                case DownloadManager.STATUS_PENDING:
                    PreferencesHelper.putLong("downState", 1l);
                    return 1;
                // 2
                case DownloadManager.STATUS_RUNNING:
                    PreferencesHelper.putLong("downState", 2l);
                    return 2;
                // 4
                case DownloadManager.STATUS_PAUSED:
                    PreferencesHelper.putLong("downState", 4l);
                    return 4;
                // 8
                case DownloadManager.STATUS_SUCCESSFUL:
                    PreferencesHelper.putLong("downState", 8l);
                    return 8;
                // 16
                case DownloadManager.STATUS_FAILED:
                    PreferencesHelper.putLong("downState", 0l);
                    break;

                default:
                    PreferencesHelper.putLong("downState", 0l);
                    break;
            }
            if (0 == PreferencesHelper.getLong("downState")) {
                // 创建下载请求
                Y.y("开始下载了...................");
                Y.y("地址解析：" + Uri.parse(encodeGB(url)));
                //            DownloadManager.Request requestDownLoad = new DownloadManager.Request(
                //                   Uri.parse(url));
                DownloadManager.Request requestDownLoad = new DownloadManager.Request(
                        Uri.parse(encodeGB(url)));
                Y.y("netWorkType：" + netWorkType);
                // 设置允许使用的网络类型
                if (netWorkType == 0) {
                    requestDownLoad
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                                    | DownloadManager.Request.NETWORK_WIFI);
                } else if (netWorkType == 1) {
                    requestDownLoad
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                } else if (netWorkType == 2) {
                    requestDownLoad
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
                } else {
                    requestDownLoad
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                                    | DownloadManager.Request.NETWORK_WIFI);
                }
                Y.y("notificationVisibility：" + notificationVisibility);
                // 禁止发出通知，既后台下载
                if (notificationVisibility == 0) {
                    requestDownLoad
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                } else if (notificationVisibility == 1) {
                    requestDownLoad
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                } else if (notificationVisibility == 1) {
                    requestDownLoad
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                } else if (notificationVisibility == 1) {
                    requestDownLoad
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
                } else {
                    requestDownLoad
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                }
                Y.y("setVisibleInDownloadsUi：" + setVisibleInDownloadsUi);
                // 是否显示下载界面
                if (setVisibleInDownloadsUi) {
                    requestDownLoad.setVisibleInDownloadsUi(true);
                } else {
                    requestDownLoad.setVisibleInDownloadsUi(false);
                }
                Y.y("setAllowedOverRoaming：" + setAllowedOverRoaming);
                // 判断漫游
                if (setAllowedOverRoaming) {
                    requestDownLoad.setAllowedOverRoaming(true);
                } else {
                    requestDownLoad.setAllowedOverRoaming(false);
                }
                String tempPath = null;
                // 设置下载路径
                try {
                    if (Utils.isSDcardExist()) {
                        PreferencesHelper.putBoolean("SDAvailable", true);
                        if (null != destinationInExternalFilesDir
                                && !"".equals(destinationInExternalFilesDir)) {
                            // 完全定义的路径存在
                            requestDownLoad.setDestinationInExternalFilesDir(
                                    mContext, destinationInExternalFilesDir,
                                    fileName);
                            tempPath = destinationInExternalFilesDir;
                        } else if (null != destinationInExternalPublicDir
                                && !"".equals(destinationInExternalPublicDir)) {
                            // 非完全定义的路径不存在,则设置系统路径
                            requestDownLoad.setDestinationInExternalPublicDir(
                                    destinationInExternalPublicDir, fileName);
                            tempPath = destinationInExternalPublicDir;
                        }
                    } else {
                        Y.y("SDcard is not available ...");
                        PreferencesHelper.putBoolean("SDAvailable", false);
                        try {
                            requestDownLoad.setDestinationInExternalPublicDir(
                                    destinationInExternalPublicDir, fileName);
                            tempPath = destinationInExternalFilesDir;
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    try {
                        requestDownLoad.setDestinationInExternalPublicDir(
                                destinationInExternalPublicDir, fileName);
                        tempPath = destinationInExternalFilesDir;
                    } catch (Exception e2) {
                        // TODO Auto-generated catch block
                    }
                }
                requestDownLoad.setTitle(notifyTitle);
                Y.y("开始加入请求队列11111：");
                try {
                    Y.y("判断问价是否存在：1:" + tempPath);
                    tempPath = Environment.getExternalStorageDirectory().getPath() + File.separator + tempPath;
                    File f = new File(tempPath);
                    Y.y("判断问价是否存在：2:" + Environment.getExternalStorageDirectory().getPath());
                    Y.y("判断问价是否存在：2:" + Environment.getExternalStorageDirectory().getAbsolutePath());
                    if (f.exists()) {
                        Y.y("判断问价是否存在：3" + f.getPath());
                        Y.y("判断问价是否存在：3" + f.getAbsolutePath());
                        Y.y("判断问价是否存在：3" + f.getName());
                        File[] files = f.listFiles();
                        Y.y("判断问价是否存在：4");
                        if (null != files) {
                            int len = files.length;
                            Y.y("判断问价是否存在：5-----" + len);
                            for (int i = 0; i < len; i++) {
                                Y.y("判断问价是否存在：6===" + i);
                                //文件包含了apk
                                try {
                                    if (files[i].isFile() && files[i].exists() && files[i].getName().contains(mContext.getResources().getString(R.string.app_name))) {
                                        Y.y("判断问价是否存在：7===" + files[i].getPath());
                                        if (PackageManagerUtil.isApkCanInstall(mContext, File.separator + File.separator + files[i].getPath())) {
                                            Y.y("判断问价是否存在：versionCode:" + Utils.getVersionCode(mContext));
                                            if (PackageManagerUtil.getApkVersionCode(mContext, File.separator + File.separator + files[i].getPath()) >= Utils.getVersionCode(mContext)) {
                                                installAPK(mContext, File.separator + File.separator + files[i].getPath());
                                                Y.y("判断问价是否存在：8===成功安装");
                                                return 100;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    Y.y("判断问价是否存在：8" + e.getMessage());
                                }
                            }
                        }
                        Y.y("判断问价是否存在：9");
                    }
                } catch (Exception e) {
                    Y.y("判断问价是否存在：e:" + e.getMessage());
                    e.printStackTrace();
                }
                Y.y("开始加入请求队列123456：");
                // 将下载请求放入队列
                long id = downloadManager.enqueue(requestDownLoad);
                Y.y("开始加入请求队列22222：");
                // 保存id
                PreferencesHelper.putString("fileName", fileName);
                PreferencesHelper.putLong(fileName, id);
                return 0;
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    // registerReceiver(receiver, new
    // IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    /**
     * @param filterById 查询下载的id
     * @param fileName   在sharepreference里面存的fileName键
     * @return 查询下载状态
     */
    public int queryDownloadStatus(long filterById, String fileName) {
        try {
            Y.y("查询：" + "开始0.1  " + filterById + "  " + fileName);
            DownloadManager.Query query = new DownloadManager.Query();
            Y.y("查询：" + "开始0.2");
            query.setFilterById(filterById);
            Y.y("查询：" + "开始0.3");
            Cursor c = downloadManager.query(query);
            Y.y("查询：" + "开始");
            String path = null;
            if (c.moveToFirst()) {
                Y.y("查询：" + "开始1");
                int status = c.getInt(c
                        .getColumnIndex(DownloadManager.COLUMN_STATUS));
                path = c.getString(c
                        .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                Y.y("查询status：" + status);
                Y.y("查询path：" + path);
                c.close();
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        Y.y("down" + "STATUS_PAUSED");
                        return DownloadManager.STATUS_PAUSED;
                    case DownloadManager.STATUS_PENDING:
                        Y.y("down" + "STATUS_PENDING");
                        return DownloadManager.STATUS_PENDING;
                    case DownloadManager.STATUS_RUNNING:
                        // 正在下载，不做任何事情
                        Y.y("down" + "STATUS_RUNNING");
                        return DownloadManager.STATUS_RUNNING;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        // 完成
                        Y.y("down" + "下载完成");
//                        if (PackageManagerUtil.isApkCanInstall(mContext, path)) {
//                            if (!TextUtils.isEmpty(path))
//                                installAPK(mContext, path);
//                            return DownloadManager.STATUS_SUCCESSFUL;
//                        } else {
                        return -1;
//                        }
                    case DownloadManager.STATUS_FAILED:
                        // 清除已下载的内容，重新下载
                        Y.y("down" + "STATUS_FAILED");
                        if (null == fileName || "".equals(fileName)) {
                            PreferencesHelper.putLong(DownLoad_ID, 0l);
                        } else {
                            PreferencesHelper.putLong("downState", 0l);
                        }
                        return DownloadManager.STATUS_FAILED;
                    default:
                        return -1;
                }

            } else {
                Y.y("查询：" + "end  -1");
                return -1;
            }
        } catch (Exception e) {
            Y.y("查询：" + "Exception  -1" + e.getMessage());
            return -1;
        }
    }

    public int queryDownloadStatus(long filterById) {
        return queryDownloadStatus(filterById, null);
    }

    private void installAPK(Context context, String path) {
        try {
            Y.y("installAPK:00" + "  " + PreferencesHelper.getBoolean("SDAvailable"));
            if (PreferencesHelper.getBoolean("SDAvailable")) {
                Y.y("installAPK:" + 11);
                PackageManagerUtil.installApk(context, path);// SD卡可用
            } else {
                Y.y("installAPK:" + 22);
                File file = new File(
                        Environment.getExternalStoragePublicDirectory("Download"),
                        DownloadManager.COLUMN_TITLE);
                if (file.exists()) {
                    PackageManagerUtil.installApk(context, file.getPath());
                }
            }
        } catch (Exception e) {
        }
    }
}
