package com.bcp.SFA_Native;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by IT-SUPERMASTER on 25/02/2016.
 */
public class AdapterQPPembayaran extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Data_QPFaktur> fakturdatalist = null;
    private ArrayList<Data_QPFaktur> arraylist;
    DecimalFormatSymbols symbol;
    DecimalFormat formatter;

    public AdapterQPPembayaran(Context context, List<Data_QPFaktur> fakturdatalist) {
        mContext = context;
        this.fakturdatalist = fakturdatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Data_QPFaktur>();
        this.arraylist.addAll(fakturdatalist);
    }

    public class ViewHolder {
        TextView Kodenota,Faktur,TotalBayar,Pembayaran;
    }

    @Override
    public int getCount() {
        return fakturdatalist.size();
    }

    @Override
    public Data_QPFaktur getItem(int position) {
        return fakturdatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.l_pembayaranqp, null);
            // Locate the TextViews in listview_item.xml
            holder.Kodenota = (TextView) view.findViewById(R.id.QPFaktur_Kodenota);
            holder.Faktur = (TextView) view.findViewById(R.id.QPFaktur_Faktur);
            holder.TotalBayar = (TextView) view.findViewById(R.id.QPFaktur_TotalBayar);
            holder.Pembayaran = (TextView) view.findViewById(R.id.QPFaktur_Pembayaran);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        symbol = new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        // Set the results into TextViews
        holder.Kodenota.setText(fakturdatalist.get(position).getKodenota());
        holder.Faktur.setText(fakturdatalist.get(position).getFaktur());
        if (fakturdatalist.get(position).getTT().equals("1")){
            holder.Pembayaran.setText("Tanda Terima");
        }else if (fakturdatalist.get(position).getUC().equals("1")){
            holder.Pembayaran.setText("Tidak Terkirim");
        }else if (fakturdatalist.get(position).getStempel().equals("1")){
            holder.Pembayaran.setText("Stempel");
        }else{
            holder.Pembayaran.setText(formatter.format((Float.parseFloat(fakturdatalist.get(position).getTunai())+Float.parseFloat(fakturdatalist.get(position).getBG())+Float.parseFloat(fakturdatalist.get(position).getTransferJml()))));
        }
        holder.TotalBayar.setText(formatter.format(Float.parseFloat(fakturdatalist.get(position).getTotalBayar())));
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        fakturdatalist.clear();
        if (charText.equals("")) {
            fakturdatalist.addAll(arraylist);
        }else {
            for (Data_QPFaktur plg : arraylist)
            {
                if ((plg.getFaktur().toLowerCase(Locale.getDefault()).contains(charText)))
                {
                    fakturdatalist.add(plg);
                }
            }
        }

        notifyDataSetChanged();
    }


    public Float Bayar() {
        float a=0f;
        for (Data_QPFaktur plg : arraylist)
        {
            a = a + (Float.parseFloat(plg.getTunai())+ (Float.parseFloat(plg.getTransferJml())+ (Float.parseFloat(plg.getBG()))));
        }
        return a;
    }

    public String TotalBayar() {
        float a=0f;
        for (Data_QPFaktur plg : arraylist)
        {
            a = a + (Float.parseFloat(plg.getTotalBayar()));
        }
        return formatter.format(a);
    }
}
