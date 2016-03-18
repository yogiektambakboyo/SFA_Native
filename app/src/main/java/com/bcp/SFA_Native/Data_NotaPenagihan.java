package com.bcp.SFA_Native;

/**
 * Created by IT-SUPERMASTER on 21/10/2015.
 */
public class Data_NotaPenagihan {
    String Tgl;
    String Kodenota;
    String Faktur;
    String Collector;
    String NamaCollector;
    String ShipTo;
    String Perusahaan;
    String Alamat;
    String Brg;
    String Hint;
    String Keterangan;
    String Jml;
    String JmlCRT;
    String HrgSatuan;
    String DiscRp;
    String TotalBayar;
    String RasioMax;
    String OverDue;

    public Data_NotaPenagihan(String tgl, String kodenota, String faktur, String collector, String shipto, String perusahaan, String alamat, String brg, String hint, String keterangan, String jml, String jmlCRT, String hrgSatuan, String discRp, String totalBayar, String rasioMax, String namacollector,String overdue) {
        Tgl = tgl;
        Kodenota = kodenota;
        Faktur = faktur;
        Collector = collector;
        ShipTo = shipto;
        Perusahaan = perusahaan;
        Alamat = alamat;
        Brg = brg;
        Hint = hint;
        Keterangan = keterangan;
        Jml = jml;
        JmlCRT = jmlCRT;
        HrgSatuan = hrgSatuan;
        DiscRp = discRp;
        TotalBayar = totalBayar;
        RasioMax = rasioMax;
        NamaCollector = namacollector;
        OverDue = overdue;
    }

    public String getOverDue() {
        return OverDue;
    }

    public void setOverDue(String overDue) {
        OverDue = overDue;
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

    public String getCollector() {
        return Collector;
    }

    public void setCollector(String collector) {
        Collector = collector;
    }

    public String getShipTo() {
        return ShipTo;
    }

    public void setShipTo(String shipto) {
        ShipTo = shipto;
    }

    public String getPerusahaan() {
        return Perusahaan;
    }

    public void setPerusahaan(String perusahaan) {
        Perusahaan = perusahaan;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }

    public String getBrg() {
        return Brg;
    }

    public void setBrg(String brg) {
        Brg = brg;
    }

    public String getHint() {
        return Hint;
    }

    public void setHint(String hint) {
        Hint = hint;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }

    public String getJml() {
        return Jml;
    }

    public void setJml(String jml) {
        Jml = jml;
    }

    public String getJmlCRT() {
        return JmlCRT;
    }

    public void setJmlCRT(String jmlCRT) {
        JmlCRT = jmlCRT;
    }

    public String getHrgSatuan() {
        return HrgSatuan;
    }

    public void setHrgSatuan(String hrgSatuan) {
        HrgSatuan = hrgSatuan;
    }

    public String getDiscRp() {
        return DiscRp;
    }

    public void setDiscRp(String discRp) {
        DiscRp = discRp;
    }

    public String getTotalBayar() {
        return TotalBayar;
    }

    public void setTotalBayar(String totalBayar) {
        TotalBayar = totalBayar;
    }

    public String getRasioMax() {
        return RasioMax;
    }

    public void setRasioMax(String rasioMax) {
        RasioMax = rasioMax;
    }

    public String getNamaCollector() {
        return NamaCollector;
    }

    public void setNamaCollector(String namaCollector) {
        NamaCollector = namaCollector;
    }
}
