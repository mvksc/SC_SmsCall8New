package sc.dmev.sgsemhists.sms;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import sc.dmev.sgsemhists.AllCommand;
import sc.dmev.sgsemhists.BuildConfig;
import sc.dmev.sgsemhists.SendData;
import sc.dmev.sgsemhists.utile.Utile;

/**
 * Created by Varayut on 28/9/2558.
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";
    public static final String SMS_FORMAT = "format";
    private Context mContext;


    @SuppressLint("MissingPermission")
    public void onReceive(Context context, Intent intent) {
        try {

            mContext = context;
            if (getBooleanShare(mContext,Utile.SHARE_IS_VIBRATE_SMS,false)){
                Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(500);
                }
            }

            Bundle bundle = intent.getExtras();
            int slot = -1;
            if (bundle != null) {
                Set<String> keySet = bundle.keySet();
                for (String key : keySet) {
                    switch (key) {
                        case "slot":
                            slot = bundle.getInt("slot", -1);
                            break;
                        case "simId":
                            slot = bundle.getInt("simId", -1);
                            break;
                        case "simSlot":
                            slot = bundle.getInt("simSlot", -1);
                            break;
                        case "slot_id":
                            slot = bundle.getInt("slot_id", -1);
                            break;
                        case "simnum":
                            slot = bundle.getInt("simnum", -1);
                            break;
                        case "slotId":
                            slot = bundle.getInt("slotId", -1);
                            break;
                        case "slotIdx":
                            slot = bundle.getInt("slotIdx", -1);
                            break;
                        case "sub_id":
                            slot = bundle.getInt("sub_id", -1);
                            break;
                        default:
                            if (key.toLowerCase().contains("slot") | key.toLowerCase().contains("sim")) {
                                String value = bundle.getString(key, "-1");
                                if (value.equals("0") | value.equals("1") | value.equals("2")) {
                                    slot = bundle.getInt(key, -1);
                                }
                            }
                    }
                }
            }
            Bundle intentExtras = intent.getExtras();
            if (intentExtras != null) {
                String smsBody ="",address="";
                Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
                String format = intent.getStringExtra(SMS_FORMAT);
                String sMsg = "",sFrom="",sTo = "",sDate = "",sTime = "",sError = "0";
                String batt = String.valueOf(getBatteryPercentage(mContext));

                try {
                    SubscriptionManager manager = SubscriptionManager.from(context);
                    SubscriptionInfo subscriptionInfo = manager.getActiveSubscriptionInfoForSimSlotIndex(slot);
                    sTo = subscriptionInfo.getNumber();
                }catch (Exception e){
                    sTo = "";
                }
                if (sTo == null || sTo.length() <= 0){
                    AllCommand allCommand = new AllCommand();
                    String phone1 = allCommand.getStringShare(mContext,Utile.SHARE_PHONE1,"");
                    String phone2 = allCommand.getStringShare(mContext,Utile.SHARE_PHONE2,"");
                    if (phone1.trim().length() > 0 || phone2.trim().length() > 0){
                        if ((phone1.trim().length() > 0) && (phone2.trim().length() > 0)){
                            sTo = phone1 + "," + phone2;
                        }else if (phone1.trim().length() > 0){
                            sTo = phone1;
                        }else if (phone2.trim().length() > 0){
                            sTo = phone2;
                        }
                    }
                    sError = "1";
                }

                long lSmsTime = 0L;
                String msgShow = "";
                for (int i = 0; i < sms.length; ++i) {
                    SmsMessage smsMessage = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        smsMessage = SmsMessage.createFromPdu((byte[]) sms[i], format);
                    }else {
                        smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                    }
                    smsBody = smsMessage.getMessageBody();
                    address = smsMessage.getOriginatingAddress();
                    lSmsTime = smsMessage.getTimestampMillis();
                    sFrom = address;
                    sMsg += smsBody;
                }
                try {
                    Toast.makeText(mContext.getApplicationContext(),sMsg,Toast.LENGTH_LONG).show();
                }catch (Exception e){}

                if (lSmsTime > 0){
                    DateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
                    DateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(lSmsTime);
                    sDate = formatDate.format(calendar.getTime());
                    sTime = formatTime.format(calendar.getTime());
                }
            /*Log.e("*** Check Sms55 ***",
                    "From = " + sFrom +
                            "\n : To = " + sTo +
                            "\n : Title = " + (sMsg +" : "+ batt) +
                            "\n : Date = " + sDate +
                            "\n : Time = " + sTime +
                            "\n : Batt = " + batt +
                            "\n : Error = " + sError);
                onShowToast(context,"C-01 SAVE_SMS : " +
                        "\n  From = " + sFrom +
                        "\n  To = " + sTo +
                        "\n  Title = " + sMsg +
                        "\n  Date = " + sDate +
                        "\n  Time = " + sTime +
                        "\n  Error = " + sError);*/

                //writerSms(nameFile,sFrom,sTo,sMsg,sDate,sTime,sError,batt);
                String nameFile = "SMS" + System.currentTimeMillis() + ".json";
                new SendData(nameFile,sFrom,sTo,sMsg,sDate,sTime,sError,batt,context);
            }
        }catch (Exception e){
            onShowLogCat("Error","save msm error " + e.getMessage());
        }
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onReadAllSms();
            }
        },3000);*/
    }

    private String onCheckSmsToSim(Cursor cursor) {
        String[] colums = cursor.getColumnNames();
        for (String col : colums) {
            try {
                if (col.toString().trim().equals("sim_slot")){//samsung 1SIM and 2SIM OK
                    String colSimID = cursor.getString(cursor.getColumnIndex(col));
                    onShowLogCat("CheckPhoneID","PhoneID : " + col.toString() + " == colSimID : " + colSimID);
                    if (colSimID.toString().trim().equals("1")){//Sim2
                        return getStringShare(mContext, Utile.SHARE_PHONE2, "");
                    }else if(colSimID.toString().trim().equals("0")){//Sim1
                        return getStringShare(mContext, Utile.SHARE_PHONE1, "");
                    }
                }else if (col.toString().trim().equals("sim_id")){//i-mobile รอทดสอบเครื่อง 1 SIM
                    String colSimID = cursor.getString(cursor.getColumnIndex(col));
                    onShowLogCat("CheckPhoneID","PhoneID : " + col.toString() + " == colSimID : " + colSimID);
                    if((colSimID.toString().trim().equals("2")) || (colSimID.toString().trim().equals("5"))){//Sim1 IQ
                        return getStringShare(mContext, Utile.SHARE_PHONE1, "");
                    }else if(colSimID.toString().trim().equals("3")){//Sim2 IQ
                        return getStringShare(mContext, Utile.SHARE_PHONE2, "");
                    }
                }else if (col.toString().trim().equals("phone_id")){//asus รอทดสอบเครื่อง 1 SIM 2SIM OK
                    String colSimID = cursor.getString(cursor.getColumnIndex(col));
                    onShowLogCat("CheckPhoneID","PhoneID : " + col.toString() + " == colSimID : " + colSimID);
                    if (colSimID.toString().trim().equals("1")){//Sim2
                        return getStringShare(mContext, Utile.SHARE_PHONE2, "");
                    }else if(colSimID.toString().trim().equals("0")){//Sim1
                        return getStringShare(mContext, Utile.SHARE_PHONE1, "");
                    }
                }else if (col.toString().trim().equals("sub_id")){//True เครื่อง 2 SIM
                            String colSimID = cursor.getString(cursor.getColumnIndex(col));
                            onShowLogCat("CheckPhoneID","PhoneID : " + col.toString() + " == colSimID : " + colSimID);
                            if (colSimID.toString().trim().equals("3")){//Sim2
                                return getStringShare(mContext, Utile.SHARE_PHONE2, "");
                            }else if(colSimID.toString().trim().equals("2")){//Sim1
                                return getStringShare(mContext, Utile.SHARE_PHONE1, "");
                            }
                }/*else if (col.toString().trim().equals("sub_id")){//lenovo รอทดสอบเครื่อง 2 SIM
                            String colSimID = cursor.getString(cursor.getColumnIndex(col));
                            onShowToast("PhoneID : " + col.toString() + " == colSimID : " + colSimID);
                            if ((colSimID.toString().trim().equals("2")) || colSimID.toString().trim().equals("4")){//Sim2
                                return allCommand.getStringShare(mContext, allCommand.SHARE_PHONE2, "");
                            }else if((colSimID.toString().trim().equals("1")) || (colSimID.toString().trim().equals("5"))){//Sim1
                                return allCommand.getStringShare(mContext, allCommand.SHARE_PHONE1, "");
                            }
                 }*/

            } catch (Exception e) {
                onShowLogCat("***Error**", col + " ++ " + e.getMessage());
            }
        }
        return "";
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
    private int getBatteryPercentage(Context context) {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;
        float batteryPct = level / (float) scale;
        return (int) (batteryPct * 100);
    }
    public String getStringShare(Context mContext,String strKey, String strDe) {
        SharedPreferences shLang;
        shLang = mContext.getSharedPreferences(Utile.SHARE_DATA, Context.MODE_PRIVATE);
        String strShare = shLang.getString(strKey, strDe);
        return strShare;
    }
    public boolean getBooleanShare(Context mContext, String strKey, boolean strDe) {
        SharedPreferences shLang;
        shLang = mContext
                .getSharedPreferences(Utile.SHARE_DATA, Context.MODE_PRIVATE);
        boolean strShare = shLang.getBoolean(strKey, strDe);
        return strShare;
    }



    public void onShowToast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
    private void onShowLogCat(String tag , String msg){
        if (BuildConfig.DEBUG){
            Log.e("SmsBroadcastReceiver",tag + " : " + msg);
        }
    }
}
