package com.my.configs;

/**
 * 配置文件
 */

/**
 * @author Administrator
 */
public class Configs {
    /**
     * 服务器地址内网
     */
//    public static String BASE_SERVER_URL = "http://192.168.1.99:8080";
//    public static String BASE_IMAGE_URL = "http://192.168.1.120:8080/gpsGuard/";
//    public static String SHARE = "http://192.168.1.120:8080/gpsGuard/wap/user/reg_user.jsp?";
//    public static String KeepAliveURL = "192.168.1.120";
//    public static int KeepAlivePORT = 8099;
    /**
     * 服务器地址外网
     */
    public static String BASE_SERVER_URL = "https://test.haomeng.com";
    public static String BASE_IMAGE_URL = "http://192.168.1.120:8080/gpsGuard/";
    public static String SHARE = "http://192.168.1.120:8080/gpsGuard/wap/user/reg_user.jsp?";
    public static String KeepAliveURL = "192.168.1.120";
    public static int KeepAlivePORT = 8099;
    //    /**
//     * 服务器地址内网
//     */
//    public static String BASE_SERVER_URL = "http://192.168.1.120:8080/gpsGuard/entrance.pk";
//    public static String BASE_IMAGE_URL = "http://192.168.1.120:8080/gpsGuard/";
//    public static String SHARE = "http://192.168.1.120:8080/gpsGuard/wap/user/reg_user.jsp?";
//    public static String KeepAliveURL = "192.168.1.120";
//    public static int KeepAlivePORT = 8099;
//    /**
//     * 服务器地址外网
//     */
//    public static String BASE_SERVER_URL = "http://www.me2015.cn:8080/GPS/entrance.pk";
//    public static String BASE_IMAGE_URL = "http://www.me2015.cn:8080/GPS/";
//    public static String SHARE = "http://www.me2015.cn:8080/GPS/wap/user/reg_user.jsp?";
//    public static String KeepAliveURL = "59.54.54.244";
//    public static int KeepAlivePORT = 8099;
    // 验证码
    public static String captcha_register = "/api/sms/send_reg_code";
    // 忘记密码 验证码
    public static String captcha_forget = "verifyCodeNotLogin";
    // 注册手机号
    public static String register = "/api/student/register";
    /**
     * 登录
     */
    public static String login = "/api/student/login";
    public static String banner = "/api/media/index_ad_rotate";
    public static String banner_enquiries = "/api/info/index_ad_rotate";
    public static String navigation = "/api/info/nav_list";
    public static String infoList = "/api/info/list";
    public static String ad_scrollBar = "/api/platform/student_track";
    public static String commentList = "/api/teacher/comments";
    public static String coachList = "/api/area/area_complex";
    public static String cityList = "/api/teacher/area_classify_summary";
    public static String vehicleTypeList = "/api/product/all_product_name";
    public static String personalInfo = "/api/student/my_info";
    public static String coachDetail = "/api/teacher/teacherDetail";
    public static String signUp = "/api/student/signupOrUpdateSubject";
    public static String getConstant = "/api/platform/static_data";
    public static String uploadImage = "/api/student/photoUpload";
    public static String apply = "/api/student/study_flow";
    public static String myCoach = "/api/student/my_teacher_info";
    public static String myCoachHovor = "/api/student/my_choose_info";
    // 忘记密码
    public static String getCaptchaForgetpwd = "/api/sms/send_pwd_update_code";
    public static String resetPWD = "/api/student/reset_pwd";
    public static String applyState = "/api/student/my_operates";
    public static String complaintLabel = "/api/platform/complain_labels";
    public static String complaint = "/api/student/complainTeacher";
    public static String tips = "/api/student/study_flow_tip";
    public static String update = "/api/platform/app_version_update";
    public static String loginOther = "loginThird";
    // list
    public static String list = "controlledList";
    // 闪屏
    public static String startup = "startup";
    //
    public static String version = "appUpdate";
    //
    public static String systemMsg = "systemMsg";

    public static String redpacketBalance = "redpacketPoolBalance";
    public static String vipExpiryQuery = "vipExpiryQuery";
    public static String points = "controlledLocations";
    public static String recList = "queryRecs";
    /**
     * <pre>
     * 是否允许日志输出。
     * true 允许Log
     * false 不允许Log
     * <pre/>
     */
    public static boolean allowLog = true;
    public static String SUCCESS = "20000";
    public static String reLogin = "500000";
}
