package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import com.bcp.SFA_Native.FN_DBHandler;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
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

public class ActivityOrder extends Activity {
    //DB Handler
    private FN_DBHandler db,dborder;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String      DB_MASTER="MASTER";
    private String      DB_ORDER="ORDER_";

    private final String TAG_KODE = "kode";
    private final String TAG_KODEORDER = "kodeorder";
    private final String TAG_NAMA = "nama";
    private final String TAG_KETERANGAN = "keterangan";
    private final String TAG_MEREK = "merek";
    private final String TAG_VARIANT = "variant";
    private final String TAG_CRT = "crt";
    private final String TAG_HARGA = "harga";
    private final String TAG_JMLCRT = "jmlcrt";
    private final String TAG_JMLPCS = "jmlpcs";
    private final String TAG_LAST = "last";
    private final String TAG_PREF="SETTINGPREF";

    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";

    String FilMerek="",FilVariant="",FilSalesFilter="SEMUA",ShipTo="",Perusahaan="",KeySearch="",KodeOrder="";
    int FilMerekPos=0,FilVariantPos=0,FilSalesFilterPos=0,ModeOrder=0;

    private final String TAG_SHIPTO = "kode";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_MODEORDER= "modeorder";

    private final String TAG_MINORDER = "minorder";



    // Declare Variables
    ListView list;
    AdapterBarangOrderListView adapter;
    EditText editsearch;
    ImageView ImgFilter;
    TextView TxtPerusahaan,TxtShipTo,TxtTotal,TxtRp;

    String[] Kode,Nama,Merek,Variant,CRT,Harga,Assigned,AssignedImg,Keterangan,JmlCRT,JmlPCS,Last;
    ArrayList<Data_BarangOrder> BarangList = new ArrayList<Data_BarangOrder>();

    JSONArray BarangArray;
    private final String TAG_BARANGDATA= "BarangData";

    private String MinOrder = "0";

    private final String TAG_WEB = "web";
    private String Web="";

    final int[] flags = new int[]{
            R.drawable.sfa_done,R.drawable.dfa_error
    };

    public static String[] GroupMerek,GroupVariant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_order);

        if (getPref(TAG_MINORDER) == null){
            MinOrder = "0";
        }else{
            MinOrder = getPref(TAG_MINORDER);
        }


        Intent in = getIntent();
        ShipTo = in.getStringExtra(TAG_SHIPTO);
        Perusahaan = in.getStringExtra(TAG_PERUSAHAAN);
        if(Perusahaan.length()>25){
            Perusahaan = Perusahaan.substring(0,25);
        }
        ModeOrder = in.getIntExtra(TAG_MODEORDER,0);
        KodeOrder= in.getStringExtra(TAG_KODEORDER);
        Web = in.getStringExtra(TAG_WEB);

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject BarangJSON = null;

        if(dbFile.exists()){
            try {
                if(ModeOrder==0){
                    FilSalesFilter = "LAST";
                    FilSalesFilterPos = 2;
                    BarangJSON = db.GetBarangOrder(ShipTo);
                }else if(ModeOrder==1){
                    FilSalesFilter = "ORDER";
                    FilSalesFilterPos = 1;
                    BarangJSON = db.getDetailOrderBeforeSync(DB_PATH,DB_ORDER+getPref(TAG_LASTLOGIN),ShipTo,KodeOrder);
                }else{
                    FilSalesFilter = "ORDER";
                    FilSalesFilterPos = 1;
                    BarangJSON = db.getDetailOrderBeforeSync(DB_PATH,DB_ORDER+getPref(TAG_LASTLOGIN),ShipTo,KodeOrder);
                }
                // Getting Array of Barang
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
                JmlCRT = new String[BarangArray.length()];
                JmlPCS = new String[BarangArray.length()];
                Last = new String[BarangArray.length()];

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
                    String jmlcrt = c.getString(TAG_JMLCRT);
                    String jmlpcs = c.getString(TAG_JMLPCS);
                    String last = c.getString(TAG_LAST);

                    Kode[i] = kode;
                    Nama[i] = nama;
                    Merek[i] = merek;
                    Variant[i] = variant;
                    Keterangan[i] = keterangan;
                    CRT[i] = crt;
                    Harga[i] = harga;
                    Assigned[i]="0";
                    AssignedImg[i]=Integer.toString(flags[0]);
                    JmlCRT[i]=jmlcrt;
                    JmlPCS[i]=jmlpcs;
                    Last[i]=last;

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
            Data_BarangOrder brg = new Data_BarangOrder(Kode[i], Nama[i], Keterangan[i],Merek[i], Variant[i], CRT[i], Harga[i], Assigned[i], AssignedImg[i], JmlCRT[i], JmlPCS[i],Last[i]);
            BarangList.add(brg);
        }

        TxtPerusahaan = (TextView) findViewById(R.id.Order_Perusahaan);
        TxtShipTo = (TextView) findViewById(R.id.Order_ShipTo);
        TxtTotal = (TextView) findViewById(R.id.Order_SKU);
        TxtRp = (TextView) findViewById(R.id.Order_TotalRp);

        TxtPerusahaan.setText(Perusahaan);
        TxtShipTo.setText(ShipTo);


        // Pass results to ListViewAdapter Class
        adapter = new AdapterBarangOrderListView(this, BarangList);


        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        adapter.filter(KeySearch,FilMerek,FilVariant,FilSalesFilter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.notifyDataSetChanged();
                editsearch.setHint(adapter.getItem(position).getNama());
                if(ModeOrder<2){
                    ShowDialogInputJml(adapter.getItem(position).getNama(),adapter.getItem(position).getCRT(),position,adapter.getItem(position).getJmlCRT(), adapter.getItem(position).getJmlPCS());
                }
            }
        });


        // Locate the EditText in p_barang.xml
        editsearch = (EditText) findViewById(R.id.search);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                KeySearch = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                adapter.filter(KeySearch,FilMerek,FilVariant,FilSalesFilter);
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

        ImgFilter = (ImageView) findViewById(R.id.Order_ImgFilter);
        ImgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFilterBarang();
            }
        });




        dborder = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_ORDER+getPref(TAG_LASTLOGIN));

        if(ModeOrder>0){
            TxtTotal.setText(""+adapter.getTotalSKU());
            DecimalFormatSymbols symbol =
                    new DecimalFormatSymbols(Locale.GERMANY);
            symbol.setCurrencySymbol("");

            //
            // Set the new DecimalFormatSymbols into formatter object.
            //

            DecimalFormat formatter = (DecimalFormat)
                    NumberFormat.getCurrencyInstance(Locale.GERMANY);
            formatter.setDecimalFormatSymbols(symbol);
            String currency = formatter.format(adapter.getTotalRp());

            TxtRp.setText(""+currency);
        }

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

        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.sfa_save);

        final FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getTotalSKU()>0){
                    if(adapter.getTotalRp() >= Integer.parseInt(MinOrder)){
                        ShowKonfirmasiProses();
                    }else{
                        Toast.makeText(getApplicationContext(),"Nominal Order harus lebih dari Rp. "+MinOrder, Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Tidak ada barang yang di order, silahkan masukkan orderan dahulu!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(ModeOrder==2){
            actionButton.setVisibility(View.GONE);
        }

        final LinearLayout Ln = (LinearLayout) findViewById(R.id.Order_InfoTotal);


        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (ModeOrder<2){
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        actionButton.setVisibility(View.VISIBLE);
                    }else{
                        actionButton.setVisibility(View.GONE);
                    }
                }
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    Ln.setVisibility(View.VISIBLE);
                }else{
                    Ln.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            }

        });
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
        SpnMerek.setSelection(FilMerekPos);
        SpnMerek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FilMerekPos = position;
                if (position==0){
                    FilMerek = "";
                }else {
                    FilMerek = GroupMerek[position];
                }
                adapter.filter(KeySearch,FilMerek,FilVariant,FilSalesFilter);
                try {
                    GroupVariant = db.GetVariantByMerek(FilMerek);
                    ArrayAdapter adapterVariant = new ArrayAdapter(ActivityOrder.this,android.R.layout.simple_spinner_item, GroupVariant);
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
                adapter.filter(KeySearch,FilMerek,FilVariant,FilSalesFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final TextView TxtJdlSalesFilter = new TextView(this);
        TxtJdlSalesFilter.setText("Sales Filter :");

        final Spinner SpnSalesFilter = new Spinner(this);

        final String[] SalesFilterArr = new String[3];
        SalesFilterArr[0] = "SEMUA";
        SalesFilterArr[1] = "ORDER";
        SalesFilterArr[2] = "LAST ORDER";

        ArrayAdapter AdapterSalesFilter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, SalesFilterArr);
        AdapterSalesFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnSalesFilter.setAdapter(AdapterSalesFilter);

        SpnSalesFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(ModeOrder==0){
                    FilSalesFilter = SalesFilterArr[position];
                    FilSalesFilterPos = position;
                    adapter.filter(KeySearch,FilMerek,FilVariant,FilSalesFilter);
                }else{
                    if((position<2)&&(ModeOrder==1)){
                        FilSalesFilter = SalesFilterArr[position];
                        FilSalesFilterPos = position;
                        adapter.filter(KeySearch,FilMerek,FilVariant,FilSalesFilter);
                    }else {
                        Toast.makeText(getApplicationContext(),"Filter sales order dikunci",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        SpnMerek.setSelection(FilMerekPos);
        SpnVariant.setSelection(FilVariantPos);
        SpnSalesFilter.setSelection(FilSalesFilterPos);

        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);


        layout.addView(TxtLblMerek,params);
        layout.addView(SpnMerek,params);
        layout.addView(TxtLblVariant,params);
        layout.addView(SpnVariant,params);
        layout.addView(TxtJdlSalesFilter,params);
        layout.addView(SpnSalesFilter,params);


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

    public void ShowDialogInputJml(String Nama,String CRT,final int Pos,final String JmlCRT, String JmlPCS){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Jumlah");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 3, 30, 3);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText(""+Nama);
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        TxtLblNama.setTypeface(null,Typeface.BOLD);

        final TextView TxtLblPCS = new TextView(this);
        TxtLblPCS.setText("PCS :");

        final EditText InputPCS = new EditText(this);
        InputPCS.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputPCS.setSingleLine();
        InputPCS.setHint("Jml PCS");
        InputPCS.setText(JmlPCS);

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(8);
        InputPCS.setFilters(FilterArray);

        final TextView TxtLblCRT = new TextView(this);
        TxtLblCRT.setText("CRT :");


        final EditText InputCRT = new EditText(this);
        InputCRT.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputCRT.setSingleLine();
        InputCRT.setHint("Jml CRT");
        InputCRT.setText(JmlCRT);


        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);


        layout.addView(TxtLblNama,params);
        layout.addView(TxtLblCRT,params);
        layout.addView(InputCRT,params);
        layout.addView(TxtLblPCS,params);
        layout.addView(InputPCS,params);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (InputCRT.getText().toString().equals("")){
                    InputCRT.setText("0");
                }
                if (InputPCS.getText().toString().equals("")){
                    InputPCS.setText("0");
                }
                adapter.getItem(Pos).setJmlCRT(Integer.toString(Integer.parseInt(InputCRT.getText().toString())));
                adapter.getItem(Pos).setJmlPCS(Integer.toString(Integer.parseInt(InputPCS.getText().toString())));
                adapter.notifyDataSetChanged();
                TxtTotal.setText(""+adapter.getTotalSKU());
                DecimalFormatSymbols symbol =
                        new DecimalFormatSymbols(Locale.GERMANY);
                symbol.setCurrencySymbol("");

                //
                // Set the new DecimalFormatSymbols into formatter object.
                //

                DecimalFormat formatter = (DecimalFormat)
                        NumberFormat.getCurrencyInstance(Locale.GERMANY);
                formatter.setDecimalFormatSymbols(symbol);
                String currency = formatter.format(adapter.getTotalRp());

                TxtRp.setText(currency);
            }
        });

        builder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
            }
        });
        builder.show();
    }

    public void ShowDialogInputKet(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Keterangan");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 30, 0);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Masukkan Keterangan");


        final EditText InputKet = new EditText(this);
        InputKet.setSingleLine();

        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);


        layout.addView(TxtLblNama,params);
        layout.addView(InputKet,params);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String kode="";
                if(ModeOrder==0){
                    kode=getPref(TAG_LASTLOGIN)+"/"+dborder.getDateTime("yyMMdd")+"/"+String.format("%03d",Integer.parseInt(dborder.getmaxkode(getPref(TAG_LASTLOGIN)+"/"+dborder.getDateTime("yyMMdd")))+1);
                }else{
                    kode=KodeOrder;
                    dborder.deleteOrder(KodeOrder);
                }
                adapter.filter("","","","ORDER");
                for (int i=0;i<adapter.getCount();i++){
                    dborder.insertOrder(kode,getPref(TAG_LASTLOGIN).substring(0, 2)+"/"+getPref(TAG_LASTLOGIN).substring(2, 4)+"/"+getPref(TAG_LASTLOGIN).substring(4),getPref(TAG_NAMELOGIN),ShipTo,adapter.getItem(i).getKode(),Integer.parseInt(adapter.getItem(i).getJmlCRT()),Integer.parseInt(adapter.getItem(i).getJmlPCS()),((Integer.parseInt(adapter.getItem(i).getJmlPCS())*Float.parseFloat(adapter.getItem(i).getHarga()))+((Float.parseFloat(adapter.getItem(i).getHarga()))*(Integer.parseInt(adapter.getItem(i).getJmlCRT()))*(Integer.parseInt(adapter.getItem(i).getCRT())))),InputKet.getText().toString(),dborder.getToday2(),getPref(TAG_LONGITUDE),getPref(TAG_LATITUDE));
                }
                Toast.makeText(getApplicationContext(),"Order "+Perusahaan+" Berhasil Di Simpan",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(TAG_WEB,Web);
                startActivity(intent);
            }
        });

        builder.show();
    }

    public void ShowKonfirmasiProses(){
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
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ShowDialogInputKet();
            }
        });

        // Set up the buttons
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if (ModeOrder==2){
                Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(TAG_WEB,Web);
                startActivity(intent);
            }else{
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra(TAG_WEB,Web);
                                startActivity(intent);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Konfirmasi");
                builder.setMessage("Apakah anda yakin membatalkan transaksi ini?").setPositiveButton("Ya", dialogClickListener)
                        .setNegativeButton("Tidak", dialogClickListener).show();
            }
        }
        return false;
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }
}