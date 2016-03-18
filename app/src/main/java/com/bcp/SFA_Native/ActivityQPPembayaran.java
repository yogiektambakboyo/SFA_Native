package com.bcp.SFA_Native;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

public class ActivityQPPembayaran extends Activity {
    private final String TAG_SHIPTO = "kode";
    private final String TAG_PERUSAHAAN = "perusahaan";
    private final String TAG_PENAGIHAN = "penagihan";
    private final String TAG_KODENOTA= "kodenota";
    private final String TAG_FAKTUR= "faktur";
    private final String TAG_TUNAI= "tunai";
    private final String TAG_BG= "bg";
    private final String TAG_STEMPEL= "stempel";
    private final String TAG_TT= "tt";
    private final String TAG_UC= "uc";
    private final String TAG_KETERANGAN= "keterangan";
    private final String TAG_TRANFERBANK= "transferbank";
    private final String TAG_TRANFERJML= "transferjml";
    private final String TAG_TRANFERTGL= "transfertgl";
    private final String TAG_TOTALBAYAR= "totalbayar";
    private final String TAG_TGL= "tgl";
    private final String TAG_OVERDUE = "overdue";
    private final String TAG_COLLECTOR= "collector";

    String Shipto = "",Perusahaan="";
    String[] TipePembayaran;
    Calendar myCalendar = Calendar.getInstance();
    String harini="";
    int tahun,bulan,tgl;
    String SummaryPayment="";
    float Tunai=0f,Transfer=0f,BG=0f,Total=0f,Sisa=0f;
    DecimalFormatSymbols symbol;
    DecimalFormat formatter;

    TextView TxtSumPayment,TxtTotal,TxtSisa,TxtPerusahaan;
    ListView list;ImageView ImgAdd;

    private FN_DBHandler dborder;
    private String      DB_PATH= Environment.getExternalStorageDirectory()+"/SFA";
    private String DB_ORDER="ORDER_";
    private final String TAG_LASTLOGIN = "lastlogin";
    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";
    JSONObject oPembayaran = null;
    JSONArray jPembayaran;
    private final String TAG_PREF="SETTINGPREF";
    ArrayList<Data_QPFaktur> FakturList = new ArrayList<Data_QPFaktur>();
    String[] Kodenota,Faktur,TunaiArr,BGArr,Stempel,TT,UC,Keterangan,TransferBank,TransferJml,TransferTgl,TotalBayar,Tgl,Collector,OverDue;
    AdapterQPPembayaran adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_qppembayaran);

        symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        //String currency = formatter.format(Double.parseDouble(totalbayar));

        Intent in = getIntent();
        Shipto = in.getStringExtra(TAG_SHIPTO);
        Perusahaan = in.getStringExtra(TAG_PERUSAHAAN);

        dborder = new FN_DBHandler(getApplicationContext(),DB_PATH, DB_ORDER+getPref(TAG_LASTLOGIN));
        File dbFile = new File(DB_PATH+"/"+DB_ORDER+getPref(TAG_LASTLOGIN));

        if(dbFile.exists()){
            try {
                oPembayaran = dborder.getFakturQP(Shipto);
                // Getting Array of Pelanggan
                jPembayaran = oPembayaran.getJSONArray(TAG_PENAGIHAN);

                Kodenota = new String[jPembayaran.length()];
                Faktur = new String[jPembayaran.length()];
                TunaiArr = new String[jPembayaran.length()];
                BGArr = new String[jPembayaran.length()];
                Stempel = new String[jPembayaran.length()];
                TT = new String[jPembayaran.length()];
                UC = new String[jPembayaran.length()];
                Keterangan = new String[jPembayaran.length()];
                TransferBank = new String[jPembayaran.length()];
                TransferJml = new String[jPembayaran.length()];
                TransferTgl = new String[jPembayaran.length()];
                TotalBayar = new String[jPembayaran.length()];
                Tgl = new String[jPembayaran.length()];
                Collector = new String[jPembayaran.length()];
                OverDue = new String[jPembayaran.length()];

                // looping through All Pelanggan
                for(int i = 0; i < jPembayaran.length(); i++){
                    JSONObject c = jPembayaran.getJSONObject(i);

                    Kodenota[i] = c.getString(TAG_KODENOTA);
                    Faktur[i] = c.getString(TAG_FAKTUR);
                    TunaiArr[i] = c.getString(TAG_TUNAI);
                    BGArr[i] = c.getString(TAG_BG);
                    Stempel[i] = c.getString(TAG_STEMPEL);
                    TT[i] = c.getString(TAG_TT);
                    UC[i] = c.getString(TAG_UC);
                    Keterangan[i] = c.getString(TAG_KETERANGAN);
                    TransferBank[i] = c.getString(TAG_TRANFERBANK);
                    TransferJml[i] = c.getString(TAG_TRANFERJML);
                    TransferTgl[i] = c.getString(TAG_TRANFERTGL);
                    TotalBayar[i] = c.getString(TAG_TOTALBAYAR);
                    Tgl[i] = c.getString(TAG_TGL);
                    Collector[i] = c.getString(TAG_COLLECTOR);
                    OverDue[i] = c.getString(TAG_OVERDUE);
                }

                for (int i = 0; i < Kodenota.length; i++)
                {
                    Data_QPFaktur plg = new Data_QPFaktur(Kodenota[i],Faktur[i],TunaiArr[i],BGArr[i],Stempel[i],TT[i],UC[i],Keterangan[i],TransferBank[i],TransferJml[i],TransferTgl[i],TotalBayar[i],Collector[i],Tgl[i],OverDue[i]);
                    FakturList.add(plg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            dborder.close();
        }else{
            Toast.makeText(getApplicationContext(), "DB Tidak Ada", Toast.LENGTH_SHORT).show();
        }

        TipePembayaran = new String[6];
        TipePembayaran[0] = "Tunai";
        TipePembayaran[1] = "Transfer";
        TipePembayaran[2] = "Cek/BG";
        TipePembayaran[3] = "Tanda Terima";
        TipePembayaran[4] = "Stempel";
        TipePembayaran[5] = "Tidak Terkirim";

        TxtSumPayment = (TextView) findViewById(R.id.QPPembayaran_Tipe);
        TxtSumPayment.setText("-");

        TxtPerusahaan = (TextView) findViewById(R.id.QPPembayaran_Pelanggan);

        ImgAdd = (ImageView) findViewById(R.id.QPPembayaran_ImgAdd);
        ImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getCount()>0){
                    ShowDialogTambahPembayaran();
                }else{
                    Toast.makeText(ActivityQPPembayaran.this, "Tidak ada faktur yang bisa dibayar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TxtTotal = (TextView) findViewById(R.id.QPPembayaran_TotalPembayaran);
        TxtSisa = (TextView) findViewById(R.id.QPPembayaran_Sisa);
        TxtTotal.setText("Rp. "+Total);
        TxtSisa.setText("Rp. "+Sisa);

        list = (ListView) findViewById(R.id.QPPembayaran_ListView);

        adapter = new AdapterQPPembayaran(getApplicationContext(),FakturList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShowDialogDetail(i);
            }
        });

        TxtPerusahaan.setText("" + Perusahaan + " (" + adapter.getCount() + ")");

        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.sfa_save);

        final FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();


        TxtSumPayment.setText(SummaryPayment);
        Total = adapter.Bayar();
        TxtTotal.setText("Rp. " + formatter.format(Total));

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((Total>0)||(SummaryPayment.trim().length()>0)){
                    for (int c=0;c<adapter.getCount();c++){
                        if ((Float.parseFloat(adapter.getItem(c).getTunai())>0)||(Float.parseFloat(adapter.getItem(c).getTransferJml())>0)||(Float.parseFloat(adapter.getItem(c).getBG())>0)||(Float.parseFloat(adapter.getItem(c).getStempel())>0)||(Float.parseFloat(adapter.getItem(c).getUC())>0)||(Float.parseFloat(adapter.getItem(c).getTT())>0)){
                            dborder.DeleteHPenagihan(adapter.getItem(c).getKodenota(),adapter.getItem(c).getFaktur());
                            dborder.InsertHPenagihan(adapter.getItem(c).getKodenota(),adapter.getItem(c).Faktur,adapter.getItem(c).getTgl(),adapter.getItem(c).getCollector(),adapter.getItem(c).getTunai(),adapter.getItem(c).getBG(),Integer.parseInt(adapter.getItem(c).getStempel()),Integer.parseInt(adapter.getItem(c).getTT()), Integer.parseInt(adapter.getItem(c).getUC()), adapter.getItem(c).getKeterangan(), Shipto, getPref(TAG_LONGITUDE), getPref(TAG_LATITUDE), adapter.getItem(c).TransferBank, adapter.getItem(c).TransferJml, getTodayTime(), adapter.getItem(c).getTransferTgl());
                        }
                    }
                    Toast.makeText(ActivityQPPembayaran.this, "Proses Simpan Berhasil", Toast.LENGTH_SHORT).show();
                    Intent in = new Intent(getApplicationContext(), Activity_Penagihan.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    in.putExtra("TAG_TAGIH","1");
                    startActivity(in);
                }else{
                    Toast.makeText(ActivityQPPembayaran.this, "Tidak ada data yang disimpan", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public String getTodayTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public void ShowDialogTambahPembayaran() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tipe Pembayaran");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 5, 30, 5);

        final TextView TxtTotalBayar = new TextView(this);
        TxtTotalBayar.setText("Total Bayar : Rp. "+adapter.TotalBayar());
        TxtTotalBayar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        TxtTotalBayar.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        final TextView TxtLblNama = new TextView(this);
        TxtLblNama.setText("Masukkan tipe pembayaran");
        TxtLblNama.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        TxtLblNama.setTypeface(Typeface.DEFAULT, Typeface.BOLD);


        final TextView TxtLblNominal = new TextView(this);
        TxtLblNominal.setText("Masukkan nominal");
        TxtLblNominal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        TxtLblNominal.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(18);

        final EditText InputNominalTunai = new EditText(this);
        InputNominalTunai.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputNominalTunai.setFilters(FilterArray);
        InputNominalTunai.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        InputNominalTunai.setSingleLine();
        InputNominalTunai.setHint("Rp. xxxxxxx.xx");
        InputNominalTunai.setKeyListener(DigitsKeyListener.getInstance("0123456789."));

        //Transfer
        final TextView TxtLblTglTransfer = new TextView(this);
        TxtLblTglTransfer.setText("Tgl Transfer : ");
        TxtLblTglTransfer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        TxtLblTglTransfer.setTypeface(null, Typeface.BOLD);

        final EditText InputTglTransfer = new EditText(this);
        InputTglTransfer.setSingleLine();
        InputTglTransfer.setHint("Masukkan Tgl Transfer");
        InputTglTransfer.setText("");
        InputTglTransfer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);


        final TextView TxtLblNamaBankTransfer = new TextView(this);
        TxtLblNamaBankTransfer.setText("Nama Bank : ");
        TxtLblNamaBankTransfer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        TxtLblNamaBankTransfer.setTypeface(null, Typeface.BOLD);

        final EditText InputBankTransfer = new EditText(this);
        InputBankTransfer.setSingleLine();
        InputBankTransfer.setHint("Masukkan Nama Bank");
        InputBankTransfer.setText("");
        InputBankTransfer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        InputBankTransfer.setKeyListener(DigitsKeyListener.getInstance("0123456789.ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz "));

        final TextView TxtLblTransfer = new TextView(this);
        TxtLblTransfer.setText("Nominal Transfer : ");
        TxtLblTransfer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        TxtLblTransfer.setTypeface(null, Typeface.BOLD);

        final EditText InputJmlTransfer = new EditText(this);
        InputJmlTransfer.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputJmlTransfer.setSingleLine();
        InputJmlTransfer.setHint("Rp. xxxxxxx.xx");
        InputJmlTransfer.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        InputJmlTransfer.setFilters(FilterArray);
        InputJmlTransfer.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);

        //BG

        final TextView TxtNoBG = new TextView(this);
        TxtNoBG.setText("No Cek/BG :");
        TxtNoBG.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);

        final TextView TxtBankBG = new TextView(this);
        TxtBankBG.setText("Nama Bank :");
        TxtBankBG.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);

        final TextView TxtNominalBG = new TextView(this);
        TxtNominalBG.setText("Nominal :");
        TxtNominalBG.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);

        // Set up the input
        final EditText inputNoBG = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputNoBG.setInputType(InputType.TYPE_CLASS_TEXT);
        inputNoBG.setSingleLine();
        inputNoBG.setHint("Masukkan No Cek/Giro");
        inputNoBG.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        inputNoBG.setKeyListener(DigitsKeyListener.getInstance("0123456789.ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz- "));

        final EditText inputBankBG = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputBankBG.setInputType(InputType.TYPE_CLASS_TEXT);
        inputBankBG.setSingleLine();
        inputBankBG.setHint("Masukkan Nama Bank");
        inputBankBG.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        inputBankBG.setKeyListener(DigitsKeyListener.getInstance("0123456789.ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz "));

        // Set up the input
        final EditText inputNominalBG = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputNominalBG.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputNominalBG.setSingleLine();
        inputNominalBG.setHint("Masukkan Nominal Uang");
        inputNominalBG.setFilters(FilterArray);
        inputNominalBG.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);

        final TextView TxtLblTglBG = new TextView(this);
        TxtLblTglBG.setText("Tgl Jatuh Tempo : ");
        TxtLblTglBG.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        TxtLblTglBG.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);

        final EditText InputTglBG = new EditText(this);
        InputTglBG.setSingleLine();
        InputTglBG.setHint("Masukkan Tgl Jatuh Tempo");
        InputTglBG.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);


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
                InputTglTransfer.setText(harini);
                InputTglBG.setText(harini);
                Toast.makeText(getApplicationContext(),harini,Toast.LENGTH_SHORT).show();
            }

        };

        InputTglTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityQPPembayaran.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        InputTglBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityQPPembayaran.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final Spinner SpnAlasan = new Spinner(this);

        final ArrayAdapter adapterSPn = new ArrayAdapter(this,android.R.layout.simple_spinner_item, TipePembayaran);
        adapterSPn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SpnAlasan.setAdapter(adapterSPn);
        SpnAlasan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: {
                        TxtLblNominal.setVisibility(View.VISIBLE);
                        InputNominalTunai.setVisibility(View.VISIBLE);

                        TxtLblTglTransfer.setVisibility(View.GONE);
                        InputTglTransfer.setVisibility(View.GONE);
                        TxtLblNamaBankTransfer.setVisibility(View.GONE);
                        InputBankTransfer.setVisibility(View.GONE);
                        TxtLblTransfer.setVisibility(View.GONE);
                        InputJmlTransfer.setVisibility(View.GONE);

                        TxtNoBG.setVisibility(View.GONE);
                        TxtBankBG.setVisibility(View.GONE);
                        TxtNominalBG.setVisibility(View.GONE);
                        TxtLblTglBG.setVisibility(View.GONE);
                        TxtNoBG.setVisibility(View.GONE);

                        InputTglBG.setVisibility(View.GONE);
                        inputBankBG.setVisibility(View.GONE);
                        inputNoBG.setVisibility(View.GONE);
                        inputNominalBG.setVisibility(View.GONE);
                        InputTglBG.setVisibility(View.GONE);
                        break;
                    }
                    case 1: {
                        TxtLblNominal.setVisibility(View.GONE);
                        InputNominalTunai.setVisibility(View.GONE);

                        TxtLblTglTransfer.setVisibility(View.VISIBLE);
                        InputTglTransfer.setVisibility(View.VISIBLE);
                        TxtLblNamaBankTransfer.setVisibility(View.VISIBLE);
                        InputBankTransfer.setVisibility(View.VISIBLE);
                        TxtLblTransfer.setVisibility(View.VISIBLE);
                        InputJmlTransfer.setVisibility(View.VISIBLE);

                        TxtNoBG.setVisibility(View.GONE);
                        TxtBankBG.setVisibility(View.GONE);
                        TxtNominalBG.setVisibility(View.GONE);
                        TxtLblTglBG.setVisibility(View.GONE);
                        TxtNoBG.setVisibility(View.GONE);

                        InputTglBG.setVisibility(View.GONE);
                        inputBankBG.setVisibility(View.GONE);
                        inputNoBG.setVisibility(View.GONE);
                        inputNominalBG.setVisibility(View.GONE);
                        InputTglBG.setVisibility(View.GONE);
                        break;
                    }
                    case 2: {
                        TxtLblNominal.setVisibility(View.GONE);
                        InputNominalTunai.setVisibility(View.GONE);

                        TxtLblTglTransfer.setVisibility(View.GONE);
                        InputTglTransfer.setVisibility(View.GONE);
                        TxtLblNamaBankTransfer.setVisibility(View.GONE);
                        InputBankTransfer.setVisibility(View.GONE);
                        TxtLblTransfer.setVisibility(View.GONE);
                        InputJmlTransfer.setVisibility(View.GONE);

                        TxtNoBG.setVisibility(View.VISIBLE);
                        TxtBankBG.setVisibility(View.VISIBLE);
                        TxtNominalBG.setVisibility(View.VISIBLE);
                        TxtLblTglBG.setVisibility(View.VISIBLE);
                        TxtNoBG.setVisibility(View.VISIBLE);

                        InputTglBG.setVisibility(View.VISIBLE);
                        inputBankBG.setVisibility(View.VISIBLE);
                        inputNoBG.setVisibility(View.VISIBLE);
                        inputNominalBG.setVisibility(View.VISIBLE);
                        InputTglBG.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 3: {
                        TxtLblNominal.setVisibility(View.GONE);
                        InputNominalTunai.setVisibility(View.GONE);

                        TxtLblTglTransfer.setVisibility(View.GONE);
                        InputTglTransfer.setVisibility(View.GONE);
                        TxtLblNamaBankTransfer.setVisibility(View.GONE);
                        InputBankTransfer.setVisibility(View.GONE);
                        TxtLblTransfer.setVisibility(View.GONE);
                        InputJmlTransfer.setVisibility(View.GONE);

                        TxtNoBG.setVisibility(View.GONE);
                        TxtBankBG.setVisibility(View.GONE);
                        TxtNominalBG.setVisibility(View.GONE);
                        TxtLblTglBG.setVisibility(View.GONE);
                        TxtNoBG.setVisibility(View.GONE);

                        InputTglBG.setVisibility(View.GONE);
                        inputBankBG.setVisibility(View.GONE);
                        inputNoBG.setVisibility(View.GONE);
                        inputNominalBG.setVisibility(View.GONE);
                        InputTglBG.setVisibility(View.GONE);
                        break;
                    }
                    case 4: {
                        TxtLblNominal.setVisibility(View.GONE);
                        InputNominalTunai.setVisibility(View.GONE);

                        TxtLblTglTransfer.setVisibility(View.GONE);
                        InputTglTransfer.setVisibility(View.GONE);
                        TxtLblNamaBankTransfer.setVisibility(View.GONE);
                        InputBankTransfer.setVisibility(View.GONE);
                        TxtLblTransfer.setVisibility(View.GONE);
                        InputJmlTransfer.setVisibility(View.GONE);

                        TxtNoBG.setVisibility(View.GONE);
                        TxtBankBG.setVisibility(View.GONE);
                        TxtNominalBG.setVisibility(View.GONE);
                        TxtLblTglBG.setVisibility(View.GONE);
                        TxtNoBG.setVisibility(View.GONE);

                        InputTglBG.setVisibility(View.GONE);
                        inputBankBG.setVisibility(View.GONE);
                        inputNoBG.setVisibility(View.GONE);
                        inputNominalBG.setVisibility(View.GONE);
                        InputTglBG.setVisibility(View.GONE);
                        break;
                    }
                    case 5: {
                        TxtLblNominal.setVisibility(View.GONE);
                        InputNominalTunai.setVisibility(View.GONE);

                        TxtLblTglTransfer.setVisibility(View.GONE);
                        InputTglTransfer.setVisibility(View.GONE);
                        TxtLblNamaBankTransfer.setVisibility(View.GONE);
                        InputBankTransfer.setVisibility(View.GONE);
                        TxtLblTransfer.setVisibility(View.GONE);
                        InputJmlTransfer.setVisibility(View.GONE);

                        TxtNoBG.setVisibility(View.GONE);
                        TxtBankBG.setVisibility(View.GONE);
                        TxtNominalBG.setVisibility(View.GONE);
                        TxtLblTglBG.setVisibility(View.GONE);
                        TxtNoBG.setVisibility(View.GONE);

                        InputTglBG.setVisibility(View.GONE);
                        inputBankBG.setVisibility(View.GONE);
                        inputNoBG.setVisibility(View.GONE);
                        inputNominalBG.setVisibility(View.GONE);
                        InputTglBG.setVisibility(View.GONE);
                        break;
                    }

                    default:
                        break;
                }
                //Toast.makeText(getApplicationContext(),"Position = "+alasan,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        layout.addView(TxtTotalBayar, params);
        layout.addView(TxtLblNama, params);
        layout.addView(SpnAlasan, params);
        layout.addView(TxtLblNominal, params);
        layout.addView(InputNominalTunai, params);

        layout.addView(TxtLblTglTransfer, params);
        layout.addView(InputTglTransfer, params);
        layout.addView(TxtLblNamaBankTransfer, params);
        layout.addView(InputBankTransfer, params);
        layout.addView(TxtLblTransfer, params);
        layout.addView(InputJmlTransfer, params);

        layout.addView(TxtNoBG, params);
        layout.addView(inputNoBG, params);
        layout.addView(TxtLblTglBG, params);
        layout.addView(InputTglBG, params);
        layout.addView(TxtBankBG, params);
        layout.addView(inputBankBG, params);
        layout.addView(TxtNominalBG, params);
        layout.addView(inputNominalBG, params);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Proses", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (SpnAlasan.getSelectedItemPosition()) {
                    case 0: {
                        if (InputNominalTunai.getText().toString().trim().length() <= 0) {
                            InputNominalTunai.setText("0");
                        }
                        if (Float.parseFloat(InputNominalTunai.getText().toString().trim()) > 0) {
                            Tunai = Float.parseFloat(InputNominalTunai.getText().toString().trim());
                            SummaryPayment = formatter.format(Tunai) + " (Tunai) " + formatter.format(Transfer) + " (Transfer) " + formatter.format(BG) + " (Cek/BG) ";
                            Total = Tunai + Transfer + BG;
                            Sisa = (Float.parseFloat(InputNominalTunai.getText().toString().trim()));
                            for (int a = 0; a < adapter.getCount(); a++) {
                                adapter.getItem(a).setTunai("0");
                                adapter.getItem(a).setTT("0");
                                adapter.getItem(a).setStempel("0");
                                adapter.getItem(a).setUC("0");
                                adapter.notifyDataSetChanged();
                                if ((a + 1) >= adapter.getCount()) {
                                    adapter.getItem(a).setTunai(Sisa + "");
                                    Sisa = 0f;
                                } else {
                                    float Terbayar = (Float.parseFloat(adapter.getItem(a).getTunai()) + Float.parseFloat(adapter.getItem(a).getTransferJml()) + Float.parseFloat(adapter.getItem(a).getBG()));
                                    float TotalBayar = Float.parseFloat(adapter.getItem(a).getTotalBayar());
                                    float Bayar = TotalBayar - Terbayar;
                                    if ((Terbayar < TotalBayar) && (Sisa >= Bayar)) {
                                        adapter.getItem(a).setTunai("" + Bayar);
                                        Sisa = Sisa - Bayar;
                                    } else if (Bayar>=Sisa){
                                        adapter.getItem(a).setTunai("" + Sisa);
                                        Sisa = 0f;
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(ActivityQPPembayaran.this, "Nominal harus lebih dari 0", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    case 1: {
                        if (InputJmlTransfer.getText().toString().trim().length() <= 0) {
                            InputJmlTransfer.setText("0");
                        }
                        if ((Float.parseFloat(InputJmlTransfer.getText().toString().trim()) > 0) && (InputTglTransfer.getText().toString().trim().length() == 10) && (InputBankTransfer.getText().toString().length() > 0)) {
                            Transfer = Float.parseFloat(InputJmlTransfer.getText().toString().trim());
                            SummaryPayment = formatter.format(Tunai) + " (Tunai) " + formatter.format(Transfer) + " (Transfer) " + formatter.format(BG) + " (Cek/BG) ";
                            Total = Tunai + Transfer + BG;
                            Sisa = (Float.parseFloat(InputJmlTransfer.getText().toString().trim()));
                            for (int a = 0; a < adapter.getCount(); a++) {
                                adapter.getItem(a).setTransferJml("0");
                                adapter.getItem(a).setTransferBank("");
                                adapter.getItem(a).setTransferTgl("");
                                adapter.getItem(a).setTT("0");
                                adapter.getItem(a).setStempel("0");
                                adapter.getItem(a).setUC("0");
                                adapter.notifyDataSetChanged();
                                if ((a + 1) >= adapter.getCount()) {
                                    adapter.getItem(a).setTransferJml(Sisa + "");
                                    Sisa = 0f;
                                } else {
                                    float Terbayar = (Float.parseFloat(adapter.getItem(a).getTunai()) + Float.parseFloat(adapter.getItem(a).getTransferJml()) + Float.parseFloat(adapter.getItem(a).getBG()));
                                    float TotalBayar = Float.parseFloat(adapter.getItem(a).getTotalBayar());
                                    float Bayar = TotalBayar - Terbayar;
                                    if ((Terbayar < TotalBayar) && (Sisa >= Bayar)) {
                                        adapter.getItem(a).setTransferJml("" + Bayar);
                                        Sisa = Sisa - Bayar;
                                    } else if (Bayar>=Sisa){
                                        adapter.getItem(a).setTransferJml("" + Sisa);
                                        Sisa = 0f;
                                    }
                                }
                                adapter.getItem(a).setTransferBank(InputBankTransfer.getText().toString().trim());
                                adapter.getItem(a).setTransferTgl(InputTglTransfer.getText().toString().trim());
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(ActivityQPPembayaran.this, "Lengkapi dahulu isian form", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    case 2: {
                        if (inputNominalBG.getText().toString().trim().length() <= 0) {
                            inputNominalBG.setText("0");
                        }
                        if ((Float.parseFloat(inputNominalBG.getText().toString().trim()) > 0) && (InputTglBG.getText().toString().trim().length() == 10) && (inputNoBG.getText().toString().length() > 0) && (inputBankBG.getText().toString().length() > 0)) {
                            BG = Float.parseFloat(inputNominalBG.getText().toString().trim());
                            SummaryPayment = formatter.format(Tunai) + " (Tunai) " + formatter.format(Transfer) + " (Transfer) " + formatter.format(BG) + " (Cek/BG) ";
                            Total = Tunai + Transfer + BG;
                            Sisa = Float.parseFloat(inputNominalBG.getText().toString().trim());
                            for (int a = 0; a < adapter.getCount(); a++) {
                                adapter.getItem(a).setBG("0");
                                adapter.getItem(a).setTT("0");
                                adapter.getItem(a).setStempel("0");
                                adapter.getItem(a).setUC("0");
                                adapter.getItem(a).setKeterangan("");
                                adapter.notifyDataSetChanged();
                                if ((a + 1) >= adapter.getCount()) {
                                    adapter.getItem(a).setBG(Sisa + "");
                                    Sisa = 0f;
                                } else {
                                    float Terbayar = (Float.parseFloat(adapter.getItem(a).getTunai()) + Float.parseFloat(adapter.getItem(a).getTransferJml()) + Float.parseFloat(adapter.getItem(a).getBG()));
                                    float TotalBayar = Float.parseFloat(adapter.getItem(a).getTotalBayar());
                                    float Bayar = TotalBayar - Terbayar;
                                    if ((Terbayar < TotalBayar) && (Sisa >= Bayar)) {
                                        adapter.getItem(a).setBG("" + Bayar);
                                        Sisa = Sisa - Bayar;
                                    } else if (Bayar>=Sisa){
                                        adapter.getItem(a).setBG("" + Sisa);
                                        Sisa = 0f;
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                adapter.getItem(a).setKeterangan(inputNoBG.getText().toString().trim()+"&"+InputTglBG.getText().toString().trim()+"&"+adapter.getItem(a).getBG()+"&"+inputBankBG.getText().toString().trim()+"#");
                            }
                        } else {
                            Toast.makeText(ActivityQPPembayaran.this, "Lengkapi dahulu isian form", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    }
                    case 3: {
                        Total = 0;Sisa=0;
                        Transfer = 0;Tunai=0;BG=0;
                        SummaryPayment = "Tanda Terima";
                        for (int a = 0; a < adapter.getCount(); a++) {
                            adapter.getItem(a).setTT("1");
                            adapter.getItem(a).setTunai("0");
                            adapter.getItem(a).setStempel("0");
                            adapter.getItem(a).setUC("0");
                            adapter.getItem(a).setTransferJml("0");
                            adapter.getItem(a).setTransferBank("");
                            adapter.getItem(a).setTransferTgl("");
                            adapter.getItem(a).setBG("0");
                            adapter.getItem(a).setKeterangan("");
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    }
                    case 4: {
                        SummaryPayment = "Stempel";
                        Total = 0;Sisa=0;
                        Transfer = 0;Tunai=0;BG=0;
                        for (int a = 0; a < adapter.getCount(); a++) {
                            adapter.getItem(a).setTT("0");
                            adapter.getItem(a).setTunai("0");
                            adapter.getItem(a).setStempel("1");
                            adapter.getItem(a).setUC("0");
                            adapter.getItem(a).setTransferJml("0");
                            adapter.getItem(a).setTransferBank("");
                            adapter.getItem(a).setTransferTgl("");
                            adapter.getItem(a).setBG("0");
                            adapter.getItem(a).setKeterangan("");
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    }
                    case 5: {
                        SummaryPayment = "Tidak Terkirim";
                        Total = 0;Sisa=0;
                        Transfer = 0;Tunai=0;BG=0;
                        for (int a = 0; a < adapter.getCount(); a++) {
                            adapter.getItem(a).setTT("0");
                            adapter.getItem(a).setTunai("0");
                            adapter.getItem(a).setStempel("0");
                            adapter.getItem(a).setUC("1");
                            adapter.getItem(a).setTransferJml("0");
                            adapter.getItem(a).setTransferBank("");
                            adapter.getItem(a).setTransferTgl("");
                            adapter.getItem(a).setBG("0");
                            adapter.getItem(a).setKeterangan("");
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    }
                    default:
                        break;
                }

                TxtSumPayment.setText(SummaryPayment);
                TxtTotal.setText("Rp. " + formatter.format(Total));
                TxtSisa.setText("Rp. " + formatter.format(Sisa));
                adapter.notifyDataSetChanged();
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

    public void ShowDialogDetail(int Pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detail Pembayaran");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 5, 30, 0);

        final TextView TxtLblNP = new TextView(this);
        TxtLblNP.setText("DV/NP : "+adapter.getItem(Pos).getKodenota());
        TxtLblNP.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);


        final TextView TxtLblFaktur = new TextView(this);
        TxtLblFaktur.setText("Faktur : "+adapter.getItem(Pos).getFaktur());
        TxtLblFaktur.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        final TextView TxtLblOD = new TextView(this);
        TxtLblOD.setText("Overdue : "+adapter.getItem(Pos).getOverDue());
        TxtLblOD.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);


        final TextView TxtLblTunai = new TextView(this);
        TxtLblTunai.setText("Tunai : " + adapter.getItem(Pos).getTunai());
        TxtLblTunai.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        final TextView TxtLblTransfer = new TextView(this);
        TxtLblTransfer.setText("Transfer : " + adapter.getItem(Pos).getTransferJml()+" ( "+adapter.getItem(Pos).getTransferBank()+" )");
        TxtLblTransfer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        final TextView TxtLblBG = new TextView(this);
        TxtLblBG.setText("BG : " + adapter.getItem(Pos).getBG());
        TxtLblBG.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);


        layout.addView(TxtLblNP, params);
        layout.addView(TxtLblFaktur, params);
        layout.addView(TxtLblOD, params);
        layout.addView(TxtLblTunai, params);
        layout.addView(TxtLblTransfer, params);
        layout.addView(TxtLblBG, params);


        builder.setView(layout);


        // Set up the buttons
        builder.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
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
}
