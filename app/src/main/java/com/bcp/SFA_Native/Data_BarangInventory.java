package com.bcp.SFA_Native;

public class Data_BarangInventory {
    private String Kode;
    private String Nama;
    private String Keterangan;
    private String Merek;
    private String Variant;
    private String Assigned;
    private String AssignedImg;
    private String Stok;
    private String OPT1;
    private String OPT2;
    private String OPT3;
    private String Itembarcode;

    public Data_BarangInventory(String Kode,String Nama,String Keterangan, String Merek,String Variant, String Assigned, String AssignedImg,String Stok,String OPT1, String OPT2, String OPT3, String ItemBarcode){
        this.Kode = Kode;
        this.Nama = Nama;
        this.Keterangan = Keterangan;
        this.Merek = Merek;
        this.Variant = Variant;
        this.Assigned = Assigned;
        this.AssignedImg = AssignedImg;
        this.Stok = Stok;
        this.OPT1 = OPT1;
        this.OPT2 = OPT2;
        this.OPT3 = OPT3;
        this.Itembarcode = ItemBarcode;
    }

    public String getItembarcode() {
        return Itembarcode;
    }

    public void setItembarcode(String itembarcode) {
        Itembarcode = itembarcode;
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

    public String getOPT1() {
        return OPT1;
    }

    public void setOPT1(String OPT1) {
        this.OPT1 = OPT1;
    }

    public String getOPT2() {
        return OPT2;
    }

    public void setOPT2(String OPT2) {
        this.OPT2 = OPT2;
    }

    public String getOPT3() {
        return OPT3;
    }

    public void setOPT3(String OPT3) {
        this.OPT3 = OPT3;
    }
}
