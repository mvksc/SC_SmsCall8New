package sc.dmev.sgsemhists.wifi;


import android.content.Context;
import android.net.wifi.WifiManager;
/**
 * Created by KHUNTONGDANG on 6/2/2560.
 */

public final class WifiProvider {
    private static WifiManager wifimanager;

    //Singleton Pattern
    public static WifiManager getInstance(Context context){
        if (wifimanager == null){
            wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        }
        return wifimanager;
    }
}
