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
 * Created by IT-SUPERMASTER on 23/02/2016.
 */
public class AdapterPelangganQPListView extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Data_Pelanggan> pelanggandatalist = null;
    private ArrayList<Data_Pelanggan> arraylist;

    public AdapterPelangganQPListView(Context context, List<Data_Pelanggan> pelanggandatalist) {
        mContext = context;
        this.pelanggandatalist = pelanggandatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Data_Pelanggan>();
        this.arraylist.addAll(pelanggandatalist);
    }

    public class ViewHolder {
        TextView ShipTo;
        TextView Perusahaan;
        TextView Alamat;
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
            view = inflater.inflate(R.layout.l_pelangganqp, null);
            // Locate the TextViews in listview_item.xml
            holder.ShipTo = (TextView) view.findViewById(R.id.QPPelanggan_ShipTo);
            holder.Perusahaan = (TextView) view.findViewById(R.id.QPPelanggan_Perusahaan);
            holder.Alamat = (TextView) view.findViewById(R.id.QPPelanggan_Alamat);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }



        // Set the results into TextViews
        holder.ShipTo.setText(pelanggandatalist.get(position).getShipTo());
        holder.Perusahaan.setText(pelanggandatalist.get(position).getPerusahaan());
        holder.Alamat.setText(pelanggandatalist.get(position).getAlamat());


        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        pelanggandatalist.clear();
        if (charText.equals("")) {
            pelanggandatalist.addAll(arraylist);
        }else {
            for (Data_Pelanggan plg : arraylist)
            {
                if ((plg.getPerusahaan().toLowerCase(Locale.getDefault()).contains(charText)))
                {
                    pelanggandatalist.add(plg);
                }
            }
        }

        notifyDataSetChanged();
    }
}
