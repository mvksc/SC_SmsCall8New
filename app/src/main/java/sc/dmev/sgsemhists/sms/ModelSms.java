package sc.dmev.sgsemhists.sms;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Varayut on 15/11/2558.
 */
public class ModelSms implements Parcelable {
    public String from,to,msg,date,time,error,batt;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }
    public String getMsg(){
        return msg;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getBatt() {
        return batt;
    }

    public void setBatt(String batt) {
        this.batt = batt;
    }

    public ModelSms(String from, String to, String msg, String date, String time, String error, String batt){
        this.from = from;
        this.to = to;
        this.msg = msg;
        this.date = date;
        this.time = time;
        this.error = error;
        this.batt = batt;
    }

    protected ModelSms(Parcel in) {
        from = in.readString();
        to = in.readString();
        msg = in.readString();
        date = in.readString();
        time = in.readString();
        error = in.readString();
        batt = in.readString();
    }

    public static final Creator<ModelSms> CREATOR = new Creator<ModelSms>() {
        @Override
        public ModelSms createFromParcel(Parcel in) {
            return new ModelSms(in);
        }

        @Override
        public ModelSms[] newArray(int size) {
            return new ModelSms[size];
        }
    };





    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(from);
        out.writeString(to);
        out.writeString(msg);
        out.writeString(date);
        out.writeString(time);
        out.writeString(error);
        out.writeString(batt);
    }
}
