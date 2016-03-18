package com.bcp.SFA_Native;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.sql.RowSetEvent;

public class ServiceLocation extends Service {
    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";
    private final String TAG_GPSTIME = "gpstime";
    private final String TAG_REVERSEGEOCODE = "reverse";

    String Address =" ";
    List<Address> addresses;


    private LocationManager locationManager=null;
    private LocationListener locationListener=null;

    boolean gps_enabled=false;
    boolean network_enabled=false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new MyLocationListener();
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, locationListener);

        //exceptions will be thrown if provider is not permitted.
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){
            Toast.makeText(ServiceLocation.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }


        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){
            Toast.makeText(ServiceLocation.this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        if(network_enabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, locationListener);
        }else if(gps_enabled){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        }
    }

    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
               if (addresses.size() > 0)
                    Address = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getSubAdminArea()+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getPostalCode();
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (loc == null){
                loc  = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (loc != null){
                long time = loc.getTime();
                Date date = new Date(time);
                setPrefLocation(loc.getLongitude()+"",loc.getLatitude()+"",getGPSTime(date),Address);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public void setPrefLocation(String Longitude, String Latitude, String GPSTime, String Reverse){
        SharedPreferences Location = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor DayLoginEditor = Location.edit();
        Reverse = Reverse.replaceAll("'", " ");
        if (Reverse.length()>140){
            Reverse = Reverse.substring(0,139);
        }
        DayLoginEditor.putString(TAG_LONGITUDE, Longitude);
        DayLoginEditor.putString(TAG_LATITUDE, Latitude);
        DayLoginEditor.putString(TAG_GPSTIME, GPSTime);
        DayLoginEditor.putString(TAG_REVERSEGEOCODE, Reverse);
        DayLoginEditor.commit();
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY, "0");
        return  Value;
    }

    public static String getGPSTime(Date d){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = sdf.format(d);
        return result;
    }
}