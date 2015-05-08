package com.bcp.SFA_Native;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bcp.SFA_Native.Data_Pelanggan;

public class AdapterPelangganListView extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Data_Pelanggan> pelanggandatalist = null;
    private ArrayList<Data_Pelanggan> arraylist;
    int ModePlg = 0;

    public AdapterPelangganListView(Context context, List<Data_Pelanggan> pelanggandatalist, int ModePlg) {
        mContext = context;
        this.pelanggandatalist = pelanggandatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Data_Pelanggan>();
        this.arraylist.addAll(pelanggandatalist);
        this.ModePlg = ModePlg;
    }

    public class ViewHolder {
        TextView ShipTo;
        TextView Perusahaan;
        TextView Alamat;
        TextView Hari;
    }

    @Override
    public int getCount() {
        return pelanggandatalist.size();
    }

    @Override
    public Data_Pelanggan getItem(int position) {
        return pelanggandatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            if(ModePlg == 1){
                view = inflater.inflate(R.layout.l_pelangganorder, null);
            }else{
                view = inflater.inflate(R.layout.l_pelanggan, null);
            }
            // Locate the TextViews in listview_item.xml
            holder.ShipTo = (TextView) view.findViewById(R.id.Pelanggan_ShipTo);
            holder.Perusahaan = (TextView) view.findViewById(R.id.Pelanggan_Perusahaan);
            holder.Alamat = (TextView) view.findViewById(R.id.Pelanggan_Alamat);
            holder.Hari = (TextView) view.findViewById(R.id.Pelanggan_Hari);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if(ModePlg==1){
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.GERMANY);
            symbols.setCurrencySymbol("");
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
            formatter.setDecimalFormatSymbols(symbols);
            Float Total = Float.parseFloat(pelanggandatalist.get(position).getHari());
            String currency = formatter.format(Total);
            holder.Hari.setText(currency);
        }else{
            holder.Hari.setText(pelanggandatalist.get(position).getHari());
        }


        // Set the results into TextViews
        holder.ShipTo.setText(pelanggandatalist.get(position).getShipTo());
        holder.Perusahaan.setText(pelanggandatalist.get(position).getPerusahaan());
        holder.Alamat.setText(pelanggandatalist.get(position).getAlamat());


        return view;
    }

    public void filter(String charText, String key) {
        charText = charText.toLowerCase(Locale.getDefault());
        pelanggandatalist.clear();
        if ((charText.equals("semua"))&&(key.equals(""))) {
            String LastPerusahaan = "xxxlastxx";
            for (Data_Pelanggan plg : arraylist)
            {
                if (!(plg.getPerusahaan().toLowerCase(Locale.getDefault()).contains(LastPerusahaan)))
                {
                    pelanggandatalist.add(plg);
                    LastPerusahaan = plg.getPerusahaan().toLowerCase(Locale.getDefault());
                }
            }
        }
        else
        {
            if (charText.equals("semua")){
                charText = "";
            }
            String LPeru = "xxxlastxx";
            for (Data_Pelanggan plg : arraylist)
            {
                if (plg.getHari().toLowerCase(Locale.getDefault()).contains(charText))
                {

                    if (plg.getPerusahaan().toLowerCase(Locale.getDefault()).contains(key))
                    {
                        if (!(plg.getPerusahaan().toLowerCase(Locale.getDefault()).contains(LPeru)))
                        {
                            pelanggandatalist.add(plg);
                            LPeru = plg.getPerusahaan().toLowerCase(Locale.getDefault());
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}