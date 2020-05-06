package sc.dmev.sgsemhists.sms;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inthecheesefactory.thecheeselibrary.widget.AdjustableImageView;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import sc.dmev.sgsemhists.AllCommand;
import sc.dmev.sgsemhists.BuildConfig;
import sc.dmev.sgsemhists.FormatHttpPostOkHttp.BasicNameValusPostOkHttp;
import sc.dmev.sgsemhists.FormatHttpPostOkHttp.FromHttpPostOkHttp;
import sc.dmev.sgsemhists.MainActivity;
import sc.dmev.sgsemhists.R;
import sc.dmev.sgsemhists.SendData;
import sc.dmev.sgsemhists.bus.BusProvider;
import sc.dmev.sgsemhists.bus.ModelEvenBus;
import sc.dmev.sgsemhists.utile.Utile;

public class SmsFragment extends Fragment {
    private CustomAdapterItemSms customAdapterItemSms,customAdapterItemSms2;
    private RecyclerView recyclerView,recyclerView2;
    private SwipeRefreshLayout swipeRefreshSms,swipeRefreshSms2;
    private List<ModelSms> dataSet,dataSet2;
    private AllCommand allCommand;
    private String dataFromServer = "";
    private boolean bCheckBeginLoad = true,bCheckBeginLoad2 = true;
    private GridLayoutManager gridLayoutManager,gridLayoutManager2;
    private MainActivity mainActivity;
    private AdjustableImageView imgRecycleBin;


    public SmsFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("dataSet", (ArrayList<? extends Parcelable>) dataSet);
        outState.putBoolean("bCheckBeginLoad", bCheckBeginLoad);
        outState.putString("dataFromServer",dataFromServer);
        outState.putParcelableArrayList("dataSet2", (ArrayList<? extends Parcelable>) dataSet2);
        outState.putBoolean("bCheckBeginLoad2", bCheckBeginLoad2);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSet = new ArrayList<>();
        dataSet2 = new ArrayList<>();
        if (savedInstanceState != null){
            dataSet = savedInstanceState.getParcelableArrayList("dataSet");
            bCheckBeginLoad = savedInstanceState.getBoolean("bCheckBeginLoad");
            dataFromServer = savedInstanceState.getString("dataFromServer");
        }
        if (savedInstanceState != null){
            dataSet2 = savedInstanceState.getParcelableArrayList("dataSet2");
            bCheckBeginLoad2 = savedInstanceState.getBoolean("bCheckBeginLoad2");
        }
    }
    public static SmsFragment newInstance() {
        SmsFragment fragment = new SmsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sms, container, false);
        initiateView(rootView);
        return rootView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            customAdapterItemSms = new CustomAdapterItemSms(getActivity(),dataSet);
            recyclerView.setAdapter(customAdapterItemSms);
        }
        if (savedInstanceState != null) {
            customAdapterItemSms2 = new CustomAdapterItemSms(getActivity(),dataSet2);
            recyclerView2.setAdapter(customAdapterItemSms2);
        }
        if (bCheckBeginLoad) {
            bCheckBeginLoad = false;
            if (allCommand.isConnectingToInternet(getActivity())) {
                swipeRefreshSms.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshSms.setRefreshing(true);
                        loadDataFromServer();
                    }
                });
            }
        }
        if (bCheckBeginLoad2) {
            bCheckBeginLoad2 = false;
            if (allCommand.isConnectingToInternet(getActivity())) {
                swipeRefreshSms2.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshSms2.setRefreshing(true);
                        onReadSmsFromDevice();
                    }
                });
            }
        }
        setEventToView();
    }
    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }
    @Subscribe
    public void onStanByEvenBus(ModelEvenBus evenBus){
        if (evenBus != null){
            //Log.e("Check",evenBus.getKeyEvenBus() +"");
            if (evenBus.getKeyEvenBus() == 3){//Update list sms from device
                customAdapterItemSms2.notifyItemChanged(0);
            }
        }
    }
    private void setEventToView() {
        swipeRefreshSms.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromServer();

            }
        });
        swipeRefreshSms2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onReadSmsFromDevice();

            }
        });


        customAdapterItemSms.SetOnItemClickListener(new CustomAdapterItemSms.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


            }
        });
        customAdapterItemSms2.SetOnItemClickListener(new CustomAdapterItemSms.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


            }
        });

        customAdapterItemSms.SetOnLogItemClickListener(new CustomAdapterItemSms.OnLogItemClickListener() {
            @Override
            public void onItemLongClickListener(View view, int position) {
                //Toast.makeText(getActivity(), "Home", Toast.LENGTH_LONG).show();
            }
        });
        customAdapterItemSms2.SetOnLogItemClickListener(new CustomAdapterItemSms.OnLogItemClickListener() {
            @Override
            public void onItemLongClickListener(View view, int position) {
                if (position >= 0 && position < dataSet2.size()){
                    final String sFileName = dataSet2.get(position).namefile;
                    final String sFrom = dataSet2.get(position).from;
                    final String sTo = dataSet2.get(position).to;
                    final String sMsg = dataSet2.get(position).msg;
                    final String sDate = dataSet2.get(position).date;
                    final String sTime = dataSet2.get(position).time;
                    final String sError = dataSet2.get(position).error;
                    final String sBatt = dataSet2.get(position).batt;
                    final int sented = dataSet2.get(position).sented;
                    showMessageOKCancel("คุณต้องการส่งข้อมูลอีกครั้งใช่หรือไม่ \n\n" +
                                    "จาก : "+ sFrom + "\n" +
                                    "ถึง : "+ sTo + "\n\n" +
                                    "Msg : "+ sMsg + "\n\n"+
                                    "วันที่ : "+ sDate + " " +sTime+"\n"

                            , new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            dialogInterface.cancel();
                            new SendData(sFileName,sFrom,sTo,sMsg,sDate,sTime,sError,sBatt,getActivity());
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            dialogInterface.cancel();
                        }
                    });
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recycler, int newState) {
                int firstPos = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstPos > 0) {
                    swipeRefreshSms.setEnabled(false);
                } else {
                    swipeRefreshSms.setEnabled(true);
                    if(recyclerView.getScrollState() == 1)
                        if(swipeRefreshSms.isRefreshing())
                            recyclerView.stopScroll();
                }
            }
        });
        recyclerView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recycler, int newState) {
                int firstPos = gridLayoutManager2.findFirstCompletelyVisibleItemPosition();
                if (firstPos > 0) {
                    swipeRefreshSms2.setEnabled(false);
                } else {
                    swipeRefreshSms2.setEnabled(true);
                    if(recyclerView2.getScrollState() == 1)
                        if(swipeRefreshSms2.isRefreshing())
                            recyclerView2.stopScroll();
                }
            }
        });
        imgRecycleBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageOKCancel("คุณต้องการลบข้อความในตัวเครื่องทั้งหมดใช่หรือไม่", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        dialogInterface.cancel();
                        deleteMsmFromDevice();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                dialogInterface.cancel();
                    }
                });
            }
        });
    }
    private void loadDataFromServer() {
        if (dataSet.size() > 0){
            dataSet.clear();
            customAdapterItemSms.notifyDataSetChanged();
        }
        if (allCommand.isConnectingToInternet(getActivity())){
            final String URL_DATA = "http://" + allCommand.getStringShare(getActivity(), Utile.SHARE_URL,"") + "/app/get_sms.php";//ดึงรายการ sms
            final String phoneOne = allCommand.getStringShare(getActivity(),Utile.SHARE_PHONE1,"");
            final String phoneTwo = allCommand.getStringShare(getActivity(),Utile.SHARE_PHONE2,"");
            String phoneFormat = "";
            if ((phoneOne.length() > 0) && (phoneTwo.length() > 0)){
                phoneFormat = phoneOne + "," + phoneTwo;
            }else if (phoneOne.length() > 0){
                phoneFormat = phoneOne;
            }else if (phoneTwo.length() > 0){
                phoneFormat = phoneTwo;
            }
            final String finalPhoneFormat = phoneFormat;
            new AsyncTask<String, Void, Void>() {
                @Override
                protected Void doInBackground(String... strings) {
                    ArrayList<FromHttpPostOkHttp> paramsSms = new ArrayList<FromHttpPostOkHttp>();
                    paramsSms.add(new BasicNameValusPostOkHttp().BasicNameValusPostOkHttp("phone",finalPhoneFormat));
                    dataFromServer = allCommand.POST_OK_HTTP_SendData(URL_DATA, paramsSms);
                    ShowLogcat("dataFromServer", dataFromServer);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    getDataSet(dataFromServer);
                    swipeRefreshSms.setRefreshing(false);

                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else {
            mainActivity.isShowSnackbarPhone(getActivity());
            swipeRefreshSms.setRefreshing(false);
        }
    }
    private void initiateView(View rootView) {
        mainActivity = (MainActivity) getActivity();
        allCommand = new AllCommand();
        imgRecycleBin = rootView.findViewById(R.id.imgRecycleBin);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.SmsList);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(getActivity(),1);
        recyclerView.setLayoutManager(gridLayoutManager);
        swipeRefreshSms = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeSms);
        swipeRefreshSms.setColorSchemeResources(R.color.color_progressbar);
        swipeRefreshSms.setProgressBackgroundColorSchemeResource(R.color.bg_progressbar);
        if (getActivity().getResources().getInteger(R.integer.size_tab) == 1){
            swipeRefreshSms.setSize(SwipeRefreshLayout.LARGE);
        }
        customAdapterItemSms = new CustomAdapterItemSms(getActivity(),dataSet);
        recyclerView.setAdapter(customAdapterItemSms);

        recyclerView2 = (RecyclerView) rootView.findViewById(R.id.SmsList2);
        recyclerView2.setHasFixedSize(true);
        gridLayoutManager2 = new GridLayoutManager(getActivity(),1);
        recyclerView2.setLayoutManager(gridLayoutManager2);
        swipeRefreshSms2 = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeSms2);
        swipeRefreshSms2.setColorSchemeResources(R.color.color_progressbar);
        swipeRefreshSms2.setProgressBackgroundColorSchemeResource(R.color.bg_progressbar);
        if (getActivity().getResources().getInteger(R.integer.size_tab) == 1){
            swipeRefreshSms2.setSize(SwipeRefreshLayout.LARGE);
        }
        customAdapterItemSms2 = new CustomAdapterItemSms(getActivity(),dataSet2);
        recyclerView2.setAdapter(customAdapterItemSms2);

    }
    public void getDataSet(String data){
        try{
            JSONObject jObject;
            JSONArray jArray = new JSONArray(allCommand.CoverStringFromServer_Two(data));
            for (int i = 0;i< jArray.length();i++){
                jObject = jArray.getJSONObject(i);
                ModelSms modelSms = new ModelSms(
                        "",
                        jObject.getString("sms_from").trim(),
                        jObject.getString("sms_to").trim(),
                        "\t"+jObject.getString("sms_text").trim(),
                        jObject.getString("sms_date").trim(),
                        jObject.getString("sms_time").trim(),
                        "",
                        "",
                        -1);

                dataSet.add(modelSms);
                customAdapterItemSms.notifyDataSetChanged();
            }

        }catch (Exception e){
            ShowLogcat("Err","getDataSet " + e.getMessage());
        }
    }
    private void onReadSmsFromDevice(){
        if (dataSet2.size() > 0){
            dataSet2.clear();
            customAdapterItemSms2.notifyDataSetChanged();
        }

        File myDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            myDir =new File(android.os.Environment.getExternalStorageDirectory()+ Utile.PATH_PUSH_SMS
                    + getActivity().getPackageName(),Utile.PATH_PUSH_SMS_CHILD);
            File[] dirFiles = myDir.listFiles();

            if (dirFiles != null && dirFiles.length != 0) {
                List<File> directoryListing = new ArrayList<File>();
                directoryListing.addAll(Arrays.asList(dirFiles));
                Collections.sort(directoryListing, new SortFileName());
                //Collections.sort(directoryListing, new SortFolder());

                for (int i =/* dirFiles.length-1*/directoryListing.size()-1; i >= 0; i--) {
                    String fileOutput = /*dirFiles[i].toString();*/ directoryListing.get(i).toString();
                    StringBuilder builder = new StringBuilder();
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(/*dirFiles[i]*/directoryListing.get(i)));
                        String line;
                        while ((line = br.readLine())!=null){
                            builder.append(line);
                            builder.append("\n");
                        }
                        br.close();
                        JSONTokener tokener = new JSONTokener(builder.toString());
                        JSONObject jObject = new JSONObject(allCommand.CoverStringFromServer_One(tokener.toString()));
                        //Log.e("Json",i + ". " + jObject.getString("sms_filename") + " : " + jObject.getInt("sms_sented"));
                        ModelSms modelSms = new ModelSms(
                                jObject.getString("sms_filename").trim(),
                                jObject.getString("sms_from").trim(),
                                jObject.getString("sms_to").trim(),
                                "\t"+jObject.getString("sms_text").trim(),
                                jObject.getString("sms_date").trim(),
                                jObject.getString("sms_time").trim(),
                                jObject.getString("sms_error").trim(),
                                jObject.getString("sms_batt").trim(),
                                jObject.getInt("sms_sented"));
                        dataSet2.add(modelSms);
                        customAdapterItemSms2.notifyDataSetChanged();
                    }catch (Exception e){
                        ShowLogcat("Error", "Read SMS From file "+e.toString());
                    }

                }
            }

        }
        swipeRefreshSms2.setRefreshing(false);
    }
    public void deleteMsmFromDevice(){
        try {
            File myDir;
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
                myDir =new File(android.os.Environment.getExternalStorageDirectory()+ Utile.PATH_PUSH_SMS
                        + getActivity().getPackageName(),Utile.PATH_PUSH_SMS_CHILD);
                File[] dirFiles = myDir.listFiles();
                if (dirFiles.length != 0) {
                    for (int i = dirFiles.length-1; i >= 0; i--) {
                        if (dirFiles[i].isFile()){
                            dirFiles[i].delete();
                        }
                    }
                }
                if (dataSet2.size() > 0){
                    dataSet2.clear();
                    customAdapterItemSms2.notifyDataSetChanged();
                }
            }

        }catch (Exception e){
            ShowLogcat("Error","delete sms from device " + e.getMessage());
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener closeListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("ใช่", okListener)
                .setNegativeButton("ไม่ใช่", closeListener)
                .create()
                .show();
    }
    private void ShowLogcat(String tag,String title){
        if (BuildConfig.DEBUG){
            Log.e("***" + tag + "***","Sms : " + title);
        }
    }
    //sorts based on the files name
    public class SortFileName implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            return f1.getName().compareTo(f2.getName());
        }
    }

    //sorts based on a file or folder. folders will be listed first
    public class SortFolder implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            if (f1.isDirectory() == f2.isDirectory())
                return 0;
            else if (f1.isDirectory() && !f2.isDirectory())
                return -1;
            else
                return 1;
        }
    }
}
