package com.thefamulus.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thefamulus.app.util.SystemUiHider;

import java.util.Date;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {

    /**
     * String to identify my app
     */
    private static final String appId = "com.thefamulus.app";
    private static final String logCatId = "MAB";
    /**
     * App shared preferences
     */
    SharedPreferences sharedPreferences;
    private static final String PREF_DATE_LAST_USED = "Date Last Used";
    private static final String PREF_USED_COUNT = "Used Count";

    /**
     * Holder of the current next alarm
      */
    private static final int ALARM_UPDATE_MSEC = 60000;
    TextView mAlarmDisplay;
    View mAlarmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        final View containerView = findViewById(R.id.main_container);

        sharedPreferences = getSharedPreferences(appId, Context.MODE_PRIVATE);

        // Create blocks for each of the existing block types
        findAndInstantiate();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Same the shared preferences
        writeSharedPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_fullscreen_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                // Refresh list;
                Toast.makeText(getApplicationContext(), "Refresh List",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_new:
                // make new;
                newBlock();
                Toast.makeText(getApplicationContext(), "Create an New Item",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_search:
                // Search for...;
                Toast.makeText(getApplicationContext(), "Search List",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                // Settings();
                Toast.makeText(getApplicationContext(), "Settings",
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void writeSharedPreferences() {
        // Update and write out shared preferences
        long counter = 0;
        Date now = new Date();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.contains(PREF_USED_COUNT)) {
            // Used previously
            counter = sharedPreferences.getLong(PREF_USED_COUNT, 0l);
        }
        editor.putLong(PREF_USED_COUNT, ++counter);
        editor.putString(PREF_DATE_LAST_USED, now.toString());
        editor.commit();
        Toast.makeText(getApplicationContext()
                , "Application used " + String.valueOf(counter) + " times since installed and that last time was "
                    + now.toString(), Toast.LENGTH_SHORT).show();
    }

    private void newBlock() {
        // Create a new block
    }

    private void findAndInstantiate() {
        // Find each of the existing items that can be represented as blocks
        // and create the blocks for each

        // Alarms
        final Alarm nextAlarm = new Alarm();
        createAlarmBlock();
    }

    private void createAlarmBlock() {
        // Find if any alarms are set and build blocks for each
        final LinearLayout mainContainer = (LinearLayout) findViewById(R.id.main_container);
        final Context appContext = getApplicationContext();
        final Date nextAlarm = getNextAlarm(appContext);
        final Date now = new Date();

        if (nextAlarm != null) {

            // Set the time on the clock
            //final AnalogClock alarmTime = (AnalogClock) findViewById(R.id.analogClock);

            mAlarm = new AlarmTimer(nextAlarm.getTime() - now.getTime(), ALARM_UPDATE_MSEC);
            mAlarmView = getLayoutInflater().inflate(R.layout.alarm,mainContainer,false);
            mAlarmDisplay = (TextView) mAlarmView.findViewById(R.id.alarmTimer);
            final TextView alarmTime = (TextView) mAlarmView.findViewById(R.id.alarmSet);
            alarmTime.setText(Settings.System.getString(appContext.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED));

            //mAlarmDisplay.setText("@string/alarmPrepare");
            mAlarmDisplay.setText("Preparing alarm...");
            mainContainer.addView(mAlarmView);
            mAlarm.start();
        }

    }
}

