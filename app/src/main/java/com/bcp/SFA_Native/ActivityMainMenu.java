package com.bcp.SFA_Native;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
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

    FN_DBHandler dborder;

    String Web;

    // Array of integers points to images stored in /res/drawable-ldpi/
    int[] flags = new int[]{
            R.drawable.sfa_order,
            R.drawable.sfa_brg,
            R.drawable.sfa_dftrorder,
            R.drawable.sfa_sync,
            R.drawable.sfa_ordersuc
    };

    TextView TxtUser,TxtTime;

    FN_DBHandler dbmst;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String      DB_MASTER="MASTER";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_mainmenu);

        File dbFile = new File(DB_PATH+"/"+DB_ORDER+getPref("lastlogin"));
        if(!dbFile.exists()){
            dborder=new FN_DBHandler(getApplicationContext(),DB_PATH,DB_ORDER+getPref(TAG_LASTLOGIN));
            dborder.createOrder();
            dborder.close();
        }

        dbmst = new FN_DBHandler(getApplicationContext(),DB_PATH,DB_MASTER);


        TxtUser = (TextView) findViewById(R.id.MainMenu_TxtUser);
        TxtTime = (TextView) findViewById(R.id.MainMenu_TxtTime);

        TxtUser.setText(getPref(TAG_NAMELOGIN));
        TxtTime.setText("("+getPref(TAG_LASTLOGIN)+")");

        Intent in = getIntent();
        Web = in.getStringExtra(TAG_WEB);

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> OperatorMenuList = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> maporder = new HashMap<String, String>();
        maporder.put(TAG_ICON, Integer.toString(flags[0]));
        maporder.put(TAG_ID, "0");
        maporder.put(TAG_MENU, "Order");

        HashMap<String, String> mapbarang = new HashMap<String, String>();
        mapbarang.put(TAG_ICON, Integer.toString(flags[1]));
        mapbarang.put(TAG_ID, "1");
        mapbarang.put(TAG_MENU, "Barang");

        HashMap<String, String> mapdaftarorder = new HashMap<String, String>();
        mapdaftarorder.put(TAG_ICON, Integer.toString(flags[2]));
        mapdaftarorder.put(TAG_ID, "2");
        mapdaftarorder.put(TAG_MENU, "Daftar Order");

        HashMap<String, String> mapsync = new HashMap<String, String>();
        mapsync.put(TAG_ICON, Integer.toString(flags[3]));
        mapsync.put(TAG_ID, "3");
        mapsync.put(TAG_MENU, "Sync");

        HashMap<String, String> mapordersuccess = new HashMap<String, String>();
        mapordersuccess.put(TAG_ICON, Integer.toString(flags[4]));
        mapordersuccess.put(TAG_ID, "4");
        mapordersuccess.put(TAG_MENU, "Order Success");

        OperatorMenuList.add(maporder);
        OperatorMenuList.add(mapbarang);
        OperatorMenuList.add(mapdaftarorder);
        OperatorMenuList.add(mapsync);
        OperatorMenuList.add(mapordersuccess);

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
                    Intent in = new Intent(getApplicationContext(),ActivityPelanggan.class);
                    in.putExtra(TAG_WEB,Web);
                    startActivity(in);
                }

                if(menuid.equals("1")){
                    Intent in = new Intent(getApplicationContext(),ActivityBarang.class);
                    startActivity(in);
                }

                if(menuid.equals("2")){
                    Intent in = new Intent(getApplicationContext(),ActivityListOrder.class);
                    in.putExtra(TAG_ORDER,"1");
                    startActivity(in);
                }

                if(menuid.equals("3")){
                    Intent in = new Intent(getApplicationContext(),ActivitySync.class);
                    in.putExtra(TAG_WEB,Web);
                    startActivity(in);
                }

                if(menuid.equals("4")){
                    Intent in = new Intent(getApplicationContext(),ActivityListOrder.class);
                    in.putExtra(TAG_ORDER,"0");
                    startActivity(in);
                }

            }
        });
        dbmst.close();
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
}

