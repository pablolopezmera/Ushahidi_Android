
package com.ushahidi.android.app.ui.tablet;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentMapActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.view.View;

import com.ushahidi.android.app.MainApplication;
import com.ushahidi.android.app.R;
import com.ushahidi.android.app.Settings;
import com.ushahidi.android.app.ui.phone.ReportTabActivity;

public class DashboardActivity extends FragmentMapActivity implements ListMapFragmentListener {

    private boolean detailsInline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_items);

        ListMapFragment maps = (ListMapFragment)getSupportFragmentManager().findFragmentById(
                R.id.list_map_fragment);
        maps.setListMapListener(this);

        // check if we have a frame to embed list fragment
        View f = findViewById(R.id.show_fragment);

        detailsInline = (f != null
                && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) && f
                .getVisibility() == View.VISIBLE);

        if (detailsInline) {
            maps.enablePersistentSelection();
            ReportTabFragment reportTabFragment = new ReportTabFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.show_fragment, reportTabFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
            ft.commit();
        } else if (f != null) {
            f.setVisibility(View.GONE);
        }

    }

    @Override
    public void onMapSelected(int mapId) {
        log("message id" + mapId);
        if (detailsInline) {
            ListReportFragment listReportFragment = new ListReportFragment();
            listReportFragment.mListReportAdapter.refresh(this);
            listReportFragment.mListReportView.getPullToRefreshListView().setAdapter(
                    listReportFragment.mListReportAdapter);

        } else {
            Intent i = new Intent(this, ReportTabActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.home_enter, R.anim.home_exit);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_map, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_about) {
            showDialog();
            return true;
        } else if (item.getItemId() == R.id.app_settings) {
            startActivity(new Intent(this, Settings.class));

            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public void showDialog() {

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out, R.anim.slide_right_in,
                R.anim.slide_right_out);
        ft.addToBackStack(null);

        // Create and show the dialog.
        AboutFragment newFragment = AboutFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

    protected void log(String message) {
        if (MainApplication.LOGGING_MODE)
            Log.i(getClass().getName(), message);
    }

    protected void log(String format, Object... args) {
        if (MainApplication.LOGGING_MODE)
            Log.i(getClass().getName(), String.format(format, args));
    }

    protected void log(String message, Exception ex) {
        if (MainApplication.LOGGING_MODE)
            Log.e(getClass().getName(), message, ex);
    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
}
