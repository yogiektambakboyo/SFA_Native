package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Activity_Penagihan extends Activity {
    ProgressBar PbLoading;
    ListView list;
    EditText InputSearch;
    ImageView ImgUpload;

    String Web;
    public static String LOCALPOINT = "";
    private final String TAG_WEB = "web";

    ArrayList<Data_NotaPenagihan> NotaPenagihan = new ArrayList<Data_NotaPenagihan>();
    ArrayList<Data_Penagihan> Penagihan = new ArrayList<Data_Penagihan>();
    ArrayList<Data_ReadyUpload> ReadyUpload = new ArrayList<Data_ReadyUpload>();
    JSONArray jPenagihan,jReadyUpload;
    AdapterPenagihan adapter;

    private FN_DBHandler dborder;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String DB_ORDER="ORDER_";
    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_PENAGIHAN = "penagihan";
    private final String TAG_KODENOTA = "kodenota";
    private final String TAG_TGL = "tgl";
    private final String TAG_COLLECTOR = "collector";
    private String DB_PATH_CSV_SUCCESS=Environment.getExternalStorageDirectory()+"/SFA/CSV/SUCCESS";
    private String FilePathZip= Environment.getExternalStorageDirectory()+"/foto.zip";
    private String FilePath= Environment.getExternalStorageDirectory()+"/foto.zip";


    private String upLoadServerUri = "http://192.168.31.10:9020/ws/uploadfile.php";
    private int serverResponseCode = 0;
    private String StatusRequest="0";
    JSONArray UploadInfoArray = null;

    private String filename="";

    String[] Kodenota;
    String[] Tgl;
    String[] Collector;
    String[] Hito;
    String[] KdNota;
    String[] Jml;
    String[] JmlH;
    String[] Hit;

    JSONObject oPenagihan = null,oReadyUpload = null;

    ProgressDialog dialogs;
    String KD = "",TAGIH="";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_penagihan);
        Web = getPref(TAG_WEB);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent in = getIntent();
        TAGIH = in.getStringExtra("TAG_TAGIH");

        LOCALPOINT = "http://"+Web+"/pengaturan";
        upLoadServerUri = "http://"+Web+"/uploadfile.php";

        // Get Data Fron DB
        dborder = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_ORDER+getPref(TAG_LASTLOGIN));
        File dbFile = new File(DB_PATH+"/"+DB_ORDER+getPref(TAG_LASTLOGIN));

        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.sfa_menu);

        PbLoading = (ProgressBar) findViewById(R.id.Penagihan_Loading);
        PbLoading.setVisibility(View.GONE);

        if (dborder.cekExistTable("Penagihan")<=0){
            dborder.createPenagihan();
        }

        if (dborder.cekExistTable("HPenagihan")<=0){
            dborder.createHPenagihan();
        }

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
// repeat many times:
        ImageView itemIcon = new ImageView(this);
        itemIcon.setImageResource(R.drawable.sfa_add);
        SubActionButton AddPenagihan = itemBuilder.setContentView(itemIcon).build();

        ImageView itemIconII = new ImageView(this);
        itemIconII.setImageResource(R.drawable.sfa_qcash);
        SubActionButton QuickPayment = itemBuilder.setContentView(itemIconII).build();

        final  FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();


        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(AddPenagihan)
                .addSubActionView(QuickPayment)
                .attachTo(actionButton)
                .build();

        if (TAGIH.equals("0")){
            actionButton.setVisibility(View.GONE);
        }
        AddPenagihan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogInputNP();
            }
        });

        QuickPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapter.getCount()>0){
                    Toast.makeText(Activity_Penagihan.this, "Quick Payment", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(Activity_Penagihan.this,ActivityQPPelanggan.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                }else{
                    Toast.makeText(Activity_Penagihan.this, "Tambahkan DV/NP dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if(dbFile.exists()){
            try {
                if (TAGIH.equals("0")){
                    oPenagihan = dborder.getPenagihanUpload();
                }else{
                    oPenagihan = dborder.getPenagihan();
                }
                // Getting Array of Pelanggan
                jPenagihan = oPenagihan.getJSONArray(TAG_PENAGIHAN);

                Kodenota = new String[jPenagihan.length()];
                Tgl = new String[jPenagihan.length()];
                Collector = new String[jPenagihan.length()];
                Hito = new String[jPenagihan.length()];

                // looping through All Pelanggan
                for(int i = 0; i < jPenagihan.length(); i++){
                    JSONObject c = jPenagihan.getJSONObject(i);

                    Kodenota[i] = c.getString(TAG_KODENOTA);
                    Tgl[i] = c.getString(TAG_TGL);
                    Collector[i] = c.getString(TAG_COLLECTOR);
                    if (c.getString("jml").equals(c.getString("jmlh"))){
                        if (c.getString("sync").equals("0")){
                            Hito[i] = "2";
                        }else{
                            Hito[i] = "1";
                        }
                    }else{
                        Hito[i] = "0";
                    }
                }

                for (int i = 0; i < Kodenota.length; i++)
                {
                    if (!Hito[i].equals("2")){
                        Data_Penagihan p = new Data_Penagihan(Kodenota[i],Tgl[i],Collector[i],Hito[i]);
                        Penagihan.add(p);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            dborder.close();
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }

        list = (ListView) findViewById(R.id.PenagihanListView);

        adapter = new AdapterPenagihan(getApplicationContext(), Penagihan);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (TAGIH.equals("0")) {
                    ShowDialogKonfirmUploadDetail(adapter.getItem(position).getKodenota(), adapter.getItem(position).getCollector());
                } else {
                    if (dborder.cekHPenagihanUdahUpload(adapter.getItem(position).getKodenota()) > 0) {
                        if (dborder.cekHPenagihanUdahUploadBI(adapter.getItem(position).getKodenota()) > 0) {
                            ShowDialogKonfirmProses(adapter.getItem(position).getKodenota(), adapter.getItem(position).getCollector(), adapter.getItem(position).getTgl());
                        }else{
                            Toast.makeText(Activity_Penagihan.this, "NP : " + adapter.getItem(position).getKodenota() + " hanya bisa diupload ulang!!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        ShowDialogKonfirmProses(adapter.getItem(position).getKodenota(), adapter.getItem(position).getCollector(), adapter.getItem(position).getTgl());
                    }
                }
            }
        });

        InputSearch = (EditText) findViewById(R.id.PenagihanSearch);
        InputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(InputSearch.getText().toString());
                adapter.notifyDataSetChanged();
            }
        });

        ImgUpload = (ImageView) findViewById(R.id.Penagihan_Upload);
        if (TAGIH.equals("0")){
            ImgUpload.setVisibility(View.GONE);
        }
        ImgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KD = "";
                YoYo.with(Techniques.Tada).duration(700).playOn(ImgUpload);
                ReadyUpload.clear();
                try {
                    oReadyUpload = dborder.getNPReadyToUpload();
                    jReadyUpload = oReadyUpload.getJSONArray(TAG_PENAGIHAN);

                    KdNota = new String[jReadyUpload.length()];
                    Jml = new String[jReadyUpload.length()];
                    JmlH = new String[jReadyUpload.length()];
                    Hit = new String[jReadyUpload.length()];

                    for (int i = 0; i < jReadyUpload.length(); i++) {
                        JSONObject c = jReadyUpload.getJSONObject(i);

                        KdNota[i] = c.getString(TAG_KODENOTA);
                        Jml[i] = c.getString("jml");
                        JmlH[i] = c.getString("jmlh");
                        /*if (Jml[i].equals(JmlH[i])) {
                            Hit[i] = "1";
                        } else {
                            Hit[i] = "0";
                        }*/
                        // change this
                        if (Integer.parseInt(JmlH[i])>0) {
                            Hit[i] = "1";
                        } else {
                            Hit[i] = "0";
                        }
                    }

                    for (int i = 0; i < KdNota.length; i++) {
                        Data_ReadyUpload p = new Data_ReadyUpload(KdNota[i], Jml[i], JmlH[i], Hit[i]);
                        ReadyUpload.add(p);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Activity_Penagihan.this, "Gagal dapat data ready upload " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                int co = 0;
                for (int z = 0; z < ReadyUpload.size(); z++) {
                    if (ReadyUpload.get(z).getHit().equals("1")) {
                        co++;
                    }
                }

                if ((ReadyUpload.size() > 0) && (co > 0)) {
                    ShowDialogKonfirmUpload();
                } else {
                    Toast.makeText(Activity_Penagihan.this, "Tidak ada NP yang bisa diupload!!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void ShowDialogInputNP() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Nota Penagihan");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Masukkan No Nota Pangihan : ");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        final EditText InputNoNP = new EditText(this);
        InputNoNP.setHint("Masukkan No Nota");
        InputNoNP.setInputType(InputType.TYPE_CLASS_TEXT);
        InputNoNP.setSingleLine();
        //InputNoNP.setKeyListener(DigitsKeyListener.getInstance("0123456789/QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm"));

        layout.addView(TxtLblNama, params);
        layout.addView(InputNoNP, params);


        builder.setView(layout);

        builder.setPositiveButton("Cari", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (InputNoNP.getText().toString().trim().length() > 1) {
                    if (adapter.search(InputNoNP.getText().toString().trim()) == 0) {
                        if((InputNoNP.getText().toString().trim().toUpperCase()).substring(0,5).equals(getPref(TAG_LASTLOGIN).substring(0, 2) + "/" + getPref(TAG_LASTLOGIN).substring(2, 4))){
                            getNP(InputNoNP.getText().toString().trim().toUpperCase());
                        }else{
                            Toast.makeText(Activity_Penagihan.this, "Pilih NP/DV divisi anda!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Activity_Penagihan.this, "Kode NP : " + InputNoNP.getText().toString().trim().toUpperCase() + " sudah ada!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Activity_Penagihan.this, "Isi dahulu No Nota Penagihan!!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void ShowDialogKonfirmNP(final String NP,String Collectors) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Nota Penagihan");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin akan menambahkan NP : " + NP + " dengan Collector " + Collectors + " ?");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        layout.addView(TxtLblNama, params);
        builder.setView(layout);

        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i=0;i<NotaPenagihan.size();i++){
                    dborder.insertPenagihan(NotaPenagihan.get(i).getTgl(),NotaPenagihan.get(i).getKodenota(),NotaPenagihan.get(i).getFaktur(),NotaPenagihan.get(i).getCollector(),NotaPenagihan.get(i).getShipTo(),NotaPenagihan.get(i).getPerusahaan(),NotaPenagihan.get(i).getAlamat(),NotaPenagihan.get(i).getBrg(),NotaPenagihan.get(i).getHint(),NotaPenagihan.get(i).getKeterangan(),Integer.parseInt(NotaPenagihan.get(i).getJml()),NotaPenagihan.get(i).getJmlCRT(),Float.parseFloat(NotaPenagihan.get(i).getHrgSatuan()),Float.parseFloat(NotaPenagihan.get(i).getDiscRp()),Float.parseFloat(NotaPenagihan.get(i).getTotalBayar()),Integer.parseInt(NotaPenagihan.get(i).getRasioMax()),NotaPenagihan.get(i).getNamaCollector(),NotaPenagihan.get(i).getOverDue());
                }

                Penagihan.clear();

                try {
                    if (TAGIH.equals("0")){
                        oPenagihan = dborder.getPenagihanUpload();
                    }else{
                        oPenagihan = dborder.getPenagihan();
                    }
                    jPenagihan = oPenagihan.getJSONArray(TAG_PENAGIHAN);

                    Kodenota = new String[jPenagihan.length()];
                    Tgl = new String[jPenagihan.length()];
                    Collector = new String[jPenagihan.length()];
                    Hito = new String[jPenagihan.length()];


                    for(int i = 0; i < jPenagihan.length(); i++){
                        JSONObject c = jPenagihan.getJSONObject(i);

                        Kodenota[i] = c.getString(TAG_KODENOTA);
                        Tgl[i] = c.getString(TAG_TGL);
                        Collector[i] = c.getString(TAG_COLLECTOR);
                        if (c.getString("jml").equals(c.getString("jmlh"))){
                            if (c.getString("sync").equals("0")){
                                Hito[i] = "2";
                            }else{
                                Hito[i] = "1";
                            }
                        }else{
                            Hito[i] = "0";
                        }
                    }

                    for (int i = 0; i < Kodenota.length; i++)
                    {
                        if (!Hito[i].equals("2")){
                            Data_Penagihan p = new Data_Penagihan(Kodenota[i],Tgl[i],Collector[i],Hito[i]);
                            Penagihan.add(p);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter = new AdapterPenagihan(getApplicationContext(), Penagihan);
                list.setAdapter(adapter);
                Toast.makeText(Activity_Penagihan.this, "Kode NP : "+NP+" Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void getNP(final String NP){
        PbLoading.setVisibility(View.VISIBLE);
        final RestAdapter adapter =new RestAdapter.Builder()
                .setEndpoint(LOCALPOINT)
                .build();

        API_SFA api = adapter.create(API_SFA.class);
        NotaPenagihan.clear();

        api.postCariNP(getPref("cabang"),NP,getPref(TAG_LASTLOGIN).substring(0, 2)+"/"+getPref(TAG_LASTLOGIN).substring(2, 4), new Callback<List<Data_NotaPenagihan>>() {
            @Override
            public void success(List<Data_NotaPenagihan> data_notaPenagihans, Response response) {
                if (data_notaPenagihans.size()>0){
                    String col = "";
                    for (int i=0;i<data_notaPenagihans.size();i++){
                        col = data_notaPenagihans.get(i).getNamaCollector();
                        Data_NotaPenagihan d = new Data_NotaPenagihan(data_notaPenagihans.get(i).getTgl(),data_notaPenagihans.get(i).getKodenota(),data_notaPenagihans.get(i).getFaktur(),data_notaPenagihans.get(i).getCollector(),data_notaPenagihans.get(i).getShipTo(),data_notaPenagihans.get(i).getPerusahaan(),data_notaPenagihans.get(i).getAlamat(),data_notaPenagihans.get(i).getBrg(),data_notaPenagihans.get(i).getHint(),data_notaPenagihans.get(i).getKeterangan(),data_notaPenagihans.get(i).getJml(),data_notaPenagihans.get(i).getJmlCRT(),data_notaPenagihans.get(i).getHrgSatuan(),data_notaPenagihans.get(i).getDiscRp(),data_notaPenagihans.get(i).getTotalBayar(),data_notaPenagihans.get(i).getRasioMax(),data_notaPenagihans.get(i).getNamaCollector(),data_notaPenagihans.get(i).getOverDue());
                        NotaPenagihan.add(d);
                    }
                    ShowDialogKonfirmNP(NP,col);
                }else{
                    Toast.makeText(Activity_Penagihan.this, "Kode NP yang dimasukkan salah!!!", Toast.LENGTH_SHORT).show();
                }

                PbLoading.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(Activity_Penagihan.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
                PbLoading.setVisibility(View.GONE);
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

    public void ShowDialogKonfirmProses(final String NP,final String Collectors,final String Tgl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Nota Penagihan");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin akan memproses NP : "+NP+" dengan Collector "+Collectors+" ?");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        layout.addView(TxtLblNama, params);
        builder.setView(layout);

        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in = new Intent(Activity_Penagihan.this,ActivityPFaktur.class);
                in.putExtra("Kodenota",NP);
                in.putExtra("Tgl",Tgl);
                in.putExtra("TAG_TAGIH","1");
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShowDialogKonfirmDelete(NP);
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void ShowDialogKonfirmDelete(final String NP) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Hapus NP");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin akan menghapus NP : "+NP+"?");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        layout.addView(TxtLblNama, params);
        builder.setView(layout);

        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dborder.deletePenagihan(NP);
                dborder.DeleteHPenagihanPerNP(NP);
                Penagihan.clear();

                try {
                    if (TAGIH.equals("0")){
                        oPenagihan = dborder.getPenagihanUpload();
                    }else{
                        oPenagihan = dborder.getPenagihan();
                    }
                    jPenagihan = oPenagihan.getJSONArray(TAG_PENAGIHAN);

                    Kodenota = new String[jPenagihan.length()];
                    Tgl = new String[jPenagihan.length()];
                    Collector = new String[jPenagihan.length()];
                    Hito = new String[jPenagihan.length()];

                    for(int i = 0; i < jPenagihan.length(); i++){
                        JSONObject c = jPenagihan.getJSONObject(i);

                        Kodenota[i] = c.getString(TAG_KODENOTA);
                        Tgl[i] = c.getString(TAG_TGL);
                        Collector[i] = c.getString(TAG_COLLECTOR);
                        if (c.getString("jml").equals(c.getString("jmlh"))){
                            if (c.getString("sync").equals("0")){
                                Hito[i] = "2";
                            }else{
                                Hito[i] = "1";
                            }
                        }else{
                            Hito[i] = "0";
                        }
                    }

                    for (int i = 0; i < Kodenota.length; i++)
                    {
                        if (!Hito[i].equals("2")){
                            Data_Penagihan p = new Data_Penagihan(Kodenota[i],Tgl[i],Collector[i],Hito[i]);
                            Penagihan.add(p);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter = new AdapterPenagihan(getApplicationContext(), Penagihan);
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Toast.makeText(Activity_Penagihan.this, "Berhasil menghapus NP : "+NP, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public boolean generateCSV(String lastlogin,String Rekap){
        File SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        if(!SFAdircsv.exists()){
            SFAdircsv.mkdirs();
        }

        //--------------------delete file 1 minggu
        SFAdircsv = new File(DB_PATH_CSV_SUCCESS);
        for (File f : SFAdircsv.listFiles()) {
            if (f.isFile()){
                if(f.getName().toString().contains(getDateTime("ddMMyyyy",-7))){
                    f.delete();
                }
            }
        }
        filename=lastlogin+"_"+getDateTime("ddMMyyyy_HHmm",0)+"_NP.csv";

        //---------------------create file-----------------------------------
        try {
            Cursor cursor= dborder.getAllRawPenagihan(Rekap);

            FileWriter fw = new FileWriter(DB_PATH_CSV_SUCCESS+"/"+filename);
            fw.append("Kodenota;");
            fw.append("Faktur;");
            fw.append("Tgl;");
            fw.append("Collector;");
            fw.append("Tunai;");
            fw.append("CekBG;");
            fw.append("Stempel;");
            fw.append("TT;");
            fw.append("UnCall;");
            fw.append("Keterangan;");
            fw.append("Shipto;");
            fw.append("TransferBank;");
            fw.append("TransferJml;");
            fw.append("TransferTgl;");
            fw.append("StartEntry;");
            fw.append("CreateBy");
            fw.append('\n');

            if (cursor.moveToFirst()) {
                do {
                    fw.append(cursor.getString(cursor.getColumnIndex("kodenota"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("faktur"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("tgl"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("collector"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("tunai"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("bg"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("stempel"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("tt"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("uc"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("keterangan"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("shipto"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("transferbank"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("transferjml"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("transfertgl"))+";");
                    fw.append(cursor.getString(cursor.getColumnIndex("startentry"))+";");
                    fw.append(getPref(TAG_LASTLOGIN).substring(0, 2)+"/"+getPref(TAG_LASTLOGIN).substring(2, 4)+"/"+getPref(TAG_LASTLOGIN).substring(4)+"\n");
                } while (cursor.moveToNext());
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            fw.close();
            //db.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Generate CSV Gagal!!",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getDateTime(String format,int hari) {
        Calendar calendar = Calendar.getInstance();
        Date myDate = new Date();
        calendar.setTime(myDate);
        calendar.add(Calendar.DAY_OF_YEAR, hari);
        myDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, Locale.getDefault());
        return dateFormat.format(myDate);
    }

    public void packZip(String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[1024 * 1024];

            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, 1024 * 1024);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, 1024 * 1024)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int uploadFileZip(String sourceFileUri,final String filezip,final String filename) {

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 50 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialogs.dismiss();
            Log.e("uploadFile", "Source File not exist :" + filezip);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(Activity_Penagihan.this, "File Tidak Ditemukan " + filezip, Toast.LENGTH_SHORT).show();
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String urls = "http://"+Web+"/pengaturan/insertzipfilepenagihan.php?codename=1988&filezip="+filezip+"&filename="+filename;
                            final FN_JSONParser jParser = new FN_JSONParser();

                            try {
                                JSONObject json = jParser.getJSONFromUrl(urls);
                                StatusRequest = json.getString("STATUS");

                                if(StatusRequest.equals("1")){
                                    UploadInfoArray = json.getJSONArray("uploaddata");

                                    String Stat = "0";

                                    for (int i=0;i<UploadInfoArray.length();i++){
                                        JSONObject a = UploadInfoArray.getJSONObject(i);
                                        Stat = a.getString("status");
                                    }

                                    if(Stat.equals("1")){
                                        dborder.updateFlagPenagihan(KD);
                                        Toast.makeText(Activity_Penagihan.this, "Unggah Data Zip Berhasil.", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(Activity_Penagihan.this, "Unggah Data Zip Gagal 3 : Zip."+" - "+UploadInfoArray.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(Activity_Penagihan.this, "Unggah Data Gagal 2 : Zip."+" - "+UploadInfoArray.toString(), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(Activity_Penagihan.this, "Unggah Data Gagal. 1 : Zip", Toast.LENGTH_SHORT).show();
                                Log.e("123",e.getMessage());
                            }

                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialogs.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Activity_Penagihan.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialogs.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Activity_Penagihan.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server", "Exception : "  + e.getMessage(), e);
            }
            dialogs.dismiss();
            return serverResponseCode;

        } // End else block
    }

    public void ShowDialogKonfirmUpload() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Upload NP");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin akan mengupload semua NP yang sudah terproses?");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        layout.addView(TxtLblNama, params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int count = 0;
                for (int l=0;l<ReadyUpload.size();l++){
                    if (ReadyUpload.get(l).getHit().equals("1")){
                        KD = KD + ReadyUpload.get(l).getKodenota() + "#";
                        count++;
                        dborder.InsertHPenagihanUncall(ReadyUpload.get(l).getKodenota());
                    }
                }

                if (count>0){
                    generateCSV(getPref(TAG_LASTLOGIN),KD);
                    FilePath = DB_PATH_CSV_SUCCESS+"/"+filename;
                    FilePathZip = DB_PATH_CSV_SUCCESS+"/"+"ORDER_"+getPref(TAG_LASTLOGIN)+".zip";


                    String[] s = new String[1];
                    s[0] = FilePath;

                    packZip(s,FilePathZip);
                    dialogs = ProgressDialog.show(Activity_Penagihan.this, "", "Uploading file Zip...", true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadFileZip(FilePathZip,"ORDER_"+getPref(TAG_LASTLOGIN)+".zip",filename);
                        }
                    }).start();
                }else{
                    Toast.makeText(Activity_Penagihan.this, "Tidak ada NP yang bisa diupload!!!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
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

    public void ShowDialogKonfirmUploadDetail(final String NP, final String Collectors) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Nota Penagihan Sukses");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin akan memproses NP : "+NP+" dengan Collector "+Collectors+" ?");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        layout.addView(TxtLblNama, params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Detail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in = new Intent(Activity_Penagihan.this,ActivityPFaktur.class);
                in.putExtra("Kodenota", NP);
                in.putExtra("TAG_TAGIH","0");
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
                dialog.dismiss();
            }
        });

        // Set up the buttons
        builder.setNegativeButton("Buka Flag", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShowDialogKonfirmBukaFlag(NP,Collectors);
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void ShowDialogKonfirmBukaFlag(final String NP,String Collectors) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Nota Penagihan Sukses");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin akan membuka flag NP : "+NP+" dengan Collector "+Collectors+" ?");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        layout.addView(TxtLblNama, params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dborder.UpdateOpenHPenagihanUpload(NP);
                Penagihan.clear();
                try {
                    oPenagihan = dborder.getPenagihanUpload();

                    // Getting Array of Pelanggan
                    jPenagihan = oPenagihan.getJSONArray(TAG_PENAGIHAN);

                    Kodenota = new String[jPenagihan.length()];
                    Tgl = new String[jPenagihan.length()];
                    Collector = new String[jPenagihan.length()];
                    Hito = new String[jPenagihan.length()];

                    // looping through All Pelanggan
                    for(int i = 0; i < jPenagihan.length(); i++){
                        JSONObject c = jPenagihan.getJSONObject(i);

                        Kodenota[i] = c.getString(TAG_KODENOTA);
                        Tgl[i] = c.getString(TAG_TGL);
                        Collector[i] = c.getString(TAG_COLLECTOR);
                        if (c.getString("jml").equals(c.getString("jmlh"))){
                            if (c.getString("sync").equals("0")){
                                Hito[i] = "2";
                            }else{
                                Hito[i] = "1";
                            }
                        }else{
                            Hito[i] = "0";
                        }
                    }

                    for (int i = 0; i < Kodenota.length; i++)
                    {
                        if (!Hito[i].equals("2")){
                            Data_Penagihan p = new Data_Penagihan(Kodenota[i],Tgl[i],Collector[i],Hito[i]);
                            Penagihan.add(p);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter = new AdapterPenagihan(getApplicationContext(), Penagihan);
                list.setAdapter(adapter);
                Toast.makeText(Activity_Penagihan.this, "Buka Flag Nota : "+NP+" Berhasil", Toast.LENGTH_SHORT).show();
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
}
