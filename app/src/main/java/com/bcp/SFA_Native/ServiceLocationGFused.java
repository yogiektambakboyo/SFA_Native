package com.bcp.SFA_Native;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceLocationGFused extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final String TAG_PREF="SETTINGPREF";
    private final String TAG_LONGITUDE = "longitude";
    private final String TAG_LATITUDE = "latitude";
    private final String TAG_GPSTIME = "gpstime";
    private final String TAG_REVERSEGEOCODE = "reverse";

    private static final String TAG = "DRIVER";
    private double currentLat, currentLng;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private LocationListener locationListener;
    List<Address> addresses;
    String Address =" ";



    private class LocationListener implements com.google.android.gms.location.LocationListener {

        public LocationListener() {
        }

        @Override
        public void onLocationChanged(Location location) {
            //Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());

            /*try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0)
                    Address = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getSubAdminArea()+", "+addresses.get(0).getAdminArea()+", "+addresses.get(0).getPostalCode();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            Log.e(TAG, "onLocationChanged: " + location);
            currentLat = location.getLatitude();
            currentLng = location.getLongitude();
            long time = location.getTime();
            Date date = new Date(time);
            setPrefLocation((currentLng+""),(currentLat+""),getGPSTime(date),Address);
        }
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        boolean stopService = false;
        if (intent != null)
            stopService = intent.getBooleanExtra("stopservice", false);

        locationListener = new LocationListener();
        if (stopService)
            stopLocationUpdates();
        else {
            if (!mGoogleApiClient.isConnected())
                mGoogleApiClient.connect();
        }

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, locationListener);

        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(90000);
        mLocationRequest.setFastestInterval(80000);
        startLocationUpates();
    }
    private void startLocationUpates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, locationListener);
    }


    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }


    public ServiceLocationGFused() {
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
