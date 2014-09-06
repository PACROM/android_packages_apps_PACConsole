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

import java.util.ArrayList;

import com.pac.console.adapters.ListArrayItem;
import com.pac.console.adapters.aospHeader;
import com.pac.console.adapters.drawerItemAdapter;
import com.pac.console.adapters.drawerItemAdapter.RowType;
import com.pac.console.adapters.drawerItemType;
import com.pac.console.ui.About_frag;
import com.pac.console.ui.Changes_frag;
import com.pac.console.ui.Contrib_frag;
import com.pac.console.ui.OTA_frag;
import com.pac.console.ui.text_frag;

import android.os.Bundle;
import android.pacstats.PACStats;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * PAC Console
 * 
 * Nav Draw
 * 
 * Basic last item handing for rotations
 * 
 * @author pvyParts
 * 
 */
public class PacConsole extends Activity {

    // some variables...
    private ArrayList<ListArrayItem> mGameTitles;
    private ArrayList<ListArrayItem> mDrawerGameTitles;
    private ListView mDrawerList;
    private ListArrayItem mSelectedItem;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int poss = 0;
    private boolean state = false;
    Fragment mContent = null;

    // *** setup the Flags for the Frags
    private final int UPDATES_GROUP = 0;
    private final int INTERFACE_GROUP = 1;
    private final int HYBRID_GROUP = 2;
    private final int ROMINFO_GROUP = 3;

    // *** setup the Flags for the Frags
    private final int OTA_FLAG = 0;
    private final int CHANGE_FLAG = 1;
    private final int CONTRIB_FLAG = 2;
    private final int ABOUT_FLAG = 3;
    private final int STATS_FLAG = 4;

    @Override
    public void onSaveInstanceState(Bundle ofLove) {
        super.onSaveInstanceState(ofLove);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        ofLove.putInt("flag", poss);
        ofLove.putBoolean("store", true);
    }

    @Override
    public void onRestoreInstanceState(Bundle ofLove) {
        super.onRestoreInstanceState(ofLove);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        poss = ofLove.getInt("flag");
        state = ofLove.getBoolean("store");
    }

    @Override
    protected void onCreate(Bundle ofLove) {
        super.onCreate(ofLove);
        // are we new or old?
        if (ofLove != null) {
            poss = ofLove.getInt("flag");
            state = ofLove.getBoolean("store");
        } else {
            Intent intent = getIntent();
            poss = intent.getIntExtra("flag", 0);
            state = intent.getBooleanExtra("store", false);
        }

        // load the main XML
        setContentView(R.layout.pac_console);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // draw toggler listeners
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                // TODO Update the actionbar title
                if (mSelectedItem != null) {
                    getActionBar().setTitle(mSelectedItem.getTitle());
                }

            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                // TODO Update the actionbar title
                getActionBar().setTitle(
                        PacConsole.this.getResources().getString(
                                R.string.app_name));

            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Setup the Menu List
        createDrawList();
        expandList();

        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {

                // handle clicks on the drawer
                if (mDrawerGameTitles.get(arg2).getViewType() == RowType.LIST_ITEM
                        .ordinal()) {

                    attachFrag(arg2);
                    mDrawerList.setSelection(arg2);
                    poss = arg2;

                } else if (mDrawerGameTitles.get(arg2).getViewType() == RowType.HEADER_ITEM
                        .ordinal()) {

                    // redo the list and update the adapter
                    ((aospHeader)mDrawerGameTitles.get(arg2)).setGroupOpen(!((aospHeader)mDrawerGameTitles.get(arg2)).getGroupOpen());
                    expandList();
                }

            }

        });

        // setup the drawer tab
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // LOAD State
        if (!state) {
            attachFrag(1);
            mDrawerLayout.openDrawer(mDrawerList);
        } else {
            attachFrag(poss);
        }

    }
    /**
     * Some Hacktrickery for the expandable list...
     *
     * this could be better...
     *
     */
    private void expandList(){
         mDrawerGameTitles = new ArrayList<ListArrayItem>();
         boolean groupOpen = false;
         int groupId = -1;
         for(int i=0; i<mGameTitles.size();i++){
             if(mGameTitles.get(i).getViewType() == RowType.HEADER_ITEM.ordinal()) { 
                 mDrawerGameTitles.add(mGameTitles.get(i));
                 groupOpen = ((aospHeader) mGameTitles.get(i)).getGroupOpen();
                 groupId = ((aospHeader) mGameTitles.get(i)).getGroupTag();
             } else {
                 if (groupOpen && groupId==((drawerItemType) mGameTitles.get(i)).getGroup()){
                     mDrawerGameTitles.add(mGameTitles.get(i));
                 }
             }
         }
         mDrawerList.setAdapter(new drawerItemAdapter(this,
                 R.layout.drawer_list_item, mDrawerGameTitles));
    }
    
    /**
     * Attach the right Fragment
     * 
     * @param possition
     */
    private void attachFrag(int possition) {
        // TODO swap fragment out.

        /**
         * use tag to select the frag needed.
         */
        Fragment fragment = null;
        android.app.FragmentManager fragmentManager = getFragmentManager();

        if (mGameTitles.get(possition).getViewType() != RowType.HEADER_ITEM
                .ordinal() && fragment == null) {
            switch (((drawerItemType) mGameTitles.get(possition)).getFlag()) {
            case OTA_FLAG:
                fragment = new OTA_frag();
                break;
            case CHANGE_FLAG:
                fragment = new Changes_frag();
                break;
            case CONTRIB_FLAG:
                fragment = new Contrib_frag();
                break;
            case ABOUT_FLAG:
                fragment = new About_frag();
                break;
            case STATS_FLAG:
                fragment = new PACStats();
                break;
            default:
                fragment = text_frag
                .newInstance("\n\n\nThe Devs Done F**ked Up!!!!\n\nI Blame The Split Screen!\n\nYou need to add and then attach the Fragment!");
            }

            // TODO tag is miffed
            // TODO find out if thats a problem or not...
            // Insert the fragment by replacing any existing fragment

            fragmentManager
            .beginTransaction()
            .replace(
                    R.id.content_frame,
                    fragment,
                    ""
                            + ((drawerItemType) mGameTitles
                                    .get(possition)).getFlag())
                                    .commit();
        }

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(possition, true);

        mSelectedItem = (ListArrayItem) mGameTitles.get(possition);

        mDrawerLayout.closeDrawer(mDrawerList);

    }

    /**
     * Creates the Draw List
     * 
     * TODO maybe add fragment handling here and drop the switch case fragment
     * thingo
     * 
     */
    private void createDrawList() {

        mGameTitles = new ArrayList<ListArrayItem>();
        ListArrayItem holder;

        /**
         * THIS ADDS A HEADER
         * 
         * holder = new aospHeader("Updates"); mGameTitles.add(holder);
         * 
         * THIS ADDS A ITEM
         * 
         * holder = new drawerItemType(); ((drawerItemType)
         * holder).setTittle(this
         * .getResources().getString(R.string.ota_menu_lbl)); ((drawerItemType)
         * holder
         * ).setCaption(this.getResources().getString(R.string.ota_menu_cap));
         * ((drawerItemType) holder).setCaptionDisplay(true); ((drawerItemType)
         * holder).setFlag(OTA_FLAG); mGameTitles.add(holder);
         * 
         * THE SECOND PART IS IN THE attachFrag(int possition) method above
         */

        // OTA Frag
        holder = new aospHeader("Updates", UPDATES_GROUP);
        ((aospHeader) holder).setGroupOpen(true);
        
        mGameTitles.add(holder);

        holder = new drawerItemType();
        ((drawerItemType) holder).setTittle(this.getResources().getString(
                R.string.ota_menu_lbl));
        ((drawerItemType) holder).setCaption(this.getResources().getString(
                R.string.ota_menu_cap));
        ((drawerItemType) holder).setCaptionDisplay(true);
        ((drawerItemType) holder).setFlag(OTA_FLAG);
        ((drawerItemType) holder).setGroup(UPDATES_GROUP);

        mGameTitles.add(holder);

        // Changes
        holder = new drawerItemType();
        ((drawerItemType) holder).setTittle(this.getResources().getString(
                R.string.change_menu_lbl));
        ((drawerItemType) holder).setCaption(this.getResources().getString(
                R.string.change_menu_cap));
        ((drawerItemType) holder).setCaptionDisplay(true);
        ((drawerItemType) holder).setFlag(CHANGE_FLAG);
        ((drawerItemType) holder).setGroup(UPDATES_GROUP);

        mGameTitles.add(holder);
        /*
         * holder = new aospHeader("Interface");
         * 
         * mGameTitles.add(holder);
         */
        /*
         * holder = new drawerItemType(); ((drawerItemType)
         * holder).setTittle("Active Display"); ((drawerItemType)
         * holder).setCaption("Moto X Active Display"); ((drawerItemType)
         * holder).setCaptionDisplay(true); ((drawerItemType)
         * holder).setFlag(-1); // TODO ADD FRAGS FOR THESE
         * 
         * mGameTitles.add(holder);
         */
        /*
         * holder = new drawerItemType(); ((drawerItemType)
         * holder).setTittle("Battery"); ((drawerItemType)
         * holder).setCaption("Battery Icon and Notification Options");
         * ((drawerItemType) holder).setCaptionDisplay(true); ((drawerItemType)
         * holder).setFlag(-1); // TODO ADD FRAGS FOR THESE
         * 
         * mGameTitles.add(holder);
         */
        /*
         * holder = new drawerItemType(); ((drawerItemType)
         * holder).setTittle("Quick Tiles"); ((drawerItemType)
         * holder).setCaption("Notification Quick Toggle Tile Options");
         * ((drawerItemType) holder).setCaptionDisplay(true); ((drawerItemType)
         * holder).setFlag(-1); // TODO ADD FRAGS FOR THESE
         * 
         * mGameTitles.add(holder);
         */
        /*
         * holder = new drawerItemType(); ((drawerItemType)
         * holder).setTittle("Lock Screen"); ((drawerItemType)
         * holder).setCaption("Lock Screen Options"); ((drawerItemType)
         * holder).setCaptionDisplay(true); ((drawerItemType)
         * holder).setFlag(-1); // TODO ADD FRAGS FOR THESE
         * 
         * mGameTitles.add(holder);
         */
        /*
         * holder = new drawerItemType(); ((drawerItemType)
         * holder).setTittle("Signal"); ((drawerItemType)
         * holder).setCaption("Signal Icon and Notification Options");
         * ((drawerItemType) holder).setCaptionDisplay(true); ((drawerItemType)
         * holder).setFlag(-1); // TODO ADD FRAGS FOR THESE
         * 
         * mGameTitles.add(holder);
         */
        /*
         * holder = new aospHeader("Hybrid");
         * 
         * mGameTitles.add(holder);
         * 
         * holder = new drawerItemType(); ((drawerItemType)
         * holder).setTittle("Global"); ((drawerItemType)
         * holder).setCaption("Set Global Hybrid Options"); ((drawerItemType)
         * holder).setCaptionDisplay(true); ((drawerItemType)
         * holder).setFlag(-1); // TODO ADD FRAGS FOR THESE
         * 
         * mGameTitles.add(holder);
         * 
         * holder = new drawerItemType(); ((drawerItemType)
         * holder).setTittle("App Specific"); ((drawerItemType)
         * holder).setCaption("Set Per App Hybrid Options"); ((drawerItemType)
         * holder).setCaptionDisplay(true); ((drawerItemType)
         * holder).setFlag(-1); // TODO ADD FRAGS FOR THESE
         * 
         * mGameTitles.add(holder);
         */

        holder = new aospHeader("ROM Info", ROMINFO_GROUP);

        mGameTitles.add(holder);

        // Contributers
        holder = new drawerItemType();
        ((drawerItemType) holder).setTittle(this.getResources().getString(
                R.string.contrib_menu_lbl));
        ((drawerItemType) holder).setCaption(this.getResources().getString(
                R.string.contrib_menu_cap));
        ((drawerItemType) holder).setCaptionDisplay(true);
        ((drawerItemType) holder).setFlag(CONTRIB_FLAG);
        ((drawerItemType) holder).setGroup(ROMINFO_GROUP);

        mGameTitles.add(holder);

        // About PAC Frag and set as default.
        holder = new drawerItemType();
        ((drawerItemType) holder).setTittle(this.getResources().getString(
                R.string.stat_menu_lbl));
        ((drawerItemType) holder).setCaption(this.getResources().getString(
                R.string.stat_menu_cap));
        ((drawerItemType) holder).setCaptionDisplay(false);
        ((drawerItemType) holder).setFlag(STATS_FLAG);
        ((drawerItemType) holder).setGroup(ROMINFO_GROUP);

        mGameTitles.add(holder);

        // Help Frag
        holder = new drawerItemType();
        ((drawerItemType) holder).setTittle(this.getResources().getString(
                R.string.help_menu_lbl));
        ((drawerItemType) holder).setCaption(this.getResources().getString(
                R.string.help_menu_cap));
        ((drawerItemType) holder).setCaptionDisplay(true);
        ((drawerItemType) holder).setFlag(ABOUT_FLAG);
        ((drawerItemType) holder).setGroup(ROMINFO_GROUP);

        mGameTitles.add(holder);

    }

    // as of now there is no menu this will change
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.pac_console, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // TODO Handle your other itmes...

        return super.onOptionsItemSelected(item);
    }

}
