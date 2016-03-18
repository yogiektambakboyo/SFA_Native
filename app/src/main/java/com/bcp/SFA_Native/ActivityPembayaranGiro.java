package com.bcp.SFA_Native;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ActivityPembayaranGiro extends AppCompatActivity {
    private final String TAG_FAKTUR = "faktur";
    private final String TAG_PERUSAHAAN = "perusahaan";

    private final String TAG_PREF="SETTINGPREF";

    String faktur,harini="";
    TextView TxtFaktur,TxtPerusahaan;
    static TextView TxtTotalGiro;
    static TextView TxtTotalRp;
    ListView list;
    ImageView ImgSummary;

    String No,Nominal,Tgl,GiroResume="",perusahaan,Bank;
    static Float  TotalRp=0.0f;
    int GiroTotal = 0;

    ArrayList<Data_Giro> GiroList = new ArrayList<Data_Giro>();
    AdapterGiroListView adapter;

    DecimalFormatSymbols symbol;
    DecimalFormat formatter;

    Calendar myCalendar = Calendar.getInstance();
    int tahun,bulan,tgl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_giro);

           symbol = new DecimalFormatSymbols(Locale.GERMANY);
           symbol.setCurrencySymbol("");

           formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
           formatter.setDecimalFormatSymbols(symbol);

        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.sfa_save);

        final com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton actionButton = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Proses simpan Cek/BG", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (TotalRp>1f){
                    for(int i=0;i<adapter.getCount();i++)
                    {
                        GiroResume = GiroResume+adapter.girodatalist.get(i).getNo()+"&"+adapter.girodatalist.get(i).getTgl()+"&"+adapter.girodatalist.get(i).getNominal()+"&"+adapter.girodatalist.get(i).getBank()+"#";
                    }
                    Intent in = new Intent();
                    in.putExtra("GiroRp",TotalRp);
                    in.putExtra("GiroTotal",GiroTotal);
                    in.putExtra("GiroResume",GiroResume);
                    setResult(RESULT_OK,in);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Giro Harus Diisi!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Resetting Var
        TotalRp=0.0f;
        GiroTotal = 0;
       GiroResume = "";


        Intent in = getIntent();
        faktur = in.getStringExtra(TAG_FAKTUR);
        perusahaan = in.getStringExtra(TAG_PERUSAHAAN);

        TxtFaktur = (TextView) findViewById(R.id.Giro_Faktur);
        TxtPerusahaan = (TextView) findViewById(R.id.Giro_Perusahaan);

        TxtFaktur.setText(faktur);
        TxtPerusahaan.setText(perusahaan);

        list = (ListView) findViewById(R.id.Giro_List);

        adapter = new AdapterGiroListView(this,GiroList);
        list.setAdapter(adapter);

        TxtTotalGiro = (TextView) findViewById(R.id.Giro_Total);
        TxtTotalRp = (TextView) findViewById(R.id.Giro_Rp);
        TxtTotalGiro.setText("0");
        TxtTotalRp.setText("0");

       ImgSummary = (ImageView) findViewById(R.id.Giro_Tambah);
       Drawable myDrawable = getResources().getDrawable(R.drawable.sfa_add);
       ImgSummary.setImageDrawable(myDrawable);
       ImgSummary.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View arg0, MotionEvent arg1) {
               switch (arg1.getAction()) {
                   case MotionEvent.ACTION_DOWN: {
                       Drawable myDrawable = getResources().getDrawable(R.drawable.sfa_add_down);
                       ImgSummary.setImageDrawable(myDrawable);
                       break;
                   }
                   case MotionEvent.ACTION_UP: {
                       Drawable myDrawable = getResources().getDrawable(R.drawable.sfa_add);
                       ImgSummary.setImageDrawable(myDrawable);
                       InputGiro();
                       break;
                   }
               }
               return true;
           }
       });
    }


    public static void setTxtTotalGiro(String Total) {
        TxtTotalGiro.setText(Total);
    }

    public static void setTxtTotalRp(String Total) {
        TotalRp = TotalRp - Float.parseFloat(Total);
        TxtTotalRp.setText(TotalRp.toString());
    }

    public void InputGiro(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Pembayaran Giro");
        builder.setIcon(R.drawable.dfa_info_ups);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 15, 30, 0);

        final TextView TxtNo = new TextView(this);
        TxtNo.setText("No Cek/BG :");

        final TextView TxtBank = new TextView(this);
        TxtBank.setText("Nama Bank :");

        final TextView TxtNominal = new TextView(this);
        TxtNominal.setText("Nominal :");

        // Set up the input
        final EditText inputNo = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputNo.setInputType(InputType.TYPE_CLASS_TEXT);
        inputNo.setSingleLine();
        inputNo.setHint("Masukkan No Cek/Giro");
        inputNo.setKeyListener(DigitsKeyListener.getInstance("0123456789.ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz "));

        final EditText inputBank = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputBank.setInputType(InputType.TYPE_CLASS_TEXT);
        inputBank.setSingleLine();
        inputBank.setHint("Masukkan Nama Bank");
        inputBank.setKeyListener(DigitsKeyListener.getInstance("0123456789.ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz- "));

        // Set up the input
        final EditText inputNominal = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputNominal.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputNominal.setSingleLine();
        inputNominal.setHint("Masukkan Nominal Uang");
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(8);
        inputNominal.setFilters(FilterArray);

        final TextView TxtLblTgl = new TextView(this);
        TxtLblTgl.setText("Tgl Jatuh Tempo : ");
        TxtLblTgl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        final EditText InputTgl = new EditText(this);
        InputTgl.setSingleLine();
        InputTgl.setHint("Masukkan Tgl Jatuh Tempo");

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

            }

        };

        InputTgl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityPembayaranGiro.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        layout.addView(TxtNo,params);
        layout.addView(inputNo,params);
        layout.addView(TxtBank,params);
        layout.addView(inputBank,params);
        layout.addView(TxtLblTgl,params);
        layout.addView(InputTgl,params);
        layout.addView(TxtNominal,params);
        layout.addView(inputNominal, params);
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
                if (inputNominal.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Nominal Tidak Boleh Kosong!!!", Toast.LENGTH_SHORT).show();
                } else if (inputNo.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "No Giro Harus Diisi!!", Toast.LENGTH_SHORT).show();
                } else if (Float.parseFloat(inputNominal.getText().toString()) <= 0f) {
                    Toast.makeText(getApplicationContext(), "Nominal Harus Lebih dari 0!!", Toast.LENGTH_SHORT).show();
                } else {
                    No = inputNo.getText().toString();
                    Nominal = inputNominal.getText().toString();
                    Tgl = InputTgl.getText().toString();
                    Bank = inputBank.getText().toString();

                    Data_Giro Giro = new Data_Giro(No, Nominal, Tgl, Bank);
                    GiroList.add(Giro);
                    adapter.notifyDataSetChanged();

                    TxtTotalGiro.setText(GiroList.size() + "");
                    GiroTotal = GiroList.size();
                    TotalRp = TotalRp + Float.parseFloat(Nominal);

                    TxtTotalRp.setText(formatter.format(TotalRp));
                }
            }
        });
        builder.show();
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

}
