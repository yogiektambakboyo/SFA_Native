package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.*;
import com.bcp.SFA_Native.FN_DBHandler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;


public class ActivitySetting extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String      DB_SETTING="SETTING";
    private final String TAG_SETTINGSTATUS="SETSTATUS";
    private final String TAG_STATUS = "status";
    private final String TAG_WEB = "web";
    private final String TAG_CABANG = "cabang";
    private final String TAG_APPVERSION = "appversion";
    private final String TAG_DBVERSION = "dbversion";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_TGLLOGIN = "tgllogin";
    private final String TAG_EMAIL = "email";
    private final String TAG_MODE = "mode";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_DEVICEID = "DeviceID";

    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";

    private EditText InputWebServer,InputEmail;
    private ImageView ImgWebServer,ImgCabang;
    private Spinner SpnCabang;
    private Button BtnSubmit;
    private TextView TxtDeviceScreen,TxtLocation,TxtSecurityCode;
    private ToggleButton TBMode;

    int status;
    private String Web,AppVersion="1",DBVersion="1",LastLogin,NameLogin,TglLogin,ModeApp="",Cabang ="0",Email="";
    private String Screen="",longitude="",latitude="";

    String[] CabangArray;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_setting);

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_SETTING);


        Intent in = getIntent();


        if(in.getStringExtra(TAG_SETTINGSTATUS).equals("1")){
            db.InsertSetting("",1,1,"multi","01");
        }else{
            JSONObject SettingJSON = null;
            try{
                SettingJSON = db.GetSetting();
                status = SettingJSON.getInt(TAG_STATUS);
                Web = SettingJSON.getString(TAG_WEB);
                AppVersion = SettingJSON.getString(TAG_APPVERSION);
                DBVersion = SettingJSON.getString(TAG_DBVERSION);
                LastLogin = SettingJSON.getString(TAG_LASTLOGIN);
                NameLogin = SettingJSON.getString(TAG_NAMELOGIN);
                ModeApp = SettingJSON.getString(TAG_MODE);
                Cabang = SettingJSON.getString(TAG_CABANG);
                Email = SettingJSON.getString(TAG_EMAIL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Close DB Connection
        db.close();

        InputWebServer = (EditText) findViewById(R.id.Setting_InputWeb);

        InputWebServer.setText(Web);

        ImgWebServer = (ImageView) findViewById(R.id.Setting_InputWebMsg);

        ImgWebServer.setVisibility(View.GONE);

        TBMode = (ToggleButton) findViewById(R.id.SettingTBMode);
        if(ModeApp.equals("multi")){
            TBMode.setChecked(true);
        }else{
            TBMode.setChecked(false);
        }
        TBMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    ModeApp = "multi";
                }else{
                    ModeApp = "single";
                }
            }
        });

        InputEmail = (EditText) findViewById(R.id.Setting_InputEmail);
        InputEmail.setText(Email);

        BtnSubmit = (Button) findViewById(R.id.Setting_BtnSubmit);
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(InputWebServer.getText().toString().length()==0){
                    Toast.makeText(getApplicationContext(),"Web Server harus Diisi!",Toast.LENGTH_SHORT).show();
                    ImgWebServer.setVisibility(View.VISIBLE);
                }else if(SpnCabang.getSelectedItemPosition()==0){
                    Toast.makeText(getApplicationContext(),"Pilih Cabang dahulu",Toast.LENGTH_SHORT).show();
                    ImgCabang.setVisibility(View.VISIBLE);
                }else{
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    ImgWebServer.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),"Pengaturan Tersimpan",Toast.LENGTH_SHORT).show();
                                    db.UpdateSettingFull(InputWebServer.getText().toString(),ModeApp,Cabang,InputEmail.getText().toString());
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySetting.this);
                    builder.setTitle("Konfirmasi");
                    builder.setIcon(R.drawable.dfa_info_ups);
                    builder.setMessage("Simpan Pengaturan?").setPositiveButton("Ya", dialogClickListener)
                            .setNegativeButton("Tidak", dialogClickListener).show();
                }
            }
        });


        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                Screen = "Large";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                Screen = "Normal";
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                Screen = "Small";
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                Screen = "X-Large";
                break;
            default:
                Screen = "Undefined Screen";
        }

        TxtDeviceScreen = (TextView) findViewById(R.id.Setting_TxtDeviceScreen);
        TxtDeviceScreen.setText(" "+width+" x "+height+" ("+Screen+")");

        longitude = getPref(TAG_LONGITUDE);
        latitude = getPref(TAG_LATITUDE);

        TxtLocation = (TextView) findViewById(R.id.Setting_TxtLocation);
        TxtLocation.setText(" "+latitude+", "+longitude);


        TxtSecurityCode = (TextView) findViewById(R.id.Setting_TxtSecureCode);
        if (getPref(TAG_DEVICEID).length()<3){
            setPrefDeviceID(getDeviceID());
        }

        TxtSecurityCode.setText(getPref(TAG_DEVICEID));

        SpnCabang = (Spinner) findViewById(R.id.Setting_SpinnerCabang);
        CabangArray = new String[27];
        CabangArray[0]=("-- Pilih Cabang --");
        CabangArray[1]=("Surabaya");
        CabangArray[2]=("Malang");
        CabangArray[3]=("Jember");
        CabangArray[4]=("Kediri");
        CabangArray[5]=("Denpasar");
        CabangArray[6]=("Manado");
        CabangArray[7]=("Makassar Food");
        CabangArray[8]=("Pare-Pare");
        CabangArray[9]=("Palu Food");
        CabangArray[10]=("Palopo");
        CabangArray[11]=("Madura");
        CabangArray[12]=("Gorontalo");
        CabangArray[13]=("Kendari");
        CabangArray[14]=("Lombok");
        CabangArray[15]=("Latubo");
        CabangArray[16]=("USS");
        CabangArray[17]=("Luwuk");
        CabangArray[18]=("Madiun");
        CabangArray[19]=("Kupang");
        CabangArray[20]=("Mamuju");
        CabangArray[21]=("Sumbawa");
        CabangArray[22]=("Toli-Toli");
        CabangArray[23]=("Bima");
        CabangArray[24]=("Atambua");
        CabangArray[25]=("Puncak Jaya");
        CabangArray[26]=("Maumere");

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, CabangArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpnCabang.setAdapter(adapter);

        SpnCabang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < 9) {
                    Cabang = "0" + Integer.toString(position);
                } else {
                    Cabang = Integer.toString(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SpnCabang.setSelection(Integer.parseInt(Cabang));

        ImgWebServer = (ImageView) findViewById(R.id.Setting_InputWebMsg);
        ImgCabang = (ImageView) findViewById(R.id.Setting_InputCabangMsg);

        ImgWebServer.setVisibility(View.GONE);
        ImgCabang.setVisibility(View.GONE);

    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public String getDeviceID(){
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, androidId;
        tmDevice = "" + tm.getDeviceId();
        //tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), (long)tmDevice.hashCode() << 32);
        String deviceId = deviceUuid.toString();
        return  deviceId;
    }

    public void setPrefDeviceID(String DeviceID){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor SettingPrefEditor = SettingPref.edit();
        SettingPrefEditor.putString(TAG_DEVICEID,DeviceID);
        SettingPrefEditor.commit();
    }
}