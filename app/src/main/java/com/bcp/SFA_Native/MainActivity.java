package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.*;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private FN_DBHandler db,dbmst;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String      DB_SETTING="SETTING";
    private String      DB_MASTER="MASTER";
    private String      TAG_PREF="SETTINGPREF";
    private String StatusRequest="0";
    private final String TAG_STATUS = "status";
    private final String TAG_WEB = "web";
    private final String TAG_APPVERSION = "appversion";
    private final String TAG_DBVERSION = "dbversion";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_EMAIL = "email";
    private final String TAG_MINORDER = "minorder";
    private final String TAG_MODEAPP = "mode";
    private final String TAG_CABANG = "cabang";
    private final String TAG_LOGIN = "logindata";
    private final String TAG_NAMA = "nama";
    private final String TAG_SETTINGSTATUS="SETSTATUS";


    JSONArray LoginArray = null,GenerateArray = null;

    //Form
    EditText InputUsername,InputPassword;
    CheckBox CheckUpdate;
    Button BtnSubmit;
    int status;

    private String UserInput="";
    private String Web,AppVersion,DBVersion,LastLogin,NameLogin,ModeApp,Cabang,MinOrder,Email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_main);


        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3399FF")));

        Intent ServiceInt  = new Intent(this, ServiceLocation.class);
        startService(ServiceInt);

        InputUsername = (EditText) findViewById(R.id.Login_InputUsername);
        InputPassword = (EditText) findViewById(R.id.Login_InputPassword);
        CheckUpdate = (CheckBox) findViewById(R.id.MainMenu_CBUpdate);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        BtnSubmit = (Button) findViewById(R.id.Login_BtnSubmit);

        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((InputUsername.getText().toString().equals(InputPassword.getText().toString()))&&((InputUsername.getText().length())>0)){
                    UserInput=InputUsername.getText().toString();
                    if(CheckUpdate.isChecked()){
                        new CekLogin(MainActivity.this).execute();
                    }else{
                        if(InputUsername.getText().toString().equals(LastLogin)){
                            setPrefLogin(InputUsername.getText().toString(),NameLogin,MinOrder);
                            Intent in = new Intent(getApplicationContext(), ActivityMainMenu.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            in.putExtra(TAG_WEB,Web);
                            startActivity(in);
                        }else{
                            Toast.makeText(getApplicationContext(),"Username/Password Salah!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Username/Password Salah!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Get Data From DB SETTING
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_SETTING);
        File dbFile = new File(DB_PATH+"/"+DB_SETTING);

        // Get Data From DB MASTER
        dbmst = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFileMaster = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject MenuJSON = null;

        if(dbFile.exists()){
            try {
                if(db.cekColumnExistPengaturan("Pengaturan","email").equals("0")){
                    db.addColumnPengaturan("Pengaturan","email","TEXT");
                }
                MenuJSON = db.GetSetting();
                status = MenuJSON.getInt(TAG_STATUS);
                Web = MenuJSON.getString(TAG_WEB);
                AppVersion = MenuJSON.getString(TAG_APPVERSION);
                DBVersion = MenuJSON.getString(TAG_DBVERSION);
                LastLogin = MenuJSON.getString(TAG_LASTLOGIN);
                NameLogin = MenuJSON.getString(TAG_NAMELOGIN);
                ModeApp = MenuJSON.getString(TAG_MODEAPP);
                Cabang = MenuJSON.getString(TAG_CABANG);
                MinOrder = MenuJSON.getString(TAG_MINORDER);
                Email = MenuJSON.getString(TAG_EMAIL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(!dbFileMaster.exists()){
                dbmst.CreateMaster();
            }
        }else{
            File SFADir = new File(DB_PATH);
            SFADir.mkdirs();
            db.CreateSetting();
            Intent in = new Intent(getApplicationContext(), ActivitySetting.class);
            in.putExtra(TAG_SETTINGSTATUS,"1");
            startActivity(in);
        }
        db.close();
        dbmst.close();

        setPref(Web,AppVersion,DBVersion,Email);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sfa_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                DialodSetting();
                break;
/*            case R.id.action_update:
                Intent in = new Intent(getApplicationContext(),ActivityMaps.class);
                startActivity(in);
                break;*/
            default:break;
        }

        return true;
    }

    public void DialodSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Keamanan");
        builder.setIcon(R.drawable.dfa_info_ups);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine();
        input.setHint("Masukkan Password");
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(11);
        input.setFilters(FilterArray);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().equals(getToday()+"SFA")){
                    //Toast.makeText(getApplicationContext(), "Password Benar!!!", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(getApplicationContext(),ActivitySetting.class);
                    in.putExtra(TAG_SETTINGSTATUS,"0");
                    startActivity(in);
                }else{
                    Toast.makeText(getApplicationContext(), "Password Salah!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public void setPref(String Web, String AppVersion, String DBVersion,String Email){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor SettingPrefEditor = SettingPref.edit();
        SettingPrefEditor.putString(TAG_CABANG,Cabang);
        SettingPrefEditor.putString(TAG_WEB,Web);
        SettingPrefEditor.putString(TAG_APPVERSION,AppVersion);
        SettingPrefEditor.putString(TAG_DBVERSION,DBVersion);
        SettingPrefEditor.putString(TAG_EMAIL,Email);
        SettingPrefEditor.commit();
    }

    public void setPrefLogin(String LastLogin,String NameLogin,String MinOrder){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor SettingPrefEditor = SettingPref.edit();
        SettingPrefEditor.putString(TAG_NAMELOGIN,NameLogin);
        SettingPrefEditor.putString(TAG_LASTLOGIN,LastLogin);
        SettingPrefEditor.putString(TAG_MINORDER,MinOrder);
        SettingPrefEditor.commit();
    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            MainActivity.this.finish();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Konfirmasi Keluar SFA").setMessage("Yakin ingin keluar?").setPositiveButton("Ya", dialogClickListener)
                    .setNegativeButton("Tidak", dialogClickListener).show();
        }
        return false;
    }

    public class CekLogin extends AsyncTask<Void, Integer, Void> {

        Context context;
        Handler handler;
        Dialog dialog;
        TextView txtLoadingProgress;
        int showDialog=0;

        CekLogin(Context context, Handler handler){
            this.context=context;
            this.handler=handler;
        }

        CekLogin(Context context){
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
            txtLoadingProgress.setText("Logging in . .");

            String path = "file:///android_asset/kotak.gif";
            WebView wV = (WebView) dialog.findViewById(R.id.webView);
            wV.loadUrl(path);
            wV.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
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
            String url = "http://"+Web+"/pengaturan/ceklogin.php?username="+UserInput+"&cabang=01&codename=1988";
            FN_JSONParser jParser = new FN_JSONParser();

            try {
                JSONObject json = jParser.getJSONFromUrl(url);
                StatusRequest = json.getString("STATUS");
                if(StatusRequest.equals("1")){
                    LoginArray = json.getJSONArray(TAG_LOGIN);
                    if(LoginArray.length()>0){
                        for (int i=0;i<LoginArray.length();i++){
                            JSONObject c = LoginArray.getJSONObject(i);
                            LastLogin = UserInput;
                            NameLogin = c.getString(TAG_NAMA);
                        }
                    }else{
                        LastLogin="0";
                    }
                }
                showDialog = 1;

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
                    txtLoadingProgress.setText("User Verified");
                    break;
                }
                default:break;
            }

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(StatusRequest.equals("1")){
                if (LastLogin.equals("0")){
                    Toast.makeText(getApplicationContext(),"Username/Password Salah!!",Toast.LENGTH_SHORT).show();
                }else{
                    setPrefLogin(LastLogin,NameLogin,MinOrder);
                    db.UpdateSetting(LastLogin,NameLogin);
                    new UpdateMaster(MainActivity.this).execute();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Login Gagal",Toast.LENGTH_SHORT).show();
            }
        }
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
            //wV.setScrollBarStyle(WebView.OVER_SCROLL_NEVER);
            wV.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
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
            String url = "http://"+Web+"/pengaturan/generatemaster.php?username="+UserInput+"&cabang="+Cabang+"&codename=1988";
            final FN_JSONParser jParser = new FN_JSONParser();
            String fileName = "MASTER_"+UserInput;
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
                                URL urls = new URL("http://"+Web+"/sqlite/"+fileName);
                                File file = new File(DB_PATH,fileName);
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

                                    FileOutputStream fos = new FileOutputStream( file);
                                    fos.write(baf.toByteArray());
                                    fos.flush();
                                    fos.close();

                                    if(cekExistMaster()){
                                        renameOldMaster();
                                    }

                                    if (renameNewMaster(fileName)){
                                        deleteOldMaster();
                                        StatusGenerate = "1";
                                        db.updateMinOrder(minOrder);
                                    }else{
                                        renameNewMaster("MASTER_TMP");
                                        StatusGenerate = "Gagal Rename File Master";
                                    }
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
                Intent in = new Intent(getApplicationContext(),ActivityMainMenu.class);
                in.putExtra(TAG_WEB,Web);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }else{
                Toast.makeText(getApplicationContext(),"Login Gagal : "+StatusGenerate,Toast.LENGTH_SHORT).show();
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

}
