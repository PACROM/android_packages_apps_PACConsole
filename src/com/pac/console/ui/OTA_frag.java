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
package com.pac.console.ui;

import java.io.File;
import com.pac.console.R;
import com.pac.console.util.LocalTools;
import com.pac.console.util.RemoteTools;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * OTA Frag
 * 
 * loads the OTA stuff allows selction of ota types etc etc etc...
 * 
 * @author pvyParts
 *
 */
public class OTA_frag extends Fragment {

    private boolean zomby = true;
    private OTA_enabler mOTAEnabler;
    TextView update;
    Button download;
    Button flash;

    private SharedPreferences Settings;
    private Editor SettingsEditor;

    String DlUrl = "";
    String DlMd5 = "";
    String FileName = "";
    String DlVersion = "";
    AlertDialog.Builder alert;

    DownLoadComplte mDownload;

    Handler updateRemote = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!zomby){
                update.setText(msg.getData().getString("version") + "\n"
                        + msg.getData().getString("file"));
                DlUrl = msg.getData().getString("url");
                boolean fileExist = new File(Environment.DIRECTORY_DOWNLOADS+"/PAC/"+msg.getData().getString("file")).exists();

                if (DlUrl != null){
                    download.setClickable(!fileExist);
                    download.setActivated(!fileExist);
                    download.setTextColor(Color.WHITE);
                }

                DlMd5 = msg.getData().getString("md5");
                DlVersion = msg.getData().getString("version");
                FileName = msg.getData().getString("file");
            }
        }
    };

    public static OTA_frag newInstance(String content) {
        OTA_frag fragment = new OTA_frag();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mOTAEnabler.resume();
        mDownload = new DownLoadComplte();
        this.getActivity().registerReceiver(mDownload, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        zomby = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mOTAEnabler.pause();
        this.getActivity().unregisterReceiver(mDownload);
        zomby = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle ofjoy) {
        zomby = false;
        Settings = this.getActivity().getSharedPreferences("OTAPrefs", Context.MODE_PRIVATE);
        SettingsEditor = Settings.edit();

        View layout = inflater.inflate(R.layout.ota_frag_layout, null);
        TextView device = (TextView) layout.findViewById(R.id.tv_ota_device);
        TextView location = (TextView) layout.findViewById(R.id.tv_ota_location);
        location.setText(this.getActivity().getResources().getString(R.string.ota_loc) + "\n" + Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/PAC/");
        update = (TextView) layout.findViewById(R.id.tv_ota_update);
        download = (Button) layout.findViewById(R.id.bt_ota_down);
        flash = (Button) layout.findViewById(R.id.bt_ota_flash);
        Spinner type = (Spinner) layout.findViewById(R.id.sp_ota_type);

        type.setOnItemSelectedListener(new OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                if (arg2==0){
                    // unofficial
                    SettingsEditor.putString("OTAType", "checku");
                } else if (arg2==1){
                    // nightly
                    SettingsEditor.putString("OTAType", "checkn");
                } else if (arg2==2){
                    // stable
                    SettingsEditor.putString("OTAType", "checks");
                }  
                SettingsEditor.commit();
                String deviceName = LocalTools.getProp("ro.cm.device").equals("")? Build.PRODUCT : LocalTools.getProp("ro.cm.device");
                searchForOTA(deviceName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        String strtemp = Settings.getString("OTAType", "checks");
        int typeint = 0;
        if (strtemp.contains("checks")){
            typeint = 2;
        } else if (strtemp.contains("checkn")){
            typeint = 1;
        } else if (strtemp.contains("checku")){
            typeint = 0;
        } 
        type.setSelection(typeint);
        download.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (DlUrl != null && FileName != null) {
                    int con = RemoteTools.checkConnection(OTA_frag.this
                            .getActivity());
                    if (con == RemoteTools.MOBILE) {
                        alert = new AlertDialog.Builder(getActivity());

                        alert.setTitle(OTA_frag.this.getActivity().getString(
                                R.string.mobile_data));

                        alert.setMessage(OTA_frag.this.getActivity().getString(
                                R.string.wifi_rec)
                                + "\n"
                                + OTA_frag.this.getActivity().getString(
                                        R.string.carges));

                        alert.setPositiveButton("Yes!",
                                new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                RemoteTools.downloadFile(
                                        OTA_frag.this.getActivity(),
                                        DlUrl, FileName);
                            }
                        });

                        alert.setNegativeButton("No!",
                                new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {

                            }
                        });

                        alert.show();
                    } else if (con == RemoteTools.DISCONNECTED) {
                        // POPUP no connection // should not ever get here
                    } else if (con == RemoteTools.WIFI) {
                        RemoteTools.downloadFile(OTA_frag.this.getActivity(),
                                DlUrl, FileName);
                    }
                }
            }
        });

        flash.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                if (DlUrl != null && FileName != null) {
                    // TODO check for file and dl if needed

                    // TODO Flash this bad boy in CWM / what ever it is

                    // TODO add my CWM library

                }
            }
        });

        // TODO enable this guy
        flash.setClickable(false);
        flash.setActivated(false);
        flash.setTextColor(Color.GRAY);

        download.setClickable(false);
        download.setActivated(false);
        download.setTextColor(Color.GRAY);

        String deviceName = LocalTools.getProp("ro.cm.device").equals("")? Build.PRODUCT : LocalTools.getProp("ro.cm.device");
        device.setText(deviceName + " - " + Build.DEVICE + "\n" + (LocalTools.getProp("ro.pacrom.version").equals("")?getString(R.string.ota_non_pac):LocalTools.getProp("ro.pacrom.version")));

        Activity activity = getActivity();
        ActionBar actionbar = activity.getActionBar();

        Switch actionBarSwitch = new Switch(activity);

        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(actionBarSwitch, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
                | Gravity.RIGHT));

        mOTAEnabler = new OTA_enabler(getActivity(), actionBarSwitch);

        searchForOTA(deviceName);

        return layout;
    }

    private void searchForOTA(String deviceName){
        // TODO Long term push updates direct from dibs
        int con = RemoteTools.checkConnection(OTA_frag.this.getActivity());
        update.setText(this.getActivity().getString(R.string.ota_checking));
        if (con > RemoteTools.DISCONNECTED) {
            AsyncTask checkTast = new CheckRemote();
            String[] dev = { " " };
            dev[0] = (String) deviceName;
            checkTast.execute((java.lang.Object[])dev);
        } else {
            update.setText(this.getActivity().getString(R.string.no_data));
        }
    }

    private class CheckRemote extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            SharedPreferences Settings = OTA_frag.this.getActivity().getSharedPreferences("OTAPrefs", Context.MODE_PRIVATE);

            String out = RemoteTools.checkRom(arg0[0], Settings.getString("OTAType", "checks"));
            return out;
        }

        @Override
        protected void onPostExecute(final String result) {
            if (!zomby){
                Message msg = new Message();
                Bundle data = new Bundle();
                if (result != null) {
                    Log.d("REMOTE", "got this: " + result);
                    String[] results = result.split(",");
                    if (!results[0].contains("#BLAME")) {
                        data.putString("version", results[2]);
                        String[] dlurl = results[0].split("/");
                        data.putString("file", dlurl[dlurl.length - 1]);
                        data.putString("url", results[0]);
                        data.putString("md5", results[3]);
                    } else {
                        // error device not on server records!
                        if (results[1].contains("NO_STABLE_CONFIG_FOUND")) {
                            data.putString("version", getString(R.string.error_dev));
                        } else if (results[1].contains("NO_NIGHTLY_CONFIG_FOUND")) {
                            data.putString("version", getString(R.string.error_dev));
                        } else if (results[1].contains("NO_UNOFFICIAL_CONFIG_FOUND")) {
                            data.putString("version", getString(R.string.error_dev));
                        } else {
                            data.putString("version", getString(R.string.error_serv));
                        }
                        data.putString("file", getString(R.string.error));
                    }
                } else {
                    data.putString("version", getString(R.string.error_serv));
                    data.putString("file", getString(R.string.error));
                }
                msg.setData(data);
                updateRemote.sendMessage(msg);
            }
        }
    }

    private class DownLoadComplte extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!zomby){
                if (intent.getAction().equalsIgnoreCase(
                        DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    Toast.makeText(context, "Download Complete", Toast.LENGTH_LONG)
                    .show();
                }
            }
        }
    }
}
