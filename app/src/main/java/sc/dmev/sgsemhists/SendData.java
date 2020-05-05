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
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import sc.dmev.sgsemhists.FormatHttpPostOkHttp.BasicNameValusPostOkHttp;
import sc.dmev.sgsemhists.FormatHttpPostOkHttp.FromHttpPostOkHttp;
import sc.dmev.sgsemhists.utile.Utile;
import sc.dmev.sgsemhists.wifi.WifiProvider;

/**
 * Created by Varayut on 28/9/2558.
 */
public class SendData {
    private Handler handlerSms,handlerSendSms;
    private HandlerThread backgroundHandlerThreadSms;
    private Handler backgroundHandlerSms;
    private Handler mainHandlerSms;

    public SendData(final String sFrom, final String sTo, final String sMsg, final String sDate, final String sTime, final String sError, final String batt,final Context context){//SMS
        onShowLogCat("Check SMS",isConnectingToInternet(context) + "");
        backgroundHandlerThreadSms = new HandlerThread("backgroundHandlerThreadSms");
        backgroundHandlerThreadSms.start();
        //backgroundHandlerThreadSms.quit();
        backgroundHandlerSms = new Handler(backgroundHandlerThreadSms.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //run with background
                boolean isNetwork = isInternetWeb(getStringShare(context,Utile.SHARE_PING_IP,"www.google.com"));
                Message msgMainSms = new Message();
                msgMainSms.arg1 = (isNetwork) ? 1 : -1;
                msgMainSms.arg2 = msg.arg2;
                mainHandlerSms.sendMessage(msgMainSms);
            }
        };
        mainHandlerSms = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //run with main thread
                onShowLogCat("Check SMS","ตรวจสอบ wifi " + msg.arg1 + " : " + msg.arg2);
                if (msg.arg1 == 1){// เน็ตใช้งานได้
                    onPostSmsToServer(sFrom, sTo, sMsg, sDate, sTime,sError,batt,context,msg.arg2 + 1);
                }else {
                    int noWifi = (msg.arg2 + 1);
                    if (noWifi == 1){
                        String wifi1 = getStringShare(context,Utile.SHARE_NAME_WIFI1,"");
                        if (wifi1.toString().trim().length() > 0){
                            onConWifiSms(wifi1,getStringShare(context,Utile.SHARE_PASS_WIFI1,""),context,msg.arg2 + 1);
                        }else {
                            onQuitThreadSms();
                        }
                    }else if (noWifi == 2){
                        String wifi2 = getStringShare(context,Utile.SHARE_NAME_WIFI2,"");
                        if (wifi2.toString().trim().length() > 0){
                            onConWifiSms(wifi2,getStringShare(context,Utile.SHARE_PASS_WIFI2,""),context,msg.arg2 + 1);
                        }else {
                            onQuitThreadSms();
                        }
                    }else if (noWifi == 3){
                        String wifi3 = getStringShare(context,Utile.SHARE_NAME_WIFI3,"");
                        if (wifi3.toString().trim().length() > 0){
                            onConWifiSms(wifi3,getStringShare(context,Utile.SHARE_PASS_WIFI3,""),context,msg.arg2 + 1);
                        }else {
                            onQuitThreadSms();
                        }
                    }else if (noWifi == 4){
                        String wifi4 = getStringShare(context,Utile.SHARE_NAME_WIFI4,"");
                        if (wifi4.toString().trim().length() > 0){
                            onConWifiSms(wifi4,getStringShare(context,Utile.SHARE_PASS_WIFI4,""),context,msg.arg2 + 1);
                        }else {
                           onQuitThreadSms();
                        }
                    }else if (noWifi == 5){
                        String wifi5 = getStringShare(context,Utile.SHARE_NAME_WIFI5,"");
                        if (wifi5.toString().trim().length() > 0){
                            onConWifiSms(wifi5,getStringShare(context,Utile.SHARE_PASS_WIFI5,""),context,msg.arg2 + 1);
                        }else {
                            onQuitThreadSms();
                        }
                    }else {
                        onQuitThreadSms();
                    }
                }
            }
        };


        handlerSms = new android.os.Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //Run with Main Thread
                if (WifiProvider.getInstance(context).isWifiEnabled() == false){// Wifi Off
                    if (isConnectingToInternet(context)){
                        //เริ่มต้นตรวจสอบเน็ตใช้งานได้ไหม
                        Message msgBack = new Message();
                        msgBack.arg1 = -1;//สถานะเน็ตใช้ได้ไหม
                        msgBack.arg2 = 0;//เชื่อมต่อ wifi ตัวที่ 0
                        backgroundHandlerSms.sendMessageDelayed(msgBack,1 * 1000);
                    }else {
                        WifiProvider.getInstance(context).setWifiEnabled(true);
                        //ถ้ายังไม่เปิด สั่งเปิดแล้วอีก 10 วินาที ตรวจสอบใหม่
                        onShowLogCat("Check SMS","กำลังเปิด Wifi อีก 10 วิ ตรวจสอบใหม่");
                        Message msgMain = new Message();
                        msgMain.arg1 = 0;
                        msgMain.arg2 = msg.arg2;
                        handlerSms.sendMessageDelayed(msgMain,(1000 * 10));
                    }
                }else {
                    if (isConnectingToInternet(context)) {//Connect Internet
                        onShowLogCat("Check SMS","เปิด Wifi เชื่อมต่อแล้ว และกำลังส่งข้อมูล...");
                        //เริ่มต้นตรวจสอบเน็ตใช้งานได้ไหม
                        Message msgBack = new Message();
                        msgBack.arg1 = -1;//สถานะเน็ตใช้ได้ไหม
                        msgBack.arg2 = msg.arg2;//เชื่อมต่อ wifi ตัวที่
                        backgroundHandlerSms.sendMessageDelayed(msgBack,1 * 1000);

                    }else {//On Wifi bus No Connect Internet
                        Message msgMain = new Message();
                        msgMain.arg1 = msg.arg1 + 1;
                        msgMain.arg2 = msg.arg2;
                        if (msg.arg1 >= 10){// Check Connect 10 loop/ms
                            msgMain.arg2 = msg.arg2 + 1;
                            onShowLogCat("Check SMS","เปิด Wifi เปลี่ยน wifi " + (msg.arg2 + 1));
                            //Connect Wifi new network
                            int noWifi = (msg.arg2 + 1);
                            if (noWifi == 1){
                                String wifi1 = getStringShare(context,Utile.SHARE_NAME_WIFI1,"");
                                if (wifi1.toString().trim().length() > 0){
                                    onConWifiSms(wifi1,getStringShare(context,Utile.SHARE_PASS_WIFI1,""),context,msg.arg2 + 1);
                                }else {
                                    onQuitThreadSms();
                                }
                            }else if (noWifi == 2){
                                String wifi2 = getStringShare(context,Utile.SHARE_NAME_WIFI2,"");
                                if (wifi2.toString().trim().length() > 0){
                                    onConWifiSms(wifi2,getStringShare(context,Utile.SHARE_PASS_WIFI2,""),context,msg.arg2 + 1);
                                }else {
                                    onQuitThreadSms();
                                }
                            }else if (noWifi == 3){
                                String wifi3 = getStringShare(context,Utile.SHARE_NAME_WIFI3,"");
                                if (wifi3.toString().trim().length() > 0){
                                    onConWifiSms(wifi3,getStringShare(context,Utile.SHARE_PASS_WIFI3,""),context,msg.arg2 + 1);
                                }else {
                                    onQuitThreadSms();
                                }
                            }else if (noWifi == 4){
                                String wifi4 = getStringShare(context,Utile.SHARE_NAME_WIFI4,"");
                                if (wifi4.toString().trim().length() > 0){
                                    onConWifiSms(wifi4,getStringShare(context,Utile.SHARE_PASS_WIFI4,""),context,msg.arg2 + 1);
                                }else {
                                    onQuitThreadSms();
                                }
                            }else if (noWifi == 5){
                                String wifi5 = getStringShare(context,Utile.SHARE_NAME_WIFI5,"");
                                if (wifi5.toString().trim().length() > 0){
                                    onConWifiSms(wifi5,getStringShare(context,Utile.SHARE_PASS_WIFI5,""),context,msg.arg2 + 1);
                                }else {
                                    onQuitThreadSms();
                                }
                            }else {
                                onQuitThreadSms();
                            }
                        }else {
                            //เปิดแล้วแต่ยังไม่เชื่อมต่อ
                            onShowLogCat("Check SMS","เปิด Wifi ตรวจสอบ new wifi " + msg.arg1);
                            handlerSms.sendMessageDelayed(msgMain,(1000 * 1));
                        }
                    }
                }
            }
        };
        onShowLogCat("Check SMS","เริ่มตรวจสอบ");
        Message msgMain = new Message();
        msgMain.arg1 = 0;//ตรวจสอบกี่รอบ
        msgMain.arg2 = 0;//wifi ที่
        handlerSms.sendMessageDelayed(msgMain,(1000 * 1));

        //ส่งข้อความเมื่อไม่สามารถเชื่อมต่อ wifi ได้
        handlerSendSms = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //run on main thread
                try {
                    String data = (String) msg.obj.toString().trim();
                    JSONObject jObject = new JSONObject(CoverStringFromServer_One(data.toString().trim()));
                    onShowLogCat("Check Sms",jObject.getString("status").toString().trim() + " : " + jObject.getString("msg").toString().trim());
                    if (jObject.getString("status").toString().trim().equals("1")){
                        if (backgroundHandlerThreadSms != null){
                            backgroundHandlerThreadSms.quit();
                        }
                    }else {
                        Message msgMain = new Message();
                        msgMain.arg1 = 0;
                        msgMain.arg2 = msg.arg2;
                        handlerSms.sendMessageDelayed(msgMain,(1000 * 10));
                    }
                }catch (Exception e){
                    onShowLogCat("Check Sms","Error get data result from server " + e.getMessage());
                    Message msgMain = new Message();
                    msgMain.arg1 = 0;
                    msgMain.arg2 = msg.arg2;
                    handlerSms.sendMessageDelayed(msgMain,(1000 * 10));
                }
            }
        };

    }
    private void onConWifiSms(String user, String pass,Context context,int msg2) {
        onShowLogCat("onConWifiSms",user + " : " + pass);
        if (user.toString().trim().length() > 0 && pass.toString().trim().length() > 0){
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", user.toString().trim());
            wifiConfig.preSharedKey = String.format("\"%s\"", pass.toString().trim());
            int netId = WifiProvider.getInstance(context).addNetwork(wifiConfig);
            WifiProvider.getInstance(context).disconnect();
            WifiProvider.getInstance(context).enableNetwork(netId, true);
            WifiProvider.getInstance(context).reconnect();
        }
        Message msgMain = new Message();
        msgMain.arg1 = 0;
        msgMain.arg2 = msg2;
        handlerSms.sendMessageDelayed(msgMain,(1000 * 10));
    }
    private void onPostSmsToServer(final String sFrom, final String sTo, final String sMsg, final String sDate, final String sTime, final String sError,final String batt, final Context context, final int arg2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String URL_DATA = "http://" + getStringShare(context,Utile.SHARE_URL, "") + "/app/save_sms2.php";//ส่ง SMS 2
                onShowLogCat("URL_DATA",URL_DATA);
                try{
                    ArrayList<FromHttpPostOkHttp> paramsSms = new ArrayList<FromHttpPostOkHttp>();
                    paramsSms.add(new BasicNameValusPostOkHttp().BasicNameValusPostOkHttp("from", sFrom.toString().trim()));
                    paramsSms.add(new BasicNameValusPostOkHttp().BasicNameValusPostOkHttp("to", sTo.toString().trim()));
                    paramsSms.add(new BasicNameValusPostOkHttp().BasicNameValusPostOkHttp("msg", sMsg.toString().trim()));
                    paramsSms.add(new BasicNameValusPostOkHttp().BasicNameValusPostOkHttp("date", sDate.toString().trim()));
                    paramsSms.add(new BasicNameValusPostOkHttp().BasicNameValusPostOkHttp("time", sTime.toString().trim()));
                    paramsSms.add(new BasicNameValusPostOkHttp().BasicNameValusPostOkHttp("error", sError.toString().trim()));
                    paramsSms.add(new BasicNameValusPostOkHttp().BasicNameValusPostOkHttp("batt", batt.toString().trim()));

                    OkHttpClient client = new OkHttpClient();
                    MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
                    for (int i = 0;i<paramsSms.size();i++){
                        onShowLogCat("Check Data SMS", paramsSms.get(i).getKEY_POST().toString() + " : " + paramsSms.get(i).getVALUS_POST().toString());
                        multipartBuilder.addFormDataPart(paramsSms.get(i).getKEY_POST().toString(),paramsSms.get(i).getVALUS_POST().toString());
                    }
                    RequestBody requestBody = multipartBuilder.build();
                    Request request = new Request.Builder()
                            .url(URL_DATA)
                            .post(requestBody)
                            //.post(RequestBody.create(MEDIA_TYPE_MARKDOWN,String.valueOf(requestBody)))
                            .build();

                    Response response = client.newCall(request).execute();
                    String data = response.body().string().toString() + "";
                    onShowLogCat("*** Data SMS ***", data);

                    if (backgroundHandlerThreadSms != null){
                        backgroundHandlerThreadSms.quit();
                    }

                    /*Message msgMainResultSms = new Message();
                    msgMainResultSms.arg2 = arg2;
                    msgMainResultSms.obj = data;
                    handlerSendSms.sendMessage(msgMainResultSms);*/
                }catch (Exception e){
                    if (backgroundHandlerThreadSms != null){
                        backgroundHandlerThreadSms.quit();
                    }
                    onShowLogCat("*** Err ***", "send data sms " + e.getMessage());
                }
            }
        }).start();

    }
    private void onQuitThreadSms(){
        if (backgroundHandlerThreadSms != null){
            backgroundHandlerThreadSms.quit();
        }
        onShowLogCat("Check Sms","Quit ThreadSms");
    }

    public boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
        //return true;
    }
    public String getStringShare(Context mContext,String strKey, String strDe) {
        SharedPreferences shLang;
        shLang = mContext.getSharedPreferences(Utile.SHARE_DATA, Context.MODE_PRIVATE);
        String strShare = shLang.getString(strKey, strDe);
        return strShare;
    }
    public int getIntShare(Context mContext, String strKey, int strDe) {
        SharedPreferences shLang;
        shLang = mContext.getSharedPreferences(Utile.SHARE_DATA, Context.MODE_PRIVATE);
        int strShare = shLang.getInt(strKey, strDe);
        return strShare;
    }
    public String CoverStringFromServer_One(String strData){
        try {
            return  strData.substring(strData.indexOf("{"), strData.lastIndexOf("}") + 1);
        } catch (Exception e) {
            return "";
        }
    }


    /*//---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }*/

    public boolean isInternetHost(final String host){
        try {
            return InetAddress.getByName(host).isReachable(5 * 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean isInternetWeb(final String web){
        try {
            //return InetAddress.getByName(host).isReachable(5 * 1000);
            InetAddress ipAddr = InetAddress.getByName(web); //You can replace it with your name
            return !ipAddr.equals("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean isInternetAvailable(){
        String command = "ping -c 1 google.com";
        try {
            return (Runtime.getRuntime().exec (command).waitFor() == 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void onShowLogCat(String tag , String msg){
        if (BuildConfig.DEBUG){
            Log.e("SendData",tag + " : " + msg);
        }
    }
}
