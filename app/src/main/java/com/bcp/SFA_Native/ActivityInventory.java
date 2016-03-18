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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityInventory extends Activity {
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
    private final String TAG_STOK = "stok";
    private final String TAG_OPT1 = "opt1";
    private final String TAG_OPT2 = "opt2";
    private final String TAG_OPT3 = "opt3";
    private final String TAG_ITEMBARCODE = "itembarcode";
    private final String TAG_PREF="SETTINGPREF";

    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";
    private final String TAG_GPSTIME = "gpstime";
    private final String TAG_REVERSEGEOCODE = "reverse";



    String FilMerek="",FilVariant="",FilSalesFilter="SEMUA",ShipTo="",Perusahaan="",KeySearch="",KodeOrder="";
    public int FilMerekPos=0,FilVariantPos=0,FilSalesFilterPos=0,ModeOrder=0,ModeInv=0;

    private final String TAG_SHIPTO = "kode";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_MODEORDER= "modeorder";
    private final String TAG_MODEINV= "modeinv";


    // Declare Variables
    ListView list;
    AdapterBarangInventoryListView adapter;
    EditText editsearch;
    ImageView ImgFilter;
    TextView TxtPerusahaan,TxtShipTo,TxtTotal,TxtInv_LblOpt1,TxtInv_LblOpt2,TxtInv_LblOpt3;

    String[] Kode,Nama,Merek,Variant,Keterangan,Stok,OPT1,OPT2,OPT3,Assigned,AssignedImg,ItemBarcode;
    ArrayList<Data_BarangInventory> BarangList = new ArrayList<Data_BarangInventory>();

    JSONArray BarangArray;
    private final String TAG_BARANGDATA= "BarangData";

    private final String TAG_WEB = "web";
    private String Web="";

    final int[] flags = new int[]{
            R.drawable.sfa_done,R.drawable.dfa_error
    };

    public static String[] GroupMerek,GroupVariant;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_inventory);

        Intent in = getIntent();
        ShipTo = in.getStringExtra(TAG_SHIPTO);
        Perusahaan = in.getStringExtra(TAG_PERUSAHAAN);
        if(Perusahaan.length()>25){
            Perusahaan = Perusahaan.substring(0,25);
        }
        ModeOrder = in.getIntExtra(TAG_MODEORDER,0);
        KodeOrder= in.getStringExtra(TAG_KODEORDER);
        Web = in.getStringExtra(TAG_WEB);
        ModeInv = in.getIntExtra(TAG_MODEINV,0);

        // Get Data Fron DB
        db = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);
        File dbFile = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject BarangJSON = null;

        if(dbFile.exists()){
            try {
                if(ModeOrder==0){
                    FilSalesFilter = "SEMUA";
                    FilSalesFilterPos = 0;
                    BarangJSON = db.GetBarangInventory();
                }else{
                    FilSalesFilter = "INVENTORY";
                    FilSalesFilterPos = 1;
                    BarangJSON = db.getDetailInventoryEdit(DB_PATH, DB_ORDER + getPref(TAG_LASTLOGIN), ShipTo, KodeOrder);
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
                Stok = new String[BarangArray.length()];
                OPT1 = new String[BarangArray.length()];
                OPT2 = new String[BarangArray.length()];
                OPT3 = new String[BarangArray.length()];
                ItemBarcode = new String[BarangArray.length()];

                // looping through All Barang
                for(int i = 0; i < BarangArray.length(); i++){
                    JSONObject c = BarangArray.getJSONObject(i);

                    String kode = c.getString(TAG_KODE);
                    String nama = c.getString(TAG_NAMA);
                    String merek = c.getString(TAG_MEREK);
                    String variant = c.getString(TAG_VARIANT);
                    String keterangan = c.getString(TAG_KETERANGAN);
                    String stok = c.getString(TAG_STOK);
                    String opt1 = c.getString(TAG_OPT1);
                    String opt2 = c.getString(TAG_OPT2);
                    String opt3 = c.getString(TAG_OPT3);
                    String itembarcode = c.getString(TAG_ITEMBARCODE);

                    Kode[i] = kode;
                    Nama[i] = nama;
                    Merek[i] = merek;
                    Variant[i] = variant;
                    Keterangan[i] = keterangan;
                    Assigned[i]="0";
                    AssignedImg[i]=Integer.toString(flags[0]);
                    Stok[i]=stok;
                    OPT1[i]=opt1;
                    OPT2[i]=opt2;
                    OPT3[i]=opt3;
                    ItemBarcode[i]=itembarcode;

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
            Data_BarangInventory brg = new Data_BarangInventory(Kode[i], Nama[i], Keterangan[i],Merek[i], Variant[i],Assigned[i],AssignedImg[i],Stok[i],OPT1[i],OPT2[i],OPT3[i],ItemBarcode[i]);
            BarangList.add(brg);
        }

        TxtPerusahaan = (TextView) findViewById(R.id.Inv_Perusahaan);
        TxtShipTo = (TextView) findViewById(R.id.Inv_ShipTo);
        TxtTotal = (TextView) findViewById(R.id.Inv_SKU);
        TxtInv_LblOpt1 = (TextView) findViewById(R.id.Inv_LblOpt1);
        TxtInv_LblOpt2 = (TextView) findViewById(R.id.Inv_LblOpt2);
        TxtInv_LblOpt3 = (TextView) findViewById(R.id.Inv_LblOpt3);

        TxtPerusahaan.setText(Perusahaan);
        TxtShipTo.setText(ShipTo);
        TxtInv_LblOpt1.setText(getPref(TAG_OPT1));
        TxtInv_LblOpt2.setText(getPref(TAG_OPT2));
        TxtInv_LblOpt3.setText(getPref(TAG_OPT3));

        // Pass results to ListViewAdapter Class
        adapter = new AdapterBarangInventoryListView(this, BarangList);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        adapter.filter(KeySearch, FilMerek, FilVariant, FilSalesFilter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.notifyDataSetChanged();
                editsearch.setHint(adapter.getItem(position).getNama());
                if (ModeOrder < 2) {
                    ShowDialogInputJml(adapter.getItem(position).getNama(), position, adapter.getItem(position).getStok(), adapter.getItem(position).getOPT1(), adapter.getItem(position).getOPT2(), adapter.getItem(position).getOPT3());
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
                adapter.filter(KeySearch, FilMerek, FilVariant, FilSalesFilter);
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

        ImgFilter = (ImageView) findViewById(R.id.Inv_ImgFilter);
        ImgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFilterBarang(FilSalesFilterPos);
            }
        });




        dborder = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_ORDER+getPref(TAG_LASTLOGIN));

        if(ModeOrder>0){
            TxtTotal.setText("" + adapter.getTotalSKU());
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
                if (adapter.getTotalSKU() > 0) {
                    ShowKonfirmasiProses();
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak ada barang yang di masukkan, silahkan masukkan inventory dahulu!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(ModeOrder==2){
            actionButton.setVisibility(View.GONE);
        }

        final LinearLayout Ln = (LinearLayout) findViewById(R.id.Inv_InfoTotal);


        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (ModeOrder < 2) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        actionButton.setVisibility(View.VISIBLE);
                    } else {
                        actionButton.setVisibility(View.GONE);
                    }
                }
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    Ln.setVisibility(View.VISIBLE);
                } else {
                    Ln.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            }

        });
    }

    public void ShowFilterBarang(int FilSalesPos){
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
                if (position == 0) {
                    FilMerek = "";
                } else {
                    FilMerek = GroupMerek[position];
                }
                adapter.filter(KeySearch, FilMerek, FilVariant, FilSalesFilter);
                try {
                    GroupVariant = db.GetVariantByMerek(FilMerek);
                    ArrayAdapter adapterVariant = new ArrayAdapter(ActivityInventory.this, android.R.layout.simple_spinner_item, GroupVariant);
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
                if (position == 0) {
                    FilVariant = "";
                } else {
                    FilVariant = GroupVariant[position];
                }
                adapter.filter(KeySearch, FilMerek, FilVariant, FilSalesFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final TextView TxtJdlSalesFilter = new TextView(this);
        TxtJdlSalesFilter.setText("Sales Filter :");

        final Spinner SpnSalesFilter = new Spinner(this);

        final String[] SalesFilterArr = new String[2];
        SalesFilterArr[0] = "SEMUA";
        SalesFilterArr[1] = "INVENTORY";

        ArrayAdapter AdapterSalesFilter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, SalesFilterArr);
        AdapterSalesFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnSalesFilter.setAdapter(AdapterSalesFilter);
        SpnSalesFilter.setSelection(FilSalesFilterPos);

        SpnSalesFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ModeOrder == 0) {
                    FilSalesFilter = SalesFilterArr[position];
                    FilSalesFilterPos = position;
                    adapter.filter(KeySearch, FilMerek, FilVariant, FilSalesFilter);
                } else {
                    if ((position < 2) && (ModeOrder == 1)) {
                        FilSalesFilter = SalesFilterArr[position];
                        FilSalesFilterPos = position;
                        adapter.filter(KeySearch, FilMerek, FilVariant, FilSalesFilter);
                    } else {
                        Toast.makeText(getApplicationContext(), "Filter sales order dikunci", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SpnMerek.setSelection(FilMerekPos);
        SpnVariant.setSelection(FilVariantPos);
        //SpnSalesFilter.setSelection(FilSalesFilterPos);

        layout.addView(TxtLblMerek,params);
        layout.addView(SpnMerek, params);
        layout.addView(TxtLblVariant,params);
        layout.addView(SpnVariant,params);
        layout.addView(TxtJdlSalesFilter,params);
        layout.addView(SpnSalesFilter, params);


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

    public void ShowDialogInputJml(String Nama,final int Pos,final String Stok,final String OPT1, String OPT2,String OPT3){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Jumlah (PCS)");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 3, 30, 3);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("" + Nama);
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        TxtLblNama.setTypeface(null, Typeface.BOLD);

        final TextView TxtLblOPT1 = new TextView(this);
        TxtLblOPT1.setText(getPref(TAG_OPT1)+" :");

        final EditText InputOPT1 = new EditText(this);
        InputOPT1.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputOPT1.setSingleLine();
        InputOPT1.setHint("Jml "+getPref(TAG_OPT1));
        InputOPT1.setText(OPT1);

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(8);
        InputOPT1.setFilters(FilterArray);

        final TextView TxtLblOPT2 = new TextView(this);
        TxtLblOPT2.setText(getPref(TAG_OPT2)+" :");

        final TextView TxtLblStok = new TextView(this);
        TxtLblStok.setText("Stok Toko :");

        final EditText InputStok = new EditText(this);
        InputStok.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputStok.setFilters(FilterArray);
        InputStok.setSingleLine();
        InputStok.setHint("Jml Stok");
        InputStok.setText(Stok);

        final EditText InputOPT2 = new EditText(this);
        InputOPT2.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputOPT2.setSingleLine();
        InputOPT2.setHint("Jml "+getPref(TAG_OPT2));
        InputOPT2.setText(OPT2);
        InputOPT2.setFilters(FilterArray);

        final TextView TxtLblOPT3 = new TextView(this);
        TxtLblOPT3.setText(getPref(TAG_OPT3)+" :");


        final EditText InputOPT3 = new EditText(this);
        InputOPT3.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputOPT3.setSingleLine();
        InputOPT3.setHint("Jml "+getPref(TAG_OPT3));
        InputOPT3.setText(OPT3);
        InputOPT3.setFilters(FilterArray);


        layout.addView(TxtLblNama, params);
        layout.addView(TxtLblStok, params);
        layout.addView(InputStok, params);
        layout.addView(TxtLblOPT1, params);
        layout.addView(InputOPT1,params);
        layout.addView(TxtLblOPT2,params);
        layout.addView(InputOPT2,params);
        layout.addView(TxtLblOPT3,params);
        layout.addView(InputOPT3,params);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (InputStok.getText().toString().equals("")){
                    InputStok.setText("0");
                }
                if (InputOPT1.getText().toString().equals("")){
                    InputOPT1.setText("0");
                }
                if (InputOPT2.getText().toString().equals("")){
                    InputOPT2.setText("0");
                }
                if (InputOPT3.getText().toString().equals("")){
                    InputOPT3.setText("0");
                }
                adapter.getItem(Pos).setStok(Integer.toString(Integer.parseInt(InputStok.getText().toString())));
                adapter.getItem(Pos).setOPT1(Integer.toString(Integer.parseInt(InputOPT1.getText().toString())));
                adapter.getItem(Pos).setOPT2(Integer.toString(Integer.parseInt(InputOPT2.getText().toString())));
                adapter.getItem(Pos).setOPT3(Integer.toString(Integer.parseInt(InputOPT3.getText().toString())));
                adapter.notifyDataSetChanged();
                TxtTotal.setText(""+adapter.getTotalSKU());
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
                String kode="";
                dborder.updateKunjunganLongLat(getToday(),getPref(TAG_LASTLOGIN).substring(0, 2)+"/"+getPref(TAG_LASTLOGIN).substring(2, 4)+"/"+getPref(TAG_LASTLOGIN).substring(4),ShipTo,getPref(TAG_LONGITUDE),getPref(TAG_LATITUDE),getPref(TAG_GPSTIME),getPref(TAG_REVERSEGEOCODE));
                if(ModeOrder==0){
                    kode=getPref(TAG_LASTLOGIN)+"/"+dborder.getDateTime("yyMMdd")+"/"+String.format("%03d",Integer.parseInt(dborder.getMaxKodeInv(getPref(TAG_LASTLOGIN) + "/" + dborder.getDateTime("yyMMdd")))+1);
                    dborder.updateKunjunganCall(getToday(),getPref(TAG_LASTLOGIN).substring(0, 2)+"/"+getPref(TAG_LASTLOGIN).substring(2, 4)+"/"+getPref(TAG_LASTLOGIN).substring(4), ShipTo, "1");
                }else{
                    kode=KodeOrder;
                    dborder.deleteInventory(KodeOrder);
                }
                adapter.filter("","","","INVENTORY");
                for (int i=0;i<adapter.getCount();i++){
                    dborder.insertInventory(kode,getPref(TAG_LASTLOGIN).substring(0, 2)+"/"+getPref(TAG_LASTLOGIN).substring(2, 4)+"/"+getPref(TAG_LASTLOGIN).substring(4),getPref(TAG_NAMELOGIN),ShipTo,adapter.getItem(i).getKode(),Integer.parseInt(adapter.getItem(i).getStok()),Integer.parseInt(adapter.getItem(i).getOPT1()),Integer.parseInt(adapter.getItem(i).getOPT2()),Integer.parseInt(adapter.getItem(i).getOPT3()));
                }
                Toast.makeText(getApplicationContext(),"Inventory "+Perusahaan+" Berhasil Di Simpan",Toast.LENGTH_SHORT).show();
                //Edit This
                if(ModeOrder==0){
                    //Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
                    Intent intent = new Intent(getApplicationContext(), ActivityMenuTask.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(TAG_SHIPTO, ShipTo);
                    intent.putExtra(TAG_PERUSAHAAN, Perusahaan);
                    intent.putExtra(TAG_WEB,Web);
                    startActivity(intent);
                }else if(ModeInv==1){
                    Intent intent = new Intent(getApplicationContext(), ActivityMenuTask.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(TAG_SHIPTO, ShipTo);
                    intent.putExtra(TAG_PERUSAHAAN, Perusahaan);
                    intent.putExtra(TAG_WEB,Web);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(TAG_SHIPTO, ShipTo);
                    intent.putExtra(TAG_PERUSAHAAN, Perusahaan);
                    intent.putExtra(TAG_WEB,Web);
                    startActivity(intent);
                }
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

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if (ModeOrder==2){
                Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(TAG_SHIPTO, ShipTo);
                intent.putExtra(TAG_PERUSAHAAN, Perusahaan);
                intent.putExtra(TAG_WEB,Web);
                startActivity(intent);
            }else{
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                if(ModeOrder==0){
                                    //Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
                                    Intent intent = new Intent(getApplicationContext(), ActivityMenuTask.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra(TAG_SHIPTO, ShipTo);
                                    intent.putExtra(TAG_PERUSAHAAN, Perusahaan);
                                    intent.putExtra(TAG_WEB,Web);
                                    startActivity(intent);
                                }else if(ModeInv==1){
                                    Intent intent = new Intent(getApplicationContext(), ActivityMenuTask.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra(TAG_SHIPTO, ShipTo);
                                    intent.putExtra(TAG_PERUSAHAAN, Perusahaan);
                                    intent.putExtra(TAG_WEB,Web);
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra(TAG_SHIPTO, ShipTo);
                                    intent.putExtra(TAG_PERUSAHAAN, Perusahaan);
                                    intent.putExtra(TAG_WEB,Web);
                                    startActivity(intent);
                                }
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