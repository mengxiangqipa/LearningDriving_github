package com.my.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PackageManagerUtil {
    /**
     * <pre>
     * 获取已安装程序列表包名
     *
     * @param context
     * @param name
     * @return <pre/>
     */
    public static String getInstallAppPackage(Context context, String name) {
        PackageManager manager = context.getPackageManager();
        List<ApplicationInfo> aiList = manager
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo ai : aiList) {
            if ((ApplicationInfo.FLAG_SYSTEM & ai.flags) == 0) {
                if (name.equals(manager.getApplicationLabel(ai).toString())) {
                    return ai.packageName;
                }
            }
        }
        return "";
    }

    /**
     * <pre>
     * 打开应用程序
     *
     * @param context
     * @param packageName
     * @throws Exception <pre/>
     */
    public static void openApp(Context context, String packageName)
            throws Exception {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(packageName, 0);
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);
        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String pkg = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(pkg, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    /**
     * <pre>
     * 卸载程序
     *
     * @param context
     * @param pkg     <pre>
     */
    public static void unInstallApp(Context context, String package1) {
        Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:"
                + package1));
        context.startActivity(intent);
    }

    /**
     * <pre>
     *
     * @param apkUrl 安装APP
     *               <p>
     *               <pre/>
     */
    public static void installApk(Context context, String apkUrl) {
        if (null == apkUrl) {
            return;
        }
        if (apkUrl.endsWith(".apk")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (apkUrl.startsWith("file:")) {
                intent.setDataAndType(Uri.parse(apkUrl.toString()),
                        "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(new File(apkUrl)),
                        "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        } else {
            // ToastFactory.getToast(context, "请到" + apkUrl + "解压安装！", true)
            // .show();
        }
    }

    //
    // @SuppressWarnings("deprecation")
    // public static void notityMe(Context context, String key, String apkUrl) {
    // if (apkUrl.endsWith(".apk")) {
    // NotificationManager manager = (NotificationManager) context
    // .getSystemService(Context.NOTIFICATION_SERVICE);
    // Notification notification = new Notification(
    // R.drawable.ic_launcher, "下载完成", System.currentTimeMillis());
    // notification.flags |= Notification.FLAG_AUTO_CANCEL;
    // Intent intent = new Intent(Intent.ACTION_VIEW);
    // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // if (apkUrl.startsWith("file:")) {
    // intent.setDataAndType(Uri.parse(apkUrl.toString()),
    // "application/vnd.android.package-archive");
    // } else {
    // intent.setDataAndType(Uri.fromFile(new File(apkUrl)),
    // "application/vnd.android.package-archive");
    // }
    // PendingIntent pintent = PendingIntent.getActivity(context, 0,
    // intent, PendingIntent.FLAG_UPDATE_CURRENT);
    // notification.setLatestEventInfo(context, "下载完成",
    // key + " 下载完成点击安装！", pintent);
    // manager.notify(0, notification);
    // } else {
    // ToastFactory.getToast(context, "请到" + apkUrl + "解压安装！", true)
    // .show();
    // }
    // }

    /**
     * 检测SDcard是否可用
     *
     * @return
     */
    public static boolean inspectSDcardIsAvailable() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;// usable
        } else {
            return false;// unusable
        }
    }

    public static String getTargetPath(Context context, String url,
                                       String fileName) {
        String extention = url.substring(url.lastIndexOf("."));
        return setLocalePath(context) + fileName + System.currentTimeMillis()
                + extention;
    }

    private static String setLocalePath(Context context) {
        if (PackageManagerUtil.inspectSDcardIsAvailable()) {
            String path = Environment
                    .getExternalStoragePublicDirectory("fruits")
                    + File.separator + "apks";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath() + File.separator;
        } else {
            String path = context.getCacheDir() + File.separator + "apks";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String[] args = {"chmod", "705", file.getAbsolutePath()};
            PackageManagerUtil.exec(args);
            return file.getAbsolutePath() + File.separator;
        }
    }

    /**
     * 执行Linux命令，并返回执行结果。
     */
    public static String exec(String[] args) {
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    /**
     * <pre>
     * scrollTo方法可以调整view的显示位置。 在需要的地方调用以下方法即可。
     * scroll表示外层的view，inner表示内层的view，其余内容都在inner里。 注意，方法中开一个新线程是必要的。
     * 否则在数据更新导致换行时getMeasuredHeight方法并不是最新的高度。
     *
     * @param scroll
     * @param inner  <pre/>
     */
    public static void scrollToBottom(final View scroll, final View inner) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (scroll == null || inner == null) {
                    return;
                }
                int offset = inner.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }
                scroll.scrollTo(0, offset);
            }
        });
    }

    /**
     * 判断apk文件是否能安装
     * @param mContext
     * @param filePath apk路劲
     * @return
     */
    public static boolean isApkCanInstall(Context mContext, String filePath) {
        Y.y("isApkCanInstall:1");
        try {
            PackageManager pm = mContext.getPackageManager();
            Y.y("isApkCanInstall:2");
            PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            Y.y("isApkCanInstall:3");
            if (info != null) {
                Y.y("isApkCanInstall:true");
                return true;
            } else {
                Y.y("isApkCanInstall:false");
                return false;
            }
        } catch (Exception e) {
            Y.y("isApkCanInstall:false");
            return false;
        }
    }

    /**
     *获取指定路径中apk的版本code
     * @param mContext
     * @param filePath
     * @return  -1为非完整apk文件
     */
    public static int getApkVersionCode(Context mContext, String filePath) {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                return info.versionCode;
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }
    public static int getCurrentApkVersionCode(Context mContext) {
        try {
            int code = 0;
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(),
                    0);
            code = info.versionCode;
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    /**
     * 获取应用名称
     *
     * @return
     */
    public static String getAppName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            CharSequence applicationLabel = packageManager
                    .getApplicationLabel(packageManager.getApplicationInfo(
                            context.getPackageName(), 0));
            return applicationLabel.toString();
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
    /**
     * 获取版本号
     *
     * @return
     */
    public static String getVersion(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "1.0.0";
        }
    }
}
