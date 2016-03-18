package com.bcp.SFA_Native;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class ActivitySync extends ListActivity {

    private final String TAG_MENU = "menu";
    private final String TAG_ICON = "1";
    private final String TAG_ID = "id";
    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_CABANG = "cabang";
    private final String TAG_WEB = "web";
    private String DB_ORDER="ORDER_";
    private String StatusRequest="0";
    private final String TAG_LOGIN = "logindata";
    private final String TAG_EMAIL = "email";

    JSONArray GenerateArray = null;

    FN_DBHandler db,dbset;
    private String      DB_SETTING="SETTING";
    private String DB_PATH_CSV_SUCCESS=Environment.getExternalStorageDirectory()+"/SFA/CSV/SUCCESS";
    private String FilePath= Environment.getExternalStorageDirectory()+"/foto.zip";
    private String FilePathInv= Environment.getExternalStorageDirectory()+"/foto.zip";
    private String FilePathRetur= Environment.getExternalStorageDirectory()+"/foto.zip";
    private String FilePathKunjungan= Environment.getExternalStorageDirectory()+"/foto.zip";
    private String FilePathZip= Environment.getExternalStorageDirectory()+"/foto.zip";
    private String filename="",finalfilename="",filenameinv="",finalfilenameinv="",filenameretur="",finalfilenameretur="",filenamekunjungan="",finalfilenamekunjungan="";
    private int serverResponseCode = 0;
    private String upLoadServerUri = "http://192.168.31.10:9020/ws/uploadfile.php";

    private final String TAG_OPT1 = "opt1";
    private final String TAG_OPT2 = "opt2";
    private final String TAG_OPT3 = "opt3";

    JSONArray UploadInfoArray = null;
    String Web;

    int[] flags = new int[]{
            R.drawable.sfa_download,
            R.drawable.sfa_upload,
            R.drawable.sfa_manual
    };

    TextView TxtUser,TxtTime;
    ImageView ImgIcon;
    ImageView ImgSummary;

    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    ProgressDialog dialog;

    private static final String username = "cslocationservice01@gmail.com";
    private static final String password = "cslocation1988";

    String email = "it.yogi@borwita.co.id";
    String subject = "SFA ORDER MANUAL";
    String message = "SFA ORDER MANUAL";

    String SalesName = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_mainmenu);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        db = new FN_DBHandler(getApplicationContext(),DB_PATH,DB_ORDER+getPref(TAG_LASTLOGIN));
        dbset = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_SETTING);

        TextView TxtJudul = (TextView) findViewById(R.id.txtMainMenuUtama);
        TxtJudul.setText("SINKRON");

        TxtUser = (TextView) findViewById(R.id.MainMenu_TxtUser);
        TxtTime = (TextView) findViewById(R.id.MainMenu_TxtTime);
        ImgIcon = (ImageView) findViewById(R.id.MainMenu_Icon);

        ImgIcon.setImageResource(R.drawable.sfa_sync);

        SalesName = getPref(TAG_NAMELOGIN);

        if ((SalesName.length())>20){
            SalesName = SalesName.substring(0,20);
        }

        TxtUser.setText(SalesName);
        TxtTime.setText("("+getPref(TAG_LASTLOGIN)+")");

        Intent in = getIntent();
        Web = in.getStringExtra(TAG_WEB);

        JSONObject SettingJSON = null;
        try{
            SettingJSON = dbset.GetSetting();
            Web = SettingJSON.getString(TAG_WEB);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dbset.close();

        ArrayList<HashMap<String, String>> OperatorMenuList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> maporder = new HashMap<String, String>();
        maporder.put(TAG_ICON, Integer.toString(flags[0]));
        maporder.put(TAG_ID, "0");
        maporder.put(TAG_MENU, "Download");

        HashMap<String, String> mapbarang = new HashMap<String, String>();
        mapbarang.put(TAG_ICON, Integer.toString(flags[1]));
        mapbarang.put(TAG_ID, "1");
        mapbarang.put(TAG_MENU, "Upload");

        HashMap<String, String> mapmanual = new HashMap<String, String>();
        mapmanual.put(TAG_ICON, Integer.toString(flags[2]));
        mapmanual.put(TAG_ID, "2");
        mapmanual.put(TAG_MENU, "Manual");

        OperatorMenuList.add(maporder);
        OperatorMenuList.add(mapbarang);
        OperatorMenuList.add(mapmanual);

        ListAdapter adapter = new AdapterCustomSimple(this, OperatorMenuList,
                R.layout.l_mainmenu,
                new String[] { TAG_ICON, TAG_MENU, TAG_ID },
                new int[] { R.id.imageViewOP, R.id.MainMenuNama, R.id.MainMenuID });

        setListAdapter(adapter);

        upLoadServerUri = "http://"+Web+"/uploadfile.php";

        // selecting single ListView item
        ListView lv = getListView();

        // Launching new screen on Selecting Single ListItem
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String nama = ((TextView) view.findViewById(R.id.MainMenuNama)).getText().toString();
                String menuid = ((TextView) view.findViewById(R.id.MainMenuID)).getText().toString();

                if(menuid.equals("0")){
                    ShowKonfirmasiProsesZip("0");
                }

                if(menuid.equals("1")){
                    if ((db.getCekExistOrderPelangganNotSync()<=0)&&(db.getCekExistCountInventoryPelanggan()<=0)&&(db.getCekExistCountReturPelanggan()<=0)){
                        Toast.makeText(getApplicationContext(),"Tidak Ada Data Yang Di Upload",Toast.LENGTH_SHORT).show();
                    }else{
                        ShowKonfirmasiProsesZip("1");
                    }
                }

                if(menuid.equals("2")){
                    if ((db.getCekExistOrderPelangganNotSync()<=0)&&(db.getCekExistCountInventoryPelanggan()<=0)&&(db.getCekExistCountReturPelanggan()<=0)){
                        Toast.makeText(getApplicationContext(),"Tidak Ada Data Yang Di Generate",Toast.LENGTH_SHORT).show();
                    }else{
                        ShowKonfirmasiProsesZip("2");
                    }
                }
            }
        });

        email = getPref(TAG_EMAIL);

        ImgSummary = (ImageView) findViewById(R.id.MainMenu_ImgSummary);
        ImgSummary.setVisibility(View.GONE);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
            intent.putExtra(TAG_WEB,Web);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return false;
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public class UpdateMaster extends AsyncTask<Void, Integer, Void> {

        Context context;
        Handler handler;
        Dialog dialog;
        TextView txtLoadingProgress;
        String StatusGenerate ="0";
        int showDialog=0;

        UpdateMaster(Context context, Handler handler){
            this.context=context;
            this.handler=handler;
        }

        UpdateMaster(Context context){
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // create dialog
            dialog=new Dialog(context);
            dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.p_gifloading);
            txtLoadingProgress =(TextView) dialog.findViewById(R.id.txtLoading2);
            txtLoadingProgress.setText("Downloading . .");

            String path = "file:///android_asset/gambar.gif";
            WebView wV = (WebView) dialog.findViewById(R.id.webView);
            wV.loadUrl(path);
            wV.setScrollBarStyle(WebView.OVER_SCROLL_NEVER);
            // disable scroll on touch
            wV.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return (event.getAction() == MotionEvent.ACTION_MOVE);
                }
            });
            wV.setVerticalScrollBarEnabled(false);
            wV.setHorizontalScrollBarEnabled(false);

            dialog.show();
        }


        @Override
        protected Void doInBackground(Void... arg0) {
            String url = "http://"+Web+"/pengaturan/generatemaster_dev.php?username="+getPref(TAG_LASTLOGIN)+"&cabang="+getPref(TAG_CABANG)+"&codename=1988";
            final FN_JSONParser jParser = new FN_JSONParser();
            String fileName = "MASTER_"+getPref(TAG_LASTLOGIN);
            try {

                JSONObject json = jParser.getJSONFromUrl(url);
                StatusRequest = json.getString("STATUS");
                if(StatusRequest.equals("1")){
                    showDialog=1;
                    // Get JSON KKPDV
                    GenerateArray = json.getJSONArray(TAG_LOGIN);

                    for (int j=0;j<GenerateArray.length();j++){
                        JSONObject d = GenerateArray.getJSONObject(j);
                        String Stat = d.getString("status");
                        String minOrder = d.getString("minorder");
                        if (Stat.equals("1")){
                            showDialog=2;
                            try {
                                URL urls = new URL("http://"+Web+"/sqlite/"+fileName+".zip");
                                File file = new File(DB_PATH,fileName+".zip");
                                URLConnection uconn = null;
                                try {
                                    uconn = urls.openConnection();
                                    uconn.setReadTimeout(50000);
                                    uconn.setConnectTimeout(50000);

                                    InputStream is = uconn.getInputStream();
                                    BufferedInputStream bufferinstream = new BufferedInputStream(is);

                                    ByteArrayBuffer baf = new ByteArrayBuffer(5000);
                                    int current = 0;
                                    while((current = bufferinstream.read()) != -1){
                                        baf.append((byte) current);
                                    }

                                    FileOutputStream fos = new FileOutputStream(file);
                                    fos.write(baf.toByteArray());
                                    fos.flush();
                                    fos.close();

                                    if(cekExistMaster()){
                                        renameOldMaster();
                                    }

                                    //Edit Here
                                    if(unpackZip(DB_PATH + "/", "MASTER_" + getPref(TAG_LASTLOGIN) + ".zip")){
                                        if (renameNewMaster(fileName)){
                                            deleteOldMaster();
                                            StatusGenerate = "1";
                                            //db.updateMinOrder(minOrder);
                                        }else{
                                            renameNewMaster("MASTER_TMP");
                                            StatusGenerate = "Gagal Rename File Master";
                                        }
                                    }
                                    deleteOldMasterZip();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    StatusGenerate = e.getMessage();
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                StatusGenerate = e.getMessage();
                            }
                        }
                        publishProgress(j);
                    }
                }else{
                    Log.e("0", "Status 0");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showDialog=0;
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (showDialog){
                case 0:{
                    dialog.dismiss();
                    break;
                }
                case 1:{
                    txtLoadingProgress.setText("Downloading Data.. ");
                    break;
                }
                case 2:{
                    txtLoadingProgress.setText("Receiving Data . . .");
                    break;
                }
                default:break;
            }

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(StatusGenerate.equals("1")){
                Toast.makeText(getApplicationContext(),"Download Berhasil",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"Download Gagal : "+StatusGenerate,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean deleteOldMaster(){
        File file = new File(DB_PATH,"MASTER_TMP");
        if(file.exists()){
            boolean deleted = file.delete();
            return deleted;
        }
        return true;
    }
    public boolean deleteOldMasterZip(){
        File file = new File(DB_PATH,"MASTER_"+getPref(TAG_LASTLOGIN)+".zip");
        if(file.exists()){
            boolean deleted = file.delete();
            return deleted;
        }
        return true;
    }
    public boolean cekExistMaster(){
        File file = new File(DB_PATH,"MASTER");
        if(file.exists()){
            return true;
        }
        return false;
    }
    public boolean renameNewMaster(String fileName){
        File from = new File(DB_PATH,fileName);
        File to = new File(DB_PATH,"MASTER");
        boolean rename = from.renameTo(to);
        return rename;
    }

    public boolean renameOldMaster(){
        File from = new File(DB_PATH,"MASTER");
        File to = new File(DB_PATH,"MASTER_TMP");
        boolean rename = from.renameTo(to);
        return rename;
    }

    public boolean renameMasterManual(String filename){
        File from = new File(DB_PATH_CSV_SUCCESS,filename);
        File to = new File(DB_PATH_CSV_SUCCESS,"OrderEntry_"+getPref(TAG_LASTLOGIN).substring(0,4)+"_"+getToday()+".csv");
        boolean rename = from.renameTo(to);
        finalfilename = "OrderEntry_"+getPref(TAG_LASTLOGIN).substring(0,4)+"_"+getToday()+".csv";
        return rename;
    }

    public boolean renameMasterInvManual(String filename){
        File from = new File(DB_PATH_CSV_SUCCESS,filename);
        File to = new File(DB_PATH_CSV_SUCCESS,"InvEntry_"+getPref(TAG_LASTLOGIN).substring(0,4)+"_"+getToday()+".csv");
        boolean rename = from.renameTo(to);
        finalfilenameinv = "InvEntry_"+getPref(TAG_LASTLOGIN).substring(0,4)+"_"+getToday()+".csv";
        return rename;
    }

    public boolean renameMasterReturManual(String filename){
        File from = new File(DB_PATH_CSV_SUCCESS,filename);
        File to = new File(DB_PATH_CSV_SUCCESS,"ReturEntry_"+getPref(TAG_LASTLOGIN).substring(0,4)+"_"+getToday()+".csv");
        boolean rename = from.renameTo(to);
        finalfilenameretur = "ReturEntry_"+getPref(TAG_LASTLOGIN).substring(0,4)+"_"+getToday()+".csv";
        return rename;
    }

    public boolean renameMasterKunjunganManual(String filename){
        File from = new File(DB_PATH_CSV_SUCCESS,filename);
        File to = new File(DB_PATH_CSV_SUCCESS,"KunjunganEntry_"+getPref(TAG_LASTLOGIN).substring(0,4)+"_"+getToday()+".csv");
        boolean rename = from.renameTo(to);
        finalfilenamekunjungan = "KunjunganEntry_"+getPref(TAG_LASTLOGIN).substring(0,4)+"_"+getToday()+".csv";
        return rename;
    }

    public boolean generateCSV(String lastlogin){
        File SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        if(!SFAdircsv.exists()){
            SFAdircsv.mkdirs();
        }

        //--------------------delete file 1 minggu
        SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        for (File f : SFAdircsv.listFiles()) {
            if (f.isFile()){
                if(f.getName().toString().contains(getDateTime("ddMMyyyy",-7))){
                    f.delete();
                }
            }
        }
        filename=lastlogin+"_"+getDateTime("ddMMyyyy_HHmm",0)+".csv";

        //---------------------create file-----------------------------------
        try {
            Cursor cursor= db.getAllRawPenjualan();

            FileWriter fw = new FileWriter(DB_PATH_CSV_SUCCESS+"/"+filename);
            fw.append("DistributorCode;");
            fw.append("BranchCode;");
            fw.append("SalesRepCode;");
            fw.append("RetailerCode;");
            fw.append("OrderNo;");
            fw.append("OrderDate;");
            fw.append("UploadDate;");
            fw.append("ChildSKUCode;");
            fw.append("OrderQty;");
            fw.append("OrderQty(cases);");
            fw.append("DeliveryDate;");
            fw.append("Keterangan;");
            fw.append("EntryTime;");
            fw.append("Longitude;");
            fw.append("Latitude;");
            fw.append("Seq");
            fw.append('\n');

            if (cursor.moveToFirst()) {
                do {
                    fw.append("DB001;");
                    fw.append(lastlogin.substring(0, 2)+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("sales"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("shipto"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("kode"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("tgl"))+";");
                    fw.append(getDateTime("MM/dd/yyyy", 0)+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("brg"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("pcs"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("crt"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("tgl"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("ket"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("entrytime"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("longitude"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("latitude"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("seq"))+"\n");
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            fw.close();
            //db.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Generate CSV Gagal!!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean generateCSVManual(String lastlogin){
        File SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        if(!SFAdircsv.exists()){
            SFAdircsv.mkdirs();
        }

        //--------------------delete file 1 minggu
        SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        for (File f : SFAdircsv.listFiles()) {
            if (f.isFile()){
                if(f.getName().toString().contains(getDateTime("ddMMyyyy",-7))){
                    f.delete();
                }
            }
        }
        filename=lastlogin+"_"+getDateTime("ddMMyyyy_HHmm",0)+".csv";

        //---------------------create file-----------------------------------
        try {
            Cursor cursor= db.getAllRawPenjualan();

            FileWriter fw = new FileWriter(DB_PATH_CSV_SUCCESS+"/"+filename);
            fw.append("DistributorCode;");
            fw.append("BranchCode;");
            fw.append("SalesRepCode;");
            fw.append("RetailerCode;");
            fw.append("OrderNo;");
            fw.append("OrderDate;");
            fw.append("UploadDate;");
            fw.append("ChildSKUCode;");
            fw.append("OrderQty;");
            fw.append("OrderQty(cases);");
            fw.append("DeliveryDate;");
            fw.append("Keterangan");
            fw.append('\n');

            if (cursor.moveToFirst()) {
                do {
                    fw.append("DB001;");
                    fw.append(lastlogin.substring(0, 2)+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("sales"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("shipto"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("kode"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("tgl"))+";");
                    fw.append(getDateTime("MM/dd/yyyy", 0)+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("brg"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("pcs"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("crt"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("tgl"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("ket"))+"\n");
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            fw.close();
            //db.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Generate CSV Gagal!!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean generateCSVInv(String lastlogin){
        File SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        if(!SFAdircsv.exists()){
            SFAdircsv.mkdirs();
        }

        //--------------------delete file 1 minggu
        SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        for (File f : SFAdircsv.listFiles()) {
            if (f.isFile()){
                if(f.getName().toString().contains(getDateTime("ddMMyyyy",-7))){
                    f.delete();
                }
            }
        }
        filenameinv=lastlogin+"_"+getDateTime("ddMMyyyy_HHmm",0)+"_Inv.csv";

        //---------------------create file-----------------------------------
        try {
            Cursor cursor= db.getAllRawInventory(getToday2());

            FileWriter fw = new FileWriter(DB_PATH_CSV_SUCCESS+"/"+filenameinv);
            fw.append("SalesRepCode;");
            fw.append("RetailerCode;");
            fw.append("InvNo;");
            fw.append("ProductCode;");
            fw.append("Stok;");
            fw.append(getPref(TAG_OPT1)+";");
            fw.append(getPref(TAG_OPT2)+";");
            fw.append(getPref(TAG_OPT3)+";");
            fw.append("CreateDate;");
            fw.append('\n');

            if (cursor.moveToFirst()) {
                do {
                    fw.append(cursor.getString(cursor.getColumnIndex("sales"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("shipto"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("kode"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("brg"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("stok"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("opt1"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("opt2"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("opt3"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("tgl"))+"\n");
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            fw.close();
            //db.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Generate CSV Inv Gagal!!",Toast.LENGTH_SHORT).show();
            return false;
        }finally {
            db.close();
        }
        return true;
    }

    public boolean generateCSVRetur(String lastlogin){
        File SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        if(!SFAdircsv.exists()){
            SFAdircsv.mkdirs();
        }

        //--------------------delete file 1 minggu
        SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        for (File f : SFAdircsv.listFiles()) {
            if (f.isFile()){
                if(f.getName().toString().contains(getDateTime("ddMMyyyy",-7))){
                    f.delete();
                }
            }
        }
        filenameretur=lastlogin+"_"+getDateTime("ddMMyyyy_HHmm",0)+"_Retur.csv";

        //---------------------create file-----------------------------------
        try {
            Cursor cursor= db.getAllRawRetur();

            FileWriter fw = new FileWriter(DB_PATH_CSV_SUCCESS+"/"+filenameretur);
            fw.append("DistributorCode;");
            fw.append("BranchCode;");
            fw.append("SalesRepCode;");
            fw.append("RetailerCode;");
            fw.append("ReturNo;");
            fw.append("ReturDate;");
            fw.append("UploadDate;");
            fw.append("ChildSKUCode;");
            fw.append("OrderQty;");
            fw.append("OrderQty(cases);");
            fw.append("Alasan;");
            fw.append("EntryTime;");
            fw.append('\n');

            if (cursor.moveToFirst()) {
                do {
                    fw.append("DB001;");
                    fw.append(lastlogin.substring(0, 2)+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("sales"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("shipto"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("kode"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("tgl"))+";");
                    fw.append(getDateTime("MM/dd/yyyy", 0)+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("brg"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("pcs"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("crt"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("alasan"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("entrytime"))+"\n");
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            fw.close();
            //db.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Generate CSV Retur Gagal!!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean generateCSVKunjungan(String lastlogin){
        File SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        if(!SFAdircsv.exists()){
            SFAdircsv.mkdirs();
        }

        //--------------------delete file 1 minggu
        SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        for (File f : SFAdircsv.listFiles()) {
            if (f.isFile()){
                if(f.getName().toString().contains(getDateTime("ddMMyyyy",-7))){
                    f.delete();
                }
            }
        }
        filenamekunjungan=lastlogin+"_"+getDateTime("ddMMyyyy_HHmm",0)+"_Kunjungan.csv";

        //---------------------create file-----------------------------------
        try {
            Cursor cursor= db.getAllRawKunjungan(getToday2());

            FileWriter fw = new FileWriter(DB_PATH_CSV_SUCCESS+"/"+filenamekunjungan);
            fw.append("BranchCode;");
            fw.append("Tgl;");
            fw.append("SalesRepCode;");
            fw.append("RetailerCode;");
            fw.append("Call;");
            fw.append("PCall;");
            fw.append("Deviasi;");
            fw.append("UnCall;");
            fw.append("Reason;");
            fw.append("InStore;");
            fw.append("OutStore;");
            fw.append("Longitude;");
            fw.append("Latitude;");
            fw.append("GPSTime;");
            fw.append("Reverse;");
            fw.append('\n');

            if (cursor.moveToFirst()) {
                do {
                    fw.append(lastlogin.substring(0, 2)+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("tgl"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("sales"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("shipto"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("call"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("pcall"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("deviasi"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("uncall"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("reason"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("instore"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("outstore"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("longitude"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("latitude"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("gpstime"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("reverse"))+";");
                    fw.append(""+"\n");
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            fw.close();
            //db.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Generate CSV Kunjungan Gagal!!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private String getDateTime(String format,int hari) {
        Calendar calendar = Calendar.getInstance();
        Date myDate = new Date();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_YEAR, hari);
        myDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, Locale.getDefault());
        return dateFormat.format(myDate);
    }

    public int uploadFile(String sourceFileUri,final String filename) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :" + FilePath);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ActivitySync.this, "File Tidak Ditemukan " + FilePath, Toast.LENGTH_SHORT).show();
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String urls = "http://" + Web + "/pengaturan/insertorderfile.php?codename=1988&filename=" + filename + "&salesname=" + getPref(TAG_NAMELOGIN).replaceAll("[^a-zA-Z]+", "_");
                            final FN_JSONParser jParser = new FN_JSONParser();

                            try {
                                JSONObject json = jParser.getJSONFromUrl(urls);
                                StatusRequest = json.getString("STATUS");

                                if (StatusRequest.equals("1")) {
                                    UploadInfoArray = json.getJSONArray("uploaddata");

                                    String Stat = "0";

                                    for (int i = 0; i < UploadInfoArray.length(); i++) {
                                        JSONObject a = UploadInfoArray.getJSONObject(i);
                                        Stat = a.getString("status");
                                    }

                                    if (Stat.equals("1")) {
                                        FilePathInv = DB_PATH_CSV_SUCCESS + "/" + filenameinv;
                                        dialog = ProgressDialog.show(ActivitySync.this, "", "Uploading file Inventory...", true);
                                            new Thread(new Runnable() {
                                                public void run() {
                                                    uploadFileInv(FilePathInv, filenameinv);
                                                }
                                            }).start();
                                        db.updateFlagOrder();
                                        //Toast.makeText(ActivitySync.this, "Unggah Data Berhasil.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ActivitySync.this, "Unggah Data Gagal 3." + " - " + UploadInfoArray.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(ActivitySync.this, "Unggah Data Gagal 2." + " - " + UploadInfoArray.toString(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(ActivitySync.this, "Unggah Data Gagal. 1", Toast.LENGTH_SHORT).show();
                                Log.e("123", e.getMessage());
                            }

                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ActivitySync.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ActivitySync.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "Exception : "  + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

    public int uploadFileInv(String sourceFileUri,final String filename) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :" + FilePathInv);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ActivitySync.this, "File Tidak Ditemukan " + FilePathInv, Toast.LENGTH_SHORT).show();
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String urls = "http://"+Web+"/pengaturan/insertinvfile.php?codename=1988&filename="+filename+"&salesname="+getPref(TAG_NAMELOGIN).replaceAll("[^a-zA-Z]+","_");
                            final FN_JSONParser jParser = new FN_JSONParser();

                            try {
                                JSONObject json = jParser.getJSONFromUrl(urls);
                                StatusRequest = json.getString("STATUS");

                                if(StatusRequest.equals("1")){
                                    UploadInfoArray = json.getJSONArray("uploaddata");

                                    String Stat = "0";

                                    for (int i=0;i<UploadInfoArray.length();i++){
                                        JSONObject a = UploadInfoArray.getJSONObject(i);
                                        Stat = a.getString("status");
                                    }

                                    if(Stat.equals("1")){
                                        Toast.makeText(ActivitySync.this, "Unggah Data Berhasil.", Toast.LENGTH_SHORT).show();
                                        db.updateFlagInventory();
                                    }else{
                                        Toast.makeText(ActivitySync.this, "Unggah Data Gagal 3 : Inv."+" - "+UploadInfoArray.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(ActivitySync.this, "Unggah Data Gagal 2 : Inv."+" - "+UploadInfoArray.toString(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(ActivitySync.this, "Unggah Data Gagal. 1 : Inv", Toast.LENGTH_SHORT).show();
                                Log.e("123",e.getMessage());
                            }

                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ActivitySync.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ActivitySync.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "Exception : "  + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

    public void ShowDialogOption(final String command){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Pilihan Proses");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Pilih data yang akan diproses?");

        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);

        final TextView TxtLblPilihan = new TextView(this);
        TxtLblPilihan.setText("Pilihan data : ");

        String[] arrData = new String[2];
        arrData[0] = "Order + Inventory";
        arrData[1] = "Inventory";
        final Spinner SpnData = new Spinner(this);

        ArrayAdapter Adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrData);
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnData.setAdapter(Adapter);

        layout.addView(TxtLblNama,params);
        layout.addView(SpnData,params);


        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoge, int which) {
                dialoge.dismiss();
                if(command.equals("1")){
                    if(SpnData.getSelectedItemPosition()==0){
                        if (db.getCekExistOrderPelangganNotSync()<=0){
                            Toast.makeText(getApplicationContext(),"Tidak Ada Order Yang Di Upload",Toast.LENGTH_SHORT).show();
                        }else {
                            ShowKonfirmasiProses("1","0");
                        }
                    }else{
                        if(db.getCekExistCountInventoryPelanggan()<=0){
                            Toast.makeText(getApplicationContext(),"Tidak Ada Inventory Yang Di Upload",Toast.LENGTH_SHORT).show();
                        }else{
                            ShowKonfirmasiProses("1","1");
                        }
                    }
                }else {
                    if(SpnData.getSelectedItemPosition()==0){
                        if (db.getCekExistOrderPelangganNotSync()<=0){
                            Toast.makeText(getApplicationContext(),"Tidak Ada Order Yang Di Upload",Toast.LENGTH_SHORT).show();
                        }else {
                            ShowKonfirmasiProses("2","0");
                        }
                    }else{
                        if(db.getCekExistCountInventoryPelanggan()<=0){
                            Toast.makeText(getApplicationContext(),"Tidak Ada Inventory Yang Di Upload",Toast.LENGTH_SHORT).show();
                        }else {
                            ShowKonfirmasiProses("2", "1");
                        }
                    }
                }
            }
        });

        // Set up the buttons
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoge, int which) {
                dialoge.dismiss();
            }
        });

        builder.show();
    }

    public void ShowKonfirmasiProses(final String command,final String uploadonlyinv){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Proses");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        if (command.equals("0")){
            TxtLblNama.setText("Apakah anda yakin ingin mengunduh master yang baru?");
        }else if(command.equals("1")){
            TxtLblNama.setText("Apakah anda yakin ingin mengunggah?");
        }else{
            TxtLblNama.setText("Apakah anda yakin memproses manual?");
        }
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);


        layout.addView(TxtLblNama,params);


        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoge, int which) {
                dialoge.dismiss();
                if(command.equals("0")){
                    new UpdateMaster(ActivitySync.this).execute();
                }else if (command.equals("1")){
                    generateCSV(getPref(TAG_LASTLOGIN));
                    generateCSVInv(getPref(TAG_LASTLOGIN));
                    if (uploadonlyinv.equals("1")){
                        FilePathInv = DB_PATH_CSV_SUCCESS+"/"+filenameinv;
                        dialog = ProgressDialog.show(ActivitySync.this, "", "Uploading file Inventory...", true);
                        new Thread(new Runnable() {
                            public void run() {
                                uploadFileInv(FilePathInv,filenameinv);
                            }
                        }).start();
                    }else{
                        FilePath = DB_PATH_CSV_SUCCESS+"/"+filename;
                        dialog = ProgressDialog.show(ActivitySync.this, "", "Uploading file Order...", true);
                        new Thread(new Runnable() {
                            public void run() {
                                uploadFile(FilePath,filename);
                            }
                        }).start();
                    }
                }else{
                    if (uploadonlyinv.equals("1")){
                        generateCSVInv(getPref(TAG_LASTLOGIN));
                        renameMasterInvManual(filenameinv);
                    }else{
                        generateCSV(getPref(TAG_LASTLOGIN));
                        renameMasterManual(filename);

                        generateCSVInv(getPref(TAG_LASTLOGIN));
                        renameMasterInvManual(filenameinv);
                    }

                    subject = "SFA ORDER MANUAL";
                    message = "SFA ORDER MANUAL - "+getPref(TAG_LASTLOGIN) + " - " + getPref(TAG_NAMELOGIN).replaceAll("[^a-zA-Z]+", "_") +" - " + getToday();
                    sendMail(email, subject, message);
                }
            }
        });

        // Set up the buttons
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoge, int which) {
                dialoge.dismiss();
            }
        });

        builder.show();
    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmm");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public String getToday2(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("cslocationservice01@gmail.com", "cslocationservice01"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);

        Multipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(messageBody, "text/html");
        mp.addBodyPart(htmlPart);

        File gpxfile = new File(DB_PATH_CSV_SUCCESS, finalfilename);
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        FileDataSource fileDataSource =new FileDataSource(gpxfile);
        messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
        messageBodyPart.setFileName(gpxfile.getName());

        mp.addBodyPart(messageBodyPart);

        File gpxfile2 = new File(DB_PATH_CSV_SUCCESS, finalfilenameinv);
        MimeBodyPart messageBodyPart2 = new MimeBodyPart();
        FileDataSource fileDataSource2 =new FileDataSource(gpxfile2);
        messageBodyPart2.setDataHandler(new DataHandler(fileDataSource2));
        messageBodyPart2.setFileName(gpxfile2.getName());

        mp.addBodyPart(messageBodyPart2);

        File gpxfile3 = new File(DB_PATH_CSV_SUCCESS, finalfilenameretur);
        MimeBodyPart messageBodyPart3 = new MimeBodyPart();
        FileDataSource fileDataSource3 =new FileDataSource(gpxfile3);
        messageBodyPart3.setDataHandler(new DataHandler(fileDataSource3));
        messageBodyPart3.setFileName(gpxfile3.getName());

        mp.addBodyPart(messageBodyPart3);

        File gpxfile4 = new File(DB_PATH_CSV_SUCCESS, finalfilenamekunjungan);
        MimeBodyPart messageBodyPart4 = new MimeBodyPart();
        FileDataSource fileDataSource4 =new FileDataSource(gpxfile4);
        messageBodyPart4.setDataHandler(new DataHandler(fileDataSource4));
        messageBodyPart4.setFileName(gpxfile4.getName());

        mp.addBodyPart(messageBodyPart4);

        message.setContent(mp);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;
        int Done =0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ActivitySync.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(Done==1){
                Toast.makeText(ActivitySync.this,"Berhasil Mengirim Email ke "+email,Toast.LENGTH_SHORT).show();
                db.updateFlagOrder();
                db.updateFlagInventory();
                db.updateFlagRetur();
                dbset.UpdateSettingTglLogin();
            }else{
                Toast.makeText(ActivitySync.this,"Gagal Mengirim Email",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                Done = 1;
            } catch (MessagingException e) {
                e.printStackTrace();
                Done = 0;
            }
            return null;
        }
    }

    //Unzip File
    private boolean unpackZip(String path, String zipname){
        InputStream is;
        ZipInputStream zis;
        try
        {
            String filename;
            is = new FileInputStream(path + zipname);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null)
            {
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (ze.isDirectory()) {
                    File fmd = new File(path + filename);
                    fmd.mkdirs();
                    continue;
                }

                FileOutputStream fout = new FileOutputStream(path + filename);

                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1)
                {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void packZip(String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[1024 * 1024];

            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, 1024 * 1024);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, 1024 * 1024)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int uploadFileZip(String sourceFileUri,final String filezip,final String filename,final String filenameinv, final String filenameretur, final String filenamekunjungans) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 50 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();
            Log.e("uploadFile", "Source File not exist :" + filezip);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ActivitySync.this, "File Tidak Ditemukan " + filezip, Toast.LENGTH_SHORT).show();
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String urls = "http://"+Web+"/pengaturan/insertzipfilev025.php?codename=1988&filezip="+filezip+"&filename="+filename+"&filenameinv="+filenameinv+"&filenameretur="+filenameretur+"&filenamekunjungan="+filenamekunjungans+"&salesname="+getPref(TAG_NAMELOGIN).replaceAll("[^a-zA-Z]+","_");
                            final FN_JSONParser jParser = new FN_JSONParser();

                            try {
                                JSONObject json = jParser.getJSONFromUrl(urls);
                                StatusRequest = json.getString("STATUS");

                                if(StatusRequest.equals("1")){
                                    UploadInfoArray = json.getJSONArray("uploaddata");

                                    String Stat = "0";

                                    for (int i=0;i<UploadInfoArray.length();i++){
                                        JSONObject a = UploadInfoArray.getJSONObject(i);
                                        Stat = a.getString("status");
                                    }

                                    if(Stat.equals("1")){
                                        db.updateFlagOrder();
                                        db.updateFlagInventory();
                                        db.updateFlagRetur();
                                        dbset.UpdateSettingTglLogin();
                                        Toast.makeText(ActivitySync.this, "Unggah Data Zip Berhasil.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(ActivitySync.this, "Unggah Data Zip Gagal 3 : Zip."+" - "+UploadInfoArray.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(ActivitySync.this, "Unggah Data Gagal 2 : Zip."+" - "+UploadInfoArray.toString(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(ActivitySync.this, "Unggah Data Gagal. 1 : Zip", Toast.LENGTH_SHORT).show();
                                Log.e("123",e.getMessage());
                            }

                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ActivitySync.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ActivitySync.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "Exception : "  + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }


    public void ShowKonfirmasiProsesZip(final String command){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Proses");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        if (command.equals("0")){
            TxtLblNama.setText("Apakah anda yakin ingin mengunduh master yang baru?");
        }else if(command.equals("1")){
            TxtLblNama.setText("Apakah anda yakin ingin mengunggah?");
        }else{
            TxtLblNama.setText("Apakah anda yakin memproses manual?");
        }
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);


        layout.addView(TxtLblNama,params);


        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoge, int which) {
                dialoge.dismiss();
                if(command.equals("0")){
                    new UpdateMaster(ActivitySync.this).execute();
                }else if (command.equals("1")){
                    // New
                    generateCSV(getPref(TAG_LASTLOGIN));
                    generateCSVInv(getPref(TAG_LASTLOGIN));
                    generateCSVRetur(getPref(TAG_LASTLOGIN));
                    generateCSVKunjungan(getPref(TAG_LASTLOGIN));
                    FilePathInv = DB_PATH_CSV_SUCCESS+"/"+filenameinv;
                    FilePathRetur = DB_PATH_CSV_SUCCESS+"/"+filenameretur;
                    FilePathKunjungan = DB_PATH_CSV_SUCCESS+"/"+filenamekunjungan;
                    FilePath = DB_PATH_CSV_SUCCESS+"/"+filename;
                    FilePathZip = DB_PATH_CSV_SUCCESS+"/"+"ORDER_"+getPref(TAG_LASTLOGIN)+".zip";

                    String[] s = new String[4];
                    s[0] = FilePath;
                    s[1] = FilePathInv;
                    s[2] = FilePathRetur;
                    s[3] = FilePathKunjungan;

                    packZip(s,FilePathZip);
                    dialog = ProgressDialog.show(ActivitySync.this, "", "Uploading file Zip...", true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadFileZip(FilePathZip,"ORDER_"+getPref(TAG_LASTLOGIN)+".zip",filename,filenameinv,filenameretur,filenamekunjungan);
                        }
                    }).start();

                }else{
                    generateCSVManual(getPref(TAG_LASTLOGIN));
                    renameMasterManual(filename);

                    generateCSVInv(getPref(TAG_LASTLOGIN));
                    renameMasterInvManual(filenameinv);

                    generateCSVRetur(getPref(TAG_LASTLOGIN));
                    renameMasterReturManual(filenameretur);

                    generateCSVKunjungan(getPref(TAG_LASTLOGIN));
                    renameMasterKunjunganManual(filenamekunjungan);


                    subject = "SFA ORDER MANUAL";
                    message = "SFA ORDER MANUAL - "+getPref(TAG_LASTLOGIN) + " - " + getPref(TAG_NAMELOGIN).replaceAll("[^a-zA-Z]+", "_") +" - " + getToday();
                    sendMail(email, subject, message);
                }
            }
        });

        // Set up the buttons
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialoge, int which) {
                dialoge.dismiss();
            }
        });

        builder.show();
    }
}

