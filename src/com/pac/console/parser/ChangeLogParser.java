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
package com.pac.console.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.text.format.DateFormat;

import com.pac.console.adapters.changeHeader;
import com.pac.console.adapters.changeItemType;
import com.pac.console.adapters.ListArrayItem;

/**
 * Parser for the PAC changelog json generator!
 * json -> ArrayList<ListArrayItem>
 * complete with day headers
 *
 * @author pvyParts
 *
 */
public class ChangeLogParser {
    public static ArrayList<ListArrayItem> ChangeLogParser(String JsonData)
            throws JSONException {

        JSONArray jsonArray = new JSONArray(JsonData);
        ArrayList<changeItemType> JSONArray = new ArrayList<changeItemType>();
        long time = Build.TIME;
        Date buildTime = new Date(time);
        for (int i = 0; i < jsonArray.length(); i++) {
            changeItemType hold = new changeItemType();
            JSONObject json_data = jsonArray.getJSONObject(i);
            hold.setSHA(json_data.getString("SHA"));
            hold.setTitle(json_data.getString("Repository"));
            hold.setCaption(json_data.getString("Message"));
            //2013-12-02 21:44:31
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(json_data.getString("CommitDate"));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (date.after(buildTime)){
                hold.setNewChange(true);
            }
            hold.setDate(json_data.getString("CommitDate"));
            hold.setAuthor(json_data.getString("GitUsername"));

            JSONArray.add(hold);
        }
        ArrayList<ListArrayItem> formatedArray = new ArrayList<ListArrayItem>();
        String str_date = "";
        for (int i = 0; i < JSONArray.size(); i++){
            if (!str_date.equals(JSONArray.get(i).getDate().substring(0, 10))){
                str_date = JSONArray.get(i).getDate().substring(0, 10);
                changeHeader hold = new changeHeader(JSONArray.get(i).getDate().substring(0, 10));
                formatedArray.add(hold);
            }
            changeItemType hold2 = new changeItemType();
            hold2.setSHA(JSONArray.get(i).getSHA());
            hold2.setTitle(JSONArray.get(i).getTitle());
            hold2.setCaption(JSONArray.get(i).getCaption());
            hold2.setDate(JSONArray.get(i).getDate());
            hold2.setAuthor(JSONArray.get(i).getAuthor());
            hold2.setNewChange(JSONArray.get(i).getNew());

            formatedArray.add(hold2);
        }
        return formatedArray;

    }
}
