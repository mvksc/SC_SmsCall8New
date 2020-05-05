package sc.dmev.sgsemhists.bus;

import com.squareup.otto.Bus;

/**
 * Created by KHUNTONGDANG on 6/2/2560.
 */

public final class BusProvider {
    private static Bus bus;

    //Singleton Pattern
    public static Bus getInstance(){
        if (bus == null){
            bus = new Bus();
        }
        return bus;
    }
}
