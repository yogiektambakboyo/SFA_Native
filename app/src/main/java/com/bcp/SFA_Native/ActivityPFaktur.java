package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class ActivityPFaktur extends Activity {
    ProgressBar PbLoading;
    ListView list;
    EditText InputSearch;
    TextView TxtNP;

    private FN_DBHandler dborder;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String DB_ORDER="ORDER_";
    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_PENAGIHAN = "penagihan";

    ArrayList<Data_Faktur> ListFaktur = new ArrayList<Data_Faktur>();

    String[] Kodenota,Faktur,TotalBayar,Alamat,Shipto,Perusahaan,Collector,Hit;
    String InKodenota="",InTgl;

    JSONObject oFaktur = null,oPembayaran = null;
    JSONArray jFaktur,pFaktur;

    AdapterPenagihanFaktur adapter;

    String[] AlasanArray,Filter;
    String tagih="1",alasan = "",startEntry="",tunai="",bg="",stempel="",tt="",uc="",transferbank="",transferjml="";

    Spinner SpnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_pfaktur);

        startEntry = getDateTime();
        AlasanArray = new String[10];
        AlasanArray[0] = "Toko Tutup";
        AlasanArray[1] = "Truck Tidak Cukup";
        AlasanArray[2] = "Waktu Kirim Tidak Cukup";
        AlasanArray[3] = "Permintaan Toko / SR";
        AlasanArray[4] = "Menunggu Truck Yg Ke Depo Penuh";
        AlasanArray[5] = "Ekspedisi Pihak Ke-3 Terlambat";
        AlasanArray[6] = "Warehouse Salah Ambil Barang";
        AlasanArray[7] = "Tidak Sesuai FJP Delivery";
        AlasanArray[8] = "Toko Minta Pending (Stok Opname/Gudang Penuh)";
        AlasanArray[9] = "Delivery brkt kesiangan ( >08:30 )";

        Intent in = getIntent();
        InKodenota = in.getStringExtra("Kodenota");
        InTgl= in.getStringExtra("Tgl");
        tagih = in.getStringExtra("TAG_TAGIH");

        TxtNP = (TextView) findViewById(R.id.PenagihanFaktur_NP);
        TxtNP.setText(""+InKodenota);

        PbLoading = (ProgressBar) findViewById(R.id.PenagihanFaktur_Loading);
        PbLoading.setVisibility(View.GONE);

        dborder = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_ORDER+getPref(TAG_LASTLOGIN));
        File dbFile = new File(DB_PATH+"/"+DB_ORDER+getPref(TAG_LASTLOGIN));

        ListFaktur.clear();

        if(dbFile.exists()){
            try {
                oFaktur = dborder.getFaktur(InKodenota);
                // Getting Array of Pelanggan
                jFaktur = oFaktur.getJSONArray(TAG_PENAGIHAN);

                Kodenota = new String[jFaktur.length()];
                TotalBayar = new String[jFaktur.length()];
                Alamat = new String[jFaktur.length()];
                Faktur = new String[jFaktur.length()];
                Shipto = new String[jFaktur.length()];
                Perusahaan = new String[jFaktur.length()];
                Collector = new String[jFaktur.length()];
                Hit = new String[jFaktur.length()];

                // looping through All Pelanggan
                for(int i = 0; i < jFaktur.length(); i++){
                    JSONObject c = jFaktur.getJSONObject(i);

                    Kodenota[i] = c.getString("kodenota");
                    TotalBayar[i] = c.getString("totalbayar");
                    Alamat[i] = c.getString("alamat");
                    Faktur[i] = c.getString("faktur");
                    Shipto[i] = c.getString("shipto");
                    Perusahaan[i] = c.getString("perusahaan");
                    Collector[i] = c.getString("collector");
                    Hit[i] = c.getString("hit");
                }


                for (int j = 0; j < Kodenota.length; j++)
                {
                    Data_Faktur p = new Data_Faktur(Kodenota[j],Faktur[j],Shipto[j],Perusahaan[j],Alamat[j],TotalBayar[j],Collector[j],Hit[j]);
                    ListFaktur.add(p);
                }

            } catch (JSONException e) {
                Toast.makeText(ActivityPFaktur.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            dborder.close();
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }
        list = (ListView) findViewById(R.id.PenagihanFaktur_ListView);
        adapter = new AdapterPenagihanFaktur(ActivityPFaktur.this,ListFaktur);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItem(position).getHit().toString().equals("0")) {
                    ShowDialogKonfirmProses(adapter.getItem(position).getKodenota(), adapter.getItem(position).getFaktur(), adapter.getItem(position).getPerusahaan(), adapter.getItem(position).getTotalbayar(), adapter.getItem(position).getShipto(), adapter.getItem(position).getCollector(),InTgl);
                } else {

                        try {
                            oPembayaran = dborder.getPembayaranFaktur(adapter.getItem(position).getKodenota(),adapter.getItem(position).getFaktur());
                            pFaktur = oPembayaran.getJSONArray("penagihan");

                            for (int k=0;k<pFaktur.length();k++){
                                JSONObject d = pFaktur.getJSONObject(k);
                                tunai = d.getString("tunai");
                                bg = d.getString("bg");
                                tt = d.getString("tt");
                                stempel = d.getString("stempel");
                                uc = d.getString("uc");
                                transferbank = d.getString("transferbank");
                                transferjml = d.getString("transferjml");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ShowDialogKonfirmProsesDataExist(adapter.getItem(position).getKodenota(), adapter.getItem(position).getFaktur(), adapter.getItem(position).getPerusahaan(),tunai,bg,tt,stempel,uc,transferbank,transferjml,adapter.getItem(position).getTotalbayar(),adapter.getItem(position).getShipto(),adapter.getItem(position).getCollector());

                }
            }
        });

        InputSearch = (EditText) findViewById(R.id.PenagihanFakturSearch);


        Filter = new String[3];
        Filter[0] = "Semua";
        Filter[1] = "Terproses";
        Filter[2] = "Belum";

        SpnFilter = (Spinner) findViewById(R.id.PenagihanFaktur_SpnFilter);
        ArrayAdapter a = new ArrayAdapter(ActivityPFaktur.this,android.R.layout.simple_list_item_1,Filter);
        a.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        SpnFilter.setAdapter(a);

        SpnFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String a = "semua";
                if (i == 0) {
                    a = "semua";
                } else if (i == 2) {
                    a = "0";
                } else {
                    a = "1";
                }
                adapter.filter(InputSearch.getText().toString(), a);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (tagih.equals("0")){
            SpnFilter.setVisibility(View.GONE);
        }

        InputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String a = "semua";
                if (SpnFilter.getSelectedItemPosition() == 0) {
                    a = "semua";
                } else if (SpnFilter.getSelectedItemPosition() == 2) {
                    a = "0";
                } else {
                    a = "1";
                }
                adapter.filter(InputSearch.getText().toString(), a );
            }
        });

    }

    public void ShowDialogKonfirmProses(final String NP,final String faktur, final String Perusahaan, final String TotalBayar,final String Shipto,final String Collector,final String Tgl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Faktur");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin akan memproses Faktur : "+faktur+" dengan Perusahaan "+Perusahaan+" ?");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        layout.addView(TxtLblNama, params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in = new Intent(ActivityPFaktur.this,ActivityPembayaran.class);
                in.putExtra("Kodenota",NP);
                in.putExtra("Faktur",faktur);
                in.putExtra("Perusahaan",Perusahaan);
                in.putExtra("TotalBayar",TotalBayar);
                in.putExtra("Shipto",Shipto);
                in.putExtra("Collector",Collector);
                in.putExtra("Edit","0");
                in.putExtra("Tgl",InTgl);
                startActivity(in);
                dialog.dismiss();
            }
        });

        // Set up the buttons
        builder.setNegativeButton("Tidak Terkirim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShowDialogKonfirmTidakTerkirim(NP,faktur,Perusahaan,TotalBayar,Shipto,Collector,Tgl);
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void ShowDialogKonfirmTidakTerkirim(final String NP,final String faktur, final String perusahaan,String totalbayar, final String shipto,final String collector,final String tgl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Faktur");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params2.setMargins(20, 0, 30, 0);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin akan memproses faktur ini menjadi tidak terkirim ?");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        TxtLblNama.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        final TextView TxtLblNP = new TextView(this);
        TxtLblNP.setText("NP : "+NP);
        TxtLblNP.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        final TextView TxtLblFaktur = new TextView(this);
        TxtLblFaktur.setText("Faktur  : "+faktur);
        TxtLblFaktur.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        final TextView TxtLblPerusahaan = new TextView(this);
        TxtLblPerusahaan.setText("Perusahaan  : "+perusahaan);
        TxtLblPerusahaan.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        DecimalFormatSymbols symbol =
                new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        String currency = formatter.format(Double.parseDouble(totalbayar));

        final TextView TxtLblTotalBayar = new TextView(this);
        TxtLblTotalBayar.setText("Total Bayar  : Rp. " + currency);
        TxtLblTotalBayar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        final TextView TxtLblAlasan = new TextView(this);
        TxtLblAlasan.setText("Alasan Tidak Terkirim  : ");
        TxtLblAlasan.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        TxtLblAlasan.setTypeface(Typeface.DEFAULT,Typeface.BOLD);

        final Spinner SpnAlasan = new Spinner(this);

        alasan ="";
        ArrayAdapter adapterSPn = new ArrayAdapter(this,android.R.layout.simple_spinner_item, AlasanArray);
        adapterSPn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnAlasan.setAdapter(adapterSPn);
        SpnAlasan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:{alasan = "C01"; break;}
                    case 1:{alasan = "C02";break;}
                    case 2:{alasan = "C03";break;}
                    case 3:{alasan = "C04";break;}
                    case 4:{alasan = "C05";break;}
                    case 5:{alasan = "C06";break;}
                    case 6:{alasan = "C07";break;}
                    case 7:{alasan = "C08";break;}
                    case 8:{alasan = "C09";break;}
                    case 9:{alasan = "C10";break;}
                    default:break;
                }
                //Toast.makeText(getApplicationContext(),"Position = "+alasan,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        layout.addView(TxtLblNama, params);
        layout.addView(TxtLblNP, params2);
        layout.addView(TxtLblFaktur, params2);
        layout.addView(TxtLblPerusahaan, params2);
        layout.addView(TxtLblTotalBayar, params2);
        layout.addView(TxtLblAlasan, params);
        layout.addView(SpnAlasan, params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ActivityPFaktur.this, "Ubah status faktur "+faktur+" tidak terkirim - Berhasil", Toast.LENGTH_SHORT).show();
                dborder.InsertHPenagihan(NP, faktur, tgl, collector, "0", "0", 0, 0, 1, alasan, shipto, "", "", "", "0", startEntry,"");
                ListFaktur.clear();
                try {
                    oFaktur = dborder.getFaktur(InKodenota);
                    // Getting Array of Pelanggan
                    jFaktur = oFaktur.getJSONArray(TAG_PENAGIHAN);

                    Kodenota = new String[jFaktur.length()];
                    TotalBayar = new String[jFaktur.length()];
                    Alamat = new String[jFaktur.length()];
                    Faktur = new String[jFaktur.length()];
                    Shipto = new String[jFaktur.length()];
                    Perusahaan = new String[jFaktur.length()];
                    Collector = new String[jFaktur.length()];
                    Hit = new String[jFaktur.length()];

                    // looping through All Pelanggan
                    for(int i = 0; i < jFaktur.length(); i++){
                        JSONObject c = jFaktur.getJSONObject(i);

                        Kodenota[i] = c.getString("kodenota");
                        TotalBayar[i] = c.getString("totalbayar");
                        Alamat[i] = c.getString("alamat");
                        Faktur[i] = c.getString("faktur");
                        Shipto[i] = c.getString("shipto");
                        Perusahaan[i] = c.getString("perusahaan");
                        Collector[i] = c.getString("collector");
                        Hit[i] = c.getString("hit");
                    }


                    for (int j = 0; j < Kodenota.length; j++)
                    {
                        Data_Faktur p = new Data_Faktur(Kodenota[j],Faktur[j],Shipto[j],Perusahaan[j],Alamat[j],TotalBayar[j],Collector[j],Hit[j]);
                        ListFaktur.add(p);
                    }

                } catch (JSONException e) {
                    Toast.makeText(ActivityPFaktur.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                adapter = new AdapterPenagihanFaktur(ActivityPFaktur.this,ListFaktur);
                list.setAdapter(adapter);

                String a = "semua";
                if (SpnFilter.getSelectedItemPosition() == 0) {
                    a = "semua";
                } else if (SpnFilter.getSelectedItemPosition() == 2) {
                    a = "0";
                } else {
                    a = "1";
                }
                adapter.filter(InputSearch.getText().toString(), a);
                dialog.dismiss();
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

    public String getDateTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedTime = df.format(c.getTime());
        return  formattedTime;
    }

    public void ShowDialogKonfirmProsesDataExist(final String NP,final String faktur, final String Perusahaan, final String tunai,final String bg,final String tt,final String stempel,final String uc, final String transferbank,final String transferjml,final String totalbayar,final String shipto,final String collector) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Faktur");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        if (tagih.equals("1")){
            TxtLblNama.setText("Apakah anda yakin akan mengedit Faktur : " + faktur + " dengan Perusahaan " + Perusahaan + " ?");
        }else{
            TxtLblNama.setText("Detail pembayaran untuk  Faktur : " + faktur + " dengan Perusahaan " + Perusahaan + "  : ");
        }
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        final TextView TxtPembayaran = new TextView(this);
        TxtPembayaran.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        String a = "";
        if(uc.equals("1")){
            a = "Tidak Terkirim";
        }else{
            if (tt.equals("1")){
                a = "Tanda Terima";
            }else{
                if (stempel.equals("1")){
                    a = "Stempel";
                }else{
                    if (Float.parseFloat(tunai)>0){
                        a = " Tunai : Rp. "+tunai+" - ";
                    }
                    if (Float.parseFloat(bg)>0){
                        a = a + " BG : Rp. "+bg+" - ";
                    }
                    if (Float.parseFloat(transferjml)>0){
                        a = a + " Transfer : Rp. "+transferjml;
                    }
                }
            }
        }


        TxtPembayaran.setText("Pembayaran : "+a);

        layout.addView(TxtLblNama, params);
        layout.addView(TxtPembayaran, params);
        builder.setView(layout);


        if ((dborder.cekFakturUCBI(NP)>0)&&(!uc.equals("1"))){
            Toast.makeText(ActivityPFaktur.this, "Faktur ini tidak bisa diedit karna sudah pernah diupload", Toast.LENGTH_SHORT).show();
        }
        else{
            if (tagih.equals("1")){
                // Set up the buttons
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent in = new Intent(ActivityPFaktur.this,ActivityPembayaran.class);
                        in.putExtra("Kodenota",NP);
                        in.putExtra("Faktur",faktur);
                        in.putExtra("Perusahaan",Perusahaan);
                        in.putExtra("TotalBayar",totalbayar);
                        in.putExtra("Shipto",shipto);
                        in.putExtra("Collector",collector);
                        in.putExtra("Edit","1");
                        in.putExtra("Tgl",InTgl);
                        startActivity(in);
                        dialog.dismiss();
                    }
                });

                // Set up the buttons
                builder.setNegativeButton("Reset Penagihan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowDialogKonfirmProsesReset(NP,faktur,Perusahaan);
                        dialog.dismiss();
                    }
                });
            }
        }


        builder.show();
    }

    public void ShowDialogKonfirmProsesReset(final String NP,final String faktur, final String perusahaan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Reset Faktur");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin akan menghapus data penagihan Faktur : "+faktur+" dengan Perusahaan "+perusahaan+" ?");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        layout.addView(TxtLblNama, params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ActivityPFaktur.this, "Me-reset data penagihan faktur "+faktur+" berhasil", Toast.LENGTH_SHORT).show();
                dborder.DeleteHPenagihan(NP, faktur);
                ListFaktur.clear();
                try {
                    oFaktur = dborder.getFaktur(InKodenota);
                    // Getting Array of Pelanggan
                    jFaktur = oFaktur.getJSONArray(TAG_PENAGIHAN);

                    Kodenota = new String[jFaktur.length()];
                    TotalBayar = new String[jFaktur.length()];
                    Alamat = new String[jFaktur.length()];
                    Faktur = new String[jFaktur.length()];
                    Shipto = new String[jFaktur.length()];
                    Perusahaan = new String[jFaktur.length()];
                    Collector = new String[jFaktur.length()];
                    Hit = new String[jFaktur.length()];

                    // looping through All Pelanggan
                    for(int i = 0; i < jFaktur.length(); i++){
                        JSONObject c = jFaktur.getJSONObject(i);

                        Kodenota[i] = c.getString("kodenota");
                        TotalBayar[i] = c.getString("totalbayar");
                        Alamat[i] = c.getString("alamat");
                        Faktur[i] = c.getString("faktur");
                        Shipto[i] = c.getString("shipto");
                        Perusahaan[i] = c.getString("perusahaan");
                        Collector[i] = c.getString("collector");
                        Hit[i] = c.getString("hit");
                    }


                    for (int j = 0; j < Kodenota.length; j++)
                    {
                        Data_Faktur p = new Data_Faktur(Kodenota[j],Faktur[j],Shipto[j],Perusahaan[j],Alamat[j],TotalBayar[j],Collector[j],Hit[j]);
                        ListFaktur.add(p);
                    }

                } catch (JSONException e) {
                    Toast.makeText(ActivityPFaktur.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                adapter = new AdapterPenagihanFaktur(ActivityPFaktur.this,ListFaktur);
                list.setAdapter(adapter);
                dialog.dismiss();
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
            Intent in = new Intent(getApplicationContext(), Activity_Penagihan.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            in.putExtra("TAG_TAGIH",tagih);
            startActivity(in);
        }
        return false;
    }
}
