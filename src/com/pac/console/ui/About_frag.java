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

import com.pac.console.R;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;
/**
 * About Pac frag launch some links n shit
 * 
 * @author pvyParts
 *
 */
public class About_frag extends PreferenceFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.about_pac);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference.getKey().equals("web")){
            launchIntent(getString(R.string.pref_abt_web_link));
        } else if (preference.getKey().equals("irc")){
            launchIntent(getString(R.string.pref_abt_irc_link));
        } else if (preference.getKey().equals("facebook")){
            launchIntent(getString(R.string.pref_abt_facebook_link));
        } else if (preference.getKey().equals("google")){
            launchIntent(getString(R.string.pref_abt_google_link));
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);


    }

    private void launchIntent(String uri){
        try{
            Intent launchIntents = new Intent(
                    "android.intent.action.VIEW", Uri.parse(uri));
            launchIntents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launchIntents);

        } catch (ActivityNotFoundException e){
            Toast.makeText(this.getActivity(), "#BLAMETHESPLITSCREEN - No App Installed Capable of launching This!" , Toast.LENGTH_SHORT).show();
        }

    }

    public static About_frag newInstance(String content) {
        About_frag fragment = new About_frag();		
        return fragment;

    }


}
