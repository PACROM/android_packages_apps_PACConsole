/*
 *    PAC ROM Console. Settings and OTA
 *    Copyright (C) 2014  pvyParts (Aaron Kable)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pac.console;

import java.util.Calendar;
import java.util.Date;

import com.pac.console.util.LocalTools;
import com.pac.console.util.RemoteTools;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
/**
 * OTA service for PAC 
 * 
 * only a poler nothing special.
 *
 * @author pvyParts
 *
 */
public class updateChecker extends Service {

    Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        // INITIALIZE SCREEN RECEIVER
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        Thread check = new Thread(checkOTA);
        check.start();
    }

    private void notifyUser(){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        // TODO Stringify these and make / steal a icon
        mBuilder.setSmallIcon(R.drawable.ico_pac_update);
        mBuilder.setContentTitle("PAC Updates!");
        mBuilder.setContentText("New Update Available!");

        //launch into OTA straight away when clicking
        Intent notifyIntent = new Intent(this, PacConsole.class);
        notifyIntent.putExtra("flag", 0);
        notifyIntent.putExtra("store", true);

        PendingIntent resultIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT );
        mBuilder.setContentIntent(resultIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // only ever 1 noteification so id = 1
        // and show
        mNotificationManager.notify(1, mBuilder.build());

    }

    class ScreenReceiver extends BroadcastReceiver {
        // THANKS JASON
        public boolean wasScreenOn = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // DO WHATEVER YOU NEED TO DO HERE
                wasScreenOn = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (!wasScreenOn){
                    Thread check = new Thread(checkOTA);
                    check.start();
                }
                wasScreenOn = true;
            }
        }
    }

    private Thread checkOTA = new Thread(new Runnable(){

        public void run() {
            // TODO Auto-generated method stub
            // checky McCheck
            Calendar currentDate = Calendar.getInstance();

            Date today = currentDate.getTime();
            Date finalDay = null;
            try {
                finalDay = new Date(Settings.System.getLong(getContentResolver(), "lastUpdate"));
            } catch (Exception e) {
                finalDay = new Date(0);
            }
            int numberOfHours = (int) ((finalDay.getTime() - today.getTime()) / (1000 * ( 3600 * 6 /* testing code (6 minutes not hours) / 10*/) ));

            Log.d("SERVICE", "hours - " + numberOfHours);
            //TODO 6 hours passed? 
            if (numberOfHours+6 <= 0){	
                Settings.System.putLong(getContentResolver(), "lastUpdate", today.getTime());
                AsyncTask checkTast = new CheckRemote();
                String[] dev = { " " };
                dev[0] = (String) (LocalTools.getProp("ro.cm.device")!=null? LocalTools.getProp("ro.cm.device"): Build.DEVICE);
                checkTast.execute((java.lang.Object[])dev);
            }
            //check for update on server adn reset time oonce done

            //else do nothing
            today = null;
            finalDay = null;
            currentDate = null;
        }

    });

    private class CheckRemote extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            SharedPreferences Settings = getSharedPreferences("OTAPrefs", Context.MODE_PRIVATE);

            String out = RemoteTools.checkRom(arg0[0], Settings.getString("OTAType", "checks"));
            return out;
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {
                Log.d("REMOTE", "got this: " + result);
                String[] results = result.split(",");

                String version = results[2];
                String[] dlurl = results[0].split("/");

                //data.putString("file", dlurl[dlurl.length - 1]);
                //data.putString("url", results[0]);
                //data.putString("md5", results[3]);
                String localVer = LocalTools.getProp("ro.pacrom.version");
                Settings.System.putString(getContentResolver(), "OTA_Update", result);
                // NOTIFY!
                if (!localVer.equals(version)){
                    notifyUser();
                }
            }
        }
    }
}
