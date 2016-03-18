package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private final String TAG_STOK = "stok";
    private final String TAG_ITEMBARCODE = "itembarcode";
    private final String TAG_LASTORDERQTY = "lastorderqty";
    private final String TAG_PREF="SETTINGPREF";

    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";
    private final String TAG_GPSTIME = "gpstime";
    private final String TAG_REVERSEGEOCODE = "reverse";


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

    String[] Kode,Nama,Merek,Variant,CRT,Harga,Assigned,AssignedImg,Keterangan,JmlCRT,JmlPCS,Last,Stok,LastOrderQty,ItemBarcode;
    ArrayList<Data_BarangOrder> BarangList = new ArrayList<Data_BarangOrder>();

    JSONArray BarangArray,SaldoArray;
    private final String TAG_BARANGDATA= "BarangData";

    private String MinOrder = "0";

    private final String TAG_WEB = "web";
    private String Web="";

    final int[] flags = new int[]{
            R.drawable.sfa_done,R.drawable.dfa_error
    };

    public static String[] GroupMerek,GroupVariant;
    private String EntryTime = "2000-01-01 00:00";
    public float SaldoLimit = 0f,SaldoAktif=0f;

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
                    BarangJSON = db.GetBarangOrder(DB_PATH,DB_ORDER+getPref(TAG_LASTLOGIN),ShipTo);
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
                Stok = new String[BarangArray.length()];
                LastOrderQty = new String[BarangArray.length()];
                ItemBarcode = new String[BarangArray.length()];

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
                    String stok = c.getString(TAG_STOK);
                    String lastorderqty = c.getString(TAG_LASTORDERQTY);
                    String itembarcode = c.getString(TAG_ITEMBARCODE);

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
                    Stok[i]=stok;
                    LastOrderQty[i]=lastorderqty;
                    ItemBarcode[i]=itembarcode;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            EntryTime = db.getDateTime("yyyy-MM-dd HH:mm");

            JSONObject JSaldo = null;

            try{
                JSaldo = db.getLimitCust(ShipTo);

                SaldoArray = JSaldo.getJSONArray(TAG_BARANGDATA);

                for (int z=0;z<SaldoArray.length();z++){
                    JSONObject d = SaldoArray.getJSONObject(z);
                    SaldoAktif = Float.parseFloat(d.getString("saldoaktif"));
                    SaldoLimit = Float.parseFloat(d.getString("saldolimit"));
                }

            }catch (JSONException e){
                e.printStackTrace();
            }

            db.close();
        }else{
            Toast.makeText(getApplicationContext(),"DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }

        list = (ListView) findViewById(R.id.listview);
        for (int i = 0; i < BarangArray.length(); i++)
        {
            Data_BarangOrder brg = new Data_BarangOrder(Kode[i], Nama[i], Keterangan[i],Merek[i], Variant[i], CRT[i], Harga[i], Assigned[i], AssignedImg[i], JmlCRT[i], JmlPCS[i],Last[i],Stok[i],LastOrderQty[i],ItemBarcode[i],getTimeStamp());
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

        adapter.filter(KeySearch, FilMerek, FilVariant, FilSalesFilter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.notifyDataSetChanged();
                editsearch.setHint(adapter.getItem(position).getNama());
                if (ModeOrder < 2) {
                    ShowDialogInputJml(adapter.getItem(position).getNama(), adapter.getItem(position).getCRT(), position, adapter.getItem(position).getJmlCRT(), adapter.getItem(position).getJmlPCS());
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
                if (adapter.getTotalSKU() > 0) {
                    if (adapter.getTotalRp() >= Integer.parseInt(MinOrder)) {
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                        try {
                            int isAutoDate = 0;
                            int isAutoZonaTime = 0;
                            int isAirPlaneMode = 0;
                            isAutoDate = Settings.System.getInt(getContentResolver(),Settings.Global.AUTO_TIME);
                            isAutoZonaTime = Settings.System.getInt(getContentResolver(),Settings.Global.AUTO_TIME_ZONE);
                            isAirPlaneMode = Settings.System.getInt(getContentResolver(),Settings.Global.AIRPLANE_MODE_ON);
                            if ((isAutoDate == 0)||(isAutoZonaTime == 0)||(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))||(checkMockLocation())||(isAirPlaneMode == 1)||(!isMobileDataEnabled())){
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
                                new AlertDialog.Builder(ActivityOrder.this)
                                        .setTitle("Information")
                                        .setMessage(Msg)
                                        .setPositiveButton("Setelan", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                            }
                                        })
                                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .setCancelable(true)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }else{
                                ShowKonfirmasiProses();
                            }
                        } catch (Settings.SettingNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Nominal Order harus lebih dari Rp. " + MinOrder, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak ada barang yang di order, silahkan masukkan orderan dahulu!!!", Toast.LENGTH_SHORT).show();
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

    public boolean checkMockLocation(){
        if (Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else return true;
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
                if (position == 0) {
                    FilMerek = "";
                } else {
                    FilMerek = GroupMerek[position];
                }
                adapter.filter(KeySearch, FilMerek, FilVariant, FilSalesFilter);
                try {
                    GroupVariant = db.GetVariantByMerek(FilMerek);
                    ArrayAdapter adapterVariant = new ArrayAdapter(ActivityOrder.this, android.R.layout.simple_spinner_item, GroupVariant);
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
        SpnSalesFilter.setSelection(FilSalesFilterPos);

        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);


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

    public void ShowDialogInputJml(String Nama,final String CRT,final int Pos,final String JmlCRT,final String JmlPCS){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Jumlah");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 2, 30, 2);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("" + Nama);
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        TxtLblNama.setTypeface(null, Typeface.BOLD);

        final TextView TxtLblSKU = new TextView(this);
        TxtLblSKU.setText("SKU : " + adapter.getItem(Pos).getKode());
        TxtLblSKU.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        TxtLblSKU.setTypeface(null, Typeface.BOLD);

        int a = 0,b = 0,c =0;
        if (adapter.getItem(Pos).getLastOrderQty().length()>0){
            a = (int)Double.parseDouble(adapter.getItem(Pos).getLastOrderQty());
        }
        if (adapter.getItem(Pos).getLastOrderQty().length()>0){
            b = (int)Double.parseDouble(adapter.getItem(Pos).getStok());
        }

        if (b>a){
            c=0;
        }else {
            c = a-b;
        }

        final TextView TxtLblLastQty = new TextView(this);
        TxtLblLastQty.setText("Suggest Order : " + c + " PCS");
        TxtLblLastQty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        TxtLblLastQty.setTypeface(null, Typeface.BOLD);

        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        final DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);

        Float totalharga = 0.0f;
        totalharga = ((Integer.parseInt(JmlPCS))*(Float.parseFloat(adapter.getItem(Pos).getHarga()))) + ((Integer.parseInt(JmlCRT))*(Float.parseFloat(adapter.getItem(Pos).getHarga())*(Integer.parseInt(CRT))));

        String cu = formatter.format(totalharga);
        final TextView TxtLblPrice = new TextView(this);
        TxtLblPrice.setText("Total Harga : Rp. " + cu);
        TxtLblPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        TxtLblPrice.setTypeface(null, Typeface.BOLD);

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
        InputCRT.setFilters(FilterArray);

        layout.addView(TxtLblNama, params);
        layout.addView(TxtLblSKU, params);
        layout.addView(TxtLblLastQty,params);
        layout.addView(TxtLblPrice,params);
        layout.addView(TxtLblCRT,params);
        layout.addView(InputCRT,params);
        layout.addView(TxtLblPCS,params);
        layout.addView(InputPCS,params);

        builder.setView(layout);

        InputPCS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int pcs = 0;
                if (InputPCS.getText().toString().length() > 0) {
                    pcs = Integer.parseInt(InputPCS.getText().toString());
                }

                int crt = 0;
                if (InputCRT.getText().toString().length() > 0) {
                    crt = Integer.parseInt(InputCRT.getText().toString());
                }

                float hrg = ((pcs) * (Float.parseFloat(adapter.getItem(Pos).getHarga()))) + ((crt) * (Float.parseFloat(adapter.getItem(Pos).getHarga()) * (Integer.parseInt(CRT))));

                String frompcs = formatter.format(hrg);
                TxtLblPrice.setText("Total Harga : Rp. " + frompcs);

            }
        });

        InputCRT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int pcs = 0;
                if (InputPCS.getText().toString().length() > 0) {
                    pcs = Integer.parseInt(InputPCS.getText().toString());
                }

                int crt = 0;
                if (InputCRT.getText().toString().length() > 0) {
                    crt = Integer.parseInt(InputCRT.getText().toString());
                }

                float hrg = ((pcs) * (Float.parseFloat(adapter.getItem(Pos).getHarga()))) + ((crt) * (Float.parseFloat(adapter.getItem(Pos).getHarga()) * (Integer.parseInt(CRT))));

                String fromcrt = formatter.format(hrg);
                TxtLblPrice.setText("Total Harga : Rp. " + fromcrt);
            }
        });

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
                adapter.getItem(Pos).setTimeStamp(getTimeStamp());
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

                if((adapter.getTotalRp()+SaldoAktif)>SaldoLimit){
                    TxtRp.setTextColor(Color.RED);
                }else{
                    TxtRp.setTextColor(Color.BLACK);
                }
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
                dborder.updateKunjunganLongLat(getToday(),getPref(TAG_LASTLOGIN).substring(0, 2)+"/"+getPref(TAG_LASTLOGIN).substring(2, 4)+"/"+getPref(TAG_LASTLOGIN).substring(4),ShipTo,getPref(TAG_LONGITUDE),getPref(TAG_LATITUDE),getPref(TAG_GPSTIME),getPref(TAG_REVERSEGEOCODE));
                if(ModeOrder==0){
                    dborder.updateKunjungan(getToday(),getPref(TAG_LASTLOGIN).substring(0, 2)+"/"+getPref(TAG_LASTLOGIN).substring(2, 4)+"/"+getPref(TAG_LASTLOGIN).substring(4),ShipTo,"1","1","0","");
                    kode=getPref(TAG_LASTLOGIN)+"/"+dborder.getDateTime("yyMMdd")+"/"+String.format("%03d",Integer.parseInt(dborder.getmaxkode(getPref(TAG_LASTLOGIN)+"/" + dborder.getDateTime("yyMMdd")))+1);
                }else{
                    kode=KodeOrder;
                    dborder.deleteOrder(KodeOrder);
                }
                adapter.filter("","","","ORDER");
                for (int i=0;i<adapter.getCount();i++){
                    dborder.insertOrder(kode,getPref(TAG_LASTLOGIN).substring(0, 2)+"/"+getPref(TAG_LASTLOGIN).substring(2, 4)+"/"+getPref(TAG_LASTLOGIN).substring(4),getPref(TAG_NAMELOGIN),ShipTo,adapter.getItem(i).getKode(),Integer.parseInt(adapter.getItem(i).getJmlCRT()),Integer.parseInt(adapter.getItem(i).getJmlPCS()),((Integer.parseInt(adapter.getItem(i).getJmlPCS())*Float.parseFloat(adapter.getItem(i).getHarga()))+((Float.parseFloat(adapter.getItem(i).getHarga()))*(Integer.parseInt(adapter.getItem(i).getJmlCRT()))*(Integer.parseInt(adapter.getItem(i).getCRT())))),InputKet.getText().toString(), EntryTime,getPref(TAG_LONGITUDE),getPref(TAG_LATITUDE),adapter.getItem(i).getTimeStamp());
                }
                Toast.makeText(getApplicationContext(),"Order "+Perusahaan+" Berhasil Di Simpan",Toast.LENGTH_SHORT).show();
                if(ModeOrder==0){
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

        builder.show();
    }

    public void ShowKonfirmasiProses() {
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
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        final TextView TxtLblLimit = new TextView(this);
        TxtLblLimit.setText("");
        TxtLblLimit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        TxtLblLimit.setTextColor(Color.RED);

        final TextView TxtLblLongitude = new TextView(this);
        TxtLblLongitude.setText("Long. : "+getPref(TAG_LONGITUDE)+", Lat. : "+getPref(TAG_LATITUDE));
        TxtLblLongitude.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        TxtLblLongitude.setTextColor(Color.BLACK);


        DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);

        if((adapter.getTotalRp() + SaldoAktif) > SaldoLimit){
            TxtLblLimit.setText("Jumlah orderan melebihi limit : Rp. " + formatter.format(SaldoLimit) + " - Saldo Aktif : " + formatter.format(SaldoAktif)) ;
        }

        layout.addView(TxtLblNama, params);
        layout.addView(TxtLblLongitude, params);
        layout.addView(TxtLblLimit, params);


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
                                //Edit This
                                if(ModeOrder==0){
                                    //Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
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

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public String getTimeStamp(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
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