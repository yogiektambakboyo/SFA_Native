package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dd.CircularProgressButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ActivityPembayaran extends Activity {

    TextView TxtKodenota,TxtPerusahaan,TxtTotalBayar;
    CircularProgressButton cbSubmit;
    ImageView ImgTunai,ImgTransfer,ImgCekBg,ImgTT,ImgStempel;
    ImageView ImgTunaiDone,ImgTransferDone,ImgCekBgDone,ImgTTDone,ImgStempelDone;
    TextView TxtTunai,TxtTransfer,TxtCekBG;

    Float NominalTransfer=0.0f,NominalCekBG=0.0f,NominalTunai=0.0f;
    String harini="",GiroResume="";
    int NominalTT=0,NominalStempel=0,GiroTotal=0;
    String BankTransfer="",NP="",Collector="",Shipto="",TglTransfer="",InTgl;

    DecimalFormatSymbols symbol;
    DecimalFormat formatter;

    Calendar myCalendar = Calendar.getInstance();
    int tahun,bulan,tgl;
    String Edit = "0";

    private FN_DBHandler dborder;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String DB_ORDER="ORDER_";
    private final String TAG_LASTLOGIN = "lastlogin";
    JSONObject oPembayaran = null;
    JSONArray jPembayaran;
    private final String TAG_PREF="SETTINGPREF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_pembayaran);

        Intent in = getIntent();
        NP = in.getStringExtra("Kodenota");
        Edit = in.getStringExtra("Edit");
        Shipto = in.getStringExtra("Shipto");
        Collector = in.getStringExtra("Collector");
        InTgl = in.getStringExtra("Tgl");

        dborder = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_ORDER+getPref(TAG_LASTLOGIN));
        File dbFile = new File(DB_PATH+"/"+DB_ORDER+getPref(TAG_LASTLOGIN));

        if((dbFile.exists())&&(Edit.equals("1"))){
            try {
                oPembayaran = dborder.getPembayaranFaktur(NP, in.getStringExtra("Faktur"));
                jPembayaran = oPembayaran.getJSONArray("penagihan");

                for (int k=0;k<jPembayaran.length();k++){
                    JSONObject d = jPembayaran.getJSONObject(k);
                    NominalTunai = Float.parseFloat(d.getString("tunai"));
                    NominalCekBG = Float.parseFloat(d.getString("bg"));
                    NominalTT = Integer.parseInt(d.getString("tt"));
                    NominalStempel = Integer.parseInt(d.getString("stempel"));
                    BankTransfer = d.getString("transferbank");
                    NominalTransfer = Float.parseFloat(d.getString("transferjml"));
                    harini = d.getString("transfertgl");
                    GiroResume = d.getString("keterangan");
                }
            } catch (JSONException e) {
                Toast.makeText(ActivityPembayaran.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            dborder.close();
        }
        TxtKodenota = (TextView) findViewById(R.id.Pembayaran_Kodenota);
        TxtKodenota.setText(in.getStringExtra("Faktur"));
        TxtPerusahaan = (TextView) findViewById(R.id.Pembayaran_Perusahaan);
        TxtPerusahaan.setText(in.getStringExtra("Perusahaan"));
        TxtTotalBayar = (TextView) findViewById(R.id.Pembayaran_TotalBayar);

        TxtTunai = (TextView) findViewById(R.id.Pembayaran_Tunai);
        TxtTransfer = (TextView) findViewById(R.id.Pembayaran_Transfer);
        TxtCekBG = (TextView) findViewById(R.id.Pembayaran_BG);

        symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        String currency = formatter.format(Double.parseDouble(in.getStringExtra("TotalBayar")));
        String currencytunai = formatter.format(NominalTunai);
        String currencytransfer = formatter.format(NominalTransfer);
        String currencybg = formatter.format(NominalCekBG);

        TxtTotalBayar.setText(currency);
        TxtTunai.setText(currencytunai);
        TxtTransfer.setText(currencytransfer);
        TxtCekBG.setText(currencybg);

        cbSubmit = (CircularProgressButton) findViewById(R.id.btnPembayaranSubmit);
        cbSubmit.setProgress(0);
        cbSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cbSubmit.setIndeterminateProgressMode(true);
                cbSubmit.setProgress(50);
                if((NominalTunai+NominalCekBG+NominalTT+NominalTransfer+NominalStempel)>0){
                    ShowDialogKonfirmasiSimpan();
                }else{
                    Toast.makeText(ActivityPembayaran.this, "Isi dahulu pembayaran!!!", Toast.LENGTH_SHORT).show();
                    cbSubmit.setProgress(0);
                }
            }
        });

        ImgTunai = (ImageView) findViewById(R.id.Pembayaran_ImgTunai);
        ImgTunai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Tada)
                        .duration(700)
                        .playOn(ImgTunai);
                ShowDialogInputTunai(NominalTunai);
            }
        });
        ImgCekBg = (ImageView) findViewById(R.id.Pembayaran_ImgCekBG);
        ImgCekBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Tada)
                        .duration(700)
                        .playOn(ImgCekBg);

                Intent in = new Intent(getApplicationContext(),ActivityPembayaranGiro.class);
                in.putExtra("faktur",TxtKodenota.getText().toString());
                in.putExtra("perusahaan",TxtPerusahaan.getText().toString());
                startActivityForResult(in, 1);
            }
        });
        ImgTransfer = (ImageView) findViewById(R.id.Pembayaran_ImgTransfer);
        ImgTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Tada)
                        .duration(700)
                        .playOn(ImgTransfer);
                ShowDialogInputTransfer(harini,BankTransfer,NominalTransfer);
            }
        });
        ImgTT = (ImageView) findViewById(R.id.Pembayaran_ImgTT);
        ImgTT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Tada)
                        .duration(700)
                        .playOn(ImgTT);
                if ((NominalTunai+NominalTransfer+NominalCekBG)>0){
                    Toast.makeText(ActivityPembayaran.this, "Pastikan Nominal Tunai/Transfer/CekBG sudah 0", Toast.LENGTH_SHORT).show();
                }else{
                    if (NominalTT == 0){
                        NominalTT=1;
                        NominalStempel=0;
                        ImgTTDone.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.Landing).duration(700).playOn(ImgTTDone);
                        ImgStempelDone.setVisibility(View.GONE);
                    }else{
                        NominalTT=0;
                        ImgTTDone.setVisibility(View.GONE);
                    }
                }
            }
        });
        ImgStempel = (ImageView) findViewById(R.id.Pembayaran_ImgStempel);
        ImgStempel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.Tada)
                        .duration(700)
                        .playOn(ImgStempel);
                if ((NominalTunai+NominalTransfer+NominalCekBG)>0){
                    Toast.makeText(ActivityPembayaran.this, "Pastikan Nominal Tunai/Transfer/CekBG sudah 0", Toast.LENGTH_SHORT).show();
                }else{
                    if (NominalStempel == 0){
                        NominalStempel=1;
                        NominalTT=0;
                        ImgStempelDone.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.Landing).duration(700).playOn(ImgStempelDone);
                        ImgTTDone.setVisibility(View.GONE);
                    }else{
                        NominalStempel=0;
                        ImgStempelDone.setVisibility(View.GONE);
                    }
                }
            }
        });

        ImgTunaiDone = (ImageView) findViewById(R.id.Pembayaran_TunaiDone);
        if (NominalTunai > 0){
            ImgTunaiDone.setVisibility(View.VISIBLE);
        }else{
            ImgTunaiDone.setVisibility(View.GONE);
        }
        ImgCekBgDone = (ImageView) findViewById(R.id.Pembayaran_CekBGDone);
        if (NominalCekBG > 0){
            ImgCekBgDone.setVisibility(View.VISIBLE);
        }else{
            ImgCekBgDone.setVisibility(View.GONE);
        }
        ImgTransferDone = (ImageView) findViewById(R.id.Pembayaran_TransferDone);
        if (NominalTransfer > 0){
            ImgTransferDone.setVisibility(View.VISIBLE);
        }else {
            ImgTransferDone.setVisibility(View.GONE);
        }
        ImgTTDone = (ImageView) findViewById(R.id.Pembayaran_TTDone);
        if(NominalTT ==0){
            ImgTTDone.setVisibility(View.GONE);
        }else{
            ImgTTDone.setVisibility(View.VISIBLE);
        }
        ImgStempelDone = (ImageView) findViewById(R.id.Pembayaran_StempelDone);
        if (NominalStempel==0){
            ImgStempelDone.setVisibility(View.GONE);
        }else{
            ImgStempelDone.setVisibility(View.VISIBLE);
        }
    }

    public void ShowDialogInputTunai(final Float Jml){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Tunai");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 3, 30, 3);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Masukkan Nominal Tunai : ");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        TxtLblNama.setTypeface(null, Typeface.BOLD);

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(18);

        final EditText InputJml = new EditText(this);
        InputJml.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputJml.setSingleLine();
        InputJml.setHint("Rp. xxxxxxx.xx");
        InputJml.setText(Jml + "");
        InputJml.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        InputJml.setFilters(FilterArray);

        layout.addView(TxtLblNama, params);
        layout.addView(InputJml, params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (InputJml.getText().toString().trim().length() <= 0) {
                    InputJml.setText("0");
                }

                String str = InputJml.getText().toString().trim();
                String findStr = ".";
                int lastIndex = 0;
                int count = 0;

                while (lastIndex != -1) {
                    lastIndex = str.indexOf(findStr, lastIndex);
                    if (lastIndex != -1) {
                        count++;
                        lastIndex += findStr.length();
                    }
                }

                if (count > 1) {
                    InputJml.setText("0");
                    Toast.makeText(ActivityPembayaran.this, "Nominal yang diinputkan salah!!!", Toast.LENGTH_SHORT).show();
                }

                NominalTunai = Float.parseFloat(InputJml.getText().toString().trim());
                TxtTunai.setText("" + formatter.format(NominalTunai));
                if (NominalTunai > 0) {
                    ImgStempelDone.setVisibility(View.GONE);
                    ImgTTDone.setVisibility(View.GONE);
                    NominalTT = 0;
                    NominalStempel = 0;
                    ImgTunaiDone.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Landing).duration(700).playOn(ImgTunaiDone);
                } else {
                    ImgTunaiDone.setVisibility(View.GONE);
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

    public void ShowDialogInputTransfer(final String Tgl,final String Bank,final Float Jml){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input Transfer");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 3, 30, 3);

        final TextView TxtLblTgl = new TextView(this);
        TxtLblTgl.setText("Tgl Transfer : ");
        TxtLblTgl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        TxtLblTgl.setTypeface(null, Typeface.BOLD);

        final EditText InputTgl = new EditText(this);
        InputTgl.setSingleLine();
        InputTgl.setHint("Masukkan Tgl Transfer");
        InputTgl.setText("" + Tgl);

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

                harini = tahun+"-"+(bulan<10?("0"+bulan):(bulan))+"-"+(tgl<10?("0"+tgl):(tgl));
                InputTgl.setText(harini);
                Toast.makeText(getApplicationContext(),harini,Toast.LENGTH_SHORT).show();
                TglTransfer = InputTgl.getText().toString();
            }

        };

        InputTgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityPembayaran.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final TextView TxtLblNamaBank = new TextView(this);
        TxtLblNamaBank.setText("Nama Bank : ");
        TxtLblNamaBank.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        TxtLblNamaBank.setTypeface(null, Typeface.BOLD);

        final EditText InputBank = new EditText(this);
        InputBank.setSingleLine();
        InputBank.setHint("Masukkan Nama Bank");
        InputBank.setText("" + Bank);
        InputBank.setKeyListener(DigitsKeyListener.getInstance("0123456789.ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz- "));

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(18);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Nominal Transfer : ");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        TxtLblNama.setTypeface(null, Typeface.BOLD);

        final EditText InputJml = new EditText(this);
        InputJml.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputJml.setSingleLine();
        InputJml.setHint("Rp. xxxxxxx.xx");
        InputJml.setText(Jml + "");
        InputJml.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        InputJml.setFilters(FilterArray);

        layout.addView(TxtLblTgl, params);
        layout.addView(InputTgl, params);
        layout.addView(TxtLblNamaBank, params);
        layout.addView(InputBank, params);
        layout.addView(TxtLblNama, params);
        layout.addView(InputJml, params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (InputJml.getText().toString().trim().length() <= 0) {
                    InputJml.setText("0");
                }

                String str = InputJml.getText().toString().trim();
                String findStr = ".";
                int lastIndex = 0;
                int count = 0;

                while (lastIndex != -1) {
                    lastIndex = str.indexOf(findStr, lastIndex);
                    if (lastIndex != -1) {
                        count++;
                        lastIndex += findStr.length();
                    }
                }

                if (count > 1) {
                    InputJml.setText("0");
                    Toast.makeText(ActivityPembayaran.this, "Nominal yang diinputkan salah!!!", Toast.LENGTH_SHORT).show();
                }

                NominalTransfer = Float.parseFloat(InputJml.getText().toString().trim());
                TxtTransfer.setText("" + formatter.format(NominalTransfer));
                if ((NominalTransfer > 0)&&(InputBank.getText().toString().trim().length()>1)&&(InputTgl.getText().toString().trim().length()>1)) {
                    ImgStempelDone.setVisibility(View.GONE);
                    ImgTTDone.setVisibility(View.GONE);
                    NominalTT = 0;
                    NominalStempel = 0;
                    ImgTransferDone.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Landing).duration(700).playOn(ImgTransferDone);
                    BankTransfer = InputBank.getText().toString().trim();
                } else {
                    Toast.makeText(ActivityPembayaran.this, "Lengkapi dahulu Tgl Trasfer,Jml dan Nama Bank!!!", Toast.LENGTH_SHORT).show();
                    ImgTransferDone.setVisibility(View.GONE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if(resultCode == RESULT_OK){
                NominalCekBG = NominalCekBG + data.getFloatExtra("GiroRp",0.0f);
                GiroTotal = GiroTotal + data.getIntExtra("GiroTotal", 0);
                GiroResume = GiroResume + data.getStringExtra("GiroResume");
                Toast.makeText(ActivityPembayaran.this, ""+GiroResume, Toast.LENGTH_LONG).show();
                TxtCekBG.setText(formatter.format(NominalCekBG)+" ("+GiroTotal+")");
                ImgCekBgDone.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.Landing).duration(700).playOn(ImgCekBgDone);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Edit This
                                Intent intent = new Intent(getApplicationContext(), ActivityPFaktur.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("Kodenota", NP);
                                intent.putExtra("TAG_TAGIH","1");
                                intent.putExtra("Tgl", InTgl);
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
        return false;
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


    public String getTodayTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public void ShowDialogKonfirmasiSimpan(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi Simpan");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 3, 30, 3);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Apakah anda yakin akan memproses pembayaran faktur : " + TxtKodenota.getText().toString().trim() + " ?");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        TxtLblNama.setTypeface(null, Typeface.BOLD);

        String pmb = "";
        if (NominalTT>0){
            pmb = "Tanda Terima";
        }else if(NominalStempel>0){
            pmb = "Stempel";
        }else if (NominalTunai>0){
            pmb = "Tunai : Rp. "+NominalTunai;
        }

        if (NominalTransfer>0){
            pmb = pmb + " - Transfer : Rp. "+NominalTransfer;
        }
        if (NominalCekBG>0){
            pmb = pmb + "- Cek/BG : Rp. "+NominalCekBG;
        }

        final TextView TxtLblPembayaran = new TextView(this);
        TxtLblPembayaran.setText("Pembayaran : " + pmb);
        TxtLblPembayaran.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        layout.addView(TxtLblNama, params);
        layout.addView(TxtLblPembayaran, params);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Edit.equals("1")) {
                    dborder.DeleteHPenagihan(NP, TxtKodenota.getText().toString().trim());
                }
                dborder.UpdateOpenHPenagihanUpload(NP);
                dborder.InsertHPenagihan(NP, TxtKodenota.getText().toString().trim(), InTgl, Collector, NominalTunai.toString(), NominalCekBG.toString(), NominalStempel, NominalTT, 0, GiroResume, Shipto, "", "", BankTransfer, NominalTransfer.toString(), getTodayTime(), TglTransfer);
                Toast.makeText(ActivityPembayaran.this, "Pembayaran Berhasil", Toast.LENGTH_SHORT).show();
                cbSubmit.setProgress(100);
                dialog.dismiss();
                Intent in = new Intent(ActivityPembayaran.this, ActivityPFaktur.class);
                in.putExtra("TAG_TAGIH", "1");
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.putExtra("Kodenota", NP);
                in.putExtra("Tgl", InTgl);
                startActivity(in);
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cbSubmit.setProgress(0);
            }
        });
        builder.show();
    }
}
