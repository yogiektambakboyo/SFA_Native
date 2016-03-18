package com.bcp.SFA_Native;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by IT-SUPERMASTER on 21/10/2015.
 */
public interface API_SFA {
    @GET("/api_penagihan.php")
    public void postCariNP(@Query("cabang") String cabang,@Query("notapenagihan") String notapenagihan,@Query("divisi") String divisi, Callback<List<Data_NotaPenagihan>> response);
}
