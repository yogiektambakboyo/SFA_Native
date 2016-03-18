package com.bcp.SFA_Native;

/**
 * Created by IT-SUPERMASTER on 25/02/2016.
 */
public class Data_QPFaktur {
    String Kodenota,Faktur,Tunai,BG,Stempel,TT,UC,Keterangan,TransferBank,TransferJml,TransferTgl,TotalBayar,Collector,Tgl,OverDue;

    public Data_QPFaktur(String kodenota, String faktur, String tunai, String BG, String stempel, String TT, String UC, String keterangan, String transferBank, String transferJml, String transferTgl, String totalBayar, String collector,String tgl,String overdue) {
        Kodenota = kodenota;
        Faktur = faktur;
        Tunai = tunai;
        this.BG = BG;
        Stempel = stempel;
        this.TT = TT;
        this.UC = UC;
        Keterangan = keterangan;
        TransferBank = transferBank;
        TransferJml = transferJml;
        TransferTgl = transferTgl;
        TotalBayar = totalBayar;
        Tgl = tgl;
        Collector = collector;
        OverDue = overdue;
    }

    public String getOverDue() {
        return OverDue;
    }

    public void setOverDue(String overDue) {
        OverDue = overDue;
    }

    public String getCollector() {
        return Collector;
    }

    public void setCollector(String collector) {
        Collector = collector;
    }

    public String getTgl() {
        return Tgl;
    }

    public void setTgl(String tgl) {
        Tgl = tgl;
    }

    public String getKodenota() {
        return Kodenota;
    }

    public void setKodenota(String kodenota) {
        Kodenota = kodenota;
    }

    public String getFaktur() {
        return Faktur;
    }

    public void setFaktur(String faktur) {
        Faktur = faktur;
    }

    public String getTunai() {
        return Tunai;
    }

    public void setTunai(String tunai) {
        Tunai = tunai;
    }

    public String getBG() {
        return BG;
    }

    public void setBG(String BG) {
        this.BG = BG;
    }

    public String getStempel() {
        return Stempel;
    }

    public void setStempel(String stempel) {
        Stempel = stempel;
    }

    public String getTT() {
        return TT;
    }

    public void setTT(String TT) {
        this.TT = TT;
    }

    public String getUC() {
        return UC;
    }

    public void setUC(String UC) {
        this.UC = UC;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }

    public String getTransferBank() {
        return TransferBank;
    }

    public void setTransferBank(String transferBank) {
        TransferBank = transferBank;
    }

    public String getTransferJml() {
        return TransferJml;
    }

    public void setTransferJml(String transferJml) {
        TransferJml = transferJml;
    }

    public String getTransferTgl() {
        return TransferTgl;
    }

    public void setTransferTgl(String transferTgl) {
        TransferTgl = transferTgl;
    }

    public String getTotalBayar() {
        return TotalBayar;
    }

    public void setTotalBayar(String totalBayar) {
        TotalBayar = totalBayar;
    }
}
