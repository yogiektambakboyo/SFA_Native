package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityPelanggan extends Activity {
    //DB Handler
    private FN_DBHandler db,dborder;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String      DB_MASTER="MASTER";
    private String DB_ORDER="ORDER_";
    private final String TAG_SHIPTO = "kode";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_MODEORDER= "modeorder";
    private final String TAG_ALAMAT = "alamat";
    private final String TAG_HARI = "hari";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_WEB = "web";
    private String Web="";

    // Declare Variables
    ListView list;
    AdapterPelangganListView adapter;
    EditText TxtCari;
    Spinner SpnHari;
    String[] ArrHari;
    ImageView ImgFilter;

    String[] ShipTo,Perusahaan,Alamat,Hari;
    ArrayList<Data_Pelanggan> PelangganList = new ArrayList<Data_Pelanggan>();

    JSONArray PelangganArray;
    private final String TAG_PELANGGANDATA= "PelangganData";

    List AreaList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_pelanggan);

        AreaList = new ArrayList();

        Intent in = getIntent();
        Web = in.getStringExtra(TAG_WEB);

        ImgFilter = (ImageView) findViewById(R.id.Pelanggan_ImgFilter);
        ImgFilter.setVisibility(View.GONE);

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        dborder = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_ORDER+getPref(TAG_LASTLOGIN));

        JSONObject PelangganJSON = null;

        if(dbFile.exists()){
            try {
                PelangganJSON = db.getPelanggan();
                // Getting Array of Pelanggan
                PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

                ShipTo = new String[PelangganArray.length()];
                Perusahaan = new String[PelangganArray.length()];
                Alamat = new String[PelangganArray.length()];
                Hari = new String[PelangganArray.length()];

                // looping through All Pelanggan
                for(int i = 0; i < PelangganArray.length(); i++){
                    JSONObject c = PelangganArray.getJSONObject(i);

                    //status = c.getInt(TAG_STATUS);
                    String ShipToS = c.getString(TAG_SHIPTO);
                    String PerusahaanS = c.getString(TAG_PERUSAHAAN);
                    String AlamatS= c.getString(TAG_ALAMAT);
                    String HariS = c.getString(TAG_HARI);

                    ShipTo[i] = ShipToS;
                    Perusahaan[i] = PerusahaanS;
                    Alamat[i] = AlamatS;
                    Hari[i] = HariS;

                }

                for (int i = 0; i < ShipTo.length; i++)
                {
                    Data_Pelanggan plg = new Data_Pelanggan(ShipTo[i], Perusahaan[i], Alamat[i],Hari[i]);
                    PelangganList.add(plg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            db.close();
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }


        list = (ListView) findViewById(R.id.PelangganListView);

        // Pass results to ListViewAdapter Class
        adapter = new AdapterPelangganListView(getApplicationContext(), PelangganList,0);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final int pos = position;
                int d = dborder.getCekExistOrderPelanggan(adapter.getItem(position).getShipTo(),getToday());
                if (d>0){
                    CekExistOrder(position,adapter.getItem(position).getPerusahaan());
                }else{
                    Intent ints = new Intent(getApplicationContext(), ActivityOrder.class);
                    ints.putExtra(TAG_SHIPTO, adapter.getItem(position).getShipTo());
                    ints.putExtra(TAG_PERUSAHAAN, adapter.getItem(position).getPerusahaan());
                    ints.putExtra(TAG_WEB,Web);
                    startActivity(ints);
                }
            }
        });

        TxtCari = (EditText) findViewById(R.id.Pelanggan_Search);

        // Capture Text in EditText
        TxtCari.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                adapter.filter(SpnHari.getSelectedItem().toString(),TxtCari.getText().toString().toLowerCase(Locale.getDefault()));
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

        ArrHari = new String[8];
        ArrHari[0] = "Semua";
        ArrHari[1] = "Minggu";
        ArrHari[2] = "Senin";
        ArrHari[3] = "Selasa";
        ArrHari[4] = "Rabu";
        ArrHari[5] = "Kamis";
        ArrHari[6] = "Jumat";
        ArrHari[7] = "Sabtu";

        ArrayAdapter adapters = new ArrayAdapter(this,android.R.layout.simple_spinner_item, ArrHari);
        adapters.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnHari = (Spinner) findViewById(R.id.SpnPelangganDay);
        SpnHari.setAdapter(adapters);


        Calendar cald = Calendar.getInstance();
        int day = cald.get(Calendar.DAY_OF_WEEK);

        SpnHari.setSelection(day);

        SpnHari.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.filter(ArrHari[position],"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public void CekExistOrder(final int Pos,final String Perusahaan){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView Msg = new TextView(this);
        Msg.setText("Pelanggan "+Perusahaan+" hari ini sudah order, apakah pelanggan ini akan order lagi?");
        Msg.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

        layout.addView(Msg,params);
        builder.setView(layout);

        // Set up the buttons
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent ints = new Intent(getApplicationContext(), ActivityOrder.class);
                ints.putExtra(TAG_SHIPTO, adapter.getItem(Pos).getShipTo());
                ints.putExtra("kodeorder", "");
                ints.putExtra(TAG_PERUSAHAAN, adapter.getItem(Pos).getPerusahaan());
                ints.putExtra(TAG_MODEORDER, 0);
                startActivity(ints);
            }
        });

        builder.show();
    }

}