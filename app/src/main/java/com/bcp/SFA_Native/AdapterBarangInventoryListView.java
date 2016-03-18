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

public class AdapterBarangInventoryListView extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Data_BarangInventory> barangdatalist = null;
    private ArrayList<Data_BarangInventory> arraylist;

    public AdapterBarangInventoryListView(Context context, List<Data_BarangInventory> barangdatalist) {
        mContext = context;
        this.barangdatalist = barangdatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Data_BarangInventory>();
        this.arraylist.addAll(barangdatalist);
    }

    public class ViewHolder {
        TextView SKU;
        TextView Hint;
        TextView Assigned;
        TextView Stok;
        TextView OPT1;
        TextView OPT2;
        TextView OPT3;
        ImageView AssignedImg;
    }

    @Override
    public int getCount() {
        return barangdatalist.size();
    }

    public List<Data_BarangInventory> getBarangdatalist() {
        return barangdatalist;
    }

    @Override
    public Data_BarangInventory getItem(int position) {
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
            view = inflater.inflate(R.layout.l_baranginventory, null);
            // Locate the TextViews in listview_item.xml
            holder.SKU = (TextView) view.findViewById(R.id.Inv_TxtSKUCode);
            holder.Hint = (TextView) view.findViewById(R.id.Inv_TxtHint);
            holder.Assigned = (TextView) view.findViewById(R.id.Inv_TxtAssigned);
            holder.AssignedImg = (ImageView) view.findViewById(R.id.Inv_ImgCheck);
            holder.Stok= (TextView) view.findViewById(R.id.Inv_TxtStok);
            holder.OPT1 = (TextView) view.findViewById(R.id.Inv_TxtOPT1);
            holder.OPT2 = (TextView) view.findViewById(R.id.Inv_TxtOPT2);
            holder.OPT3 = (TextView) view.findViewById(R.id.Inv_TxtOPT3);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.SKU.setText(barangdatalist.get(position).getKode());
        holder.Hint.setText(barangdatalist.get(position).getKeterangan());

        holder.Assigned.setText(barangdatalist.get(position).getAssigned());
        holder.AssignedImg.setBackgroundResource(Integer.parseInt(barangdatalist.get(position).getAssignedImg()));
        holder.Stok.setText(barangdatalist.get(position).getStok());
        holder.OPT1.setText(barangdatalist.get(position).getOPT1());
        holder.OPT2.setText(barangdatalist.get(position).getOPT2());
        holder.OPT3.setText(barangdatalist.get(position).getOPT3());
        return view;
    }

    public void filter(String charText, String Merek, String Variant, String SalesFilter) {
        Merek = Merek.toLowerCase(Locale.getDefault());
        Variant = Variant.toLowerCase(Locale.getDefault());
        charText = charText.toLowerCase(Locale.getDefault());
        barangdatalist.clear();
        if ((SalesFilter.length()==0)&&(Merek.length() == 0)&&(Variant.length()==0)&&(charText.length() == 0)) {
            barangdatalist.addAll(arraylist);
        }
        else
        {
            for (Data_BarangInventory brg : arraylist)
            {
                if (brg.getKeterangan().toLowerCase(Locale.getDefault()).contains(charText)||(brg.getKode().toLowerCase(Locale.getDefault()).contains(charText))||(brg.getItembarcode().toLowerCase(Locale.getDefault()).contains(charText)))
                {
                    if (brg.getMerek().toLowerCase(Locale.getDefault()).contains(Merek))
                    {
                        if (brg.getVariant().toLowerCase(Locale.getDefault()).contains(Variant)){
                            if (SalesFilter.equals("SEMUA")){
                                barangdatalist.add(brg);
                            } else if(SalesFilter.equals("INVENTORY")){
                                if((Integer.parseInt(brg.getOPT1())>0)||(Integer.parseInt(brg.getOPT2())>0)||(Integer.parseInt(brg.getOPT3())>0)||(Integer.parseInt(brg.getStok())>0)){
                                    barangdatalist.add(brg);
                                }
                            }
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public int getTotalSKU() {
        int TotalSKU=0;
        for (Data_BarangInventory brg : arraylist)        {
            if((Integer.parseInt(brg.getOPT1())>0)||(Integer.parseInt(brg.getOPT2())>0)||(Integer.parseInt(brg.getOPT3())>0)||(Integer.parseInt(brg.getStok())>0)){
                TotalSKU = TotalSKU + 1;
            }
        }
        return TotalSKU;
    }

    public float getTotalRp() {
        float TotalSKU=0f;
        return TotalSKU;
    }

}
