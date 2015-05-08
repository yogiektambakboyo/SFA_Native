package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import com.bcp.SFA_Native.FN_DBHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityBarang extends Activity {
    //DB Handler
    private FN_DBHandler db;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String      DB_MASTER="MASTER";

    private final String TAG_KODE = "kode";
    private final String TAG_NAMA = "nama";
    private final String TAG_KETERANGAN = "keterangan";
    private final String TAG_MEREK = "merek";
    private final String TAG_VARIANT = "variant";
    private final String TAG_CRT = "crt";
    private final String TAG_HARGA = "harga";

    String FilMerek="",FilVariant="";
    int FilMerekPos=0,FilVariantPos=0;

    ListView list;
    AdapterBarangListView adapter;
    EditText editsearch;
    ImageView ImgFilter;

    String[] Kode,Nama,Merek,Variant,CRT,Harga,Assigned,AssignedImg,Keterangan;
    ArrayList<Data_Barang> BarangList = new ArrayList<Data_Barang>();

    JSONArray BarangArray;
    private final String TAG_BARANGDATA= "BarangData";

    private Float TotalRp = 0f;

    final int[] flags = new int[]{
            R.drawable.sfa_done,R.drawable.dfa_error
    };

    public static String[] GroupMerek,GroupVariant;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_barang);

        // Get Data From DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject BarangJSON = null;

        if(dbFile.exists()){
            try {
                BarangJSON = db.GetBarang();
                BarangArray = BarangJSON.getJSONArray(TAG_BARANGDATA);

                Kode = new String[BarangArray.length()];
                Nama = new String[BarangArray.length()];
                Merek = new String[BarangArray.length()];
                Variant = new String[BarangArray.length()];
                Keterangan = new String[BarangArray.length()];
                Assigned = new String[BarangArray.length()];
                AssignedImg = new String[BarangArray.length()];
                CRT = new String[BarangArray.length()];
                Harga = new String[BarangArray.length()];

                // looping through All Barang
                for(int i = 0; i < BarangArray.length(); i++){
                    JSONObject c = BarangArray.getJSONObject(i);

                    String kode = c.getString(TAG_KODE);
                    String nama = c.getString(TAG_NAMA);
                    String merek = c.getString(TAG_MEREK);
                    String variant = c.getString(TAG_VARIANT);
                    String keterangan = c.getString(TAG_KETERANGAN);
                    String crt = c.getString(TAG_CRT);
                    String harga = c.getString(TAG_HARGA);

                    if(kode.length()>10){
                        Kode[i] = kode.substring(3,12);
                    }else{
                        Kode[i] = kode.substring(3,kode.length());
                    }
                    Nama[i] = nama;
                    Merek[i] = merek;
                    Variant[i] = variant;
                    Keterangan[i] = keterangan;
                    CRT[i] = crt;
                    Harga[i] = harga;
                    Assigned[i]="0";
                    AssignedImg[i]=Integer.toString(flags[0]);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            db.close();
        }else{
            Toast.makeText(getApplicationContext(),"DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }

        list = (ListView) findViewById(R.id.listview);

        for (int i = 0; i < BarangArray.length(); i++)
        {

            Data_Barang brg = new Data_Barang(Kode[i], Nama[i], Keterangan[i],Merek[i], Variant[i], CRT[i], Harga[i], Assigned[i], AssignedImg[i]);
            BarangList.add(brg);
        }

        // Pass results to ListViewAdapter Class
        adapter = new AdapterBarangListView(this, BarangList);


        DecimalFormatSymbols symbol =
                new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        //
        // Set the new DecimalFormatSymbols into formatter object.
        //

        DecimalFormat formatter = (DecimalFormat)
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        String currency = formatter.format(TotalRp);


        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.notifyDataSetChanged();
                editsearch.setHint(adapter.getItem(position).getNama());
            }
        });

        // Locate the EditText in p_barang.xml
        editsearch = (EditText) findViewById(R.id.search);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(text,FilMerek,FilVariant);
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

        ImgFilter = (ImageView) findViewById(R.id.Barang_ImgFilter);
        ImgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFilterBarang();
            }
        });

        try {
            GroupMerek = db.GetMerek();
            GroupVariant = db.GetVariant();
        } catch (JSONException e) {
            GroupMerek = new String[1];
            GroupMerek[0] = "SEMUA";
            GroupVariant = new String[1];
            GroupVariant[0] = "SEMUA";
            Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    public void ShowFilterBarang(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Barang");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtLblMerek = new TextView(this);
        TxtLblMerek.setText("Merek :");

        final Spinner SpnMerek = new Spinner(this);

        final TextView TxtLblVariant = new TextView(this);
        TxtLblVariant.setText("Variant :");

        final Spinner SpnVariant = new Spinner(this);

        ArrayAdapter adapterMerek = new ArrayAdapter(this,android.R.layout.simple_spinner_item, GroupMerek);
        adapterMerek.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnMerek.setAdapter(adapterMerek);
        final String[] finalGroupMerek = GroupMerek;
        SpnMerek.setSelection(FilMerekPos);
        SpnMerek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FilMerekPos = position;
                if (position==0){
                    FilMerek = "";
                }else {
                    FilMerek = finalGroupMerek[position];
                }
                adapter.filterMerekVar(FilMerek,FilVariant);
                try {
                    GroupVariant = db.GetVariantByMerek(FilMerek);
                    ArrayAdapter adapterVariant = new ArrayAdapter(ActivityBarang.this,android.R.layout.simple_spinner_item, GroupVariant);
                    adapterVariant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    SpnVariant.setAdapter(adapterVariant);
                    SpnVariant.setSelection(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter adapterVariant = new ArrayAdapter(this,android.R.layout.simple_spinner_item, GroupVariant);
        adapterVariant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnVariant.setAdapter(adapterVariant);
        SpnVariant.setSelection(FilVariantPos);
        SpnVariant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FilVariantPos = position;
                if (position==0){
                    FilVariant = "";
                }else{
                    FilVariant = GroupVariant[position];
                }
                adapter.filterMerekVar(FilMerek,FilVariant);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);


        layout.addView(TxtLblMerek,params);
        layout.addView(SpnMerek,params);
        layout.addView(TxtLblVariant,params);
        layout.addView(SpnVariant,params);


        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.show();
    }
}