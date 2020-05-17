package sc.dmev.sgsemhists.sms;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Varayut on 15/11/2558.
 */
public class ModelSms implements Parcelable {
    public String from,to,msg,date,time,error,batt,namefile,msgError;
    public int sented;//-1 = ไม่สนใจ, 0 = ยังไม่ส่ง, 1 = ส่งแล้ว

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

    public String getNamefile() {
        return namefile;
    }

    public void setNamefile(String namefile) {
        this.namefile = namefile;
    }

    public int getSented() {
        return sented;
    }

    public void setSented(int sented) {
        this.sented = sented;
    }

    public String getMsgError() {
        return msgError;
    }

    public void setMsgError(String msgError) {
        this.msgError = msgError;
    }

    public ModelSms(String namefile, String from, String to, String msg, String date, String time, String error, String batt, int sented, String msgError){
        this.namefile = namefile;
        this.from = from;
        this.to = to;
        this.msg = msg;
        this.date = date;
        this.time = time;
        this.error = error;
        this.batt = batt;
        this.sented = sented;
        this.msgError = msgError;

    }

    protected ModelSms(Parcel in) {
        namefile = in.readString();
        from = in.readString();
        to = in.readString();
        msg = in.readString();
        date = in.readString();
        time = in.readString();
        error = in.readString();
        batt = in.readString();
        sented = in.readInt();
        msgError = in.readString();
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
        out.writeString(namefile);
        out.writeString(from);
        out.writeString(to);
        out.writeString(msg);
        out.writeString(date);
        out.writeString(time);
        out.writeString(error);
        out.writeString(batt);
        out.writeInt(sented);
        out.writeString(msgError);
    }
}
