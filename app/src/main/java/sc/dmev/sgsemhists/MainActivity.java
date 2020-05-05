package sc.dmev.sgsemhists;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.otto.Subscribe;
import sc.dmev.sgsemhists.bus.BusProvider;
import sc.dmev.sgsemhists.bus.ModelEvenBus;
import sc.dmev.sgsemhists.setting.SettingFragment;
import sc.dmev.sgsemhists.sms.SmsFragment;
import sc.dmev.sgsemhists.utile.Utile;

public class MainActivity extends AppCompatActivity {
    private AllCommand allCommand;
    private String TAG_SMS = "TAG_SMS",TAG_SETTING = "TAG_SETTING";
    private int KEY_MENU = 2;
    private TextView tvTitleToolBar;
    private Snackbar snackbar = null;
    private LinearLayout lnMenuSms,lnMenuSetting;
    private ImageView imgMenuSms,imgMenuSetting;
    private TextView tvMenuSms,tvMenuSetting;
    private static final int REQUEST_MUTIPLE = 1,REQUEST_DEFAULT_APP = 2;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BusProvider.getInstance().register(this);
        tvTitleToolBar = findViewById(R.id.tvTitleToolBar);

        initView();
        onClickMenu();
        if (savedInstanceState == null){
            addFragmentFirst();
        }

        onChangeDefaultApp();
        onSelectMenuBottom(KEY_MENU);
        String url = allCommand.getStringShare(MainActivity.this, Utile.SHARE_URL, "");//ตรวจสอบกรอก url ครั้งแรก
        if (url == null || url.trim().equals("")){
            allCommand.saveStringShare(MainActivity.this,Utile.SHARE_URL,"x1.autobet.com");
        }
    }
    @Override
    public void onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,getResources().getText(R.string.txt_exit_app), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;

                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }
    private void onClickMenu() {
        lnMenuSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allCommand.hideKeyboard(MainActivity.this);
                if (checkDataUrlPhoneNumber()) {
                    onManagerFragment(1);
                }
            }
        });
        lnMenuSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allCommand.hideKeyboard(MainActivity.this);
                onManagerFragment(2);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onShowLogCat("onDestroy","onDestroy");
        BusProvider.getInstance().unregister(this);
    }
    private void initView() {
        allCommand = new AllCommand();
        lnMenuSms = findViewById(R.id.lnMenuSms);
        lnMenuSetting = findViewById(R.id.lnMenuSetting);
        imgMenuSms = findViewById(R.id.imgMenuSms);
        imgMenuSetting = findViewById(R.id.imgMenuSetting);
        tvMenuSms = findViewById(R.id.tvMenuSms);
        tvMenuSetting = findViewById(R.id.tvMenuSetting);
    }
    private void onManagerFragment(int iKeyMenu){
        switch (iKeyMenu){
            case 1:
                tvTitleToolBar.setText(getResources().getString(R.string.title_bar_sms));
                imgMenuSms.setColorFilter(getResources().getColor(R.color.color_title_menu_bottom_s));
                tvMenuSms.setTextColor(getResources().getColor(R.color.color_title_menu_bottom_s));
                if (!(ShowFragmentNow(R.id.framContentHome).toString().trim().equals(TAG_SMS))){
                    Fragment fAttach = (SmsFragment)
                            getSupportFragmentManager().findFragmentByTag(TAG_SMS);
                    getSupportFragmentManager().beginTransaction()
                            //.setCustomAnimations(R.anim.anim_ac_in, R.anim.anim_ac_out)
                            .detach(onFragmentIsShow())
                            .attach(fAttach)
                            .commit();
                }
                if (!allCommand.isConnectingToInternet(MainActivity.this)) {
                    isShowSnackbarPhone(MainActivity.this);
                }
                onSelectMenuBottom(iKeyMenu);
                break;
            case 2:
                tvTitleToolBar.setText(getResources().getString(R.string.title_bar_setting));
                imgMenuSetting.setColorFilter(getResources().getColor(R.color.color_title_menu_bottom_s));
                tvMenuSetting.setTextColor(getResources().getColor(R.color.color_title_menu_bottom_s));

                if (!(ShowFragmentNow(R.id.framContentHome).toString().trim().equals(TAG_SETTING))){
                    Fragment fAttach = (SettingFragment)
                            getSupportFragmentManager().findFragmentByTag(TAG_SETTING);
                    getSupportFragmentManager().beginTransaction()
                            //.setCustomAnimations(R.anim.anim_ac_in, R.anim.anim_ac_out)
                            .detach(onFragmentIsShow())
                            .attach(fAttach)
                            .commit();
                }
                if (!allCommand.isConnectingToInternet(MainActivity.this)) {
                    isShowSnackbarPhone(MainActivity.this);
                }
                onSelectMenuBottom(iKeyMenu);
                break;
        }
    }
    private void onSelectMenuBottom(int index) {
        KEY_MENU = index;
        tvMenuSms.setTextColor(getResources().getColor(R.color.color_title_menu_bottom_u));
        tvMenuSetting.setTextColor(getResources().getColor(R.color.color_title_menu_bottom_u));
        imgMenuSms.setColorFilter(getResources().getColor(R.color.color_title_menu_bottom_u));
        imgMenuSetting.setColorFilter(getResources().getColor(R.color.color_title_menu_bottom_u));
        switch (index){
            case 1:
                tvTitleToolBar.setText(getResources().getString(R.string.title_bar_sms));
                imgMenuSms.setColorFilter(getResources().getColor(R.color.color_title_menu_bottom_s));
                tvMenuSms.setTextColor(getResources().getColor(R.color.color_title_menu_bottom_s));
                break;
            case 2:
                tvTitleToolBar.setText(getResources().getString(R.string.title_bar_setting));
                imgMenuSetting.setColorFilter(getResources().getColor(R.color.color_title_menu_bottom_s));
                tvMenuSetting.setTextColor(getResources().getColor(R.color.color_title_menu_bottom_s));
                break;
        }
    }
    private boolean checkDataUrlPhoneNumber() {
        boolean bStatus = true;
        String sUrl = allCommand.getStringShare(MainActivity.this, Utile.SHARE_URL, "");//ตรวจสอบกรอก url
        String sPhone1 = allCommand.getStringShare(MainActivity.this, Utile.SHARE_PHONE1, "");
        String sPhone2 = allCommand.getStringShare(MainActivity.this, Utile.SHARE_PHONE2, "");
        if (sUrl.toString().trim().length() <= 0 || ((sPhone1.toString().trim().length() <= 0 ) && (sPhone2.toString().trim().length() <= 0 ))) {
            bStatus = false;
        }
        return bStatus;
    }
    private void onChangeDefaultApp(){
        final String packageName = getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(!Telephony.Sms.getDefaultSmsPackage(MainActivity.this).equals(packageName)) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
                startActivityForResult(intent,REQUEST_DEFAULT_APP);
            }else {
                allowMultipleSuccess();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("KEY_MENU",KEY_MENU);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        KEY_MENU = savedInstanceState.getInt("KEY_MENU");
    }

    public String ShowFragmentNow(int idFragment){
        String strTag = "";
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(idFragment);
        if (f != null) {
            strTag = f.getTag();
        }
        return strTag;
    }
    public void isShowSnackbarPhone(Activity activity){
        CoordinatorLayout coorLayout = (CoordinatorLayout) findViewById(R.id.coorLayoutMain);
        String title = activity.getResources().getString(R.string.title_no_connect);
        String action = activity.getResources().getString(R.string.title_action_net);
        snackbar = Snackbar.make(coorLayout,title,Snackbar.LENGTH_LONG)
                .setAction(action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        View sbView = snackbar.getView();
        TextView txtMessage = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        txtMessage.setTextColor(Color.RED);
        txtMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP,activity.getResources().getInteger(R.integer.size_title_buttom));
        txtMessage.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        TextView txtAction = sbView.findViewById(com.google.android.material.R.id.snackbar_action);
        txtAction.setTextSize(TypedValue.COMPLEX_UNIT_SP,activity.getResources().getInteger(R.integer.size_title_buttom));
        txtAction.setTextColor(Color.YELLOW);
        txtAction.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        //sbView.setBackgroundColor(Color.parseColor("#009688"));
        sbView.setBackgroundResource(R.color.colorPrimaryDark);
        //sbView.setPadding(0,0,0,50);
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DEFAULT_APP) {
            if (resultCode == RESULT_OK) {
                allowMultipleSuccess();
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener closeListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("ตกลง", okListener)
                .setNegativeButton("ออกจากแอพ", closeListener)
                .create()
                .show();
    }
    private void allowMultipleSuccess(){
        ModelEvenBus evenBus = new ModelEvenBus();
        evenBus.setKeyEvenBus(1);
        BusProvider.getInstance().post(evenBus);
    }

    @Subscribe
    public void onStanByEvenBus(ModelEvenBus evenBus){
        if (evenBus != null){
            if (evenBus.getKeyEvenBus() == 2){//Check Allow Permission
                //onPermissionMultiple();
                onChangeDefaultApp();
            }
        }
    }

    private void addFragmentFirst() {

        SmsFragment smsFragment = SmsFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.framContentHome, smsFragment,TAG_SMS)
                .detach(smsFragment)
                .commit();

        SettingFragment settingFragment = SettingFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.framContentHome, settingFragment,TAG_SETTING)
                .commit();
    }
    private Fragment onFragmentIsShow() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.framContentHome);
        if (f != null) {
            return f;
        }
        return null;
    }

    private void onShowLogCat(String tag, String msg){
        if (BuildConfig.DEBUG){
            Log.e("*** MainActivity ***",tag +" : " + msg);
        }
    }
}
