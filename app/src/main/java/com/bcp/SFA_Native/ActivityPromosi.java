package com.bcp.SFA_Native;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ActivityPromosi extends Activity {
    //DB Handler
    private FN_DBHandler db,dborder;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String      DB_MASTER="MASTER";
    private String DB_ORDER="ORDER_";
    private final String TAG_NOPROGRAM = "noprogram";
    private final String TAG_CAPTION = "caption";
    private final String TAG_KETERANGAN = "keterangan";
    private final String TAG_SEGMENT = "segment";
    private final String TAG_TGLMULAI = "tglmulai";
    private final String TAG_TGLAKHIR = "tglakhir";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LASTLOGIN = "lastlogin";

    // Declare Variables
    ListView list;
    AdapterPromosi adapter;
    EditText TxtCari;

    String[] NoProgram,Caption,Keterangan,Segment,TglMulai,TglAkhir;
    ArrayList<Data_Promosi> PromosiList = new ArrayList<Data_Promosi>();

    JSONArray PromosiArray;
    private final String TAG_PELANGGANDATA= "PelangganData";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_promosi);


        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        dborder = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_ORDER+getPref(TAG_LASTLOGIN));

        JSONObject PromosiJSON = null;

        if(dbFile.exists()){
            try {
                PromosiJSON = db.getPromosi(getPref(TAG_LASTLOGIN).substring(2,4));
                // Getting Array of Pelanggan
                PromosiArray = PromosiJSON.getJSONArray(TAG_PELANGGANDATA);

                NoProgram = new String[PromosiArray.length()];
                Caption = new String[PromosiArray.length()];
                Keterangan = new String[PromosiArray.length()];
                Segment = new String[PromosiArray.length()];
                TglMulai = new String[PromosiArray.length()];
                TglAkhir = new String[PromosiArray.length()];

                // looping through All Pelanggan
                for(int i = 0; i < PromosiArray.length(); i++){
                    JSONObject c = PromosiArray.getJSONObject(i);

                    //status = c.getInt(TAG_STATUS);
                    String NoProgramS = c.getString(TAG_NOPROGRAM);
                    String CaptionS = c.getString(TAG_CAPTION);
                    String KeteranganS= c.getString(TAG_KETERANGAN);
                    String SegmentS= c.getString(TAG_SEGMENT);
                    String TglMulaiS= c.getString(TAG_TGLMULAI);
                    String TglAkhirS= c.getString(TAG_TGLAKHIR);

                    NoProgram[i] = NoProgramS;
                    Caption[i] = CaptionS;
                    Keterangan[i] = KeteranganS;
                    Segment[i] = SegmentS;
                    TglMulai[i] = TglMulaiS;
                    TglAkhir[i] = TglAkhirS;

                }

                for (int i = 0; i < NoProgram.length; i++)
                {
                    Data_Promosi plg = new Data_Promosi(NoProgram[i], Caption[i], Keterangan[i],Segment[i],TglMulai[i],TglAkhir[i]);
                    PromosiList.add(plg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            db.close();
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }


        list = (ListView) findViewById(R.id.PromosiListView);

        // Pass results to ListViewAdapter Class
        adapter = new AdapterPromosi(getApplicationContext(), PromosiList);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(ActivityPromosi.this, adapter.getItem(position).getKeterangan(), Toast.LENGTH_SHORT).show();
            }
        });

        TxtCari = (EditText) findViewById(R.id.Pelanggan_Search);

        // Capture Text in EditText
        TxtCari.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                adapter.filter(TxtCari.getText().toString().toLowerCase(Locale.getDefault()));
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY, "0");
        return  Value;
    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

}