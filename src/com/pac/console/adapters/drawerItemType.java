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
package com.pac.console.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.pac.console.R;
import com.pac.console.R.id;
import com.pac.console.adapters.ListArrayItem;
import com.pac.console.adapters.changeItemAdapter.RowType;

/**
 * Custom Draw List Type
 *
 * @author pvyParts
 *
 */
public class drawerItemType implements ListArrayItem {

    private int FLAG;  // fragment shit
    private int GROUP; // expandable shit
    
    private String title;

    private boolean caption_display = false;
    private String caption;

    private boolean URL_launch = false;
    private String URL;

    private boolean package_launch = false;
    private String package_name;
    private String package_path;

    private boolean toggle_display = false;
    private OnCheckedChangeListener ontoggle;

    public int getFlag() {
        return this.FLAG;
    }

    public int getGroup() {
        return this.GROUP;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean getCaptionDisplay() {
        return this.caption_display;
    }

    public String getCaption() {
        return this.caption;
    }

    public boolean getURLLaunch() {
        return this.URL_launch;
    }

    public String getURL() {
        return this.URL;
    }

    public String getPackageName() {
        return this.package_name;
    }

    public boolean getPackageLaunch() {
        return this.package_launch;
    }

    public String getPackagePath() {
        return this.package_path;
    }

    public boolean getToggleDisplay() {
        return this.toggle_display;
    }

    public OnCheckedChangeListener getOnToggleListener() {
        return this.ontoggle;
    }

    public void setFlag(int in) {
        this.FLAG = in;
    }

    public void setGroup(int in) {
        this.GROUP = in;
    }

    public void setTittle(String in) {
        this.title = in;
    }

    public void setCaptionDisplay(boolean in) {
        this.caption_display = in;
    }

    public void setCaption(String in) {
        this.caption = in;
    }

    public void setURLLaunch(boolean in) {
        this.URL_launch = in;
    }

    public void setURL(String in) {
        this.URL = in;
    }

    public void setPackageName(String in) {
        this.package_name = in;
    }

    public void setPackageLaunch(boolean in) {
        this.package_launch = in;
    }

    public void setPackagePath(String in) {
        this.package_path = in;
    }

    public void setToggleDisplay(boolean in) {
        this.toggle_display = in;
    }

    public void setOnToggleListener(OnCheckedChangeListener in) {
        this.ontoggle = in;
    }

    @Override
    public int getViewType() {
        // TODO Auto-generated method stub
        return RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.drawer_list_item, null);
        }

        /*
         * Only show the required parts of the view as long as there is a item
         * to show default is just a title
         */

        // set the title
        TextView tvTit = (TextView) view.findViewById(id.dli_title);
        tvTit.setText(this.getTitle());
        // show the caption
        if (this.getCaptionDisplay()) {
            TextView tvCap = (TextView) view.findViewById(id.dli_summary);
            tvCap.setText(this.getCaption());
            tvCap.setVisibility(View.VISIBLE);

        } else {
            TextView tvCap = (TextView) view.findViewById(id.dli_summary);
            tvCap.setVisibility(View.GONE);
        }

        return view;

    }

}
