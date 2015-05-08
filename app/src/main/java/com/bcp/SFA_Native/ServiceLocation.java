package com.bcp.SFA_Native;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.*;
import android.os.Bundle;
import android.os.IBinder;

public class ServiceLocation extends Service {
    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";
    //private final String TAG_NAMAJALAN = "namajalan";

    //String cityName=" ";
    //List<Address> addresses;


    private LocationManager locationManager=null;
    private LocationListener locationListener=null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10,locationListener);

    }

    /*----------Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            //Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

            //try {
            //    addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            //    if (addresses.size() > 0)
            //        cityName=addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1);
            //} catch (IOException e) {
            //    e.printStackTrace();
            //}

            if (loc == null){
                loc  = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (loc != null){
                setPrefLocation(loc.getLongitude()+"",loc.getLatitude()+"");
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

    public void setPrefLocation(String Longitude, String Latitude){
        SharedPreferences Location = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor DayLoginEditor = Location.edit();
        DayLoginEditor.putString(TAG_LONGITUDE, Longitude);
        DayLoginEditor.putString(TAG_LATITUDE, Latitude);
        DayLoginEditor.commit();
    }

    public String getPref(String KEY){
        SharedPreferences SettingPref = getSharedPreferences(TAG_PREF, Context.MODE_PRIVATE);
        String Value=SettingPref.getString(KEY,"0");
        return  Value;
    }
}