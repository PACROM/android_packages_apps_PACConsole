/*
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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pac.console.R;
import com.pac.console.helpers.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pvyparts on 1/25/16.
 */
public class InfoFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "InfoFrag" ;

    private LinearLayout mLayoutUpdateBtn;
    private LinearLayout mLayoutUpdateInfo;
    private TextView tvUpdate_version;
    private TextView tvUpdate_date;
    private TextView tvUpdate_size;
    private TextView tvUpdate_md5;

    private TextView tvUpdateAvailable;
    private Button btnUpdate;

    public static InfoFragment newInstance(){
        return new InfoFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_info, null);
        TextView tvRomName = (TextView) rootView.findViewById(R.id.tv_rom);
        ImageView ivDeviceImage = (ImageView) rootView.findViewById(R.id.iv_device_image);

        TextView tvBuildVer = (TextView) rootView.findViewById(R.id.tv_version);
        tvBuildVer.setText(String.format(getString(R.string.prefix_build_version), Utils.getVersion()));

        TextView tvBuildDate = (TextView) rootView.findViewById(R.id.tv_build_date);
        String formattedDate = SimpleDateFormat.getDateTimeInstance().format(new Date(Utils.getInstalledBuildDate() * 1000));
        tvBuildDate.setText(String.format(getString(R.string.prefix_build_date), formattedDate));

        tvRomName.setText("PAC-ROM");
        ivDeviceImage.setImageDrawable(this.getActivity().getDrawable(R.drawable.pac_logo));

        return rootView;
    }

    @Override
    public void onClick(View v) {
        //TODO UPDATE like a boss! we have no data tho...
    }
}
