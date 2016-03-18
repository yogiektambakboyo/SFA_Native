package com.bcp.SFA_Native;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
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
import java.util.HashMap;
import java.util.Locale;


public class ActivityMainMenu extends ListActivity {

    private final String TAG_MENU = "menu";
    private final String TAG_ICON = "1";
    private final String TAG_ID = "id";
    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_NAMELOGIN = "namelogin";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_WEB = "web";
    private final String TAG_ORDER = "order";
    private String DB_ORDER="ORDER_";
    private final String TAG_PELANGANDATA= "PelangganData";
    private final String TAG_OPT1 = "opt1";
    private final String TAG_OPT2 = "opt2";
    private final String TAG_OPT3 = "opt3";

    FN_DBHandler dborder;
    JSONArray InvArray;
    String Web;

    int[] flags = new int[]{
            R.drawable.sfa_order,
            R.drawable.sfa_brg,
            R.drawable.sfa_dftrorder,
            R.drawable.sfa_sync,
            R.drawable.sfa_ordersuc,
            R.drawable.sfa_shop,
            R.drawable.sfa_inventory,
            R.drawable.sfa_return,
            R.drawable.sfa_promo,
            R.drawable.sfa_bill,
            R.drawable.sfa_bill_upload
    };

    TextView TxtUser,TxtTime;
    ImageView ImgSummary;

    FN_DBHandler dbmst;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String      DB_MASTER="MASTER";
    public String       Kode,Opt1,Opt2,Opt3;
    String SalesName = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_mainmenu);

        File dbFile = new File(DB_PATH+"/"+DB_ORDER+getPref("lastlogin"));
        dborder=new FN_DBHandler(getApplicationContext(),DB_PATH,DB_ORDER+getPref(TAG_LASTLOGIN));
        if(!dbFile.exists()){
            dborder.createOrder();
            dborder.close();
        }

        if(dborder.cekColumnExistPengaturan("Penjualan","seq").equals("0")){
            dborder.addColumnPengaturan("Penjualan", "seq", "TEXT");
        }

        if(dborder.cekColumnExistPengaturan("HPenagihan","kodenota").equals("1")){
            dborder.DeleteHistory6M(getDateLast6M());
        }

        if(dborder.cekColumnExistPengaturan("Kunjungan","reverse").equals("0")){
            dborder.addColumnPengaturan("Kunjungan","reverse","TEXT");
        }

        if(dborder.cekColumnExistPengaturan("Penagihan","overdue").equals("0")){
            dborder.addColumnPengaturan("Penagihan","overdue","TEXT");
        }


        if(dborder.cekColumnExistPengaturan("Kunjungan","longitude").equals("0")){
            dborder.addColumnPengaturan("Kunjungan","longitude","TEXT");
            dborder.addColumnPengaturan("Kunjungan","latitude","TEXT");
            dborder.addColumnPengaturan("Kunjungan","gpstime","DATETIME");
        }

        dbmst = new FN_DBHandler(getApplicationContext(),DB_PATH,DB_MASTER);
        File dbFileMst = new File(DB_PATH+"/"+DB_MASTER);

        JSONObject InvJSON = null;

        if(dbFileMst.exists()){
            try{
                InvJSON = dbmst.GetInvParameter();
                InvArray = InvJSON.getJSONArray(TAG_PELANGANDATA);

                for (int i=0;i<InvArray.length();i++){
                    JSONObject c = InvArray.getJSONObject(i);

                    Opt1 = c.getString(TAG_OPT1);
                    Opt2 = c.getString(TAG_OPT2);
                    Opt3 = c.getString(TAG_OPT3);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

        setPrefInvParameter(Opt1,Opt2,Opt3);

        TxtUser = (TextView) findViewById(R.id.MainMenu_TxtUser);
        TxtTime = (TextView) findViewById(R.id.MainMenu_TxtTime);

        SalesName = getPref(TAG_NAMELOGIN);

        if ((SalesName.length())>20){
            SalesName = SalesName.substring(0,20);
        }

        TxtUser.setText(SalesName);
        TxtTime.setText("("+getPref(TAG_LASTLOGIN)+")");

        Intent in = getIntent();
        Web = in.getStringExtra(TAG_WEB);

        ArrayList<HashMap<String, String>> OperatorMenuList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> maporder = new HashMap<String, String>();
        maporder.put(TAG_ICON, Integer.toString(flags[5]));
        maporder.put(TAG_ID, "0");
        maporder.put(TAG_MENU, "Kunjungan Toko");

        HashMap<String, String> mapbarang = new HashMap<String, String>();
        mapbarang.put(TAG_ICON, Integer.toString(flags[1]));
        mapbarang.put(TAG_ID, "1");
        mapbarang.put(TAG_MENU, "Barang");

        HashMap<String, String> mapdaftarorder = new HashMap<String, String>();
        mapdaftarorder.put(TAG_ICON, Integer.toString(flags[2]));
        mapdaftarorder.put(TAG_ID, "2");
        mapdaftarorder.put(TAG_MENU, "Ringkasan Task");

        HashMap<String, String> mapsync = new HashMap<String, String>();
        mapsync.put(TAG_ICON, Integer.toString(flags[3]));
        mapsync.put(TAG_ID, "3");
        mapsync.put(TAG_MENU, "Sinkron");

        HashMap<String, String> mappromo = new HashMap<String, String>();
        mappromo.put(TAG_ICON, Integer.toString(flags[8]));
        mappromo.put(TAG_ID, "5");
        mappromo.put(TAG_MENU, "Promosi");

        HashMap<String, String> mapordersuccess = new HashMap<String, String>();
        mapordersuccess.put(TAG_ICON, Integer.toString(flags[4]));
        mapordersuccess.put(TAG_ID, "4");
        mapordersuccess.put(TAG_MENU, "Ringkasan Task Sukses");

        HashMap<String, String> mappenagihan = new HashMap<String, String>();
        mappenagihan.put(TAG_ICON, Integer.toString(flags[9]));
        mappenagihan.put(TAG_ID, "6");
        mappenagihan.put(TAG_MENU, "Penagihan");

        OperatorMenuList.add(maporder);
        OperatorMenuList.add(mapbarang);
        OperatorMenuList.add(mapdaftarorder);
        OperatorMenuList.add(mapsync);
        OperatorMenuList.add(mappromo);
        OperatorMenuList.add(mapordersuccess);
        OperatorMenuList.add(mappenagihan);

        /**
         * Updating parsed JSON data into ListView
         * */
        ListAdapter adapter = new AdapterCustomSimple(this, OperatorMenuList,
                R.layout.l_mainmenu,
                new String[] { TAG_ICON, TAG_MENU, TAG_ID },
                new int[] { R.id.imageViewOP, R.id.MainMenuNama, R.id.MainMenuID });

        setListAdapter(adapter);

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String menuid = ((TextView) view.findViewById(R.id.MainMenuID)).getText().toString();

                if (menuid.equals("0")) {
                    Intent in = new Intent(getApplicationContext(), ActivityPelangganDev.class);
                    in.putExtra(TAG_WEB, Web);
                    startActivity(in);
                }

                if (menuid.equals("1")) {
                    Intent in = new Intent(getApplicationContext(), ActivityBarang.class);
                    startActivity(in);
                }

                if (menuid.equals("2")) {
                    ShowRingkasanSuksesMenu("1");
                }

                if (menuid.equals("3")) {
                    Intent in = new Intent(getApplicationContext(), ActivitySync.class);
                    in.putExtra(TAG_WEB, Web);
                    startActivity(in);
                }

                if (menuid.equals("4")) {
                    ShowRingkasanSuksesMenu("0");
                }

                if (menuid.equals("5")) {
                    Intent in = new Intent(getApplicationContext(), ActivityPromosi.class);
                    startActivity(in);
                }

                if(menuid.equals("6")){
                    ShowRingkasanPenagihanMenu();
                }


            }
        });
        dbmst.close();

        ImgSummary = (ImageView) findViewById(R.id.MainMenu_ImgSummary);
        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_action_go_to_today);
        ImgSummary.setImageDrawable(myDrawable);
        ImgSummary.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_action_go_to_today_down);
                        ImgSummary.setImageDrawable(myDrawable);
                        String Result = dborder.KunjunganSummary(getToday(), getPref(TAG_LASTLOGIN).substring(0, 2) + "/" + getPref(TAG_LASTLOGIN).substring(2, 4) + "/" + getPref(TAG_LASTLOGIN).substring(4), getFirstDateToday());
                        dborder.close();
                        String[] split = Result.split("#");
                        ShowSummary(split[0], split[1], split[2], split[3], split[4], split[5], split[6]);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        Drawable myDrawable = getResources().getDrawable(R.drawable.ic_action_go_to_today);
                        ImgSummary.setImageDrawable(myDrawable);
                        break;
                    }
                }
                return true;
            }
        });
    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public String getDateLast6M(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -6);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public String getFirstDateToday(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Konfirmasi Log Out");
            builder.setMessage("Yakin ingin logout?").setPositiveButton("Ya", dialogClickListener)
                    .setNegativeButton("Tidak", dialogClickListener).show();
        }
        return false;
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    private void ShowRingkasanSuksesMenu(final String cmd){
        final Dialog dialog = new Dialog(ActivityMainMenu.this, android.R.style.Theme_Holo_Light_Dialog);
        if(cmd.equals("0")){
            dialog.setTitle("Menu Ringkasan Task Sukses");
        }else{
            dialog.setTitle("Menu Ringkasan Task");
        }
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.d_mainmenuvisitstore);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.d_mainmenuvisitstore, null);


        ArrayList<HashMap<String, String>> OperatorMenuVisit = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> maporder = new HashMap<String, String>();
        maporder.put(TAG_ID, "0");
        if(cmd.equals("0")){
            maporder.put(TAG_ICON, Integer.toString(flags[4]));
            maporder.put(TAG_MENU, "Order Sukses");
        }else{
            maporder.put(TAG_ICON, Integer.toString(flags[2]));
            maporder.put(TAG_MENU, "Daftar Order");
        }

        HashMap<String, String> mapvisit = new HashMap<String, String>();
        mapvisit.put(TAG_ICON, Integer.toString(flags[6]));
        mapvisit.put(TAG_ID, "1");
        if(cmd.equals("0")){
            mapvisit.put(TAG_MENU, "Inventory Sukses");
        }else{
            mapvisit.put(TAG_MENU, "Daftar Inventory");
        }

        HashMap<String, String> mapretur = new HashMap<String, String>();
        mapretur.put(TAG_ICON, Integer.toString(flags[7]));
        mapretur.put(TAG_ID, "2");
        if(cmd.equals("0")){
            mapretur.put(TAG_MENU, "Retur Sukses");
        }else{
            mapretur.put(TAG_MENU, "Daftar Retur");
        }

        OperatorMenuVisit.add(maporder);
        OperatorMenuVisit.add(mapvisit);
        OperatorMenuVisit.add(mapretur);

        ListView list;
        list = (ListView) dialog.findViewById(R.id.listviewvisitmenu);

        ListAdapter adapter = new AdapterCustomSimple(this, OperatorMenuVisit,
                R.layout.l_mainmenu,
                new String[] { TAG_ICON, TAG_MENU, TAG_ID },
                new int[] { R.id.imageViewOP, R.id.MainMenuNama, R.id.MainMenuID });

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String menuid = ((TextView) view.findViewById(R.id.MainMenuID)).getText().toString();
                if (menuid.equals("0")) {
                    Intent in = new Intent(getApplicationContext(), ActivityListOrder.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (cmd.equals("0")) {
                        in.putExtra(TAG_ORDER, "0");
                    } else {
                        in.putExtra(TAG_ORDER, "1");
                    }
                    startActivity(in);
                    dialog.dismiss();
                }

                if (menuid.equals("1")) {
                    Intent in = new Intent(getApplicationContext(), ActivityListInv.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (cmd.equals("0")) {
                        in.putExtra(TAG_ORDER, "0");
                    } else {
                        in.putExtra(TAG_ORDER, "1");
                    }
                    startActivity(in);
                    dialog.dismiss();
                }

                if (menuid.equals("2")) {
                    Intent in = new Intent(getApplicationContext(), ActivityListRetur.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (cmd.equals("0")) {
                        in.putExtra(TAG_ORDER, "0");
                    } else {
                        in.putExtra(TAG_ORDER, "1");
                    }
                    startActivity(in);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        if(cmd.equals("0")){
            dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.sfa_ordersuc);
        }else{
            dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,R.drawable.sfa_dftrorder);
        }
    }

    public void setPrefInvParameter(String Opt1,String Opt2, String Opt3){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor SettingPrefEditor = SettingPref.edit();
        SettingPrefEditor.putString(TAG_OPT1,Opt1);
        SettingPrefEditor.putString(TAG_OPT2,Opt2);
        SettingPrefEditor.putString(TAG_OPT3,Opt3);
        SettingPrefEditor.commit();
    }

    public void ShowSummary(String fjp,String fjptotal,String deviasi,String call,String pcall,String pcallvol,String uncall){
        final Context context = ActivityMainMenu.this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.d_summary);
        dialog.setTitle("Ringkasan Kunjungan");

        TextView FJP = (TextView) dialog.findViewById(R.id.Summary_FJP);
        FJP.setText("" + fjp);

        TextView FJPTotal = (TextView) dialog.findViewById(R.id.Summary_FJPTotal);
        FJPTotal.setText(""+fjptotal);

        TextView DeviasiFJP = (TextView) dialog.findViewById(R.id.Summary_FJPDeviasi);
        DeviasiFJP.setText(""+deviasi);

        TextView Call = (TextView) dialog.findViewById(R.id.Summary_Call);
        Call.setText(""+call);

        TextView PCall = (TextView) dialog.findViewById(R.id.Summary_PCall);
        PCall.setText(""+pcall);

        DecimalFormatSymbols symbol =
                new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        DecimalFormat formatter = (DecimalFormat)
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        String currency = formatter.format(Float.parseFloat(pcallvol));

        TextView PCallVol = (TextView) dialog.findViewById(R.id.Summary_PCallVol);
        PCallVol.setText(""+currency);

        TextView UnCall = (TextView) dialog.findViewById(R.id.Summary_UnCall);
        UnCall.setText(""+uncall);

        dialog.show();
    }

    private void ShowRingkasanPenagihanMenu(){
        final Dialog dialog = new Dialog(ActivityMainMenu.this, android.R.style.Theme_Holo_Light_Dialog);
        dialog.setTitle("Menu Penagihan");
        dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        dialog.setContentView(R.layout.d_mainmenuvisitstore);
        Context dContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) dContext.getSystemService(LAYOUT_INFLATER_SERVICE);


        ArrayList<HashMap<String, String>> OperatorMenuVisit = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> mappenagihan = new HashMap<String, String>();
        mappenagihan.put(TAG_ID, "0");
        mappenagihan.put(TAG_ICON, Integer.toString(flags[9]));
        mappenagihan.put(TAG_MENU, "Daftar Penagihan");

        HashMap<String, String> mappsukses = new HashMap<String, String>();
        mappsukses.put(TAG_ICON, Integer.toString(flags[10]));
        mappsukses.put(TAG_ID, "1");
        mappsukses.put(TAG_MENU, "Penagihan Sukses");


        OperatorMenuVisit.add(mappenagihan);
        OperatorMenuVisit.add(mappsukses);

        ListView list;
        list = (ListView) dialog.findViewById(R.id.listviewvisitmenu);

        ListAdapter adapter = new AdapterCustomSimple(this, OperatorMenuVisit,
                R.layout.l_mainmenu,
                new String[] { TAG_ICON, TAG_MENU, TAG_ID },
                new int[] { R.id.imageViewOP, R.id.MainMenuNama, R.id.MainMenuID });

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String menuid = ((TextView) view.findViewById(R.id.MainMenuID)).getText().toString();
                if (menuid.equals("0")) {
                    Intent in = new Intent(getApplicationContext(), Activity_Penagihan.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    in.putExtra("TAG_TAGIH","1");
                    startActivity(in);
                    dialog.dismiss();
                }

                if (menuid.equals("1")) {
                    Intent in = new Intent(getApplicationContext(), Activity_Penagihan.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    in.putExtra("TAG_TAGIH", "0");
                    startActivity(in);
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
        dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.sfa_bill);
    }
}

