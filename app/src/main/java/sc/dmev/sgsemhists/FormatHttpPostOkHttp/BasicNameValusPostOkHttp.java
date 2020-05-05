package sc.dmev.sgsemhists.FormatHttpPostOkHttp;

/**
 * Created by Varayut on 17/8/2558.
 */
public class BasicNameValusPostOkHttp {
    public FromHttpPostOkHttp BasicNameValusPostOkHttp(String key_post,String valus_post){
        FromHttpPostOkHttp fromHttpPostOkHttp = new FromHttpPostOkHttp();
        fromHttpPostOkHttp.setKEY_POST(key_post.trim().toString());
        fromHttpPostOkHttp.setVALUS_POST(valus_post.trim().toString());
        return fromHttpPostOkHttp;
    }
}
