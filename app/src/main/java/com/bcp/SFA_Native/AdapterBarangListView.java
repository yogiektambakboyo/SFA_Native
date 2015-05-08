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

public class AdapterBarangListView extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Data_Barang> barangdatalist = null;
    private ArrayList<Data_Barang> arraylist;

    public AdapterBarangListView(Context context, List<Data_Barang> barangdatalist) {
        mContext = context;
        this.barangdatalist = barangdatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Data_Barang>();
        this.arraylist.addAll(barangdatalist);

    }

    public class ViewHolder {
        TextView SKU;
        TextView Hint;
        TextView HrgSatuan;
        TextView Assigned;
        ImageView AssignedImg;
    }

    @Override
    public int getCount() {
        return barangdatalist.size();
    }

    @Override
    public Data_Barang getItem(int position) {
        return barangdatalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.l_barang, null);
            // Locate the TextViews in listview_item.xml
            holder.SKU = (TextView) view.findViewById(R.id.Barang_TxtSKUCode);
            holder.Hint = (TextView) view.findViewById(R.id.Barang_TxtHint);
            holder.HrgSatuan = (TextView) view.findViewById(R.id.Barang_TxtPrice);
            holder.Assigned = (TextView) view.findViewById(R.id.Barang_TxtAssigned);
            holder.AssignedImg = (ImageView) view.findViewById(R.id.Barang_ImgCheck);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.SKU.setText(barangdatalist.get(position).getKode());
        holder.Hint.setText(barangdatalist.get(position).getKeterangan());

        Float hrg = (Float.parseFloat(barangdatalist.get(position).getHarga()));

        DecimalFormatSymbols symbol =
                new DecimalFormatSymbols(Locale.GERMANY);
        symbol.setCurrencySymbol("");

        //
        // Set the new DecimalFormatSymbols into formatter object.
        //

        DecimalFormat formatter = (DecimalFormat)
                NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formatter.setDecimalFormatSymbols(symbol);
        String currency = formatter.format(hrg);

        holder.HrgSatuan.setText(currency);
        holder.Assigned.setText(barangdatalist.get(position).getAssigned());
        holder.AssignedImg.setBackgroundResource(Integer.parseInt(barangdatalist.get(position).getAssignedImg()));
        return view;
    }

    public void filter(String charText, String Merek, String Variant) {
        Merek = Merek.toLowerCase(Locale.getDefault());
        Variant = Variant.toLowerCase(Locale.getDefault());
        charText = charText.toLowerCase(Locale.getDefault());
        barangdatalist.clear();
        if ((Merek.length() == 0)&&(Variant.length()==0)&&(charText.length() == 0)) {
            barangdatalist.addAll(arraylist);
        }
        else
        {
            for (Data_Barang brg : arraylist)
            {
                if (brg.getKeterangan().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    if (brg.getMerek().toLowerCase(Locale.getDefault()).contains(Merek))
                    {
                        if (brg.getVariant().toLowerCase(Locale.getDefault()).contains(Variant)){
                            barangdatalist.add(brg);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterMerekVar(String Merek,String Variant) {
        Merek = Merek.toLowerCase(Locale.getDefault());
        Variant = Variant.toLowerCase(Locale.getDefault());
        barangdatalist.clear();
        if ((Merek.length() == 0)&&(Variant.length()==0)) {
            barangdatalist.addAll(arraylist);
        }
        else
        {
            for (Data_Barang brg : arraylist)
            {
                if (brg.getMerek().toLowerCase(Locale.getDefault()).contains(Merek))
                {
                    if (brg.getVariant().toLowerCase(Locale.getDefault()).contains(Variant)){
                        barangdatalist.add(brg);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }
}