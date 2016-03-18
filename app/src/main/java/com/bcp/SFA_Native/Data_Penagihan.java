package com.bcp.SFA_Native;

/**
 * Created by IT-SUPERMASTER on 22/10/2015.
 */
public class Data_Penagihan {
    String Kodenota;
    String Tgl;
    String Collector;
    String Hit;

    public Data_Penagihan(String kodenota, String tgl, String collector,String hit) {
        Kodenota = kodenota;
        Tgl = tgl;
        Collector = collector;
        Hit = hit;
    }

    public String getKodenota() {
        return Kodenota;
    }

    public void setKodenota(String kodenota) {
        Kodenota = kodenota;
    }

    public String getTgl() {
        return Tgl;
    }

    public void setTgl(String tgl) {
        Tgl = tgl;
    }

    public String getCollector() {
        return Collector;
    }

    public void setCollector(String collector) {
        Collector = collector;
    }

    public String getHit() {
        return Hit;
    }

    public void setHit(String hit) {
        Hit = hit;
    }
}
