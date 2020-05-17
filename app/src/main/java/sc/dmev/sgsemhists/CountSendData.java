package sc.dmev.sgsemhists;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.util.ArrayList;

import sc.dmev.sgsemhists.FormatHttpPostOkHttp.BasicNameValusPostOkHttp;
import sc.dmev.sgsemhists.FormatHttpPostOkHttp.FromHttpPostOkHttp;
import sc.dmev.sgsemhists.bus.BusProvider;
import sc.dmev.sgsemhists.bus.ModelEvenBus;
import sc.dmev.sgsemhists.utile.Utile;
import sc.dmev.sgsemhists.wifi.WifiProvider;

/**
 * Created by Varayut on 28/9/2558.
 */
public class CountSendData {
    private HandlerThread backgroundHandlerThreadCountSms;
    private Handler backgroundHandlerCountSms;
    private Handler mainHandlerCountSms;

    public CountSendData(final String sNameFile,final String sFrom, final String sTo, final String sMsg,
                         final String sDate, final String sTime, final String sError, final String batt,final int countSend,final Context context){//SMS
        backgroundHandlerThreadCountSms = new HandlerThread("backgroundHandlerThreadCountSms");
        backgroundHandlerThreadCountSms.start();
        backgroundHandlerCountSms = new Handler(backgroundHandlerThreadCountSms.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //run with background
                onShowLogCat("Count Send",msg.obj + " : " +msg.arg1);
                if (msg.arg1 < 30){
                    Message msgBack = new Message();
                    msgBack.arg1 = (msg.arg1 + 1);//นับเวลา 30 วินาที เริ่มต้นที่ 1
                    msgBack.obj = msg.obj;
                    backgroundHandlerCountSms.sendMessageDelayed(msgBack,1 * 1000);
                }else {
                    Message msgMainSms = new Message();
                    msgMainSms.arg1 = 1;//สถานะส่งใหม่อีกครั้ง
                    msgMainSms.obj = msg.obj;
                    mainHandlerCountSms.sendMessage(msgMainSms);
                }
            }
        };
        mainHandlerCountSms = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //run with main thread
                if (msg.arg1 == 1){
                    new SendData(sNameFile,sFrom,sTo,sMsg,sDate,sTime,sError,batt,(countSend + 1),context);//ส่งใหม่อีกที
                }
            }
        };

        Message msgBack = new Message();
        msgBack.arg1 = 1;//นับเวลา 30 วินาที เริ่มต้นที่ 1
        msgBack.obj = sNameFile;
        backgroundHandlerCountSms.sendMessageDelayed(msgBack,1 * 1000);
    }

    private void onShowLogCat(String tag , String msg){
        if (BuildConfig.DEBUG){
            Log.e("CountSendData",tag + " : " + msg);
        }
    }
}
