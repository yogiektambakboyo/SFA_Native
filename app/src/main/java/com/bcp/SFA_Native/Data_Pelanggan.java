package com.bcp.SFA_Native;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SUPERMASTER
 * Date: 8/12/14
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class Data_Pelanggan {
    private String ShipTo;
    private String Perusahaan;
    private String Alamat;
    private String Hari;


    public Data_Pelanggan(String ShipTo, String Perusahaan, String Alamat, String Hari){
        this.ShipTo = ShipTo;
        this.Perusahaan = Perusahaan;
        this.Alamat = Alamat;
        this.Hari = Hari;
    }

    public String getShipTo() {
        return ShipTo;
    }

    public String getPerusahaan() {
        return Perusahaan;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }

    public void setPerusahaan(String perusahaan) {
        Perusahaan = perusahaan;
    }

    public void setShipTo(String shipTo) {
        ShipTo = shipTo;
    }

    public String getHari() {
        return Hari;
    }

    public void setHari(String hari) {
        Hari = hari;
    }
}
