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

import com.pac.console.R;
import com.pac.console.adapters.changeItemAdapter.RowType;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Custom ChangeLog List Type
 *
 * @author pvyParts
 *
 */
public class changeItemType implements ListArrayItem {

    private String SHA;
    private String title;
    private String caption;
    private String date;
    private String author;
    private String URL;
    private boolean newChange = false;

    public changeItemType(){

    }

    public boolean getNew(){
        return this.newChange;
    }

    public String getSHA(){
        return this.SHA;
    }
    public String getTitle(){
        return this.title;
    }
    public String getCaption(){
        return this.caption;
    }
    public String getDate(){
        return this.date;
    }
    public String getAuthor(){
        return this.author;
    }
    public String getURL(){
        return this.URL;
    }

    public void setSHA(String in){
        this.SHA = in;
    }
    public void setTitle(String in){
        this.title = in;
    }
    public void setCaption(String in){
        this.caption = in;
    }
    public void setDate(String in){
        this.date = in;
    }
    public void setAuthor(String in){
        this.author = in;
    }
    public void setURL(String in){
        this.URL = in;
    }
    public void setNewChange(boolean in){
        this.newChange = in;
    }

    @Override
    public int getViewType() {
        // TODO Auto-generated method stub
        return RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {

        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.changes_list_item, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView tvNew = (TextView) view.findViewById(R.id.cli_tb_data_new);

        if (this.newChange){
            tvNew.setText("NEW");
            tvNew.setTextColor(Color.GREEN);
        } else {
            tvNew.setText("INC");
            tvNew.setTextColor(Color.parseColor("#33B5E5"));
        }
        // set the title
        TextView tvTit = (TextView) view.findViewById(R.id.cli_title);
        tvTit.setText(this.caption);
        // show the caption
        TextView tvCap = (TextView) view.findViewById(R.id.cli_caption);
        tvCap.setText(this.title+" - "+this.author);
        return view;

    }


}
