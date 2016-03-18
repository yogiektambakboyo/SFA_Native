package com.bcp.SFA_Native;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ActivityQPPelanggan extends Activity {
    private FN_DBHandler dborder;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String      DB_MASTER="MASTER";
    private String DB_ORDER="ORDER_";
    private final String TAG_SHIPTO = "kode";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_ALAMAT = "alamat";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LASTLOGIN = "lastlogin";

    ListView list;
    EditText TxtCari;
    AdapterPelangganQPListView adapter;

    String[] ShipTo,Perusahaan,Alamat;

    ArrayList<Data_Pelanggan> PelangganList = new ArrayList<Data_Pelanggan>();

    JSONArray PelangganArray;
    private final String TAG_PELANGGANDATA= "PelangganData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_qppelanggan);

        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        dborder = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_ORDER+getPref(TAG_LASTLOGIN));

        JSONObject PelangganJSON = null;

        if(dbFile.exists()){
            try {
                PelangganJSON = dborder.getPelangganQP();
                // Getting Array of Pelanggan
                PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

                ShipTo = new String[PelangganArray.length()];
                Perusahaan = new String[PelangganArray.length()];
                Alamat = new String[PelangganArray.length()];

                // looping through All Pelanggan
                for(int i = 0; i < PelangganArray.length(); i++){
                    JSONObject c = PelangganArray.getJSONObject(i);
                    String ShipToS = c.getString(TAG_SHIPTO);
                    String PerusahaanS = c.getString(TAG_PERUSAHAAN);
                    String AlamatS= c.getString(TAG_ALAMAT);

                    ShipTo[i] = ShipToS;
                    Perusahaan[i] = PerusahaanS;
                    Alamat[i] = AlamatS;

                }

                for (int i = 0; i < ShipTo.length; i++)
                {
                    Data_Pelanggan plg = new Data_Pelanggan(ShipTo[i], Perusahaan[i], Alamat[i],"");
                    PelangganList.add(plg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            dborder.close();
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }


        list = (ListView) findViewById(R.id.QPPelanggan_ListView);

        // Pass results to ListViewAdapter Class
        adapter = new AdapterPelangganQPListView(getApplicationContext(), PelangganList);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent in = new Intent(ActivityQPPelanggan.this,ActivityQPPembayaran.class);
                in.putExtra(TAG_SHIPTO,adapter.getItem(position).getShipTo());
                in.putExtra(TAG_PERUSAHAAN,adapter.getItem(position).getPerusahaan());
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
            }
        });

        TxtCari = (EditText) findViewById(R.id.QPPelanggan_Search);

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
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }
}
