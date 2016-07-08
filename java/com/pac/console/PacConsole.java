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

package com.pac.console;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.pac.console.fragments.BatteryFragment;
import com.pac.console.fragments.ClockFragment;
import com.pac.console.fragments.InfoFragment;
import com.pac.console.fragments.ListFragmentDemo;
import com.pac.console.fragments.LockscreenFragment;
import com.pac.console.fragments.NetworkTrafficFragment;
import com.pac.console.fragments.SettingsFragment;
import com.pac.console.fragments.StatusBarFragment;

/**
 * Created by pvyparts on 1/25/16.
 *
 * TODO get vector image assets
 * TODO add update stuffs
 *
 */
public class PacConsole extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    InfoFragment mInfoFrag = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pac_console);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mInfoFrag = InfoFragment.newInstance();

        commitFragment(mInfoFrag);
        navigationView.setCheckedItem(R.id.nav_updates);
        hideMenuItems();

        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        updateNav();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        } else {
            int count = getFragmentManager().getBackStackEntryCount();
            if (count == 1) {
                super.onBackPressed();
            } else {
                getFragmentManager().popBackStack();
                updateNav();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.pac_console, menu);
        return false; // no menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // we have no options ATM
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    } // we have no menu yet

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_updates) {
            commitFragment(mInfoFrag);
        } else if (id == R.id.com_web) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.pac-rom.com")));
        } else if (id == R.id.com_gplus) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://gplus.pac-rom.com")));
        } else if (id == R.id.com_facebook) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://facebook.pac-rom.com")));
        } else if (id == R.id.com_twitter) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.pac-rom.com")));
        } else if (id == R.id.com_instagram) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.pac-rom.com")));
        } else if (id == R.id.com_github) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.pac-rom.com")));
        } else if (id == R.id.com_gerrit) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://review.pac-rom.com")));
        } else if (id == R.id.nav_two) {
            commitFragment(SettingsFragment.newInstance());
        } else if (id == R.id.nav_three) {
            commitFragment(ListFragmentDemo.newInstance());
        } else if (id == R.id.nav_four) {
            commitFragment(BatteryFragment.newInstance());
        } else if (id == R.id.nav_five) {
            commitFragment(ClockFragment.newInstance());
        } else if (id == R.id.nav_six) {
            commitFragment(LockscreenFragment.newInstance());
        } else if (id == R.id.nav_seven) {
            commitFragment(NetworkTrafficFragment.newInstance());
        } else if (id == R.id.nav_eight) {
            commitFragment(StatusBarFragment.newInstance());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void commitFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().replace(R.id.frag_content_area, fragment)
                .addToBackStack(null).commit();

        updateNav();
    }

    private void updateNav() {
        Fragment f = getFragmentManager().findFragmentById(R.id.frag_content_area);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (f instanceof InfoFragment) {
            navigationView.setCheckedItem(R.id.nav_updates);
            getSupportActionBar().setTitle(R.string.nav_info);
        } else if (f instanceof SettingsFragment) {
            navigationView.setCheckedItem(R.id.nav_two);
            getSupportActionBar().setTitle(R.string.nav_settings_demo);
        } else if (f instanceof ListFragmentDemo) {
            navigationView.setCheckedItem(R.id.nav_three);
            getSupportActionBar().setTitle(R.string.nav_list_demo);
        } else if (f instanceof BatteryFragment) {
            navigationView.setCheckedItem(R.id.nav_four);
            getSupportActionBar().setTitle(R.string.battery_settings_title);
        } else if (f instanceof ClockFragment) {
            navigationView.setCheckedItem(R.id.nav_five);
            getSupportActionBar().setTitle(R.string.clock_setting_title);
        } else if (f instanceof LockscreenFragment) {
            navigationView.setCheckedItem(R.id.nav_six);
            getSupportActionBar().setTitle(R.string.lock_screen_title);
        } else if (f instanceof NetworkTrafficFragment) {
            navigationView.setCheckedItem(R.id.nav_seven);
            getSupportActionBar().setTitle(R.string.network_traffic_title);
        } else if (f instanceof StatusBarFragment) {
            navigationView.setCheckedItem(R.id.nav_eight);
            getSupportActionBar().setTitle(R.string.status_bar_title);
        }
    }

    // Hide unused items
    private void hideMenuItems() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu navigationMenu = navigationView.getMenu();

        navigationMenu.findItem(R.id.nav_two).setVisible(false);
        navigationMenu.findItem(R.id.nav_three).setVisible(false);
    }

}
