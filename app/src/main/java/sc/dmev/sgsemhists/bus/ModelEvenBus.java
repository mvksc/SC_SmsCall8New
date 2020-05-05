package sc.dmev.sgsemhists.bus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by KHUNTONGDANG on 26/4/2560.
 */

public class ModelEvenBus implements Parcelable{
    private int keyEvenBus;
    public ModelEvenBus(){}
    protected ModelEvenBus(Parcel in) {
        keyEvenBus = in.readInt();
    }

    public static final Creator<ModelEvenBus> CREATOR = new Creator<ModelEvenBus>() {
        @Override
        public ModelEvenBus createFromParcel(Parcel in) {
            return new ModelEvenBus(in);
        }

        @Override
        public ModelEvenBus[] newArray(int size) {
            return new ModelEvenBus[size];
        }
    };

    public int getKeyEvenBus() {
        return keyEvenBus;
    }

    public void setKeyEvenBus(int keyEvenBus) {
        this.keyEvenBus = keyEvenBus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(keyEvenBus);
    }
}
