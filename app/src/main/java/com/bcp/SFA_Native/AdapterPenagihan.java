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

public class AdapterPenagihan extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Data_Penagihan> penagihandatalist = null;
    private ArrayList<Data_Penagihan> arraylist;

    public AdapterPenagihan(Context context, List<Data_Penagihan> penagihandatalist) {
        mContext = context;
        this.penagihandatalist = penagihandatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Data_Penagihan>();
        this.arraylist.addAll(penagihandatalist);
    }

    public class ViewHolder {
        TextView Kodenota;
        TextView Tgl;
        TextView Collector;
        ImageView ImgHit;
    }

    @Override
    public int getCount() {
        return penagihandatalist.size();
    }

    @Override
    public Data_Penagihan getItem(int position) {
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
            view = inflater.inflate(R.layout.l_penagihan, null);
            holder.Kodenota = (TextView) view.findViewById(R.id.LPenagihan_Kodenota);
            holder.Tgl = (TextView) view.findViewById(R.id.LPenagihan_Tgl);
            holder.Collector = (TextView) view.findViewById(R.id.LPenagihanCollector);
            holder.ImgHit = (ImageView) view.findViewById(R.id.LPenagihan_Image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        // Set the results into TextViews
        holder.Kodenota.setText(penagihandatalist.get(position).getKodenota());
        holder.Tgl.setText(penagihandatalist.get(position).getTgl().substring(0,10));
        holder.Collector.setText(penagihandatalist.get(position).getCollector());

        if (penagihandatalist.get(position).getHit().toString().equals("0")){
            holder.ImgHit.setBackgroundResource(R.drawable.arrow_left);
        }else{
            holder.ImgHit.setBackgroundResource(R.drawable.sfa_done);
        }


        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        penagihandatalist.clear();
        if (charText.equals("")) {
            for (Data_Penagihan plg : arraylist)
            {
                penagihandatalist.add(plg);
            }
        }
        else
        {
            for (Data_Penagihan plg : arraylist)
            {
                if (plg.getKodenota().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    penagihandatalist.add(plg);
                }
            }
        }
        notifyDataSetChanged();
    }

    public int search(String charText){
        int r = 0;
        charText = charText.toLowerCase(Locale.getDefault());
        for (Data_Penagihan plg : arraylist)
        {
            if (plg.getKodenota().toLowerCase(Locale.getDefault()).contains(charText))
            {
                r=1;
            }
        }
        return r;
    }
}