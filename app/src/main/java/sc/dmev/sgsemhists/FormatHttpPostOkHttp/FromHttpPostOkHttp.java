package sc.dmev.sgsemhists.FormatHttpPostOkHttp;

/**
 * Created by Varayut on 23/7/2558.
 */
public class FromHttpPostOkHttp {
    private String KEY_POST;
    private String VALUS_POST;
    public FromHttpPostOkHttp(){
        KEY_POST = "";
        VALUS_POST = "";
    }
    public void setKEY_POST(String key_post){
        this.KEY_POST = key_post;
    }
    public void setVALUS_POST(String valus_post){
        this.VALUS_POST = valus_post;
    }
    public String getKEY_POST(){
        return KEY_POST;
    }
    public String getVALUS_POST(){
        return VALUS_POST;
    }

}



