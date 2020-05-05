package sc.dmev.sgsemhists;

import android.content.Context;

import sc.dmev.sgsemhists.BuildConfig;

/**
 * Created by MDEV on 14/9/2559.
 */
public class ContextAppSms{
    private static ContextAppSms instance;
    public static ContextAppSms getInstance() {
        if (instance == null)
            instance = new ContextAppSms();
        return instance;
    }

    private Context mContext;
    private ContextAppSms() {}
    public void init(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    private void ShowLogCat(String tag, String msg){
        if (BuildConfig.DEBUG){
            //Log.e("***TTS_ThaiEng***",tag + " : " + msg);
        }
    }
}
