package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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

public class ActivityListInv extends Activity {
    //DB Handler
    private FN_DBHandler dborder;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String      DB_MASTER="MASTER";
    private String DB_ORDER="ORDER_";
    private final String TAG_STATUS = "status";
    private final String TAG_KODE = "kode";
    private final String TAG_SHIPTO = "shipto";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_TOTAL = "total";

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_WEB = "web";
    private final String TAG_ORDER = "order";
    private String Web="",Order="1";

    // Declare Variables
    ListView list;
    AdapterPelangganListView adapter;
    EditText TxtCari;
    Spinner SpnHari;
    ImageView ImgFilter,ImgIcon;


    String[] ShipTo,Perusahaan,Kode,Total;
    ArrayList<Data_Pelanggan> PelangganList = new ArrayList<Data_Pelanggan>();

    JSONArray PelangganArray;
    private final String TAG_PELANGGANDATA= "PelangganData";
    private final String TAG_MODEORDER= "modeorder";

    List AreaList;
    Calendar myCalendar = Calendar.getInstance();
    int tahun,bulan,tgl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_pelanggan);

        AreaList = new ArrayList();

        Intent in = getIntent();
        Web = in.getStringExtra(TAG_WEB);
        Order = in.getStringExtra(TAG_ORDER);

        // Get Data Fron DB
        File dbFile = new File(DB_PATH+"/"+DB_ORDER+getPref(TAG_LASTLOGIN));

        dborder = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_ORDER+getPref(TAG_LASTLOGIN));

        JSONObject PelangganJSON = null;

        if(dbFile.exists()){
            try {
                if (Order.equals("1")){
                    PelangganJSON = dborder.getAllInvBeforeSync(DB_PATH, DB_MASTER);
                }else{
                    PelangganJSON = dborder.getAllInvAfterSync(DB_PATH, DB_MASTER, dborder.getDateTime("yyyy-MM-dd"));
                }
                // Getting Array of Pelanggan
                PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

                ShipTo = new String[PelangganArray.length()];
                Perusahaan = new String[PelangganArray.length()];
                Kode = new String[PelangganArray.length()];
                Total = new String[PelangganArray.length()];

                // looping through All Pelanggan
                for(int i = 0; i < PelangganArray.length(); i++){
                    JSONObject c = PelangganArray.getJSONObject(i);

                    //status = c.getInt(TAG_STATUS);
                    String ShipToS = c.getString(TAG_SHIPTO);
                    String PerusahaanS = c.getString(TAG_PERUSAHAAN);
                    String KodeS= c.getString(TAG_KODE);
                    String TotalS= c.getString(TAG_TOTAL);

                    ShipTo[i] = ShipToS;
                    Perusahaan[i] = PerusahaanS;
                    Kode[i] = KodeS;
                    Total[i] = TotalS;
                }

                for (int i = 0; i < ShipTo.length; i++)
                {
                    Data_Pelanggan plg = new Data_Pelanggan(ShipTo[i], Perusahaan[i], Kode[i],Total[i]);
                    PelangganList.add(plg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            dborder.close();
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }


        list = (ListView) findViewById(R.id.PelangganListView);

        // Pass results to ListViewAdapter Class
        adapter = new AdapterPelangganListView(getApplicationContext(), PelangganList,1);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (Order.equals("0")){
                    ShowKonfirmasiProsesReUpload(position);
                }else{
                    ShowKonfirmasiProses(position);
                }
            }
        });

        TxtCari = (EditText) findViewById(R.id.Pelanggan_Search);

        // Capture Text in EditText
        TxtCari.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                adapter.filter("",TxtCari.getText().toString().toLowerCase(Locale.getDefault()));
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
        SpnHari = (Spinner) findViewById(R.id.SpnPelangganDay);
        SpnHari.setVisibility(View.GONE);

        ImgFilter = (ImageView) findViewById(R.id.Pelanggan_ImgFilter);

        if (Order.equals("1")){
            ImgFilter.setVisibility(View.GONE);
        }

        ImgIcon = (ImageView) findViewById(R.id.Pelanggan_Icon);
        if (Order.equals("0")){
            ImgIcon.setImageResource(R.drawable.sfa_ordersuc);
        }else {
            ImgIcon.setImageResource(R.drawable.sfa_dftrorder);
        }

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                tgl = myCalendar.get(Calendar.DAY_OF_MONTH);
                bulan = myCalendar.get(Calendar.MONTH)+1;
                tahun = myCalendar.get(Calendar.YEAR);

                String harini = tahun+"-"+(bulan<10?("0"+bulan):(bulan))+"-"+(tgl<10?("0"+tgl):(tgl));
                Toast.makeText(getApplicationContext(),harini,Toast.LENGTH_SHORT).show();

                JSONObject PelangganJSON = null;
                PelangganList.clear();
                try {
                    PelangganJSON = dborder.getAllInvAfterSync(DB_PATH,DB_MASTER,harini);
                    // Getting Array of Pelanggan
                    PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

                    ShipTo = new String[PelangganArray.length()];
                    Perusahaan = new String[PelangganArray.length()];
                    Kode = new String[PelangganArray.length()];
                    Total = new String[PelangganArray.length()];

                    // looping through All Pelanggan
                    for(int i = 0; i < PelangganArray.length(); i++){
                        JSONObject c = PelangganArray.getJSONObject(i);

                        //status = c.getInt(TAG_STATUS);
                        String ShipToS = c.getString(TAG_SHIPTO);
                        String PerusahaanS = c.getString(TAG_PERUSAHAAN);
                        String KodeS= c.getString(TAG_KODE);
                        String TotalS= c.getString(TAG_TOTAL);

                        ShipTo[i] = ShipToS;
                        Perusahaan[i] = PerusahaanS;
                        Kode[i] = KodeS;
                        Total[i] = TotalS;
                    }


                    for (int i = 0; i < ShipTo.length; i++)
                    {
                        Data_Pelanggan plg = new Data_Pelanggan(ShipTo[i], Perusahaan[i], Kode[i],Total[i]);
                        PelangganList.add(plg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Pass results to ListViewAdapter Class
                adapter = new AdapterPelangganListView(getApplicationContext(), PelangganList,1);

                // Binds the Adapter to the ListView
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

        };

        ImgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityListInv.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public void ShowKonfirmasiProses(final int Pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Proses");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin ingin memproses transaksi ini");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);


        layout.addView(TxtLblNama,params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent ints = new Intent(getApplicationContext(), ActivityInventory.class);
                ints.putExtra("kode", adapter.getItem(Pos).getShipTo());
                ints.putExtra("kodeorder", adapter.getItem(Pos).getAlamat());
                ints.putExtra(TAG_PERUSAHAAN, adapter.getItem(Pos).getPerusahaan());
                ints.putExtra(TAG_MODEORDER,1);
                ints.putExtra(TAG_WEB,Web);
                startActivity(ints);
            }
        });

        // Set up the buttons
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ShowKonfirmasiDelete(Pos);
            }
        });

        builder.show();
    }

    public void ShowKonfirmasiDelete(final int Pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Proses");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin ingin menghapus transaksi ini");
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
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dborder.deleteInventory(adapter.getItem(Pos).getAlamat());
                dborder.RecekKunjungan(getToday(), getPref(TAG_LASTLOGIN).substring(0, 2) + "/" + getPref(TAG_LASTLOGIN).substring(2, 4)+"/"+getPref(TAG_LASTLOGIN).substring(4), adapter.getItem(Pos).getShipTo());
                JSONObject PelangganJSON = null;
                PelangganList.clear();
                try {
                    PelangganJSON = dborder.getAllInvBeforeSync(DB_PATH,DB_MASTER);
                    // Getting Array of Pelanggan
                    PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

                    ShipTo = new String[PelangganArray.length()];
                    Perusahaan = new String[PelangganArray.length()];
                    Kode = new String[PelangganArray.length()];
                    Total = new String[PelangganArray.length()];

                    // looping through All Pelanggan
                    for(int i = 0; i < PelangganArray.length(); i++){
                        JSONObject c = PelangganArray.getJSONObject(i);

                        //status = c.getInt(TAG_STATUS);
                        String ShipToS = c.getString(TAG_SHIPTO);
                        String PerusahaanS = c.getString(TAG_PERUSAHAAN);
                        String KodeS= c.getString(TAG_KODE);
                        String TotalS= c.getString(TAG_TOTAL);

                        ShipTo[i] = ShipToS;
                        Perusahaan[i] = PerusahaanS;
                        Kode[i] = KodeS;
                        Total[i] = TotalS;
                    }


                    for (int i = 0; i < ShipTo.length; i++)
                    {
                        Data_Pelanggan plg = new Data_Pelanggan(ShipTo[i], Perusahaan[i], Kode[i],Total[i]);
                        PelangganList.add(plg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Pass results to ListViewAdapter Class
                adapter = new AdapterPelangganListView(getApplicationContext(), PelangganList,1);

                // Binds the Adapter to the ListView
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });

        // Set up the buttons
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void ShowKonfirmasiProsesReUpload(final int Pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Proses");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Silahkan Pilih?");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);


        layout.addView(TxtLblNama,params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Detail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent ints = new Intent(getApplicationContext(), ActivityInventory.class);
                ints.putExtra("kode", adapter.getItem(Pos).getShipTo());
                ints.putExtra("kodeorder", adapter.getItem(Pos).getAlamat());
                ints.putExtra(TAG_PERUSAHAAN, adapter.getItem(Pos).getPerusahaan());
                ints.putExtra(TAG_MODEORDER,2);
                ints.putExtra(TAG_WEB,Web);
                startActivity(ints);
            }
        });

        // Set up the buttons
        builder.setNegativeButton("Buka Flag", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dborder.updateOpenFlagInventory(adapter.getItem(Pos).getAlamat());
                Toast.makeText(getApplicationContext(),"Buka Flag Berhasil, Inventory ini bisa di upload lagi via Menu Sync",Toast.LENGTH_SHORT).show();

            }
        });

        builder.show();
    }

}