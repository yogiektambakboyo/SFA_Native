package com.bcp.SFA_Native;

public class Data_Barang {
    private String Kode;
    private String Nama;
    private String Keterangan;
    private String Merek;
    private String Variant;
    private String Assigned;
    private String AssignedImg;
    private String CRT;
    private String Harga;

    public Data_Barang(String Kode,String Nama,String Keterangan, String Merek,String Variant, String CRT,String Harga, String Assigned, String AssignedImg){
        this.Kode = Kode;
        this.Nama = Nama;
        this.Keterangan = Keterangan;
        this.Merek = Merek;
        this.Variant = Variant;
        this.CRT = CRT;
        this.Harga = Harga;
        this.Assigned = Assigned;
        this.AssignedImg = AssignedImg;
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
}
