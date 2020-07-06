package sc.dmev.sgsemhists.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.otto.Subscribe;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import sc.dmev.sgsemhists.AllCommand;
import sc.dmev.sgsemhists.BuildConfig;
import sc.dmev.sgsemhists.FormatHttpPostOkHttp.BasicNameValusPostOkHttp;
import sc.dmev.sgsemhists.FormatHttpPostOkHttp.FromHttpPostOkHttp;
import sc.dmev.sgsemhists.MainActivity;
import sc.dmev.sgsemhists.R;
import sc.dmev.sgsemhists.bus.BusProvider;
import sc.dmev.sgsemhists.bus.ModelEvenBus;
import sc.dmev.sgsemhists.utile.Utile;

public class SettingFragment extends Fragment {
    private TextView tvAddWifi,tvSave,tvAddWifiOk;
    private LinearLayout lnContentSetting,lnContentAddWifi;
    private EditText edNameWifi1,edNameWifi2,edNameWifi3,edNameWifi4,edNameWifi5;
    private EditText edPassWifi1,edPassWifi2,edPassWifi3,edPassWifi4,edPassWifi5;
    private EditText edUrl,edPhone1,edPhone2,edPingIP;
    private TextInputLayout tilEdUrl,tilEdPhone1,tilEdPhone2,tilEdPingIP;
    private TextView tvNameWifi,tvSimAuto1,tvSimAuto2,tvNoAutoSim;
    private AllCommand allCommand;
    private CoordinatorLayout coorLayout;
    private Snackbar snackbar = null;
    private Switch swSmsAlert,swSmsDefault;

    public SettingFragment() {}

    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        initView(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        edUrl.setText(allCommand.getStringShare(getActivity(), Utile.SHARE_URL,""));//แสดง url ที่หน้าตั้งค่า
        edUrl.setSelection(edUrl.length());
        edUrl.requestFocus();
        edPhone2.setNextFocusDownId(View.NO_ID);

        getPhoneSimAuto();
        String sim1 = allCommand.getStringShare(getActivity(),Utile.SHARE_PHONE1,"");
        String sim2 = allCommand.getStringShare(getActivity(),Utile.SHARE_PHONE2,"");
        edPhone1.setText(sim1);
        edPhone2.setText(sim2);

        edPingIP.setText(String.valueOf(allCommand.getStringShare(getActivity(),Utile.SHARE_PING_IP,"www.google.com")));
        edNameWifi1.setText(allCommand.getStringShare(getActivity(),Utile.SHARE_NAME_WIFI1,""));
        edNameWifi2.setText(allCommand.getStringShare(getActivity(),Utile.SHARE_NAME_WIFI2,""));
        edNameWifi3.setText(allCommand.getStringShare(getActivity(),Utile.SHARE_NAME_WIFI3,""));
        edNameWifi4.setText(allCommand.getStringShare(getActivity(),Utile.SHARE_NAME_WIFI4,""));
        edNameWifi5.setText(allCommand.getStringShare(getActivity(),Utile.SHARE_NAME_WIFI5,""));

        edPassWifi1.setText(allCommand.getStringShare(getActivity(),Utile.SHARE_PASS_WIFI1,""));
        edPassWifi2.setText(allCommand.getStringShare(getActivity(),Utile.SHARE_PASS_WIFI2,""));
        edPassWifi3.setText(allCommand.getStringShare(getActivity(),Utile.SHARE_PASS_WIFI3,""));
        edPassWifi4.setText(allCommand.getStringShare(getActivity(),Utile.SHARE_PASS_WIFI4,""));
        edPassWifi5.setText(allCommand.getStringShare(getActivity(),Utile.SHARE_PASS_WIFI5,""));

        swSmsAlert.setChecked(allCommand.getBooleanShare(getActivity(),Utile.SHARE_IS_VIBRATE_SMS,false));
        swSmsDefault.setChecked(isDefaultApp());

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                allCommand.hideKeyboard(getActivity());

                allCommand.saveStringShare(getActivity(),Utile.SHARE_URL,edUrl.getText().toString().trim());//บันทึก url ที่หน้าตั้งค่า
                allCommand.saveStringShare(getActivity(), Utile.SHARE_PHONE1, edPhone1.getText().toString().trim());
                allCommand.saveStringShare(getActivity(), Utile.SHARE_PHONE2, edPhone2.getText().toString().trim());
                ModelEvenBus evenBus = new ModelEvenBus();
                evenBus.setKeyEvenBus(2);
                BusProvider.getInstance().post(evenBus);

                if (edUrl.getText().toString().trim().length() > 0 &&
                        (edPhone1.getText().toString().trim().length() >= 10 || edPhone2.getText().toString().trim().length() >= 10)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String URL_Batt = "http://" + edUrl.getText().toString().trim() + "/app/save_batt.php";
                            final String URL_Batt2 = "http://" + edUrl.getText().toString().trim() + "/app/save_batt2.php";
                            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                            Intent batteryStatus = getActivity().registerReceiver(null, ifilter);
                            final int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                            allCommand.saveStringShare(getActivity(),Utile.SHARE_BAT_START,String.valueOf(level));
                            try{
                                String phone = "";
                                String phone1 = allCommand.getStringShare(getActivity(),Utile.SHARE_PHONE1,"");
                                String phone2 = allCommand.getStringShare(getActivity(),Utile.SHARE_PHONE2,"");
                                if (phone1.trim().length() > 0 || phone2.trim().length() > 0){
                                    if ((phone1.trim().length() > 0) && (phone2.trim().length() > 0)){
                                        phone = phone1 + "," + phone2;
                                    }else if (phone1.trim().length() > 0){
                                        phone = phone1;
                                    }else if (phone2.trim().length() > 0){
                                        phone = phone2;
                                    }
                                    ArrayList<FromHttpPostOkHttp> paramsBatt = new ArrayList<FromHttpPostOkHttp>();
                                    paramsBatt.add(new BasicNameValusPostOkHttp().BasicNameValusPostOkHttp("from", phone/*phone1 + "," + phone2*/));
                                    paramsBatt.add(new BasicNameValusPostOkHttp().BasicNameValusPostOkHttp("batt", String.valueOf(allCommand.getStringShare(getActivity(),Utile.SHARE_BAT_START,"0"))));
                                    onShowLogCat("*** Send Batt App ***", allCommand.POST_OK_HTTP_SendData(URL_Batt,paramsBatt) + "");
                                    onShowLogCat("*** Send Batt2 App ***", allCommand.POST_OK_HTTP_SendData(URL_Batt2,paramsBatt)  + "");
                                }
                            }catch (Exception e){
                                onShowLogCat("*** Err ***", "Send Online App " + e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        });

        tvAddWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lnContentSetting.setVisibility(View.GONE);
                lnContentAddWifi.setVisibility(View.VISIBLE);
                allCommand.hideKeyboard(getActivity());
            }
        });
        tvAddWifiOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allCommand.saveStringShare(getActivity(),Utile.SHARE_NAME_WIFI1,edNameWifi1.getText().toString().trim());
                allCommand.saveStringShare(getActivity(),Utile.SHARE_NAME_WIFI2,edNameWifi2.getText().toString().trim());
                allCommand.saveStringShare(getActivity(),Utile.SHARE_NAME_WIFI3,edNameWifi3.getText().toString().trim());
                allCommand.saveStringShare(getActivity(),Utile.SHARE_NAME_WIFI4,edNameWifi4.getText().toString().trim());
                allCommand.saveStringShare(getActivity(),Utile.SHARE_NAME_WIFI5,edNameWifi5.getText().toString().trim());
                allCommand.saveStringShare(getActivity(),Utile.SHARE_PASS_WIFI1,edPassWifi1.getText().toString().trim());
                allCommand.saveStringShare(getActivity(),Utile.SHARE_PASS_WIFI2,edPassWifi2.getText().toString().trim());
                allCommand.saveStringShare(getActivity(),Utile.SHARE_PASS_WIFI3,edPassWifi3.getText().toString().trim());
                allCommand.saveStringShare(getActivity(),Utile.SHARE_PASS_WIFI4,edPassWifi4.getText().toString().trim());
                allCommand.saveStringShare(getActivity(),Utile.SHARE_PASS_WIFI5,edPassWifi5.getText().toString().trim());
                lnContentAddWifi.setVisibility(View.GONE);
                lnContentSetting.setVisibility(View.VISIBLE);
                allCommand.hideKeyboard(getActivity());
            }
        });

        tvNameWifi.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                edNameWifi1.setText("SoftCareAIS_2.4G");
                edNameWifi2.setText("SoftCare500_2.4GHz");
                edNameWifi3.setText("SoftCare_3BB_2.4GHz");
                edNameWifi4.setText("SoftCare_Kasda_2.4G");
                edNameWifi5.setText("SoftCare_True");
                edPassWifi1.setText("88888888");
                edPassWifi2.setText("88888888");
                edPassWifi3.setText("88888888");
                edPassWifi4.setText("88888888");
                edPassWifi5.setText("88888888");
                return false;
            }
        });

        swSmsAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                allCommand.saveBooleanShare(getActivity(),Utile.SHARE_IS_VIBRATE_SMS,b);
            }
        });

        swSmsDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final String packageName = getActivity().getPackageName();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (isChecked){
                        if(!Telephony.Sms.getDefaultSmsPackage(getActivity()).equals(packageName)) {
                            ModelEvenBus evenBus = new ModelEvenBus();
                            evenBus.setKeyEvenBus(3);//เปิดตั้งค่าแอปเริ่มต้น
                            BusProvider.getInstance().post(evenBus);
                        }
                    }else {
                        swSmsDefault.setChecked(isDefaultApp());
                        if(Telephony.Sms.getDefaultSmsPackage(getActivity()).equals(packageName)) {
                            showMessageOK("คุณได้ตั้ง "+ getResources().getString(R.string.app_name)+" เป็นแอปเริ่มต้นแล้ว", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    dialogInterface.cancel();
                                }
                            });
                        }
                    }

                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getPhoneSimAuto() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager subscriptionManager = SubscriptionManager.from(getActivity());
                List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                tvSimAuto1.setVisibility(View.INVISIBLE);
                tvSimAuto2.setVisibility(View.INVISIBLE);
                if (subsInfoList != null){
                    tvNoAutoSim.setVisibility(View.GONE);
                    for (SubscriptionInfo subscriptionInfo : subsInfoList) {
                        String number = subscriptionInfo.getNumber().replace("+66","0");
                        if (subscriptionInfo.getSimSlotIndex() == 0 && number.trim().length() > 0){
                            tvSimAuto1.setVisibility(View.VISIBLE);
                            allCommand.saveStringShare(getActivity(),Utile.SHARE_PHONE1,number);
                        }else if (subscriptionInfo.getSimSlotIndex() == 1 && number.trim().length() > 0){
                            tvSimAuto2.setVisibility(View.VISIBLE);
                            allCommand.saveStringShare(getActivity(),Utile.SHARE_PHONE2,number);
                        }
                    }
                }else {
                    tvNoAutoSim.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception e){
            onShowLogCat("Error","getPhoneSimAuto " + e.getMessage());
        }
    }
    private void initView(View rootView) {
        allCommand = new AllCommand();
        coorLayout = rootView.findViewById(R.id.coorLayoutSetting);
        tvSave = rootView.findViewById(R.id.tvSave);
        tvAddWifi = rootView.findViewById(R.id.tvAddWifi);
        tvAddWifiOk = rootView.findViewById(R.id.tvAddWifiOk);
        lnContentSetting = rootView.findViewById(R.id.lnContentSetting);
        lnContentAddWifi = rootView.findViewById(R.id.lnContentAddWifi);
        lnContentSetting.setOnClickListener(null);
        lnContentAddWifi.setOnClickListener(null);
        edNameWifi1 = rootView.findViewById(R.id.edNameWifi1);
        edNameWifi2 = rootView.findViewById(R.id.edNameWifi2);
        edNameWifi3 = rootView.findViewById(R.id.edNameWifi3);
        edNameWifi4 = rootView.findViewById(R.id.edNameWifi4);
        edNameWifi5 = rootView.findViewById(R.id.edNameWifi5);
        edPassWifi1 = rootView.findViewById(R.id.edPassWifi1);
        edPassWifi2 = rootView.findViewById(R.id.edPassWifi2);
        edPassWifi3 = rootView.findViewById(R.id.edPassWifi3);
        edPassWifi4 = rootView.findViewById(R.id.edPassWifi4);
        edPassWifi5 = rootView.findViewById(R.id.edPassWifi5);
        tvSimAuto1 = rootView.findViewById(R.id.tvSimAuto1);
        tvSimAuto2 = rootView.findViewById(R.id.tvSimAuto2);
        edUrl = rootView.findViewById(R.id.edUrl);
        edPhone1 = rootView.findViewById(R.id.edPhoneNumber1);
        edPhone2 = rootView.findViewById(R.id.edPhoneNumber2);
        edPingIP = rootView.findViewById(R.id.edPingIP);
        tilEdUrl = rootView.findViewById(R.id.tilEdUrl);
        tilEdPhone1 = rootView.findViewById(R.id.tilEdPhoneNumber1);
        tilEdPhone2 = rootView.findViewById(R.id.tilEdPhoneNumber2);
        tilEdPingIP = rootView.findViewById(R.id.tilEdPingIP);
        tvNameWifi = rootView.findViewById(R.id.tvNameWifi);
        tvNoAutoSim = rootView.findViewById(R.id.tvNoAutoSim);
        swSmsAlert = rootView.findViewById(R.id.swSmsAlert);
        swSmsDefault = rootView.findViewById(R.id.swSmsDefault);
    }
    public void isInternetHost(final String host){
        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    //return InetAddress.getByName(host).isReachable(5 * 1000);
                    InetAddress ipAddr = InetAddress.getByName(host); //You can replace it with your name
                    return !ipAddr.equals("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                Toast.makeText(getActivity(), host + " : " + aBoolean,Toast.LENGTH_LONG).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    public boolean isConnected(){
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
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        onShowLogCat("onStart","onStart");
        BusProvider.getInstance().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        onShowLogCat("onStop","onStop");
        BusProvider.getInstance().unregister(this);
    }
    @Subscribe
    public void onStanByEvenBus(ModelEvenBus evenBus){
        if (evenBus != null){
            if (evenBus.getKeyEvenBus() == 1){//Allow Permission
                onShowLogCat("onStanByEvenBus","onStanByEvenBus");
                tilEdPhone1.setError(null);
                swSmsDefault.setChecked(isDefaultApp());
                getPhoneSimAuto();
                String sim1 = allCommand.getStringShare(getActivity(),Utile.SHARE_PHONE1,"");
                String sim2 = allCommand.getStringShare(getActivity(),Utile.SHARE_PHONE2,"");
                edPhone1.setText(sim1);
                edPhone2.setText(sim2);
                allCommand.saveStringShare(getActivity(), Utile.SHARE_PHONE1, edPhone1.getText().toString().trim());
                tilEdPhone2.setError(null);
                allCommand.saveStringShare(getActivity(), Utile.SHARE_PHONE2, edPhone2.getText().toString().trim());
                tilEdPingIP.setError(null);
                allCommand.saveStringShare(getActivity(), Utile.SHARE_PING_IP, edPingIP.getText().toString().trim());
                isShowSnackbarPhone(getActivity());
            }else if (evenBus.getKeyEvenBus() == 4){
                swSmsDefault.setChecked(isDefaultApp());
            }
        }
    }
    public void isShowSnackbarPhone(Activity activity){
        String title = activity.getResources().getString(R.string.title_save);
        String action = activity.getResources().getString(R.string.title_action_net);
        snackbar = Snackbar.make(coorLayout,title,Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        View sbView = snackbar.getView();
        TextView txtMessage = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        txtMessage.setTextColor(Color.GREEN);
        txtMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP,activity.getResources().getInteger(R.integer.size_title_buttom));
        txtMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        TextView txtAction = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_action);
        txtAction.setTextSize(TypedValue.COMPLEX_UNIT_SP,activity.getResources().getInteger(R.integer.size_title_buttom));
        txtAction.setTextColor(Color.YELLOW);
        txtAction.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        //sbView.setBackgroundColor(Color.parseColor("#009688"));
        sbView.setBackgroundResource(R.color.colorPrimaryDark);
        //sbView.setPadding(0,0,0,50);
        snackbar.show();
    }
    private void setMobileDataEnabled(Context context, boolean enabled) {
        final ConnectivityManager conman =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(
                    iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    private boolean isDefaultApp(){
        final String packageName = getActivity().getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(!Telephony.Sms.getDefaultSmsPackage(getActivity()).equals(packageName)) {
                return false;
            }else {
                return true;
            }
        }
        return false;
    }
    private void showMessageOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("ตกลง", okListener)
                .create()
                .show();
    }
    private void onShowLogCat(String tag, String msg){
        if (BuildConfig.DEBUG){
           Log.e("*** SettingFragment ***",tag +" : " + msg);
        }
    }
}
