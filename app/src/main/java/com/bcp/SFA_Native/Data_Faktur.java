package com.bcp.SFA_Native;

/**
 * Created by IT-SUPERMASTER on 23/10/2015.
 */
public class Data_Faktur {
    String kodenota,faktur,shipto,perusahaan,alamat,totalbayar,collector,hit;

    public Data_Faktur(String kodenota, String faktur, String shipto, String perusahaan, String alamat, String totalbayar,String collector,String hit) {
        this.kodenota = kodenota;
        this.faktur = faktur;
        this.shipto = shipto;
        this.perusahaan = perusahaan;
        this.alamat = alamat;
        this.totalbayar = totalbayar;
        this.collector = collector;
        this.hit = hit;
    }

    public String getHit() {
        return hit;
    }

    public void setHit(String hit) {
        this.hit = hit;
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector;
    }

    public String getKodenota() {
        return kodenota;
    }

    public void setKodenota(String kodenota) {
        this.kodenota = kodenota;
    }

    public String getFaktur() {
        return faktur;
    }

    public void setFaktur(String faktur) {
        this.faktur = faktur;
    }

    public String getShipto() {
        return shipto;
    }

    public void setShipto(String shipto) {
        this.shipto = shipto;
    }

    public String getPerusahaan() {
        return perusahaan;
    }

    public void setPerusahaan(String perusahaan) {
        this.perusahaan = perusahaan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTotalbayar() {
        return totalbayar;
    }

    public void setTotalbayar(String totalbayar) {
        this.totalbayar = totalbayar;
    }
}
