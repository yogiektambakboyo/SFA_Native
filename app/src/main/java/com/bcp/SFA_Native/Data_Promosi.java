package com.bcp.SFA_Native;

/**
 * Created by IT-SUPERMASTER on 18/08/2015.
 */
public class Data_Promosi {
    private String NoProgram;
    private String Caption;
    private String Keterangan;
    private String Segment;
    private String TglMulai;
    private String TglAkhir;

    public Data_Promosi(String noProgram, String caption, String keterangan, String segment, String tglMulai, String tglAkhir) {
        NoProgram = noProgram;
        Caption = caption;
        Keterangan = keterangan;
        Segment = segment;
        TglMulai = tglMulai;
        TglAkhir = tglAkhir;
    }

    public String getNoProgram() {
        return NoProgram;
    }

    public void setNoProgram(String noProgram) {
        NoProgram = noProgram;
    }

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }

    public String getSegment() {
        return Segment;
    }

    public void setSegment(String segment) {
        Segment = segment;
    }

    public String getTglMulai() {
        return TglMulai;
    }

    public void setTglMulai(String tglMulai) {
        TglMulai = tglMulai;
    }

    public String getTglAkhir() {
        return TglAkhir;
    }

    public void setTglAkhir(String tglAkhir) {
        TglAkhir = tglAkhir;
    }
}
