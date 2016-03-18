package com.bcp.SFA_Native;

public class Data_BarangOrder {
    private String Kode;
    private String Nama;
    private String Keterangan;
    private String Merek;
    private String Variant;
    private String Assigned;
    private String AssignedImg;
    private String CRT;
    private String Harga;
    private String JmlCRT;
    private String JmlPCS;
    private String Last;
    private String Stok;
    private String LastOrderQty;
    private String ItemBarcode;
    private String TimeStamp;

    public Data_BarangOrder(String Kode,String Nama,String Keterangan, String Merek,String Variant, String CRT,String Harga, String Assigned, String AssignedImg, String JmlCRT, String JmlPCS,String Last,String Stok,String LastOrderQty,String ItemBarcode,String TimeStamp){
        this.Kode = Kode;
        this.Nama = Nama;
        this.Keterangan = Keterangan;
        this.Merek = Merek;
        this.Variant = Variant;
        this.CRT = CRT;
        this.Harga = Harga;
        this.Assigned = Assigned;
        this.AssignedImg = AssignedImg;
        this.JmlCRT = JmlCRT;
        this.JmlPCS = JmlPCS;
        this.Last = Last;
        this.Stok = Stok;
        this.LastOrderQty = LastOrderQty;
        this.ItemBarcode = ItemBarcode;
        this.TimeStamp = TimeStamp;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getItemBarcode() {
        return ItemBarcode;
    }

    public void setItemBarcode(String itemBarcode) {
        ItemBarcode = itemBarcode;
    }

    public String getStok() {
        return Stok;
    }

    public void setStok(String stok) {
        Stok = stok;
    }

    public String getAssigned() {
        return Assigned;
    }

    public void setAssigned(String assigned) {
        Assigned = assigned;
    }

    public String getAssignedImg() {
        return AssignedImg;
    }

    public void setAssignedImg(String assignedImg) {
        AssignedImg = assignedImg;
    }

    public String getCRT() {
        return CRT;
    }

    public void setCRT(String CRT) {
        this.CRT = CRT;
    }

    public String getHarga() {
        return Harga;
    }

    public void setHarga(String harga) {
        Harga = harga;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }

    public String getKode() {
        return Kode;
    }

    public void setKode(String kode) {
        Kode = kode;
    }

    public String getMerek() {
        return Merek;
    }

    public void setMerek(String merek) {
        Merek = merek;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }

    public String getVariant() {
        return Variant;
    }

    public void setVariant(String variant) {
        Variant = variant;
    }

    public String getJmlCRT() {
        return JmlCRT;
    }

    public void setJmlCRT(String jmlCRT) {
        JmlCRT = jmlCRT;
    }

    public String getJmlPCS() {
        return JmlPCS;
    }

    public void setJmlPCS(String jmlPCS) {
        JmlPCS = jmlPCS;
    }

    public String getLast() {
        return Last;
    }

    public void setLast(String last) {
        Last = last;
    }

    public String getLastOrderQty() {
        return LastOrderQty;
    }

    public void setLastOrderQty(String lastOrderQty) {
        LastOrderQty = lastOrderQty;
    }
}
