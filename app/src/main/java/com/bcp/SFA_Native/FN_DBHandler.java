package com.bcp.SFA_Native;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FN_DBHandler extends SQLiteOpenHelper {
    private final String TAG_PELANGANDATA= "PelangganData";
    private final String TAG_BARANGDATA= "BarangData";
    private final String TAG_STATUS = "status";
    private final String TAG_PENAGIHAN = "penagihan";


    public FN_DBHandler(Context context, String DB_PATH, String DB_NAME) {
        super(context, DB_PATH+ File.separator +DB_NAME, null, 1);
    }

    // Date n Time

    public String getToday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public String getToday2(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return  formattedDate;
    }

    public String getDateTime(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                format, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedTime = df.format(c.getTime());
        return  formattedTime;
    }

    // End

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //---------------------- Setting -----------------------------//
    public void CreateSetting(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Pengaturan");
        db.execSQL("CREATE TABLE IF NOT EXISTS Pengaturan "
                + "(lastlogin TEXT,namelogin TEXT,tgllogin DATETIME,tgllogout DATETIME,"
                + "mode TEXT,web TEXT,ip TEXT,cabang TEXT,userip TEXT,passip TEXT, minorder FLOAT,appversion INTEGER, dbversion INTEGER,email TEXT)");
    }

    public void InsertSetting(String WebT, int appversion, int dbversion,String ModeApp,String Cabang){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Pengaturan(lastlogin,namelogin,tgllogin,tgllogout,web,mode,appversion,dbversion,cabang,email) VALUES('','','2015-01-01','2015-01-01','"+WebT+"','"+ModeApp+"',"+appversion+","+dbversion+",'"+Cabang+"','test@mail.com')");
    }

    public void UpdateSettingAppVersion(String update){
        Integer appversion = Integer.parseInt(update);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET appversion="+appversion);
    }

    public void UpdateSettingDBVersion(String update){
        Integer dbversion = Integer.parseInt(update);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET dbversion="+dbversion);
    }
    public void UpdateSetting(String lastlogin,String namelogin){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET lastlogin='"+lastlogin+"',namelogin='"+namelogin+"',tgllogin=datetime('now', 'localtime')");
    }
    public void UpdateSettingTglLogin(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET tgllogin=datetime('now', 'localtime')");
    }
    public void UpdateSettingFull(String WebT,String Mode,String Cabang,String Email){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET web='" + WebT + "',mode='" + Mode + "',cabang='" + Cabang + "',email='" + Email + "'");
    }
    public JSONObject GetSetting() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT lastlogin,namelogin,web,appversion,dbversion,mode,cabang,minorder,email,date(tgllogin) tgllogin FROM Pengaturan";

        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        if (cursor.moveToFirst()){
            jsonresult.put("status",1);
            jsonresult.put("lastlogin", cursor.getString(cursor.getColumnIndex("lastlogin")));
            jsonresult.put("namelogin", cursor.getString(cursor.getColumnIndex("namelogin")));
            jsonresult.put("web", cursor.getString(cursor.getColumnIndex("web")));
            jsonresult.put("appversion", cursor.getInt(cursor.getColumnIndex("appversion")));
            jsonresult.put("dbversion", cursor.getInt(cursor.getColumnIndex("dbversion")));
            jsonresult.put("mode", cursor.getString(cursor.getColumnIndex("mode")));
            jsonresult.put("cabang", cursor.getString(cursor.getColumnIndex("cabang")));
            jsonresult.put("minorder", cursor.getString(cursor.getColumnIndex("minorder")));
            jsonresult.put("email", cursor.getString(cursor.getColumnIndex("email")));
            jsonresult.put("tgllogin", cursor.getString(cursor.getColumnIndex("tgllogin")));
        }
        else{
            jsonresult.put("status",0);
        }
        cursor.close();
        return jsonresult;
    }

    // End Setting

    //------------------ get Parameter --------//

    public JSONObject GetInvParameter() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode,opt1,opt2,opt3 from InvParameter";
        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("opt1", cursor.getString(cursor.getColumnIndex("opt1")));
                JData.put("opt2", cursor.getString(cursor.getColumnIndex("opt2")));
                JData.put("opt3", cursor.getString(cursor.getColumnIndex("opt3")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    //------------------ Master----------------//

    public void CreateMaster(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Pelanggan");
        db.execSQL("DROP TABLE IF EXISTS Barang");
        db.execSQL("DROP TABLE IF EXISTS FJP");
        db.execSQL("DROP TABLE IF EXISTS HistoryTransaksi");
        db.execSQL("CREATE TABLE IF NOT EXISTS Pelanggan(kode TEXT PRIMARY KEY,perusahaan TEXT,alamat TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Barang(kode TEXT PRIMARY KEY,nama TEXT,keterangan TEXT,merek TEXT,variant TEXT,crt INT,harga FLOAT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS FJP(kode TEXT,hari TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS HistoryTransaksi(kode TEXT,brg TEXT)");
    }

    //----------------------------- PELANGGAN -----------------------------------------//

    public JSONObject getPelanggan(String Week) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select p.kode,p.perusahaan,p.alamat,IFNULL(f.hari,'semua') as hari from Pelanggan p LEFT JOIN FJP  f ON f.kode=p.kode and f.minggu='"+Week+"'  ORDER BY p.perusahaan ASC";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                JData.put("hari", cursor.getString(cursor.getColumnIndex("hari")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    public JSONObject getPelangganOne(String kode) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select kode,alamat,saldolimit,saldoaktif,segment from Pelanggan Where kode='"+kode+"'";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                JData.put("saldolimit", cursor.getString(cursor.getColumnIndex("saldolimit")));
                JData.put("saldoaktif", cursor.getString(cursor.getColumnIndex("saldoaktif")));
                JData.put("segment", cursor.getString(cursor.getColumnIndex("segment")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    //-------------------- BARANG -----------------------//

    public JSONObject GetBarang() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select *,'0' as jmlcrt,'0' as jmlpcs from Barang";
        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("nama", cursor.getString(cursor.getColumnIndex("nama")));
                JData.put("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("merek", cursor.getString(cursor.getColumnIndex("merek")));
                JData.put("variant", cursor.getString(cursor.getColumnIndex("variant")));
                JData.put("crt", cursor.getInt(cursor.getColumnIndex("crt")));
                JData.put("harga", cursor.getFloat(cursor.getColumnIndex("harga")));
                JData.put("jmlcrt", cursor.getString(cursor.getColumnIndex("jmlcrt")));
                JData.put("jmlpcs", cursor.getString(cursor.getColumnIndex("jmlpcs")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_BARANGDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    public JSONObject GetBarangOrder(String DB_PATH,String DB_NAME,String ShipTo) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dborder");
        String sql="select b.*,'0' as jmlcrt,'0' as jmlpcs,'1' as last,IFNULL(i.stok,'0') as stok,IFNULL(h.jml,'0') as lastorderqty,b.itembarcode  from barang b JOIN HistoryTransaksi h ON h.brg=b.kode LEFT JOIN dborder.Inventory i on i.brg=b.kode and i.shipto=h.kode and date(i.tgl)=date('now','localtime') where h.kode like '"+ShipTo+"' " +
                "union all " +
                " select b.*,'0' as jmlcrt,'0' as jmlpcs,'0' as last,IFNULL(i.stok,'0') as stok,'0' as lastorderqty,b.itembarcode  from barang b LEFT JOIN dborder.Inventory i on i.brg=b.kode and i.shipto='"+ShipTo+"' and date(i.tgl)=date('now','localtime') where b.kode not in (select brg from HistoryTransaksi where  kode like '"+ShipTo+"')";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("nama", cursor.getString(cursor.getColumnIndex("nama")));
                JData.put("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("merek", cursor.getString(cursor.getColumnIndex("merek")));
                JData.put("variant", cursor.getString(cursor.getColumnIndex("variant")));
                JData.put("crt", cursor.getInt(cursor.getColumnIndex("crt")));
                JData.put("harga", cursor.getFloat(cursor.getColumnIndex("harga")));
                JData.put("jmlcrt", cursor.getString(cursor.getColumnIndex("jmlcrt")));
                JData.put("jmlpcs", cursor.getString(cursor.getColumnIndex("jmlpcs")));
                JData.put("last", cursor.getString(cursor.getColumnIndex("last")));
                JData.put("stok", cursor.getString(cursor.getColumnIndex("stok")));
                JData.put("lastorderqty", cursor.getString(cursor.getColumnIndex("lastorderqty")));
                JData.put("itembarcode", cursor.getString(cursor.getColumnIndex("itembarcode")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_BARANGDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    public String[] GetMerek() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select count(distinct merek)+1 as jml from barang";
        Cursor cursor = db.rawQuery(sql, null);

        int counter = 0;

        if (cursor.moveToFirst()){
            do {
                counter = cursor.getInt(cursor.getColumnIndex("jml"));
            } while (cursor.moveToNext());
        }

        String sql2="select distinct merek as merek from barang order by merek desc";
        Cursor cursor2 = db.rawQuery(sql2, null);

        String[] Merek = new String[counter];
        Merek[0] = "SEMUA";
        if (cursor2.moveToFirst()){
            do {
                counter--;
                Merek[counter] = cursor2.getString(cursor2.getColumnIndex("merek"));
            } while (cursor2.moveToNext());
        }
        cursor.close();
        return Merek;
    }

    public String[] GetVariant() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select count(distinct variant)+1 as jml from barang";
        Cursor cursor = db.rawQuery(sql, null);

        int counter = 0;

        if (cursor.moveToFirst()){
            do {
                counter = cursor.getInt(cursor.getColumnIndex("jml"));
            } while (cursor.moveToNext());
        }

        String sql2="select distinct variant as variant from barang order by variant desc";
        Cursor cursor2 = db.rawQuery(sql2, null);

        String[] Variant = new String[counter];
        Variant[0] = "SEMUA";
        if (cursor2.moveToFirst()){
            do {
                counter--;
                Variant[counter] = cursor2.getString(cursor2.getColumnIndex("variant"));
            } while (cursor2.moveToNext());
        }
        cursor.close();
        return Variant;
    }

    public String[] GetVariantByMerek(String Merek) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select count(distinct variant)+1 as jml from barang where merek='"+Merek+"'";
        Cursor cursor = db.rawQuery(sql, null);

        int counter = 0;

        if (cursor.moveToFirst()){
            do {
                counter = cursor.getInt(cursor.getColumnIndex("jml"));
            } while (cursor.moveToNext());
        }

        String sql2="select distinct variant as variant from barang  where merek='"+Merek+"' order by variant desc";
        Cursor cursor2 = db.rawQuery(sql2, null);

        String[] Variant = new String[counter];
        Variant[0] = "SEMUA";
        if (cursor2.moveToFirst()){
            do {
                counter--;
                Variant[counter] = cursor2.getString(cursor2.getColumnIndex("variant"));
            } while (cursor2.moveToNext());
        }
        cursor.close();
        return Variant;
    }

    //----------------- ORDER -----------------------//

    public int getCekExistOrderPelanggan(String pelanggan,String tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode FROM Penjualan WHERE shipto='"+pelanggan+"' and date(tgl)='"+tgl+"'";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public void createOrder(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Penjualan");
        db.execSQL("DROP TABLE IF EXISTS Retur");
        db.execSQL("DROP TABLE IF EXISTS Inventory");
        db.execSQL("DROP TABLE IF EXISTS Kunjungan");
        db.execSQL("DROP TABLE IF EXISTS HPenagihan");
        db.execSQL("CREATE TABLE IF NOT EXISTS Penjualan(kode TEXT,sales TEXT,sr TEXT,shipto TEXT,brg TEXT,crt INT,pcs INT,total DOUBLE,tgl DATETIME,tglupload DATETIME,sync INT,ket TEXT,entrytime DATETIME,longitude TEXT,latitude TEXT,seq TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Retur(kode TEXT,sales TEXT,sr TEXT,shipto TEXT,brg TEXT,crt INT,pcs INT,tgl DATETIME,tglupload DATETIME,sync INT,alasan TEXT,entrytime DATETIME)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Inventory(kode TEXT,sales TEXT,sr TEXT,shipto TEXT,brg TEXT,stok INT,opt1 INT,opt2 INT,opt3 INT,tgl DATETIME,sync INT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Kunjungan(tgl DATETIME,sales TEXT,shipto TEXT,call TEXT,pcall TEXT,deviasi TEXT,uncall TEXT,reason TEXT,instore DATETIME,outstore DATETIME,longitude TEXT,latitude TEXT,gpstime DATETIME,reverse TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Penagihan(tgl DATETIME,kodenota TEXT,faktur TEXT,collector TEXT,shipto TEXT,perusahaan TEXT,alamat TEXT,brg TEXT,hint TEXT,keterangan TEXT,jml INT,jmlcrt TEXT,hrgsatuan FLOAT,discrp FLOAT, totalbayar TEXT,rasiomax INT,namacollector TEXT,createdate DATETIME,overdue TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS HPenagihan(kodenota TEXT,faktur TEXT,tgl DATETIME,collector TEXT,tunai TEXT,bg TEXT,stempel INTEGER,tt INTEGER,uc INTEGER,keterangan TEXT,shipto TEXT,tglupload DATETIME,longitude TEXT,latitude TEXT,transferbank TEXT,transferjml TEXT,transfertgl DATETIME,startentry DATETIME,sync INT,createdate DATETIME)");
    }

    public void updateMinOrder(String minorder){
        Float MinOrder = Float.parseFloat(minorder);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET minorder=" + MinOrder);
    }

    public String getmaxkode(String kode){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode AS kode FROM Penjualan WHERE kode like '"+kode+"%'";
        Cursor cursor = db.rawQuery(sql, null);
        String kodefinal="0000";
        if(cursor.getCount()>0){
            String sql2="SELECT MAX(kode) AS kode FROM Penjualan WHERE kode like '"+kode+"%'";
            Cursor cursor2 = db.rawQuery(sql2, null);
            cursor2.moveToFirst();
            kodefinal=cursor2.getString(cursor2.getColumnIndex("kode"));
            kodefinal = kodefinal.substring(kodefinal.length()-3,kodefinal.length());
        }
        cursor.close();
        return kodefinal;
    }

    public void insertOrder(String kode,String sales,String sr,String shipto,String brg,int crt,int pcs,double total,String ket,String entrytime,String longitude,String latitude,String seq){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Penjualan VALUES('" + kode + "','" + sales + "','" + sr + "','" + shipto + "','" + brg + "'," + crt + "," + pcs + "," + total + ",datetime('now', 'localtime'),null,1,'" + ket + "','" + entrytime + "','" + longitude + "','" + latitude + "','" + seq + "')");
    }

    public JSONObject getAllOrderBeforeSync(String DB_PATH,String DB_NAME) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dbmaster ");//attach otherdb
        String sql="SELECT * FROM (SELECT j.kode,j.shipto,p.perusahaan,SUM(j.total) AS total "
                + "FROM Penjualan j "
                + "INNER JOIN dbmaster.Pelanggan p "
                + "ON j.shipto=p.kode "
                + "WHERE j.tglupload IS NULL AND j.sync=1 "
                + "GROUP BY j.kode,j.shipto,p.perusahaan "
                + "ORDER BY p.perusahaan) a order by kode";

        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        JSONArray jArray=new JSONArray();
        if (cursor.moveToFirst()){
            jsonresult.put(TAG_STATUS,1);
            do {
                JSONObject json_data = new JSONObject();
                json_data.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                json_data.put("shipto", cursor.getString(cursor.getColumnIndex("shipto")));
                json_data.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                json_data.put("total", cursor.getString(cursor.getColumnIndex("total")));
                jArray.put(json_data);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_PELANGANDATA, jArray);
        }
        else{
            jsonresult.put(TAG_PELANGANDATA,0);
        }
        db.execSQL("DETACH DATABASE dbmaster ");
        cursor.close();
        return jsonresult;
    }

    public JSONObject getDetailOrderBeforeSync(String DB_PATH,String DB_NAME,String ShipTo,String KodeOrder) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dborder");
        String sql="SELECT b.*,IFNULL(p.crt,'0') as jmlcrt, IFNULL(p.pcs,'0') as jmlpcs,'0' as last,IFNULL(i.stok,'0') as stok,'0' as lastorderqty,b.itembarcode from Barang b JOIN dborder.Penjualan p on p.brg=b.kode LEFT JOIN dborder.Inventory i on i.brg=b.kode and i.shipto=p.shipto and date(i.tgl)=date(p.tgl) where p.shipto='"+ShipTo+"' and p.kode='"+KodeOrder+"'" +
                "UNION ALL SELECT b.*,'0' as jmlcrt,'0' as jmlpcs,'0' as last,IFNULL(i.stok,'0') as stok,'0' as lastorderqty,b.itembarcode from barang b LEFT JOIN dborder.Inventory i on i.brg=b.kode and i.shipto='"+ShipTo+"' and date(i.tgl)=date('now','localtime') WHERE b.kode not in (SELECT brg from penjualan WHERE shipto='"+ShipTo+"' and kode='"+KodeOrder+"')";
        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        JSONArray jArray=new JSONArray();
        if (cursor.moveToFirst()){
            jsonresult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("nama", cursor.getString(cursor.getColumnIndex("nama")));
                JData.put("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("merek", cursor.getString(cursor.getColumnIndex("merek")));
                JData.put("variant", cursor.getString(cursor.getColumnIndex("variant")));
                JData.put("crt", cursor.getInt(cursor.getColumnIndex("crt")));
                JData.put("harga", cursor.getFloat(cursor.getColumnIndex("harga")));
                JData.put("jmlcrt", cursor.getString(cursor.getColumnIndex("jmlcrt")));
                JData.put("jmlpcs", cursor.getString(cursor.getColumnIndex("jmlpcs")));
                JData.put("last", cursor.getString(cursor.getColumnIndex("last")));
                JData.put("stok", cursor.getString(cursor.getColumnIndex("stok")));
                JData.put("lastorderqty", cursor.getString(cursor.getColumnIndex("lastorderqty")));
                JData.put("itembarcode", cursor.getString(cursor.getColumnIndex("itembarcode")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_BARANGDATA, jArray);
        }
        else{
            jsonresult.put(TAG_STATUS,0);
        }
        db.execSQL("DETACH DATABASE dborder ");
        cursor.close();
        return jsonresult;
    }

    public JSONObject getAllOrderAfterSync(String DB_PATH,String DB_NAME,String tgl) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dbmaster ");//attach otherdb
        String sql="SELECT * FROM (SELECT j.kode,j.shipto,p.perusahaan,SUM(j.total) AS total "
                + "FROM Penjualan j "
                + "INNER JOIN dbmaster.Pelanggan p "
                + "ON j.shipto=p.kode "
                + "WHERE date(j.tgl)='"+tgl+"' AND j.sync=0 "
                + "GROUP BY j.kode,j.shipto,p.perusahaan "
                + "ORDER BY p.perusahaan) a order by kode";
        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        JSONArray jArray=new JSONArray();
        if (cursor.moveToFirst()){
            jsonresult.put("status",1);
            do {
                JSONObject json_data = new JSONObject();
                json_data.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                json_data.put("shipto", cursor.getString(cursor.getColumnIndex("shipto")));
                json_data.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                json_data.put("total", cursor.getDouble(cursor.getColumnIndex("total")));
                jArray.put(json_data);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_PELANGANDATA, jArray);
        }
        else{
            jsonresult.put(TAG_STATUS,0);
        }
        db.execSQL("DETACH DATABASE dbmaster ");
        cursor.close();
        return jsonresult;
    }


    public void deleteOrder(String Kode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Penjualan WHERE Kode='" + Kode + "'");
    }

    public Cursor getAllRawPenjualan(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode,sales,shipto,brg,crt,pcs,total,strftime('%m/%d/%Y', tgl) AS tgl,ket,entrytime,longitude,latitude,seq From Penjualan WHERE sync=1 order by kode,seq asc";

        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public void updateFlagOrder(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Penjualan SET tglupload=datetime('now', 'localtime'),sync=0 where sync=1");
    }

    public int getCekExistOrderPelangganNotSync(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode FROM Penjualan WHERE sync=1";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public void updateOpenFlagOrder(String kode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Penjualan SET sync=1 where kode='" + kode + "'");
    }

    public String cekColumnExistPengaturan(String TabelName,String ColumnName){
        String result = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "PRAGMA table_info("+TabelName+")";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            do {
                if ((cursor.getString(cursor.getColumnIndex("name"))).equals(ColumnName)){
                    result="1";
                }
            } while (cursor.moveToNext());
        }
        else{
            result = "0";
        }
        cursor.close();
        return result;
    }

    public void addColumnPengaturan(String TabelName,String ColumnName,String TipeData){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE " + TabelName + " ADD " + ColumnName + " " + TipeData);
    }

    // -------------------- Inventory --------------------------------- //
    public String getMaxKodeInv(String kode){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode AS kode FROM Inventory WHERE kode like '"+kode+"%'";
        Cursor cursor = db.rawQuery(sql, null);
        String kodefinal="0000";
        if(cursor.getCount()>0){
            String sql2="SELECT MAX(kode) AS kode FROM Inventory WHERE kode like '"+kode+"%'";
            Cursor cursor2 = db.rawQuery(sql2, null);
            cursor2.moveToFirst();
            kodefinal=cursor2.getString(cursor2.getColumnIndex("kode"));
            kodefinal = kodefinal.substring(kodefinal.length()-3,kodefinal.length());
        }
        cursor.close();
        return kodefinal;
    }

    public void insertInventory(String kode,String sales,String sr,String shipto,String brg,int stok,int opt1,int opt2,int opt3){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Inventory VALUES('" + kode + "','" + sales + "','" + sr + "','" + shipto + "','" + brg + "'," + stok + "," + opt1 + "," + opt2 + "," + opt3 + ",datetime('now', 'localtime'),1)");
    }

    public void deleteInventory(String Kode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Inventory WHERE Kode='"+Kode+"'");
    }

    public int getCekExistInventoryPelanggan(String pelanggan,String tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode FROM Inventory WHERE shipto='"+pelanggan+"' and date(tgl)='"+tgl+"'";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public int getCekExistInventoryPelangganNotSync(String pelanggan,String tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode FROM Inventory WHERE shipto='"+pelanggan+"' and date(tgl)='"+tgl+"' and sync=1";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public String getKodenotaInventoryPelanggan(String pelanggan,String tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode FROM Inventory WHERE shipto='"+pelanggan+"' and date(tgl)='"+tgl+"'";
        String kd = "";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            kd = cursor.getString(cursor.getColumnIndex("kode"));
        }while (cursor.moveToNext());
        cursor.close();
        return kd;
    }

    public JSONObject getDetailInventoryEdit(String DB_PATH,String DB_NAME,String ShipTo,String KodeOrder) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dborder");
        String sql="SELECT b.*,IFNULL(p.stok,'0') as stok,IFNULL(p.opt1,'0') as opt1, IFNULL(p.opt2,'0') as opt2,IFNULL(p.opt3,'0') as opt3 from Barang b JOIN dborder.Inventory p on p.brg=b.kode where p.shipto='"+ShipTo+"' and p.kode='"+KodeOrder+"'" +
                "UNION ALL SELECT *,'0' as stok,'0' as opt1,'0' as opt2,'0' as opt3 from barang WHERE kode not in (SELECT brg from Inventory WHERE shipto='"+ShipTo+"' and kode='"+KodeOrder+"')";

        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        JSONArray jArray=new JSONArray();
        if (cursor.moveToFirst()){
            jsonresult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("nama", cursor.getString(cursor.getColumnIndex("nama")));
                JData.put("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("merek", cursor.getString(cursor.getColumnIndex("merek")));
                JData.put("variant", cursor.getString(cursor.getColumnIndex("variant")));
                JData.put("stok", cursor.getString(cursor.getColumnIndex("stok")));
                JData.put("opt1", cursor.getString(cursor.getColumnIndex("opt1")));
                JData.put("opt2", cursor.getString(cursor.getColumnIndex("opt2")));
                JData.put("opt3", cursor.getString(cursor.getColumnIndex("opt3")));
                JData.put("itembarcode", cursor.getString(cursor.getColumnIndex("itembarcode")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_BARANGDATA, jArray);
        }
        else{
            jsonresult.put(TAG_STATUS,0);
        }
        db.execSQL("DETACH DATABASE dborder");
        cursor.close();
        return jsonresult;
    }

    public JSONObject GetBarangInventory() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select *,'0' as stok,'0' as opt1,'0' as opt2,'0' as opt3 from Barang";
        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("nama", cursor.getString(cursor.getColumnIndex("nama")));
                JData.put("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("merek", cursor.getString(cursor.getColumnIndex("merek")));
                JData.put("variant", cursor.getString(cursor.getColumnIndex("variant")));
                JData.put("stok", cursor.getString(cursor.getColumnIndex("stok")));
                JData.put("opt1", cursor.getString(cursor.getColumnIndex("opt1")));
                JData.put("opt2", cursor.getString(cursor.getColumnIndex("opt2")));
                JData.put("opt3", cursor.getString(cursor.getColumnIndex("opt3")));
                JData.put("itembarcode", cursor.getString(cursor.getColumnIndex("itembarcode")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_BARANGDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    public Cursor getAllRawInventory(String tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode,sales,shipto,brg,strftime('%m/%d/%Y', tgl) AS tgl,stok,opt1,opt2,opt3 From Inventory WHERE date(tgl)='"+tgl+"' and sync=1";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public int getCekExistCountInventoryPelanggan(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode FROM Inventory WHERE sync=1";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public void updateFlagInventory(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Inventory SET sync=0 where sync=1");
    }

    public void updateOpenFlagInventory(String kode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Inventory SET sync=1 where kode='" + kode + "'");
    }

    public JSONObject getAllInvBeforeSync(String DB_PATH,String DB_NAME) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dbmaster ");//attach otherdb
        String sql="SELECT * FROM (SELECT j.kode,j.shipto,p.perusahaan,'0' AS total "
                + "FROM Inventory j "
                + "INNER JOIN dbmaster.Pelanggan p "
                + "ON j.shipto=p.kode "
                + "WHERE j.sync=1 "
                + "GROUP BY j.kode,j.shipto,p.perusahaan "
                + "ORDER BY p.perusahaan) a order by kode";

        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        JSONArray jArray=new JSONArray();
        if (cursor.moveToFirst()){
            jsonresult.put(TAG_STATUS,1);
            do {
                JSONObject json_data = new JSONObject();
                json_data.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                json_data.put("shipto", cursor.getString(cursor.getColumnIndex("shipto")));
                json_data.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                json_data.put("total", cursor.getString(cursor.getColumnIndex("total")));
                jArray.put(json_data);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_PELANGANDATA, jArray);
        }
        else{
            jsonresult.put(TAG_PELANGANDATA,0);
        }
        db.execSQL("DETACH DATABASE dbmaster ");
        cursor.close();
        return jsonresult;
    }

    public JSONObject getAllInvAfterSync(String DB_PATH,String DB_NAME,String tgl) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dbmaster ");//attach otherdb
        String sql="SELECT * FROM (SELECT j.kode,j.shipto,p.perusahaan,'0' AS total "
                + "FROM Inventory j "
                + "INNER JOIN dbmaster.Pelanggan p "
                + "ON j.shipto=p.kode "
                + "WHERE date(j.tgl)='"+tgl+"' AND j.sync=0 "
                + "GROUP BY j.kode,j.shipto,p.perusahaan "
                + "ORDER BY p.perusahaan) a order by kode";
        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        JSONArray jArray=new JSONArray();
        if (cursor.moveToFirst()){
            jsonresult.put("status",1);
            do {
                JSONObject json_data = new JSONObject();
                json_data.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                json_data.put("shipto", cursor.getString(cursor.getColumnIndex("shipto")));
                json_data.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                json_data.put("total", cursor.getDouble(cursor.getColumnIndex("total")));
                jArray.put(json_data);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_PELANGANDATA, jArray);
        }
        else{
            jsonresult.put(TAG_STATUS,0);
        }
        db.execSQL("DETACH DATABASE dbmaster");
        cursor.close();
        return jsonresult;
    }

    public void deleteInv(String Kode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Inventory WHERE Kode='" + Kode + "'");
    }

    //------------------- Retur --------------------//
    public JSONObject getDetailReturBeforeSync(String DB_PATH,String DB_NAME,String ShipTo,String KodeOrder) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dborder");
        String sql="SELECT b.*,IFNULL(p.crt,'0') as jmlcrt, IFNULL(p.pcs,'0') as jmlpcs,'0' as last,IFNULL(i.stok,'0') as stok from Barang b JOIN dborder.Retur p on p.brg=b.kode LEFT JOIN dborder.Inventory i on i.brg=b.kode and i.shipto=p.shipto and date(i.tgl)=date(p.tgl) where p.shipto='"+ShipTo+"' and p.kode='"+KodeOrder+"'" +
                " UNION ALL SELECT b.*,'0' as jmlcrt,'0' as jmlpcs,'0' as last,IFNULL(i.stok,'0') as stok from barang b  LEFT JOIN dborder.Inventory i on i.brg=b.kode and i.shipto='"+ShipTo+"' and date(i.tgl)=date('now','localtime') WHERE b.kode not in (SELECT brg from penjualan WHERE shipto='"+ShipTo+"' and kode='"+KodeOrder+"')";

        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        JSONArray jArray=new JSONArray();
        if (cursor.moveToFirst()){
            jsonresult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                JData.put("nama", cursor.getString(cursor.getColumnIndex("nama")));
                JData.put("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("merek", cursor.getString(cursor.getColumnIndex("merek")));
                JData.put("variant", cursor.getString(cursor.getColumnIndex("variant")));
                JData.put("crt", cursor.getInt(cursor.getColumnIndex("crt")));
                JData.put("harga", cursor.getFloat(cursor.getColumnIndex("harga")));
                JData.put("jmlcrt", cursor.getString(cursor.getColumnIndex("jmlcrt")));
                JData.put("jmlpcs", cursor.getString(cursor.getColumnIndex("jmlpcs")));
                JData.put("last", cursor.getString(cursor.getColumnIndex("last")));
                JData.put("stok", cursor.getString(cursor.getColumnIndex("stok")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_BARANGDATA, jArray);
        }
        else{
            jsonresult.put(TAG_STATUS,0);
        }
        db.execSQL("DETACH DATABASE dborder ");
        cursor.close();
        return jsonresult;
    }

    public String getmaxkoderetur(String kode){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode AS kode FROM Retur WHERE kode like '"+kode+"%'";
        Cursor cursor = db.rawQuery(sql, null);
        String kodefinal="0000";
        if(cursor.getCount()>0){
            String sql2="SELECT MAX(kode) AS kode FROM Retur WHERE kode like '"+kode+"%'";
            Cursor cursor2 = db.rawQuery(sql2, null);
            cursor2.moveToFirst();
            kodefinal=cursor2.getString(cursor2.getColumnIndex("kode"));
            kodefinal = kodefinal.substring(kodefinal.length()-3,kodefinal.length());
        }
        cursor.close();
        return kodefinal;
    }

    public void insertRetur(String kode,String sales,String sr,String shipto,String brg,int crt,int pcs,String ket,String entrytime){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Retur VALUES('" + kode + "','" + sales + "','" + sr + "','" + shipto + "','" + brg + "'," + crt + "," + pcs + ",datetime('now', 'localtime'),null,1,'" + ket + "','" + entrytime + "')");
    }

    public void deleteRetur(String Kode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Retur WHERE Kode='"+Kode+"'");
    }

    public int getCekExistRetur(String pelanggan,String tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode FROM Retur WHERE shipto='"+pelanggan+"' and date(tgl)='"+tgl+"'";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public String getKodenotaRetur(String pelanggan,String tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode FROM Retur WHERE shipto='"+pelanggan+"' and date(tgl)='"+tgl+"'";
        String kd = "";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            kd = cursor.getString(cursor.getColumnIndex("kode"));
        }while (cursor.moveToNext());
        cursor.close();
        return kd;
    }

    public int getCekExistReturNotSync(String pelanggan,String tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode FROM Retur WHERE shipto='"+pelanggan+"' and date(tgl)='"+tgl+"' and sync=1";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public JSONObject getAllReturBeforeSync(String DB_PATH,String DB_NAME) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dbmaster ");//attach otherdb
        String sql="SELECT * FROM (SELECT j.kode,j.shipto,p.perusahaan,'0' AS total "
                + "FROM Retur j "
                + "INNER JOIN dbmaster.Pelanggan p "
                + "ON j.shipto=p.kode "
                + "WHERE j.tglupload IS NULL AND j.sync=1 "
                + "GROUP BY j.kode,j.shipto,p.perusahaan "
                + "ORDER BY p.perusahaan) a order by kode";

        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        JSONArray jArray=new JSONArray();
        if (cursor.moveToFirst()){
            jsonresult.put(TAG_STATUS,1);
            do {
                JSONObject json_data = new JSONObject();
                json_data.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                json_data.put("shipto", cursor.getString(cursor.getColumnIndex("shipto")));
                json_data.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                json_data.put("total", cursor.getString(cursor.getColumnIndex("total")));
                jArray.put(json_data);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_PELANGANDATA, jArray);
        }
        else{
            jsonresult.put(TAG_PELANGANDATA,0);
        }
        db.execSQL("DETACH DATABASE dbmaster ");
        cursor.close();
        return jsonresult;
    }

    public JSONObject getAllReturAfterSync(String DB_PATH,String DB_NAME,String tgl) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dbmaster ");//attach otherdb
        String sql="SELECT * FROM (SELECT j.kode,j.shipto,p.perusahaan,'0' AS total "
                + "FROM Retur j "
                + "INNER JOIN dbmaster.Pelanggan p "
                + "ON j.shipto=p.kode "
                + "WHERE date(j.tgl)='"+tgl+"' AND j.sync=0 "
                + "GROUP BY j.kode,j.shipto,p.perusahaan "
                + "ORDER BY p.perusahaan) a order by kode";
        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        JSONArray jArray=new JSONArray();
        if (cursor.moveToFirst()){
            jsonresult.put("status",1);
            do {
                JSONObject json_data = new JSONObject();
                json_data.put("kode", cursor.getString(cursor.getColumnIndex("kode")));
                json_data.put("shipto", cursor.getString(cursor.getColumnIndex("shipto")));
                json_data.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                json_data.put("total", cursor.getDouble(cursor.getColumnIndex("total")));
                jArray.put(json_data);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_PELANGANDATA, jArray);
        }
        else{
            jsonresult.put(TAG_STATUS,0);
        }
        db.execSQL("DETACH DATABASE dbmaster ");
        cursor.close();
        return jsonresult;
    }

    public void updateOpenFlagRetur(String kode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Retur SET sync=1 where kode='" + kode + "'");
    }

    public void updateFlagRetur(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Retur SET tglupload=datetime('now', 'localtime'),sync=0 where sync=1");
    }

    public int getCekExistCountReturPelanggan(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode FROM Retur WHERE sync=1";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public Cursor getAllRawRetur(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode,sales,shipto,brg,crt,pcs,strftime('%m/%d/%Y', tgl) AS tgl,alasan,entrytime From Retur WHERE sync=1";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public Cursor getAllRawKunjungan(String tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT tgl,sales,shipto,call,pcall,deviasi,uncall,reason,instore,outstore,longitude,latitude,gpstime,reverse From Kunjungan WHERE date(tgl)='"+tgl+"'";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public JSONObject getLimitCust(String kode) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT saldolimit,saldoaktif FROM Pelanggan WHERE Kode='"+kode+"'";

        Cursor cursor = db.rawQuery(sql, null);
        JSONObject jsonresult = new JSONObject();
        JSONArray jArray=new JSONArray();
        if (cursor.moveToFirst()){
            jsonresult.put(TAG_STATUS,1);
            do {
                JSONObject json_data = new JSONObject();
                json_data.put("saldolimit", cursor.getString(cursor.getColumnIndex("saldolimit")));
                json_data.put("saldoaktif", cursor.getString(cursor.getColumnIndex("saldoaktif")));
                jArray.put(json_data);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_BARANGDATA, jArray);
        }
        else{
            jsonresult.put(TAG_BARANGDATA,0);
        }
        cursor.close();
        return jsonresult;
    }

    // Promosi
    public JSONObject getPromosi(String Divisi) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select noprogram,caption,keterangan,segment,date(tglmulai) as tglmulai,date(tglakhir) as tglakhir from Promosi WHERE '"+getToday2()+"' between TglMulai and TglAkhir and noprogram like '"+Divisi+"/%' order by Keterangan";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("noprogram", cursor.getString(cursor.getColumnIndex("noprogram")));
                JData.put("caption", cursor.getString(cursor.getColumnIndex("caption")));
                JData.put("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("segment", cursor.getString(cursor.getColumnIndex("segment")));
                JData.put("tglmulai", cursor.getString(cursor.getColumnIndex("tglmulai")));
                JData.put("tglakhir", cursor.getString(cursor.getColumnIndex("tglakhir")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    public int cekExistTable(String TabelName){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT name FROM sqlite_master WHERE type='table' AND name='"+TabelName+"'";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public void createKunjungan(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Kunjungan");
        db.execSQL("CREATE TABLE IF NOT EXISTS Kunjungan(tgl DATETIME,sales TEXT,shipto TEXT,call TEXT,pcall TEXT,deviasi TEXT,uncall TEXT,reason TEXT,instore DATETIME,outstore DATETIME,longitude TEXT,latitude TEXT,gpstime DATETIME,reverse TEXT)");
    }

    public void insertAllKunjungan(String tgl,String sales,String hari,String DB_PATH, String DB_NAME, String Week){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dbmaster ");//attach otherdb
        db.execSQL("INSERT INTO Kunjungan SELECT '"+tgl+"','"+sales+"',kode,'0','0','0','1','',null,null,'0','0',null,'-' FROM dbmaster.FJP WHERE hari='"+hari+"' and minggu='"+Week+"'");
        db.execSQL("DETACH DATABASE dbmaster ");
    }

    public int cekKunjunganPerTgl(String tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT tgl FROM Kunjungan WHERE date(tgl)='"+tgl+"'";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public int cekKunjungan(String tgl,String sales,String shipto){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT shipto FROM Kunjungan WHERE date(tgl)='"+tgl+"' AND sales='"+sales+"' AND shipto='"+shipto+"'";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public void insertKunjunganDeviasi(String tgl,String sales,String shipto){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Kunjungan VALUES('"+tgl+"','"+sales+"','"+shipto+"','0','0','1','1','',datetime('now', 'localtime'),null,'0','0',null,'-')");
    }

    public void updateKunjungan(String tgl,String sales,String shipto,String call,String pcall,String uncall,String reason){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Kunjungan SET call='"+call+"',pcall='"+pcall+"',uncall='"+uncall+"',reason='"+reason+"',outstore=datetime('now', 'localtime') WHERE date(tgl)='"+tgl+"' AND sales='"+sales+"' AND shipto='"+shipto+"'");
    }

    public void updateTimeInStore(String tgl,String sales,String shipto){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Kunjungan SET instore=datetime('now', 'localtime') WHERE date(tgl)='"+tgl+"' AND sales='"+sales+"' AND shipto='"+shipto+"' AND instore is null");
    }

    public void updateKunjunganCall(String tgl,String sales,String shipto,String call){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Kunjungan SET call='"+call+"',uncall='0',outstore=datetime('now', 'localtime') WHERE date(tgl)='"+tgl+"' AND sales='"+sales+"' AND shipto='"+shipto+"'");
    }

    public void updateKunjunganLongLat(String tgl,String sales,String shipto,String Longitude,String Latitude,String GPSTime,String Reverse){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Kunjungan SET longitude='"+Longitude+"',latitude='"+Latitude+"',gpstime='"+GPSTime+"',reverse='"+Reverse+"' WHERE date(tgl)='"+tgl+"' AND sales='"+sales+"' AND shipto='"+shipto+"' AND Longitude='0' AND Latitude='0'");
    }

    public void RecekKunjungan(String tgl,String sales,String shipto){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql2="select shipto from Penjualan WHERE date(tgl)='"+tgl+"' AND sales='"+sales+"' AND shipto='"+shipto+"'";
        Cursor cursor2 = db.rawQuery(sql2, null);
        if (cursor2.getCount()<=0){
            db.execSQL("UPDATE Kunjungan SET pcall='0' WHERE date(tgl)='"+tgl+"' AND sales='"+sales+"' AND shipto='"+shipto+"'");
        }
        String sql="select shipto from Inventory WHERE date(tgl)='"+tgl+"' AND sales='"+sales+"' AND shipto='"+shipto+"' UNION ALL select shipto from Retur WHERE date(tgl)='"+tgl+"' AND sales='"+sales+"' AND shipto='"+shipto+"'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount()<=0){
            db.execSQL("UPDATE Kunjungan SET call='0' WHERE date(tgl)='"+tgl+"' AND sales='"+sales+"' AND shipto='"+shipto+"' AND pcall='0'");
        }
        if ((cursor2.getCount()<=0)&&(cursor.getCount()<=0)){
            db.execSQL("UPDATE Kunjungan SET uncall='1',instore=null,outstore=null WHERE date(tgl)='"+tgl+"' AND sales='"+sales+"' AND shipto='"+shipto+"' AND call='0' AND pcall='0'");
        }
    }

    public String KunjunganSummary(String Tgl,String Sales,String TglFirst){
        SQLiteDatabase db = this.getWritableDatabase();
        String FinalRes="";
        String sql="SELECT COUNT(1) AS JML FROM Kunjungan WHERE Sales='"+Sales+"' AND date(tgl)='"+Tgl+"'";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        FinalRes=cursor.getString(cursor.getColumnIndex("JML"));

        sql="SELECT COUNT(1) AS JML FROM Kunjungan WHERE Sales='"+Sales+"' AND date(tgl)='"+Tgl+"' AND Deviasi='0'";
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        FinalRes=FinalRes+"#"+cursor.getString(cursor.getColumnIndex("JML"));

        sql="SELECT COUNT(1) AS JML FROM Kunjungan WHERE Sales='"+Sales+"' AND date(tgl)='"+Tgl+"' AND Deviasi='1'";
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        FinalRes=FinalRes+"#"+cursor.getString(cursor.getColumnIndex("JML"));


        sql="SELECT COUNT(1) AS JML FROM Kunjungan WHERE Sales='"+Sales+"' AND date(tgl)='"+Tgl+"' AND Call='1'";
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        FinalRes=FinalRes+"#"+cursor.getString(cursor.getColumnIndex("JML"));


        sql="SELECT COUNT(1) AS JML FROM Kunjungan WHERE Sales='"+Sales+"' AND date(tgl)='"+Tgl+"' AND PCall='1'";
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        FinalRes=FinalRes+"#"+cursor.getString(cursor.getColumnIndex("JML"));

        sql="SELECT IFNULL(SUM(total),'0') AS JML FROM Penjualan WHERE Sales='"+Sales+"' AND date(tgl)='"+Tgl+"' AND sync=0";
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        FinalRes=FinalRes+"#"+cursor.getString(cursor.getColumnIndex("JML"));


        sql="SELECT COUNT(1) AS JML FROM Kunjungan WHERE Sales='"+Sales+"' AND date(tgl)='"+Tgl+"' AND Uncall='1'";
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        FinalRes=FinalRes+"#"+cursor.getString(cursor.getColumnIndex("JML"));

        return FinalRes;
    }

    public JSONObject getPenagihan() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select p.kodenota,p.tgl,p.namacollector,count(p.faktur) jml,count(h.faktur) jmlh,sum(sync) sync from Penagihan p left join hpenagihan h on h.kodenota=p.kodenota and h.faktur=p.faktur and h.uc=0  group by  p.kodenota";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kodenota", cursor.getString(cursor.getColumnIndex("kodenota")));
                JData.put("tgl", cursor.getString(cursor.getColumnIndex("tgl")));
                JData.put("collector", cursor.getString(cursor.getColumnIndex("namacollector")));
                JData.put("jml", cursor.getString(cursor.getColumnIndex("jml")));
                JData.put("jmlh", cursor.getString(cursor.getColumnIndex("jmlh")));
                JData.put("sync", cursor.getString(cursor.getColumnIndex("sync")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put("penagihan",jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    public JSONObject getPenagihanUpload() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select h.kodenota,p.tgl,p.namacollector,'1' jml,'2' jmlh,'3' sync from hpenagihan h join penagihan p on p.kodenota=h.kodenota and p.faktur=h.faktur where sync=0 group by h.kodenota";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kodenota", cursor.getString(cursor.getColumnIndex("kodenota")));
                JData.put("tgl", cursor.getString(cursor.getColumnIndex("tgl")));
                JData.put("collector", cursor.getString(cursor.getColumnIndex("namacollector")));
                JData.put("jml", cursor.getString(cursor.getColumnIndex("jml")));
                JData.put("jmlh", cursor.getString(cursor.getColumnIndex("jmlh")));
                JData.put("sync", cursor.getString(cursor.getColumnIndex("sync")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put("penagihan",jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    public JSONObject getFaktur(String Kodenota) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT DISTINCT p.kodenota,p.faktur,p.shipto,p.perusahaan,p.alamat,p.totalbayar,p.collector,IFNULL(h.kodenota,'0') as hit FROM Penagihan p LEFT JOIN HPenagihan h on h.kodenota=p.kodenota and h.faktur=p.faktur WHERE p.kodenota='"+Kodenota+"'  order by p.perusahaan";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kodenota", cursor.getString(cursor.getColumnIndex("kodenota")));
                JData.put("faktur", cursor.getString(cursor.getColumnIndex("faktur")));
                JData.put("shipto", cursor.getString(cursor.getColumnIndex("shipto")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                JData.put("totalbayar", cursor.getString(cursor.getColumnIndex("totalbayar")));
                JData.put("collector", cursor.getString(cursor.getColumnIndex("collector")));
                JData.put("hit", cursor.getString(cursor.getColumnIndex("hit")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put("penagihan",jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    public void createPenagihan(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Penagihan");
        db.execSQL("CREATE TABLE IF NOT EXISTS Penagihan(tgl DATETIME,kodenota TEXT,faktur TEXT,collector TEXT,shipto TEXT,perusahaan TEXT,alamat TEXT,brg TEXT,hint TEXT,keterangan TEXT,jml INT,jmlcrt TEXT,hrgsatuan FLOAT,discrp FLOAT, totalbayar TEXT,rasiomax INT,namacollector TEXT,createdate DATETIME,overdue TEXT)");
    }

    public void insertPenagihan(String tgl,String kodenota,String faktur,String collector,String shipto,String perusahaan,String alamat,String brg,String hint,String keterangan,int jml,String jmlcrt,float hrgsatuan,float discrp,float totalbayar,int rasiomax,String namacollector,String overdue){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Penagihan(tgl,kodenota,faktur,collector,shipto,perusahaan,alamat,brg,hint,keterangan,jml,jmlcrt,hrgsatuan,discrp,totalbayar,rasiomax,namacollector,createdate,overdue) VALUES('"+tgl+"','"+kodenota+"','"+faktur+"','"+collector+"','"+shipto+"','"+perusahaan+"','"+alamat+"','"+brg+"','"+hint+"','"+keterangan+"',"+jml+",'"+jmlcrt+"',"+hrgsatuan+","+discrp+",'"+totalbayar+"',"+rasiomax+",'"+namacollector+"',datetime('now', 'localtime'),'"+overdue+"')");
    }

    public void deletePenagihan(String Kodenota){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Penagihan WHERE kodenota='"+Kodenota+"'");
    }

    public void createHPenagihan(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS HPenagihan");
        db.execSQL("CREATE TABLE IF NOT EXISTS HPenagihan(kodenota TEXT,faktur TEXT,tgl DATETIME,collector TEXT,tunai TEXT,bg TEXT,stempel INTEGER,tt INTEGER,uc INTEGER,keterangan TEXT,shipto TEXT,tglupload DATETIME,longitude TEXT,latitude TEXT,transferbank TEXT,transferjml TEXT,transfertgl DATETIME,startentry DATETIME,sync INT,createdate DATETIME)");
    }

    public void InsertHPenagihan(String Kodenota,String Faktur,String Tgl,String Collector,String Tunai,String bg,int stempel,int tt,int uc,String Keterangan,String ShipTo, String Longitude, String Latitude, String TransferBank,String TransferJml,String StartEntry,String TglTransfer){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO HPenagihan(kodenota,faktur,tgl,collector,tunai,bg,stempel,tt,uc,keterangan,shipto,tglupload,longitude,latitude,transferbank,transferjml,startentry,sync,createdate,transfertgl) VALUES('"+Kodenota+"','"+Faktur+"','"+Tgl+"','"+Collector+"','"+Tunai+"','"+bg+"',"+stempel+","+tt+","+uc+",'"+Keterangan+"','"+ShipTo+"',null,'"+Longitude+"','"+Latitude+"','"+TransferBank+"','"+TransferJml+"','"+StartEntry+"',1,datetime('now', 'localtime'),'"+TglTransfer+"')");
    }

    public void InsertHPenagihanUncall(String Kodenota){
        SQLiteDatabase db = this.getWritableDatabase(); //group by faktur,tgl,collector,shipto
        db.execSQL("INSERT INTO HPenagihan(kodenota,faktur,tgl,collector,tunai,bg,stempel,tt,uc,keterangan,shipto,tglupload,longitude,latitude,transferbank,transferjml,startentry,sync,createdate,transfertgl) " +
                " SELECT '"+Kodenota+"',faktur,tgl,collector,'0','0',0,0,1,'UCBI',shipto,'','','','','0','',1,datetime('now', 'localtime'),'' From Penagihan WHERE Kodenota='"+Kodenota+"' AND Faktur not in (select faktur from hpenagihan where kodenota='"+Kodenota+"') group by faktur,tgl,collector,shipto");
    }

    public void DeleteHPenagihan(String Kodenota,String Faktur){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM HPenagihan WHERE kodenota='"+Kodenota+"' and faktur='"+Faktur+"'");
    }

    public void DeleteHPenagihanPerNP(String Kodenota){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM HPenagihan WHERE kodenota='"+Kodenota+"'");
    }

    public void UpdateOpenHPenagihanUpload(String Kodenota){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE HPenagihan SET sync=1 WHERE kodenota='"+Kodenota+"'");
    }

    public JSONObject getPembayaranFaktur(String Kodenota,String faktur) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT DISTINCT tunai,bg,stempel,tt,uc,transferbank,transferjml,transfertgl,keterangan FROM HPenagihan WHERE kodenota='"+Kodenota+"' and faktur='"+faktur+"' ";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("tunai", cursor.getString(cursor.getColumnIndex("tunai")));
                JData.put("bg", cursor.getString(cursor.getColumnIndex("bg")));
                JData.put("stempel", cursor.getString(cursor.getColumnIndex("stempel")));
                JData.put("tt", cursor.getString(cursor.getColumnIndex("tt")));
                JData.put("uc", cursor.getString(cursor.getColumnIndex("uc")));
                JData.put("transferbank", cursor.getString(cursor.getColumnIndex("transferbank")));
                JData.put("transferjml", cursor.getString(cursor.getColumnIndex("transferjml")));
                JData.put("transfertgl", cursor.getString(cursor.getColumnIndex("transfertgl")));
                JData.put("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put("penagihan",jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    public JSONObject getNPReadyToUpload()  throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select p.kodenota,count(p.faktur) jml,count(h.faktur) jmlh from Penagihan p left join hpenagihan h on h.kodenota=p.kodenota and h.faktur=p.faktur and h.sync=1 group by  p.kodenota";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kodenota", cursor.getString(cursor.getColumnIndex("kodenota")));
                JData.put("jml", cursor.getString(cursor.getColumnIndex("jml")));
                JData.put("jmlh", cursor.getString(cursor.getColumnIndex("jmlh")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put("penagihan",jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    public Cursor getAllRawPenagihan(String RekapKodenota){
        String[] arr = RekapKodenota.split("#");
        String h = "";
        for (int g=0;g<arr.length;g++){
            if (arr[g].length()>3){
                if (h.equals("")){
                    h = "'"+arr[g]+"'";
                }else{
                    h = h + "," + "'"+arr[g]+"'";
                }
            }
        }
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kodenota,faktur,tgl,collector,tunai,bg,stempel,tt,uc,keterangan,shipto,tglupload,longitude,latitude,transferbank,transferjml,startentry,sync,createdate,transfertgl FROM HPenagihan WHERE sync=1 AND Kodenota in ("+h+")";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public void updateFlagPenagihan(String RekapKodenota){
        String[] arr = RekapKodenota.split("#");
        String h = "";
        for (int g=0;g<arr.length;g++){
            if (arr[g].length()>3){
                if (h.equals("")){
                    h = "'"+arr[g]+"'";
                }else{
                    h = h + "," + "'"+arr[g]+"'";
                }
            }
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE HPenagihan SET tglupload=datetime('now', 'localtime'),sync=0 where sync=1 and kodenota in ("+h+")");
    }

    public int cekHPenagihanUdahUpload(String Kodenota){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT Kodenota FROM HPenagihan WHERE Kodenota='"+Kodenota+"' AND length(IFNULL(tglupload,'0'))>1 AND sync=0";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public int cekHPenagihanUdahUploadBI(String Kodenota){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT Kodenota FROM HPenagihan WHERE Kodenota='"+Kodenota+"' AND Keterangan='UCBI'";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }

    public int cekFakturUCBI(String Kodenota){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT Kodenota FROM HPenagihan WHERE Keterangan='UCBI' and Kodenota='"+Kodenota+"'";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount();
    }


    public void DeleteHistory6M(String Tgl){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM HPenagihan WHERE (tgl between '2015-01-01' and '"+Tgl+"') and sync=0");
        db.execSQL("DELETE FROM Inventory WHERE (tgl between '2015-01-01' and '"+Tgl+"') and sync=0");
        db.execSQL("DELETE FROM Kunjungan WHERE tgl between '2015-01-01' and '"+Tgl+"'");
        db.execSQL("DELETE FROM Penagihan WHERE (tgl between '2015-01-01' and '"+Tgl+"')");
        db.execSQL("DELETE FROM Penjualan WHERE (tgl between '2015-01-01' and '"+Tgl+"') and sync=0");
        db.execSQL("DELETE FROM Retur WHERE (tgl between '2015-01-01' and '"+Tgl+"') and sync=0");
    }

    public JSONObject getPelangganQP() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select distinct shipto,perusahaan,alamat from Penagihan order by perusahaan";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kode", cursor.getString(cursor.getColumnIndex("shipto")));
                JData.put("perusahaan", cursor.getString(cursor.getColumnIndex("perusahaan")));
                JData.put("alamat", cursor.getString(cursor.getColumnIndex("alamat")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_PELANGANDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }

    public JSONObject getFakturQP(String Shipto) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select p.collector,p.tgl,p.totalbayar,p.kodenota,p.faktur,p.shipto,p.perusahaan,IFNULL(h.tunai,'0') as tunai,IFNULL(h.bg,'0') as bg,IFNULL(h.stempel,'0') as stempel,IFNULL(h.tt,'0') as tt,IFNULL(h.uc,'0') as uc,IFNULL(h.transferbank,'') as transferbank,IFNULL(h.transferjml,'0') as transferjml,IFNULL(h.transfertgl,'') as transfertgl,IFNULL(h.keterangan,'') as keterangan,p.overdue from penagihan p " +
                "left join hpenagihan h on h.kodenota=p.kodenota and h.faktur=p.faktur " +
                "where p.shipto='"+Shipto+"' and p.faktur not in (select faktur from hpenagihan where sync=0 and uc=0) group by p.totalbayar,p.kodenota,p.faktur,p.shipto,p.perusahaan,h.tunai,h.bg,h.stempel,h.tt,h.uc,h.transferbank,h.transferjml,h.transfertgl,h.keterangan " +
                "order by p.overdue,p.faktur";

        Cursor cursor = db.rawQuery(sql, null);

        JSONObject jResult = new JSONObject();
        JSONArray  jArray  = new JSONArray();

        if (cursor.moveToFirst()){
            jResult.put(TAG_STATUS,1);
            do {
                JSONObject JData = new JSONObject();
                JData.put("kodenota", cursor.getString(cursor.getColumnIndex("kodenota")));
                JData.put("faktur", cursor.getString(cursor.getColumnIndex("faktur")));
                JData.put("tunai", cursor.getString(cursor.getColumnIndex("tunai")));
                JData.put("bg", cursor.getString(cursor.getColumnIndex("bg")));
                JData.put("stempel", cursor.getString(cursor.getColumnIndex("stempel")));
                JData.put("tt", cursor.getString(cursor.getColumnIndex("tt")));
                JData.put("uc", cursor.getString(cursor.getColumnIndex("uc")));
                JData.put("transferbank", cursor.getString(cursor.getColumnIndex("transferbank")));
                JData.put("transferjml", cursor.getString(cursor.getColumnIndex("transferjml")));
                JData.put("transfertgl", cursor.getString(cursor.getColumnIndex("transfertgl")));
                JData.put("keterangan", cursor.getString(cursor.getColumnIndex("keterangan")));
                JData.put("totalbayar", cursor.getString(cursor.getColumnIndex("totalbayar")));
                JData.put("tgl", cursor.getString(cursor.getColumnIndex("tgl")));
                JData.put("collector", cursor.getString(cursor.getColumnIndex("collector")));
                JData.put("overdue", cursor.getString(cursor.getColumnIndex("overdue")));
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_PENAGIHAN,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
        cursor.close();
        return jResult;
    }
}
