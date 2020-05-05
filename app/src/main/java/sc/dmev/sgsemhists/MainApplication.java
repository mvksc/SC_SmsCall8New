package sc.dmev.sgsemhists;

import android.app.Application;
import sc.dmev.sgsemhists.bus.Contextor;

/**
 * Created by MDEV on 7/11/2559.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Contextor.getInstance().init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
