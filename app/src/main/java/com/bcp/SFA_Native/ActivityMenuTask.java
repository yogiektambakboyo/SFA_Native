package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

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
import java.util.HashMap;
import java.util.Locale;


public class ActivityMenuTask extends ListActivity {
    private final String TAG_MENU = "menu";
    private final String TAG_ICON = "1";
    private final String TAG_ID = "id";
    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_WEB = "web";
    private final String TAG_SHIPTO = "kode";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_MODEORDER= "modeorder";
    private final String TAG_MODEINV= "modeinv";

    private String      DB_MASTER="MASTER";

    String Web,Shipto,Perusahaan;
    ImageView ImgDetail;

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] flags = new int[]{
            R.drawable.sfa_order,
            R.drawable.sfa_home,
            R.drawable.sfa_inventory,
            R.drawable.sfa_return
    };

    TextView TxtUser,TxtTime,TxtPerusahaan;

    FN_DBHandler dbmst,dborder;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String DB_ORDER="ORDER_";

    JSONArray PelangganArray;
    private final String TAG_PELANGGANDATA= "PelangganData";
    String saldolimit,saldoaktif,segment,alamat;
    String SalesName = "";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_menutask);

        dborder = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_ORDER+getPref(TAG_LASTLOGIN));
        dbmst = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_MASTER);

        TxtUser = (TextView) findViewById(R.id.TaskMenu_TxtUser);
        TxtTime = (TextView) findViewById(R.id.TaskMenu_TxtTime);
        TxtPerusahaan = (TextView) findViewById(R.id.TaskMenu_Perusahaan);
        ImgDetail = (ImageView) findViewById(R.id.Task_MenuDetailPelanggan);

        SalesName = getPref(TAG_NAMELOGIN);

        if ((SalesName.length())>20){
            SalesName = SalesName.substring(0,20);
        }

        TxtUser.setText(SalesName);
        TxtTime.setText("("+getPref(TAG_LASTLOGIN)+")");

        Intent in = getIntent();
        Web = in.getStringExtra(TAG_WEB);
        Shipto = in.getStringExtra(TAG_SHIPTO);
        Perusahaan = in.getStringExtra(TAG_PERUSAHAAN);

        if(Perusahaan.length()>20){
            Perusahaan = Perusahaan.substring(0,19);
        }

        TxtPerusahaan.setText(""+Perusahaan);

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> OperatorMenuList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> maporder = new HashMap<String, String>();
        maporder.put(TAG_ICON, Integer.toString(flags[0]));
        maporder.put(TAG_ID, "0");
        maporder.put(TAG_MENU, "Order");

        HashMap<String, String> mapinventory = new HashMap<String, String>();
        mapinventory.put(TAG_ICON, Integer.toString(flags[2]));
        mapinventory.put(TAG_ID, "1");
        mapinventory.put(TAG_MENU, "Inventory");

        HashMap<String, String> mapretur = new HashMap<String, String>();
        mapretur.put(TAG_ICON, Integer.toString(flags[3]));
        mapretur.put(TAG_ID, "2");
        mapretur.put(TAG_MENU, "Retur");

        HashMap<String, String> mapback = new HashMap<String, String>();
        mapback.put(TAG_ICON, Integer.toString(flags[1]));
        mapback.put(TAG_ID, "3");
        mapback.put(TAG_MENU, "Kembali ke Menu Utama");


        OperatorMenuList.add(maporder);
        OperatorMenuList.add(mapinventory);
        OperatorMenuList.add(mapretur);
        OperatorMenuList.add(mapback);

        /**
         * Updating parsed JSON data into ListView
         * */
        ListAdapter adapter = new AdapterCustomSimple(this, OperatorMenuList,
                R.layout.l_mainmenu,
                new String[] { TAG_ICON, TAG_MENU, TAG_ID },
                new int[] { R.id.imageViewOP, R.id.MainMenuNama, R.id.MainMenuID });

        setListAdapter(adapter);

        // selecting single ListView item
        ListView lv = getListView();

        // Launching new screen on Selecting Single ListItem
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String nama = ((TextView) view.findViewById(R.id.MainMenuNama)).getText().toString();
                String menuid = ((TextView) view.findViewById(R.id.MainMenuID)).getText().toString();

                if(menuid.equals("0")){
                    if (checkMockLocation()){
                        String Msg = "Setting Mock Location aktif, silahkan non aktifkan dahulu!!!";
                        new AlertDialog.Builder(ActivityMenuTask.this)
                                .setTitle("Information")
                                .setMessage(Msg)
                                .setPositiveButton("Setelan", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                    }
                                })
                                .setCancelable(false)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }else{
                        //int f = dborder.getCekExistInventoryPelanggan(Shipto, getToday());
                        //if (f>0){
                            int d = dborder.getCekExistOrderPelanggan(Shipto, getToday());
                            if (d>0){
                                CekExistOrder(Shipto, Perusahaan);
                            }else{
                                Intent ints = new Intent(getApplicationContext(), ActivityOrder.class);
                                ints.putExtra(TAG_SHIPTO, Shipto);
                                ints.putExtra(TAG_PERUSAHAAN, Perusahaan);
                                ints.putExtra(TAG_WEB,Web);
                                startActivity(ints);
                            }
                        //}else{
                            //Toast.makeText(ActivityMenuTask.this, "Lakukan dahulu Inventory (Store Check) sebelum Order!!!", Toast.LENGTH_SHORT).show();
                        //}
                    }
                }

                if(menuid.equals("1")){
                    int d = dborder.getCekExistInventoryPelanggan(Shipto, getToday());
                    if (d>0){
                        int e = dborder.getCekExistInventoryPelangganNotSync(Shipto, getToday());
                        if(e>0){
                            String kodenota = dborder.getKodenotaInventoryPelanggan(Shipto,getToday());
                            CekExistInventory(Shipto, Perusahaan,kodenota);
                        }else{
                            Toast.makeText(getApplicationContext(),"Data inventory untuk pelanggan ini tidak bisa di edit lagi karna sudah diupload",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Intent ints = new Intent(getApplicationContext(), ActivityInventory.class);
                        ints.putExtra(TAG_SHIPTO, Shipto);
                        ints.putExtra(TAG_PERUSAHAAN, Perusahaan);
                        ints.putExtra(TAG_WEB,Web);
                        startActivity(ints);
                    }
                }

                if(menuid.equals("2")){
                    int d = dborder.getCekExistRetur(Shipto, getToday());
                    if (d>0){
                        int e = dborder.getCekExistReturNotSync(Shipto,getToday());
                        if(e>0){
                            String kodenota = dborder.getKodenotaRetur(Shipto,getToday());
                            CekExistRetur(Shipto, Perusahaan,kodenota);
                        }else{
                            Toast.makeText(getApplicationContext(),"Data retur untuk pelanggan ini tidak bisa di edit lagi karna sudah diupload",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Intent ints = new Intent(getApplicationContext(), ActivityRetur.class);
                        ints.putExtra(TAG_SHIPTO, Shipto);
                        ints.putExtra(TAG_PERUSAHAAN, Perusahaan);
                        ints.putExtra(TAG_WEB,Web);
                        startActivity(ints);
                    }
                }

                if(menuid.equals("3")){
                    dborder.RecekKunjungan(getToday(),getPref(TAG_LASTLOGIN).substring(0, 2)+"/"+getPref(TAG_LASTLOGIN).substring(2, 4)+"/"+getPref(TAG_LASTLOGIN).substring(4),Shipto);
                    Intent intent = new Intent(getApplicationContext(), ActivityMainMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(TAG_WEB,Web);
                    startActivity(intent);
                }

            }
        });

        JSONObject PelangganJSON = null;

        try {
            PelangganJSON = dbmst.getPelangganOne(Shipto);
            // Getting Array of Pelanggan
            PelangganArray = PelangganJSON.getJSONArray(TAG_PELANGGANDATA);

            // looping through All Pelanggan
            for(int i = 0; i < PelangganArray.length(); i++){
                JSONObject c = PelangganArray.getJSONObject(i);

                //status = c.getInt(TAG_STATUS);
                alamat = c.getString("alamat");
                saldoaktif = c.getString("saldoaktif");
                saldolimit = c.getString("saldolimit");
                segment = c.getString("segment");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ImgDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Tada).duration(700).playOn(ImgDetail);
                DialogDetailToko(alamat,saldoaktif,saldolimit,segment);
            }
        });
    }

    public boolean checkMockLocation(){
        if (Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else return true;
    }

    public void CekExistOrder(final String Shipto,final String Perusahaan){
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
                ints.putExtra(TAG_SHIPTO, Shipto);
                ints.putExtra("kodeorder", "");
                ints.putExtra(TAG_PERUSAHAAN, Perusahaan);
                ints.putExtra(TAG_MODEORDER, 0);
                startActivity(ints);
            }
        });

        builder.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                dborder.RecekKunjungan(getToday(),getPref(TAG_LASTLOGIN).substring(0, 2)+"/"+getPref(TAG_LASTLOGIN).substring(2, 4)+"/"+getPref(TAG_LASTLOGIN).substring(4),Shipto);
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
                builder.setMessage("Apakah anda yakin akan ke Menu Utama?").setPositiveButton("Ya", dialogClickListener)
                        .setNegativeButton("Tidak", dialogClickListener).show();
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

    public void CekExistInventory(final String Shipto,final String Perusahaan,final String Kodenota){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView Msg = new TextView(this);
        Msg.setText("Input inventory untuk pelanggan "+Perusahaan+" hari ini sudah ada, apakah anda akan mengeditnya?");
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
                Intent ints = new Intent(getApplicationContext(), ActivityInventory.class);
                ints.putExtra(TAG_SHIPTO, Shipto);
                ints.putExtra("kodeorder", Kodenota);
                ints.putExtra(TAG_PERUSAHAAN, Perusahaan);
                ints.putExtra(TAG_MODEORDER, 1);
                ints.putExtra(TAG_MODEINV, 1);
                startActivity(ints);
            }
        });

        builder.show();
    }

    public void CekExistRetur(final String Shipto,final String Perusahaan,final String Kodenota){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 15);

        final TextView Msg = new TextView(this);
        Msg.setText("Input retur untuk pelanggan "+Perusahaan+" hari ini sudah ada, apakah anda akan mengeditnya?");
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
                Intent ints = new Intent(getApplicationContext(), ActivityRetur.class);
                ints.putExtra(TAG_SHIPTO, Shipto);
                ints.putExtra("kodeorder", Kodenota);
                ints.putExtra(TAG_PERUSAHAAN, Perusahaan);
                ints.putExtra(TAG_MODEORDER, 1);
                startActivity(ints);
            }
        });

        builder.show();
    }

    public void DialogDetailToko(final String alamat,final String saldo,final String limit,final String segment){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detail Toko");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 1, 30, 2);

        final TextView TxtShipto = new TextView(this);
        TxtShipto.setText("Kode : " + Shipto);
        TxtShipto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        final TextView TxtPerusahaan = new TextView(this);
        TxtPerusahaan.setText("Perusahaan : " + Perusahaan);
        TxtPerusahaan.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        final TextView TxtAlamat = new TextView(this);
        TxtAlamat.setText("Alamat : " + alamat);
        TxtAlamat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        DecimalFormatSymbols symbol =
                new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        //
        // Set the new DecimalFormatSymbols into formatter object.
        //

        DecimalFormat formatter = (DecimalFormat)
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);


        final TextView TxtSaldo = new TextView(this);
        TxtSaldo.setText("Saldo Aktif : Rp. " + formatter.format(Double.parseDouble(saldo)));
        TxtSaldo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        final TextView TxtLimit = new TextView(this);
        TxtLimit.setText("Limit Order : Rp. " + formatter.format(Double.parseDouble(limit)));
        TxtLimit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        final TextView TxtSegment = new TextView(this);
        TxtSegment.setText("Segment : " + segment);
        TxtSegment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);



        layout.addView(TxtShipto, params);
        layout.addView(TxtPerusahaan, params);
        layout.addView(TxtAlamat, params);
        layout.addView(TxtSegment,params);
        layout.addView(TxtLimit,params);
        layout.addView(TxtSaldo,params);
        builder.setView(layout);

        builder.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
