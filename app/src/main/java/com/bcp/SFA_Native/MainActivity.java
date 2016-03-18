package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;

import com.andexert.library.RippleView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.Process;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends Activity {
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
    private final String TAG_TGLLOGIN = "tgllogin";
    private final String TAG_SETTINGSTATUS="SETSTATUS";
    private final String TAG_DEVICEID = "DeviceID";

    JSONArray LoginArray = null,GenerateArray = null;

    EditText InputUsername,InputPassword;
    CheckBox CheckUpdate;
    Button BtnSubmit;
    TextView TxtVersion,TxtUmpan,TxtUmpan2;
    ImageView ImgLogo,ImgLogoUser,ImgLogoPassword;
    int status,hitupdate;

    private String UserInput="";
    private String Web,AppVersion,DBVersion,LastLogin,NameLogin,ModeApp,Cabang,MinOrder,Email,TglLogin="",VersionAPK="",DeviceID="";

    boolean animate = true;

    Intent ServiceInt,ServiceIntFused;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_main);
        animate = true;

        hitupdate = 0;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        try {
            int isAutoDate = 0;
            int isAutoZonaTime = 0;
            int isAirPlaneMode = 0;
            isAutoDate = Settings.System.getInt(getContentResolver(),Settings.Global.AUTO_TIME);
            isAutoZonaTime = Settings.System.getInt(getContentResolver(),Settings.Global.AUTO_TIME_ZONE);
            isAirPlaneMode = Settings.System.getInt(getContentResolver(),Settings.Global.AIRPLANE_MODE_ON);
            if ((isAutoDate == 0)||(isAutoZonaTime == 0)||(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||(checkMockLocation()))||(isAirPlaneMode == 1)||(!isMobileDataEnabled())){
                String Msg = "Tanggal di perangkat tidak tersetting automatic, silahkan centang auto datetime time di setting?";
                if (isAutoDate==0){
                    Msg = "Tanggal di perangkat tidak tersetting automatic, silahkan centang auto datetime time di setting?";
                }else if (isAutoZonaTime==0){
                    Msg = "Zona waktu di perangkat tidak tersetting automatic, silahkan centang auto zona waktu di setting?";
                }else if(checkMockLocation()){
                    Msg = "Setting Mock Location aktif, silahkan non aktifkan dahulu!!!";
                }else if(isAirPlaneMode == 1){
                    Msg = "Setting Air plane mode aktif, silahkan non aktifkan dahulu!!!";
                }else if(!isMobileDataEnabled()){
                    Msg = "Setting mobile data tidak aktif, silahkan aktifkan dahulu!!!";
                }else{
                    Msg = "GPS tidak aktif, silahkan aktifkan dahulu di setting?";
                }
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Information")
                        .setMessage(Msg)
                        .setPositiveButton("Setelan", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                MainActivity.this.finish();
                            }
                        })
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (getPref(TAG_DEVICEID).length()>3){
            DeviceID = getPref(TAG_DEVICEID);
        }

        // 0(Stable Version n Update Konsep).1(Mayor Update Table/Data).0(Minor Update)
        VersionAPK = "0.2.6";
        TxtVersion = (TextView) findViewById(R.id.MainMenu_TxtVersion);
        TxtVersion.setText("Versi Aplikasi : " + VersionAPK);
        TxtVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hitupdate == 5) {
                    hitupdate = 0;
                    DialodCekUpdate();
                } else {
                    hitupdate++;
                }
            }
        });

        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3399FF")));

        ServiceInt  = new Intent(this, ServiceLocation.class);
        startService(ServiceInt);

        ServiceIntFused = new Intent(this, ServiceLocationGFused.class);
        startService(ServiceIntFused);

        InputUsername = (EditText) findViewById(R.id.Login_InputUsername);
        InputUsername.setVisibility(View.INVISIBLE);
        InputPassword = (EditText) findViewById(R.id.Login_InputPassword);
        InputPassword.setVisibility(View.INVISIBLE);
        CheckUpdate = (CheckBox) findViewById(R.id.MainMenu_CBUpdate);
        CheckUpdate.setVisibility(View.INVISIBLE);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        BtnSubmit = (Button) findViewById(R.id.Login_BtnSubmit);
        BtnSubmit.setVisibility(View.INVISIBLE);

        final RippleView rippleView = (RippleView) findViewById(R.id.RpViewBtn);

        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        if((InputUsername.getText().toString().equals(InputPassword.getText().toString()))&&((InputUsername.getText().length())>0)){
                            UserInput=InputUsername.getText().toString();
                            if (TglLogin.length()<=0){
                                CheckUpdate.setChecked(true);
                            }
                            if(CheckUpdate.isChecked()){
                                new CekLogin(MainActivity.this).execute();
                            }else{
                                if (getDateDiff(getToday2(),TglLogin.replaceAll("-",""),TimeUnit.DAYS)<0){
                                    Toast.makeText(MainActivity.this, "Tgl di Device anda tidak uptodate, Silahkan cek pengaturan tgl anda!!!", Toast.LENGTH_SHORT).show();
                                }else if(getDateDiff(getToday2(),TglLogin.replaceAll("-",""),TimeUnit.DAYS)>6){
                                    Toast.makeText(MainActivity.this, "Silahkan centang update master untuk memperbarui data!!!", Toast.LENGTH_SHORT).show();
                                }else {
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
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Username/Password Salah!!",Toast.LENGTH_SHORT).show();
                        }
                    }

                });
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
                TglLogin = MenuJSON.getString(TAG_TGLLOGIN);
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

        setPref(Web, AppVersion, DBVersion, Email);

        ImgLogo = (ImageView) findViewById(R.id.Login_Icon);
        ImgLogoUser = (ImageView) findViewById(R.id.Login_IconUser);
        ImgLogoPassword = (ImageView) findViewById(R.id.Login_IconPassword);


        TxtUmpan = (TextView) findViewById(R.id.LoginUmpan);
        TxtUmpan.setVisibility(View.GONE);
        TxtUmpan2 = (TextView) findViewById(R.id.LoginUmpan2);
        TxtUmpan2.setVisibility(View.GONE);
    }

    public boolean checkMockLocation(){
        if (Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if((hasFocus)&&(animate)){
            InputUsername.setVisibility(View.INVISIBLE);
            InputPassword.setVisibility(View.INVISIBLE);
            ImgLogoUser.setVisibility(View.INVISIBLE);
            ImgLogoPassword.setVisibility(View.INVISIBLE);
            CheckUpdate.setVisibility(View.INVISIBLE);
            BtnSubmit.setVisibility(View.INVISIBLE);
            TxtVersion.setVisibility(View.INVISIBLE);
            YoYo.with(Techniques.BounceIn).duration(1200).playOn(ImgLogo);
            YoYo.with(Techniques.FadeOut).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    YoYo.with(Techniques.SlideInRight).duration(800).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            InputUsername.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ImgLogoUser.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.ZoomIn).duration(300).playOn(ImgLogoUser);
                            InputUsername.requestFocus();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(InputUsername);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).duration(100).playOn(TxtUmpan);
            YoYo.with(Techniques.FadeOut).withListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    YoYo.with(Techniques.SlideInRight).duration(800).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            InputPassword.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ImgLogoPassword.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.ZoomIn).duration(300).playOn(ImgLogoPassword);
                            YoYo.with(Techniques.BounceIn).duration(400).withListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    CheckUpdate.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }).playOn(CheckUpdate);
                            YoYo.with(Techniques.ZoomIn).withListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    BtnSubmit.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    YoYo.with(Techniques.FadeInUp).withListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                            TxtVersion.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            animate = false;
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    }).duration(300).playOn(TxtVersion);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }).duration(400).playOn(BtnSubmit);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(InputPassword);

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).duration(300).playOn(TxtUmpan2);
        }
    }

    public static long getDateDiff(String Date1, String Date2, TimeUnit timeUnit) {
        DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        Date date1 = null;
        Date date2 = null;
        long a =0;
        try {
            date1 = format.parse(Date1);
            date2 = format.parse(Date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diffInMillies = date1.getTime() - date2.getTime();
        a = timeUnit.convert(diffInMillies,TimeUnit.DAYS)/86400000;
        return a;
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
            default:break;
        }

        return true;
    }

    public void DialodSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Keamanan");
        builder.setIcon(R.drawable.dfa_info_ups);

        final EditText input = new EditText(this);
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

    public void DialodCekUpdate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Cek Update APK");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 5, 30, 0);

        final TextView labelVersi = new TextView(this);
        labelVersi.setText("Versi APK saat ini : "+VersionAPK);

        layout.addView(labelVersi, params);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Cek Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new CekUpdateAPK(MainActivity.this).execute();
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void DialodKonfirmasiUpdate(final String VersiTerbaru, final String Link){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Update APK");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 5, 30, 0);

        final TextView labelQ = new TextView(this);
        labelQ.setText("Apakah anda yakin akan memperbaharui aplikasi SFA?");

        final TextView labelVersi = new TextView(this);
        labelVersi.setText("Versi APK saat ini : " + VersionAPK);

        final TextView labelVersiNew = new TextView(this);
        labelVersiNew.setText("Versi APK Update : "+VersiTerbaru);

        layout.addView(labelQ, params);
        layout.addView(labelVersi, params);
        layout.addView(labelVersiNew,params);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Silahkan install APK setelah proses download selesai", Toast.LENGTH_LONG).show();
                //String url = "http://lucia.borwita.co.id:9020/SFA/master/"+Link;
                String url = "http://"+Web+"/master/"+Link;
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setDescription("Download aplikasi SFA versi : "+VersiTerbaru);
                request.setTitle("Proses Update");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Link);
                
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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

    public String getToday2(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
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
                            stopService(ServiceInt);
                            stopService(ServiceIntFused);
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
            String url = "http://"+Web+"/pengaturan/ceklogin.php?username="+UserInput+"&cabang="+Cabang+"&codename=1988"+"&uid="+DeviceID;
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
        String minOrder="0";

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
            String url = "http://"+Web+"/pengaturan/generatemaster_dev.php?username="+UserInput+"&cabang="+Cabang+"&codename=1988";
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
                        minOrder = d.getString("minorder");
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

                                    FileOutputStream fos = new FileOutputStream( file);
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
                                            db.updateMinOrder(minOrder);
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
                Intent in = new Intent(getApplicationContext(),ActivityMainMenu.class);
                in.putExtra(TAG_WEB,Web);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }else{
                Toast.makeText(getApplicationContext(),"Login Gagal : "+StatusGenerate,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean deleteOldMasterZip(){
        File file = new File(DB_PATH,"MASTER_"+getPref(TAG_LASTLOGIN)+".zip");
        if(file.exists()){
            boolean deleted = file.delete();
            return deleted;
        }
        return true;
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

    //Unzip File
    private boolean unpackZip(String path, String zipname)
    {
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

    public class CekUpdateAPK extends AsyncTask<Void, Integer, Void> {

        Context context;
        Handler handler;
        Dialog dialog;
        TextView txtLoadingProgress;
        int showDialog=0;
        String versi="0",link=" ";

        CekUpdateAPK(Context context, Handler handler){
            this.context=context;
            this.handler=handler;
        }

        CekUpdateAPK(Context context){
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
            txtLoadingProgress.setText("Cek Update APK. . .");

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
            String url = "http://"+Web+"/pengaturan/versionnative.php?valid=1988";
            FN_JSONParser jParser = new FN_JSONParser();

            try {
                JSONObject json = jParser.getJSONFromUrl(url);
                StatusRequest = json.getString("STATUS");
                if(StatusRequest.equals("1")){
                    versi = json.getString("versi");
                    link = json.getString("link");
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
                    txtLoadingProgress.setText("Get Info");
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
                if (versi.equals(VersionAPK)){
                    Toast.makeText(MainActivity.this, "SFA sudah update ke versi terakhir", Toast.LENGTH_SHORT).show();
                }else{
                    DialodKonfirmasiUpdate(versi,link);
                }
            }else{
                Toast.makeText(getApplicationContext(),"Cek Update Gagal",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Boolean isMobileDataEnabled(){
        Object connectivityService = getSystemService(CONNECTIVITY_SERVICE);
        ConnectivityManager cm = (ConnectivityManager) connectivityService;

        Boolean isActive = false;

        try {
            Class<?> c = Class.forName(cm.getClass().getName());
            Method m = c.getDeclaredMethod("getMobileDataEnabled");
            m.setAccessible(true);
            isActive = (Boolean)m.invoke(cm);

            if (!isActive){
                NetworkInfo a = cm.getActiveNetworkInfo();

                if (a != null){
                    isActive = a.isConnected();
                }
            }

            return isActive;
        } catch (Exception e) {
            e.printStackTrace();
            return isActive;
        }
    }
}
