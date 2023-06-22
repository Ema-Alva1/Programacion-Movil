package com.example.practica_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    TextView status, entrante;
    String mensajeTexto = "", telefono;
    private LocationManager locationManager;
    private PowerManager powerManager;
    private WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = (TextView) findViewById(R.id.Status);

        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);


        SharedPreferences sharedPreferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        String valorGuardado = sharedPreferences.getString("numero", "no hay numero registrado");
        //Toast.makeText(MainActivity.this, "valor: "+valorGuardado, Toast.LENGTH_SHORT).show();
        telefono = valorGuardado;
        status.setText("");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "MyApp:ProximityLock");


    }
    private class PhoneCallListener extends PhoneStateListener{
        private boolean isPhoneCalling = false;
        private int ringingCounter = 0;
        private Timer ringingTimer;
        private Handler handler = new Handler();

        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            String LOG_TAG = "llamada entrante";

            if(TelephonyManager.CALL_STATE_RINGING == state){
                Log.i(LOG_TAG, "RING, número: "+ phoneNumber);
                status.setText(phoneNumber);
                if(phoneNumber.equals(telefono)) {
//                  enviarMensaje(phoneNumber);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //enviarMensaje(phoneNumber);
                        }
                    }, 5000); // 10000 milisegundos = 10 segundos
                }else{
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //enviarMensaje(phoneNumber);
                        }
                        
                    }, 5000); // 10000 milisegundos = 10 segundows
                    suspendPhone(); // Suspender el teléfono durante la llamada

                }            }
            if (TelephonyManager.CALL_STATE_OFFHOOK == state){
                Log.i(LOG_TAG, "OFFHOOK");
                isPhoneCalling = true;
                Toast.makeText(getApplicationContext(), "Llamada en curso", Toast.LENGTH_SHORT).show();
            }
            if (TelephonyManager.CALL_STATE_IDLE == state) {
                Log.i(LOG_TAG, "IDLE number");
                isPhoneCalling = true;
                Toast.makeText(getApplicationContext(), "Llamada finalizada", Toast.LENGTH_SHORT).show();
                resumePhone();
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                    phoneNumber="";
                }
                if(isPhoneCalling){

                    Handler handler = new Handler();
                    boolean callogDetailActivity = handler.postDelayed(new Runnable(){

                        @Override
                        public void run() {
                            Log.i("CallLogDetailsActivity", "Getting Log activity...");
                            String[] projection = new String[]{CallLog.Calls.NUMBER};
                            Cursor cur = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, null);
                            cur.moveToFirst();
                            String lastCallNumber = cur.getString(0);
                        }
                    }, 500);
                    isPhoneCalling = false;
                }

            }
        }
    }

    private void startRingingTimer(final String phoneNumber, final String message) {
        final int[] ringingCounter = {0};
        Timer ringingTimer = new Timer();
        ringingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ringingCounter[0]++;
                if (ringingCounter[0] == 3) {
                    ringingTimer.cancel();
                    mensaje(phoneNumber, message);
                }
            }
        }, 0, 1000);
    }

    public void mensaje(String numero, String mensaje) {
        SmsManager smsManager = SmsManager.getDefault();
        //Toast.makeText(getApplicationContext(), "mensaje"+mensaje, Toast.LENGTH_SHORT).show();
        smsManager.sendTextMessage(numero, null, mensaje, null, null);
        Toast.makeText(getApplicationContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show();
    }


    public void enviarMensaje(String numero){
        try {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mensajeTexto = "SIN PERMISOS";
            } else {
                /*Se asigna a la clase LocationManager el servicio a nivel de sistema a partir del nombre.*/
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc != null) {
                    double longitudeGPS = loc.getLongitude();
                    double latitudeGPS = loc.getLatitude();
                    mensajeTexto = "Mi latitud es: " + latitudeGPS + " Mi longitud es: " + longitudeGPS;
                } else {
                    mensajeTexto = "Mi latitud es: " + "20.0821936" + " Mi longitud es: " + "-98.398177";
                }
                mensaje(numero, mensajeTexto);
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, error: "+e, Toast.LENGTH_LONG).show();
            status.setText("Error: \n"+e);
        }
    }

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double lat=location.getLatitude();
            double lon=location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private void suspendPhone() {
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

    private void resumePhone() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
}