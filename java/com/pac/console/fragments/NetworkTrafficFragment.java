/*
 * Copyright (C) 2014 The Dirty Unicorns Project
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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.TrafficStats;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pac.console.R;
import com.pac.console.helpers.PACSeekBarPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class NetworkTrafficFragment extends PreferenceFragment
    implements OnPreferenceChangeListener {

    private static final String TAG = "NetworkTraffic";

    private static final String NETWORK_TRAFFIC_STATE = "network_traffic_state";
    private static final String NETWORK_TRAFFIC_COLOR = "network_traffic_color";
    private static final String NETWORK_TRAFFIC_UNIT = "network_traffic_unit";
    private static final String NETWORK_TRAFFIC_PERIOD = "network_traffic_period";
    private static final String NETWORK_TRAFFIC_AUTOHIDE = "network_traffic_autohide";
    private static final String NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD = "network_traffic_autohide_threshold";

    private ListPreference mNetTrafficState;
    private ColorPickerPreference mNetTrafficColor;
    private ListPreference mNetTrafficUnit;
    private ListPreference mNetTrafficPeriod;
    private CheckBoxPreference mNetTrafficAutohide;
    private PACSeekBarPreference mNetTrafficAutohideThreshold;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DEFAULT_TRAFFIC_COLOR = 0xffffffff;

    private int mNetTrafficVal;
    private int MASK_UP;
    private int MASK_DOWN;
    private int MASK_UNIT;
    private int MASK_PERIOD;

    public static NetworkTrafficFragment newInstance(){
        return new NetworkTrafficFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.network_traffic_settings);

        loadResources();
        setHasOptionsMenu(true);
        ContentResolver resolver = getActivity().getContentResolver();

        mNetTrafficState = (ListPreference) findPreference(NETWORK_TRAFFIC_STATE);
        mNetTrafficUnit = (ListPreference) findPreference(NETWORK_TRAFFIC_UNIT);
        mNetTrafficPeriod = (ListPreference) findPreference(NETWORK_TRAFFIC_PERIOD);

        mNetTrafficAutohide =
                (CheckBoxPreference) findPreference(NETWORK_TRAFFIC_AUTOHIDE);
        mNetTrafficAutohide.setChecked((Settings.System.getInt(resolver,
                Settings.System.NETWORK_TRAFFIC_AUTOHIDE, 0) == 1));
        mNetTrafficAutohide.setOnPreferenceChangeListener(this);

        mNetTrafficAutohideThreshold =
                (PACSeekBarPreference) findPreference(NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD);
        int netTrafficAutohideThreshold = Settings.System.getInt(resolver,
                Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 10);
        mNetTrafficAutohideThreshold.setValue(netTrafficAutohideThreshold / 1);
        mNetTrafficAutohideThreshold.setOnPreferenceChangeListener(this);

        mNetTrafficColor =
            (ColorPickerPreference) findPreference(NETWORK_TRAFFIC_COLOR);
        mNetTrafficColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(resolver,
                Settings.System.NETWORK_TRAFFIC_COLOR, 0xffffffff);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNetTrafficColor.setSummary(hexColor);
        mNetTrafficColor.setNewPreviewColor(intColor);

        if (TrafficStats.getTotalTxBytes() != TrafficStats.UNSUPPORTED &&
                TrafficStats.getTotalRxBytes() != TrafficStats.UNSUPPORTED) {
            mNetTrafficVal = Settings.System.getInt(resolver,
                    Settings.System.NETWORK_TRAFFIC_STATE, 0);
            int intIndex = mNetTrafficVal & (MASK_UP + MASK_DOWN);
            intIndex = mNetTrafficState.findIndexOfValue(String.valueOf(intIndex));
            updateNetworkTrafficState(intIndex);

            mNetTrafficState.setValueIndex(intIndex >= 0 ? intIndex : 0);
            mNetTrafficState.setSummary(mNetTrafficState.getEntry());
            mNetTrafficState.setOnPreferenceChangeListener(this);

            mNetTrafficUnit.setValueIndex(getBit(mNetTrafficVal, MASK_UNIT) ? 1 : 0);
            mNetTrafficUnit.setSummary(mNetTrafficUnit.getEntry());
            mNetTrafficUnit.setOnPreferenceChangeListener(this);

            intIndex = (mNetTrafficVal & MASK_PERIOD) >>> 16;
            intIndex = mNetTrafficPeriod.findIndexOfValue(String.valueOf(intIndex));
            mNetTrafficPeriod.setValueIndex(intIndex >= 0 ? intIndex : 1);
            mNetTrafficPeriod.setSummary(mNetTrafficPeriod.getEntry());
            mNetTrafficPeriod.setOnPreferenceChangeListener(this);
        }
    }

    private void updateNetworkTrafficState(int mIndex) {
        if (mIndex <= 0) {
            mNetTrafficUnit.setEnabled(false);
            mNetTrafficColor.setEnabled(false);
            mNetTrafficPeriod.setEnabled(false);
            mNetTrafficAutohide.setEnabled(false);
            mNetTrafficAutohideThreshold.setEnabled(false);
        } else {
            mNetTrafficUnit.setEnabled(true);
            mNetTrafficColor.setEnabled(true);
            mNetTrafficPeriod.setEnabled(true);
            mNetTrafficAutohide.setEnabled(true);
            mNetTrafficAutohideThreshold.setEnabled(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.network_traffic_color_reset)
                .setIcon(R.drawable.ic_menu_reset)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefault();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.network_traffic_color_reset);
        alertDialog.setMessage(R.string.network_traffic_color_reset_message);
        alertDialog.setPositiveButton(R.string.menu_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                NetworkTrafficColorReset();
            }
        });
        alertDialog.setNegativeButton(R.string.menu_cancel, null);
        alertDialog.create().show();
    }

    private void NetworkTrafficColorReset() {
        ContentResolver resolver = getActivity().getContentResolver();
        Settings.System.putInt(resolver,
                Settings.System.NETWORK_TRAFFIC_COLOR, DEFAULT_TRAFFIC_COLOR);

        mNetTrafficColor.setNewPreviewColor(DEFAULT_TRAFFIC_COLOR);
        String hexColor = String.format("#%08x", (0xffffffff & DEFAULT_TRAFFIC_COLOR));
        mNetTrafficColor.setSummary(hexColor);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNetTrafficState) {
            int intState = Integer.valueOf((String)newValue);
            mNetTrafficVal = setBit(mNetTrafficVal, MASK_UP, getBit(intState, MASK_UP));
            mNetTrafficVal = setBit(mNetTrafficVal, MASK_DOWN, getBit(intState, MASK_DOWN));
            Settings.System.putInt(resolver,
                    Settings.System.NETWORK_TRAFFIC_STATE, mNetTrafficVal);
            int index = mNetTrafficState.findIndexOfValue((String) newValue);
            mNetTrafficState.setSummary(mNetTrafficState.getEntries()[index]);
            updateNetworkTrafficState(index);
            return true;
        } else if (preference == mNetTrafficColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(resolver,
                    Settings.System.NETWORK_TRAFFIC_COLOR, intHex);
            return true;
        } else if (preference == mNetTrafficUnit) {
            mNetTrafficVal = setBit(mNetTrafficVal, MASK_UNIT, ((String)newValue).equals("1"));
            Settings.System.putInt(resolver,
                    Settings.System.NETWORK_TRAFFIC_STATE, mNetTrafficVal);
            int index = mNetTrafficUnit.findIndexOfValue((String) newValue);
            mNetTrafficUnit.setSummary(mNetTrafficUnit.getEntries()[index]);
            return true;
        } else if (preference == mNetTrafficPeriod) {
            int intState = Integer.valueOf((String)newValue);
            mNetTrafficVal = setBit(mNetTrafficVal, MASK_PERIOD, false) + (intState << 16);
            Settings.System.putInt(resolver,
                    Settings.System.NETWORK_TRAFFIC_STATE, mNetTrafficVal);
            int index = mNetTrafficPeriod.findIndexOfValue((String) newValue);
            mNetTrafficPeriod.setSummary(mNetTrafficPeriod.getEntries()[index]);
            return true;
        } else if (preference == mNetTrafficAutohide) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE, value ? 1 : 0);
            return true;
        } else if (preference == mNetTrafficAutohideThreshold) {
            int threshold = (Integer) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, threshold * 1);
            return true;
        }
        return false;
    }

    private void loadResources() {
        Resources resources = getActivity().getResources();
        MASK_UP = resources.getInteger(R.integer.maskUp);
        MASK_DOWN = resources.getInteger(R.integer.maskDown);
        MASK_UNIT = resources.getInteger(R.integer.maskUnit);
        MASK_PERIOD = resources.getInteger(R.integer.maskPeriod);
    }

    private int setBit(int intNumber, int intMask, boolean blnState) {
        if (blnState) {
            return (intNumber | intMask);
        }
        return (intNumber & ~intMask);
    }

    private boolean getBit(int intNumber, int intMask) {
        return (intNumber & intMask) == intMask;
    }
}
