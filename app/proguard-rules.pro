# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\andriod_studio\android_sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
 ##################################################################################################
    #指定代码的压缩级别
    -optimizationpasses 5

    #包名不混合大小写
    -dontusemixedcaseclassnames

    #不去忽略非公共的库类
#    -dontskipnonpubliclibraryclasses

     #优化  不优化输入的类文件
    -dontoptimize

     #预校验
    -dontpreverify

     #混淆时是否记录日志
    -verbose

     # 混淆时所采用的算法
    -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

    #保护注解
    -keepattributes *Annotation*
    -keepattributes Signature,InnerClasses
    # 保持哪些类不被混淆
    -keep public class * extends android.app.Fragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.support.v4.app.FragmentActivity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    #如果有引用v4包可以添加下面这行
    -keep public class * extends android.support.v4.app.Fragment
    -keepattributes EnclosingMethod
    #忽略警告
    -ignorewarning

    #-------------记录生成的日志数据,gradle build时在本项目根目录输出##
    #apk 包内所有 class 的内部结构
#    -dump class_files.txt
#    #未混淆的类和成员
#    -printseeds seeds.txt
#    #列出从 apk 中删除的代码
#    -printusage unused.txt
#    #混淆前后的映射
#    -printmapping mapping.txt
    #-------------记录生成的日志数据，gradle build时 在本项目根目录输出-end######

     #保持 native 方法不被混淆
        -keepclasseswithmembernames class * {
            native <methods>;
        }

        #保持自定义控件类不被混淆
        -keepclasseswithmembers class * {
            public <init>(android.content.Context, android.util.AttributeSet);
        }

        #保持自定义控件类不被混淆
        -keepclassmembers class * extends android.app.Activity {
           public void *(android.view.View);
        }

        #保持 Parcelable 不被混淆
        -keep class * implements android.os.Parcelable {
          public static final android.os.Parcelable$Creator *;
        }

        #保持 Serializable 不被混淆
        -keepnames class * implements java.io.Serializable

        #保持 Serializable 不被混淆并且enum 类也不被混淆
        -keepclassmembers class * implements java.io.Serializable {
            static final long serialVersionUID;
            private static final java.io.ObjectStreamField[] serialPersistentFields;
            !static !transient <fields>;
            !private <fields>;
            !private <methods>;
            private void writeObject(java.io.ObjectOutputStream);
            private void readObject(java.io.ObjectInputStream);
            java.lang.Object writeReplace();
            java.lang.Object readResolve();
        }

        #保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
        -keepclassmembers enum * {
          public static **[] values();
          public static ** valueOf(java.lang.String);
        }

        -keepclassmembers class * {
            public void *ButtonClicked(android.view.View);
        }

        #不混淆资源类
        -keepclassmembers class **.R$* {
            public static <fields>;
        }
        -keep public class * extends android.view.View {
               public <init>(android.content.Context);
               public <init>(android.content.Context, android.util.AttributeSet);
               public <init>(android.content.Context, android.util.AttributeSet, int);
               public void set*(...);
           }

    #忽略警告/如果引用了v4或者v7包
    -dontwarn android.support.**
    #如果不想混淆 keep 掉
    #keep 友盟
    -keep class com.umeng.**{*;}
    -dontwarn com.umeng.**
    #keep 注解
    -keep class butterknife.**{*;}
    -dontwarn butterknife.**
    -dontwarn butterknife.internal.**
    -keep class **$$ViewBinder { *; }
    -keepclasseswithmembernames class * {
        @butterknife.* <fields>;
    }
    -keepclasseswithmembernames class * {
        @butterknife.* <methods>;
    }
    #keep 银联
    -keep class com.unionpay.**{*;}
    -dontwarn com.unionpay.**
    #keep tencent
    -keep class com.tencent.**{*;}
    -dontwarn com.tencent.**
    #keep baidu
    -keep class com.baidu.** {*;}
    -keep class vi.com.** {*;}
    -dontwarn com.baidu.**

    #keep 长连接
     -keep class io.netty.** {*;}
     -dontwarn io.netty.**
    -keepattributes Signature,InnerClasses
    -keepclasseswithmembers class io.netty.** {*;}
    -keepnames class io.netty.** {*;}

    #keep okio   okhttp3
     -keep class okio.** {*;}
     -dontwarn okio.**

    -keep class org.apache.** {*;}
    -dontwarn org.apache.**
    #keep nineoldandroids
    -keep class com.nineoldandroids.** {*;}
    -dontwarn com.nineoldandroids.**
    #keep volley
    -keep class com.android.volley.** {*;}
    -dontwarn com.android.volley.**
    #keep 二维码
    -keep class com.google.zxing.** {*;}
    -dontwarn com.google.zxing.**
    #keep imageloader
    -keep class com.nostra13.** {*;}
    -dontwarn com.nostra13.**
    -dontwarn java.lang.invoke**
    #项目特殊处理代码


     #keep gaodeMap
    -keep class com.amap.** {*;}
    -dontwarn com.amap.**
    -dontwarn com.amap.api.**
    -keep class com.a.a.**  {*;}
    -dontwarn com.a.a.**
   -keep class com.autonavi.** {*;}
    -dontwarn com.autonavi.**


     -keep class com.yangjie.** {*;}
     -dontwarn com.yangjie.**
