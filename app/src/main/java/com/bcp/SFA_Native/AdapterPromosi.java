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

public class AdapterPromosi extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Data_Promosi> promosidatalist = null;
    private ArrayList<Data_Promosi> arraylist;

    public AdapterPromosi(Context context, List<Data_Promosi> promosidatalist) {
        mContext = context;
        this.promosidatalist = promosidatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Data_Promosi>();
        this.arraylist.addAll(promosidatalist);
    }

    public class ViewHolder {
        TextView NoProgram;
        TextView Caption;
        TextView Keterangan;
        TextView Segment;
        TextView TglBerlaku;
    }

    @Override
    public int getCount() {
        return promosidatalist.size();
    }

    @Override
    public Data_Promosi getItem(int position) {
        return promosidatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.l_promosi, null);
            holder.NoProgram = (TextView) view.findViewById(R.id.Promosi_NoProgram);
            holder.Caption = (TextView) view.findViewById(R.id.Promosi_Caption);
            holder.Keterangan = (TextView) view.findViewById(R.id.Promosi_Keterangan);
            holder.Segment = (TextView) view.findViewById(R.id.Promosi_Segment);
            holder.TglBerlaku = (TextView) view.findViewById(R.id.Promosi_TglBerlaku);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        // Set the results into TextViews
        holder.NoProgram.setText(promosidatalist.get(position).getNoProgram());
        holder.Caption.setText(promosidatalist.get(position).getCaption());
        holder.Keterangan.setText(promosidatalist.get(position).getKeterangan());
        holder.Segment.setText(promosidatalist.get(position).getSegment());
        holder.TglBerlaku.setText(promosidatalist.get(position).getTglMulai() + " sd. " + promosidatalist.get(position).getTglAkhir());


        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        promosidatalist.clear();
        if (charText.equals("")) {
            for (Data_Promosi plg : arraylist)
            {
                promosidatalist.add(plg);
            }
        }
        else
        {
            for (Data_Promosi plg : arraylist)
            {
                if (plg.getKeterangan().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    promosidatalist.add(plg);
                }
            }
        }
        notifyDataSetChanged();
    }
}