package sc.dmev.sgsemhists;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sc.dmev.sgsemhists.BuildConfig;
import sc.dmev.sgsemhists.FormatHttpPostOkHttp.FromHttpPostOkHttp;
import sc.dmev.sgsemhists.utile.Utile;

public class AllCommand {
	public boolean isConnectingToInternet(Context _context) {
		ConnectivityManager connectivity = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
		//return true;
	}
	public String CoverStringFromServer_One(String strData){
		try {
			return  strData.substring(strData.indexOf("{"), strData.lastIndexOf("}") + 1);
		} catch (Exception e) {
			return "";
		}
	}
	public String CoverStringFromServer_Two(String strData){
		try {
			return strData.substring(strData.indexOf("["), strData.lastIndexOf("]") + 1);
		} catch (Exception e) {
			return "";
		}
	}
	public String POST_OK_HTTP_SendData(String url,ArrayList<FromHttpPostOkHttp> params_login){
		/*final MediaType MEDIA_TYPE_MARKDOWN
				= MediaType.parse("charset=utf-8");*/
		onShowLogCat("POST URL ", url);
		try{
			OkHttpClient client = new OkHttpClient();
			MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
			for (int i = 0;i<params_login.size();i++){
				onShowLogCat("Check Data", params_login.get(i).getKEY_POST().toString() + " : " + params_login.get(i).getVALUS_POST().toString());
				multipartBuilder.addFormDataPart(params_login.get(i).getKEY_POST().toString(),params_login.get(i).getVALUS_POST().toString());
			}
			RequestBody requestBody = multipartBuilder.build();
			Request request = new Request.Builder()
					.url(url)
					.post(requestBody)
							//.post(RequestBody.create(MEDIA_TYPE_MARKDOWN,String.valueOf(requestBody)))
					.build();

			Response response = client.newCall(request).execute();
			if (response.isSuccessful()){
				return response.body().string().toString();
			}
		}catch (Exception e){
			Log.e("*** Err ***", "Err POST_OK_HTTP_SendData " + e.getMessage());
			return "";
		}
		return "";
	}
	public String GET_OK_HTTP_SendData(String url){
		onShowLogCat("***GET url***", url + "");
		try{
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder().url(url).build();
			Response response = client.newCall(request).execute();
			return response.body().string();
		}catch (Exception e){
			Log.e("*** Err ***", "Err GET_OK_HTTP_SendData " + e.getMessage());
			return "";
		}
	}
	public String getStringShare(Context _context, String strKey, String strDe) {
		SharedPreferences shLang;
		shLang = _context
				.getSharedPreferences(Utile.SHARE_DATA, Context.MODE_PRIVATE);
		String strShare = shLang.getString(strKey, strDe);
		return strShare;
	}
	public void saveStringShare(Context _context, String strKey, String strDe){
		SharedPreferences shLang;
		SharedPreferences.Editor edShLang;
		shLang = _context.getSharedPreferences(Utile.SHARE_DATA, Context.MODE_PRIVATE);
		edShLang = shLang.edit();
		edShLang.remove(strKey);
		edShLang.commit();
		edShLang.putString(strKey, strDe);
		edShLang.commit();
	}
	public int getIntShare(Context _context, String strKey, int strDe) {
		SharedPreferences shLang;
		shLang = _context
				.getSharedPreferences(Utile.SHARE_DATA, Context.MODE_PRIVATE);
		int strShare = shLang.getInt(strKey, strDe);
		return strShare;
	}
	public void saveIntShare(Context _context, String strKey, int strDe){
		SharedPreferences shLang;
		SharedPreferences.Editor edShLang;
		shLang = _context.getSharedPreferences(Utile.SHARE_DATA, Context.MODE_PRIVATE);
		edShLang = shLang.edit();
		edShLang.remove(strKey);
		edShLang.commit();
		edShLang.putInt(strKey, strDe);
		edShLang.commit();
	}
	public boolean getBooleanShare(Context _context, String strKey, boolean strDe) {
		SharedPreferences shLang;
		shLang = _context
				.getSharedPreferences(Utile.SHARE_DATA, Context.MODE_PRIVATE);
		boolean strShare = shLang.getBoolean(strKey, strDe);
		return strShare;
	}
	public void saveBooleanShare(Context _context, String strKey, boolean strDe){
		SharedPreferences shLang;
		SharedPreferences.Editor edShLang;
		shLang = _context.getSharedPreferences(Utile.SHARE_DATA, Context.MODE_PRIVATE);
		edShLang = shLang.edit();
		edShLang.remove(strKey);
		edShLang.commit();
		edShLang.putBoolean(strKey, strDe);
		edShLang.commit();
	}
	public void deleteStringShare(Context _context, String strKey, String strDe){
		SharedPreferences shLang;
		SharedPreferences.Editor edShLang;
		shLang = _context.getSharedPreferences(Utile.SHARE_DATA, Context.MODE_PRIVATE);
		edShLang = shLang.edit();
		edShLang.remove(strKey);
		edShLang.commit();
	}

	public void hideKeyboard(Activity activity){
		try{
			InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		catch (Exception e){
			// Ignore exceptions if any
			onShowLogCat("Err","KeyBoardUtil " +  e.toString() + " : " +  e.getMessage());
		}
	}
	public String onCheckIsToDate(String sDate){
		try{
			Date date = new Date();
			//java.text.DateFormat fromatTimeStamp = new SimpleDateFormat("HH:mm:ss");
			java.text.DateFormat fromatDateStamp = new SimpleDateFormat("dd/MM/yyyy");
			java.text.DateFormat fromatTodate = new SimpleDateFormat("dd/MM/yyyy");
			//String strTimeStamp = fromatTimeStamp.format(timestamp);
			String strDateStamp = fromatDateStamp.format(sDate);
			String strToDate = fromatTodate.format(date);
			if (strToDate.toString().trim().equals(strDateStamp.trim())) {
				return "วันนี้ , ";
			} else {
				return " วันที่ : " + strDateStamp;
			}
		}catch (Exception e){
			return "--";
		}


		//Log.e("CoverTime",strTimeStamp.toString() + " To Date " + strToDate + " Date Stame " + strDateStamp);

	}
	public void onShowLogCat(String tag, String msg){
		if (BuildConfig.DEBUG){
			Log.e("***AllCommand***",tag + " : " + msg);
		}
	}
}
