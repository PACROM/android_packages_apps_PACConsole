/*
 * Copyright (C) 2015 crDroid Android
 * Copyright (C) 2016 The PAC-ROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pac.console.fragments;

import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.SecurityException;

import com.pac.console.R;

public class ChangelogFragment extends PreferenceFragment {

    private static final String CHANGELOG_PATH = "/system/etc/Changelog.txt";

    public static boolean fileExists(){
        try {
            File file = new File(CHANGELOG_PATH);
            return file.exists();
        } catch ( SecurityException e ) {
            e.printStackTrace();
            return false;
        }
    }

    public static ChangelogFragment newInstance(){
        return new ChangelogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.changelog);

        PreferenceScreen ps = this.getPreferenceScreen();

        InputStreamReader inputReader = null;
        StringBuilder data = new StringBuilder();

                try {
            char tmp[] = new char[2048];
            int numRead;

            inputReader = new FileReader(CHANGELOG_PATH);
            while ((numRead = inputReader.read(tmp)) >= 0) {
                data.append(tmp, 0, numRead);
            }
        } catch (IOException e) {
        } finally {
            try {
                if (inputReader != null) {
                    inputReader.close();
                }
            } catch (IOException e) {
            }
        }

        List<Change> chngs = PassChangelogText(data.toString());

        long dv = Long.valueOf(chngs.get(0).getmDate()) * 1000;
        Date date = new Date(dv);

        String lastDate = new SimpleDateFormat("yyyy MM dd").format(date);

        PreferenceCategory pc = new PreferenceCategory(getContext());
        pc.setTitle(lastDate);

        ps.addPreference(pc);

        for (int i = 0; i < chngs.size() - 1; i++) {

            dv = Long.valueOf(chngs.get(i).getmDate()) * 1000;
            date = new Date(dv);

            if (!lastDate.equalsIgnoreCase(new SimpleDateFormat("yyyy MM dd").format(date))){
                lastDate = new SimpleDateFormat("yyyy MM dd").format(date);

                PreferenceCategory holderCat = new PreferenceCategory(getContext());
                holderCat.setTitle(lastDate);
                ps.addPreference(holderCat);
            }
            Preference holderPref = new Preference(getContext());
            holderPref.setTitle(chngs.get(i).getCommitText());
            holderPref.setSummary("android_" + chngs.get(i).getmProject().substring(0, chngs.get(i).getmProject().length() -1).replace("/", "_") + /*" at " + new SimpleDateFormat("hh:mma").format(date) + */" by " + chngs.get(i).getCommitAuth());
            ps.addPreference(holderPref);

            this.setPreferenceScreen(ps);

        }

    }

    private List PassChangelogText(String RawChanges) {

        String[] SplitString = RawChanges.split("\n");
        List<Change> changes = new ArrayList<>();

        String lastProject = "";

        for (int i = 0; i < SplitString.length - 1; i++) {
            String SubStr = SplitString[i];
            if (SubStr.length() > 0) {
                if (SubStr.startsWith("|*|")) {
                    // dumped not needed
                } else if (SubStr.startsWith("project")) {
                    try {
                        lastProject = SubStr.split(" ")[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        // fail whale...
                    }
                } else {
                    try {
                        String[] stringChanges = SubStr.split("\\|");
                        changes.add(new Change(Integer.parseInt(stringChanges[1]), lastProject, stringChanges[0], stringChanges[3], stringChanges[2]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Collections.sort(changes);

        return changes;
    }

}
