package com.bcp.SFA_Native;

/**
 * Created by IT-SUPERMASTER on 29/10/2015.
 */
public class Data_ReadyUpload {
    String Kodenota;
    String Jml;
    String JmlH;
    String Hit;

    public Data_ReadyUpload(String kodenota, String jml, String jmlH, String hit) {
        Kodenota = kodenota;
        Jml = jml;
        JmlH = jmlH;
        Hit = hit;
    }

    public String getKodenota() {
        return Kodenota;
    }

    public void setKodenota(String kodenota) {
        Kodenota = kodenota;
    }

    public String getJml() {
        return Jml;
    }

    public void setJml(String jml) {
        Jml = jml;
    }

    public String getJmlH() {
        return JmlH;
    }

    public void setJmlH(String jmlH) {
        JmlH = jmlH;
    }

    public String getHit() {
        return Hit;
    }

    public void setHit(String hit) {
        Hit = hit;
    }
}
