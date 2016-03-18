package com.bcp.SFA_Native;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

public class AdapterBarangOrderListView extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<Data_BarangOrder> barangdatalist = null;
    private ArrayList<Data_BarangOrder> arraylist;

    public AdapterBarangOrderListView(Context context, List<Data_BarangOrder> barangdatalist) {
        mContext = context;
        this.barangdatalist = barangdatalist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Data_BarangOrder>();
        this.arraylist.addAll(barangdatalist);

    }

    public class ViewHolder {
        TextView SKU;
        TextView Hint;
        TextView HrgSatuan;
        TextView Assigned;
        TextView JmlCRT;
        TextView JmlPCS;
        ImageView AssignedImg;
        TextView JmlInv;
        TextView LasOrderQty;
        TextView ItemBarcode;
    }

    @Override
    public int getCount() {
        return barangdatalist.size();
    }

    public List<Data_BarangOrder> getBarangdatalist() {
        return barangdatalist;
    }

    @Override
    public Data_BarangOrder getItem(int position) {
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
            view = inflater.inflate(R.layout.l_barang_order, null);
            // Locate the TextViews in listview_item.xml
            holder.SKU = (TextView) view.findViewById(R.id.Order_TxtSKUCode);
            holder.Hint = (TextView) view.findViewById(R.id.Order_TxtHint);
            holder.HrgSatuan = (TextView) view.findViewById(R.id.Order_TxtPrice);
            holder.Assigned = (TextView) view.findViewById(R.id.Order_TxtAssigned);
            holder.AssignedImg = (ImageView) view.findViewById(R.id.Order_ImgCheck);
            holder.JmlCRT = (TextView) view.findViewById(R.id.Order_TxtCRT);
            holder.JmlPCS = (TextView) view.findViewById(R.id.Order_TxtPCS);
            holder.JmlInv = (TextView) view.findViewById(R.id.Order_TxtINV);
            holder.LasOrderQty = (TextView) view.findViewById(R.id.Order_TxtLastOrderQty);
            holder.ItemBarcode = (TextView) view.findViewById(R.id.Order_TxtItemBarcode);
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
        holder.JmlCRT.setText(barangdatalist.get(position).getJmlCRT());
        holder.JmlPCS.setText(barangdatalist.get(position).getJmlPCS());
        holder.JmlInv.setText(barangdatalist.get(position).getStok());
        holder.LasOrderQty.setText(barangdatalist.get(position).getLastOrderQty());
        holder.ItemBarcode.setText(barangdatalist.get(position).getItemBarcode());
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
            for (Data_BarangOrder brg : arraylist)
            {
                if ((brg.getKeterangan().toLowerCase(Locale.getDefault()).contains(charText))||(brg.getKode().toLowerCase(Locale.getDefault()).contains(charText))||(brg.getItemBarcode().toLowerCase(Locale.getDefault()).contains(charText)))
                {
                    if (brg.getMerek().toLowerCase(Locale.getDefault()).contains(Merek))
                    {
                        if (brg.getVariant().toLowerCase(Locale.getDefault()).contains(Variant)){
                            if (SalesFilter.equals("SEMUA")){
                                barangdatalist.add(brg);
                            } else if(SalesFilter.equals("ORDER")){
                                if((Integer.parseInt(brg.getJmlPCS())>0)||(Integer.parseInt(brg.getJmlCRT())>0)){
                                    barangdatalist.add(brg);
                                }
                            }else{
                                if (brg.getLast().equals("1")){
                                    barangdatalist.add(brg);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (SalesFilter.equals("ORDER")){
            Collections.sort(barangdatalist, new Comparator<Data_BarangOrder>() {
                @Override
                public int compare(Data_BarangOrder ord1, Data_BarangOrder ord2) {
                    return ord1.getTimeStamp().compareTo(ord2.getTimeStamp());
                }
            });
        }
        notifyDataSetChanged();
    }

    public int getTotalSKU() {
        int TotalSKU=0;
        for (Data_BarangOrder brg : arraylist)
        {
            if ((Integer.parseInt(brg.getJmlCRT().toString())>0)||(Integer.parseInt(brg.getJmlPCS().toString())>0)){
                 TotalSKU = TotalSKU + 1;
            }
        }
        return TotalSKU;
    }

    public float getTotalRp() {
        float TotalSKU=0f;
        for (Data_BarangOrder brg : arraylist)
        {
            if ((Integer.parseInt(brg.getJmlCRT().toString())>0)||(Integer.parseInt(brg.getJmlPCS().toString())>0)){
                TotalSKU = TotalSKU + (Integer.parseInt(brg.getJmlPCS())*Float.parseFloat(brg.getHarga())) + (Integer.parseInt(brg.getCRT())*Integer.parseInt(brg.getJmlCRT())*Float.parseFloat(brg.getHarga()));
            }
        }
        return TotalSKU;
    }

    public void filterRetur(String charText, String Merek, String Variant, String SalesFilter) {
        Merek = Merek.toLowerCase(Locale.getDefault());
        Variant = Variant.toLowerCase(Locale.getDefault());
        charText = charText.toLowerCase(Locale.getDefault());
        barangdatalist.clear();
        if ((SalesFilter.length()==0)&&(Merek.length() == 0)&&(Variant.length()==0)&&(charText.length() == 0)) {
            barangdatalist.addAll(arraylist);
        }
        else
        {
            for (Data_BarangOrder brg : arraylist)
            {
                if (brg.getKeterangan().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    if (brg.getMerek().toLowerCase(Locale.getDefault()).contains(Merek))
                    {
                        if (brg.getVariant().toLowerCase(Locale.getDefault()).contains(Variant)){
                            if (SalesFilter.equals("SEMUA")){
                                barangdatalist.add(brg);
                            } else if(SalesFilter.equals("RETUR")){
                                if((Integer.parseInt(brg.getJmlPCS())>0)||(Integer.parseInt(brg.getJmlCRT())>0)){
                                    barangdatalist.add(brg);
                                }
                            }else{
                                if (brg.getLast().equals("1")){
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

}