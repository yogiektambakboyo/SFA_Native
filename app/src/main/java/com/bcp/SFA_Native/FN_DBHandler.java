package com.bcp.SFA_Native;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FN_DBHandler extends SQLiteOpenHelper {
    private final String TAG_PELANGANDATA= "PelangganData";
    private final String TAG_BARANGDATA= "BarangData";
    private final String TAG_STATUS = "status";

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
                + "mode TEXT,web TEXT,ip TEXT,cabang TEXT,userip TEXT,passip TEXT, minorder FLOAT,appversion INTEGER, dbversion INTEGER)");
    }

    public void InsertSetting(String WebT, int appversion, int dbversion,String ModeApp,String Cabang){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Pengaturan(lastlogin,namelogin,tgllogin,tgllogout,web,mode,appversion,dbversion,cabang,email) VALUES('','',NULL,'test@mail.com','"+WebT+"','"+ModeApp+"',"+appversion+","+dbversion+",'"+Cabang+"','test@mail.com')");
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
    public void UpdateSettingFull(String WebT,String Mode,String Cabang,String Email){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET web='"+WebT+"',mode='"+Mode+"',cabang='"+Cabang+"',email='"+Email+"'");
    }
    public JSONObject GetSetting() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT lastlogin,namelogin,web,appversion,dbversion,mode,cabang,minorder,email FROM Pengaturan";

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
        }
        else{
            jsonresult.put("status",0);
        }

        return jsonresult;
    }

    // End Setting

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

    public void insertPelanggan(String kode,String perusahaan,String alamat){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Pelanggan VALUES('"+kode+"','"+perusahaan+"','"+alamat+"')");
    }
    public void insertBarang(String kode,String nama,String keterangan,String merek,String variant,int crt,float harga){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Barang VALUES('"+kode+"','"+nama+"','"+keterangan+"','"+merek+"','"+variant+"',"+crt+","+harga+")");
    }
    public void insertFJP(String kode,String hari){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO FJP VALUES('"+kode+"','"+hari+"')");
    }
    public void insertHistoryTransaksi(String kode,String brg){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO HistoryTransaksi VALUES('"+kode+"','"+brg+"')");
    }

    public JSONObject getPelanggan() throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="select p.*,IFNULL(f.hari,'semua') as hari from Pelanggan p LEFT JOIN FJP  f ON f.kode=p.kode  ORDER BY p.perusahaan ASC";

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
        return jResult;
    }

    public JSONObject GetBarangOrder(String ShipTo) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        //String sql="select *,'0' as jmlcrt,'0' as jmlpcs from Barang";
        String sql="select b.*,'0' as jmlcrt,'0' as jmlpcs,'1' as last  from barang b JOIN HistoryTransaksi h ON h.brg=b.kode where h.kode like '"+ShipTo+"' " +
                "union all " +
                " select *,'0' as jmlcrt,'0' as jmlpcs,'0' as last  from barang where kode not in (select brg from HistoryTransaksi where  kode like '"+ShipTo+"')";

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
                jArray.put(JData);
            } while (cursor.moveToNext());
            jResult.put(TAG_BARANGDATA,jArray);
        }
        else{
            jResult.put(TAG_STATUS,0);
        }
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
        db.execSQL("CREATE TABLE IF NOT EXISTS Penjualan(kode TEXT,sales TEXT,sr TEXT,shipto TEXT,brg TEXT,crt INT,pcs INT,total DOUBLE,tgl DATETIME,tglupload DATETIME,sync INT,ket TEXT,entrytime DATETIME,longitude TEXT,latitude TEXT)");
    }

    public void updateMinOrder(String minorder){
        Float MinOrder = Float.parseFloat(minorder);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Pengaturan SET minorder="+MinOrder);
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
        return kodefinal;
    }


    public void insertOrder(String kode,String sales,String sr,String shipto,String brg,int crt,int pcs,double total,String ket,String entrytime,String longitude,String latitude){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO Penjualan VALUES('"+kode+"','"+sales+"','"+sr+"','"+shipto+"','"+brg+"',"+crt+","+pcs+","+total+",datetime('now', 'localtime'),null,1,'"+ket+"','"+entrytime+"','"+longitude+"','"+latitude+"')");
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
        return jsonresult;
    }

    public JSONObject getDetailOrderBeforeSync(String DB_PATH,String DB_NAME,String ShipTo,String KodeOrder) throws JSONException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ATTACH DATABASE '"+ DB_PATH+ File.separator +DB_NAME +"' AS dborder");
        String sql="SELECT b.*,IFNULL(p.crt,'0') as jmlcrt, IFNULL(p.pcs,'0') as jmlpcs,'0' as last from Barang b JOIN dborder.Penjualan p on p.brg=b.kode where p.shipto='"+ShipTo+"' and p.kode='"+KodeOrder+"'" +
                "UNION ALL SELECT *,'0' as jmlcrt,'0' as jmlpcs,'0' as last from barang WHERE kode not in (SELECT brg from penjualan WHERE shipto='"+ShipTo+"' and kode='"+KodeOrder+"')";

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
                jArray.put(JData);
            } while (cursor.moveToNext());
            jsonresult.put(TAG_BARANGDATA, jArray);
        }
        else{
            jsonresult.put(TAG_STATUS,0);
        }
        db.execSQL("DETACH DATABASE dborder ");
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
        return jsonresult;
    }


    public void deleteOrder(String Kode){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM Penjualan WHERE Kode='"+Kode+"'");
    }

    public Cursor getAllRawPenjualan(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql="SELECT kode,sales,shipto,brg,crt,pcs,total,strftime('%m/%d/%Y', tgl) AS tgl,ket,entrytime,longitude,latitude From Penjualan WHERE sync=1";

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
        db.execSQL("UPDATE Penjualan SET sync=1 where kode='"+kode+"'");
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
        return result;
    }

    public void addColumnPengaturan(String TabelName,String ColumnName,String TipeData){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE "+TabelName+" ADD "+ColumnName+" "+TipeData);
    }
}
