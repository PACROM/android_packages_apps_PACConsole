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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.uncod.android.bypass.Bypass;

import com.pac.console.R;
import com.pac.console.util.RemoteTools;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/**
 * contributors frag
 * 
 * loads from github raw
 * 
 * passes MKDN to text view
 * using uncod Bypass Library
 * 
 * @author pvyParts
 *
 */
public class Contrib_frag extends Fragment {

    private boolean zomby = true;
    TextView contrib;
    public String contribs = "";
    boolean store = false;

    public static Contrib_frag newInstance(String content) {
        Contrib_frag fragment = new Contrib_frag();		
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        zomby = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        zomby = true;
    }


    @Override
    public void onSaveInstanceState(Bundle ofLove) {
        super.onSaveInstanceState(ofLove);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        ofLove.putBoolean("store", true);
        ofLove.putString("contribs", contribs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle ofLove) {
        zomby = false;
        if (ofLove!=null){
            contribs = ofLove.getString("contribs");
            store = ofLove.getBoolean("store");
        }

        contribs = Settings.System.getString(this.getActivity().getContentResolver(), "contribs");
        View layout = inflater.inflate(R.layout.contrib_frag_layout, null);
        contrib = (TextView) layout.findViewById(R.id.tv_ota_rom_header);
        contrib.setText("Loading");
        //restore old state if needed
        int con = RemoteTools.checkConnection(Contrib_frag.this.getActivity());
        if (con > RemoteTools.DISCONNECTED){
            AsyncTask checkTast = new CheckRemote();
            String[] dev = {" "};
            dev[0] = (String)Build.DEVICE;
            checkTast.execute((java.lang.Object[]) dev);
        } else {
            if (contribs==null){
                contrib.setText(Contrib_frag.this.getActivity().getString(R.string.no_data));
            }
        }
        return layout;
    }

    Handler updateRemote = new Handler(){

        @Override
        public void handleMessage(Message msg){
            if (!zomby){
                Log.d("MARKDOWN", "handler Start");
                String markdownString = msg.getData().getString("contribs");
                contribs = markdownString;
                Log.d("MARKDOWN", "parsed string");
                //msg.getData().getString("file");
                try{
                    Bypass bypass = new Bypass();
                    Log.d("MARKDOWN", "Bypass Loaded");
                    String[] formater = markdownString.split("\n");
                    markdownString = "";
                    Pattern pattern = Pattern.compile("[^\\S\\r\\n]{2,}");
                    Log.d("MARKDOWN", "Patturn Stuff Done");
                    for (int i = 0; i< formater.length; i++){
                        Matcher matcher = pattern.matcher(formater[i]);
                        //formater[i].replaceAll("\\s{2,}+||\\t", " ");
                        String str = matcher.replaceAll(" ");
                        markdownString+=str+"\n";	
                    }
                    Log.d("MARKDOWN", "String formated");
                    CharSequence string = bypass.markdownToSpannable(markdownString);
                    Log.d("MARKUP", "String Done!!!");
                    contrib.setText(string);
                    Log.d("MARKUP", "Set to TV");
                } catch (Error e){
                    e.printStackTrace();
                    contrib.setText(markdownString);
                } catch (Exception e){
                    e.printStackTrace();
                    contrib.setText(markdownString);
                }
                //contrib.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

    };
    private class CheckRemote extends
    AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            if (Contrib_frag.this.contribs != null){
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("contribs", contribs);
                msg.setData(data);
                updateRemote.sendMessage(msg);
            }
            String out = RemoteTools.getContrib();
            return out;
        }

        @Override
        protected void onPostExecute(final String result) {
            if (!zomby){
                Message msg = new Message();
                Bundle data = new Bundle();
                if (result != null){
                    data.putString("contribs", result);
                    if (Contrib_frag.this.getActivity()!=null){
                        Settings.System.putString(Contrib_frag.this.getActivity().getContentResolver(), "contribs", result);
                    }
                } else {
                    if (Contrib_frag.this.contribs==null){
                        data.putString("contribs", "Or The Split Screen Broke Something!");
                    }
                }
                msg.setData(data);
                updateRemote.sendMessage(msg);
            }
        }
    };
}
