package com.bcp.SFA_Native;

/**
 * Created by IT-SUPERMASTER on 28/10/2015.
 */
public class Data_Giro {
    private String No;
    private String Nominal;
    private String Bank;
    private String Tgl;

    public Data_Giro(String No,String Nominal, String Tgl,String Bank){
        this.No = No;
        this.Nominal = Nominal;
        this.Tgl = Tgl;
        this.Bank = Bank;
    }

    public String getNo() {
        return No;
    }

    public String getNominal() {
        return Nominal;
    }

    public String getTgl() {
        return Tgl;
    }

    public String getBank() {
        return Bank;
    }

    public void setBank(String bank) {
        Bank = bank;
    }

    public void setNo(String no) {
        No = no;
    }

    public void setNominal(String nominal) {
        Nominal = nominal;
    }

    public void setTgl(String tgl) {
        Tgl = tgl;
    }
}
