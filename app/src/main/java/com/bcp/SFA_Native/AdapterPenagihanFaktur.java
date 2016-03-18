package com.bcp.SFA_Native;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by IT-SUPERMASTER on 23/10/2015.
 */
public class AdapterPenagihanFaktur extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Data_Faktur> penagihandatalist = null;
    private ArrayList<Data_Faktur> arraylist;

    public AdapterPenagihanFaktur(Context context, List<Data_Faktur> penagihandatalist) {
        mContext = context;
        this.penagihandatalist = penagihandatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Data_Faktur>();
        this.arraylist.addAll(penagihandatalist);
    }

    public class ViewHolder {
        TextView Kodenota,Faktur,Shipto,Perusahaan,Alamat,Totalbayar,Collector,Hit;
        ImageView Icon;
    }

    @Override
    public int getCount() {
        return penagihandatalist.size();
    }

    @Override
    public Data_Faktur getItem(int position) {
        return penagihandatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.l_pfaktur, null);
            holder.Kodenota = (TextView) view.findViewById(R.id.LPenagihanFaktur_Kodenota);
            holder.Faktur = (TextView) view.findViewById(R.id.LPenagihanFaktur_Faktur);
            holder.Perusahaan = (TextView) view.findViewById(R.id.LPenagihanFaktur_Perusahaan);
            holder.Shipto = (TextView) view.findViewById(R.id.LPenagihanFaktur_Shipto);
            holder.Alamat = (TextView) view.findViewById(R.id.LPenagihanFaktur_Alamat);
            holder.Totalbayar = (TextView) view.findViewById(R.id.LPenagihanFaktur_TotalBayar);
            holder.Collector = (TextView) view.findViewById(R.id.LPenagihanFaktur_Collector);
            holder.Hit = (TextView) view.findViewById(R.id.LPenagihanFaktur_Hit);
            holder.Icon = (ImageView) view.findViewById(R.id.LPenagihanFaktur_Image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        DecimalFormatSymbols symbol =
                new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        //
        // Set the new DecimalFormatSymbols into formatter object.
        //

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        String currency = formatter.format(Double.parseDouble(penagihandatalist.get(position).getTotalbayar()));


        // Set the results into TextViews
        holder.Kodenota.setText(penagihandatalist.get(position).getKodenota());
        holder.Faktur.setText(penagihandatalist.get(position).getFaktur());
        holder.Perusahaan.setText(penagihandatalist.get(position).getPerusahaan());
        holder.Shipto.setText(penagihandatalist.get(position).getShipto());
        holder.Alamat.setText(penagihandatalist.get(position).getAlamat());
        holder.Collector.setText(penagihandatalist.get(position).getCollector());
        holder.Hit.setText(penagihandatalist.get(position).getHit());
        if (penagihandatalist.get(position).getHit().toString().equals("0")){
            holder.Icon.setBackgroundResource(R.drawable.arrow_left);
        }else{
            holder.Icon.setBackgroundResource(R.drawable.sfa_done);
        }
        holder.Totalbayar.setText("Rp. "+currency);
        return view;
    }

    public void filter(String charText,String Hit) {
        charText = charText.toLowerCase(Locale.getDefault());
        if (Hit.equals("semua")){
            Hit = "";
        }
        penagihandatalist.clear();
        if ((charText.equals(""))&&(Hit.equals("semua"))) {
            for (Data_Faktur plg : arraylist)
            {
                penagihandatalist.add(plg);
            }
        }
        else
        {
            for (Data_Faktur plg : arraylist)
            {
                if (plg.getPerusahaan().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    if (Hit.equals("")){
                        penagihandatalist.add(plg);
                    }else if (Hit.equals("0")){
                        if (plg.getHit().toLowerCase(Locale.getDefault()).equals(Hit))
                        {
                            penagihandatalist.add(plg);
                        }
                    }else{
                        if (!plg.getHit().toLowerCase(Locale.getDefault()).equals("0"))
                        {
                            penagihandatalist.add(plg);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}
