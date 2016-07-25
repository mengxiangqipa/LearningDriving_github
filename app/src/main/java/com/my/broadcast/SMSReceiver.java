package com.my.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.widget.EditText;

import com.my.utils.SMSutils;
import com.my.utils.Y;

/**
 * Created by Administrator on 2015/11/2 0002.
 * <uses-permission android:name="android.permission.RECEIVE_SMS" />
 */
public class SMSReceiver extends BroadcastReceiver {
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    EditText mEditText;
    String phoneNum;
    String contains;
    int lenth;

    public SMSReceiver(EditText mEditText, String phoneNum, String contains, int lenth) {
        super();
        this.mEditText = mEditText;
        this.phoneNum = phoneNum;
        this.lenth = lenth;
        this.contains = contains;
    }

    public SMSReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            Y.y("我是接到消息：111");
            try {
                SmsMessage[] messages = getMessagesFromIntent(intent);
                for (SmsMessage message : messages) {
                    Y.y(message.getOriginatingAddress() + " : " +
                            message.getDisplayOriginatingAddress() + " : " +
                            message.getDisplayMessageBody() + " : " +
                            message.getTimestampMillis());
                    String smsContent = message.getDisplayMessageBody();
                    Y.y("接收短信内容:" + smsContent);
                    Y.y("接收短信:null == mEditText:" + (null == mEditText));
                    if (null != mEditText) {
                        //                    mEditText.setText(SMSutils.filterFromSMS(,4));
                        try {
                            if (null != SMSutils.getLatestCaptcha((Activity) context, phoneNum, contains, lenth)) {
                                mEditText.setText(SMSutils.getLatestCaptcha((Activity) context, phoneNum, contains, lenth));
                                mEditText.requestFocus();
                                mEditText.setSelection(lenth);
                            }
                            Y.y("呀呀呀：" + SMSutils.getLatestCaptcha((Activity) context, phoneNum, contains, lenth));
                        } catch (Exception e) {
                            Y.y("接收短信：" + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    Y.y(smsContent);
                }
            } catch (Exception e) {
            }
        }
    }

    public SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];
        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }
}