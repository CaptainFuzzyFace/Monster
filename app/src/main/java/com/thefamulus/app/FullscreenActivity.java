package com.thefamulus.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thefamulus.app.util.SystemUiHider;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
    private static final int msecMinute = 1000 * 60;
    private static final int msecHour = msecMinute * 60;
    private static final int msecDay = msecHour * 24;

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
    AlarmTimer mAlarm;
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
        final LinearLayout parentView = (LinearLayout) findViewById(R.id.main_container);

        // Alarms
        findNextAlarm(parentView);
    }

    private void findNextAlarm(LinearLayout mainContainer) {
        // Find if any alarms are set and build blocks for each
        //String nextAlarm = Settings.System.getString(getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED);
        final Date nextAlarm = getNextAlarm(getApplicationContext());
        final Date now = new Date();

        if (nextAlarm != null) {

            // Set the time on the clock
            //final AnalogClock alarmTime = (AnalogClock) findViewById(R.id.analogClock);

            mAlarm = new AlarmTimer(nextAlarm.getTime() - now.getTime(), ALARM_UPDATE_MSEC);
            mAlarmView = getLayoutInflater().inflate(R.layout.alarm,mainContainer,false);
            mAlarmDisplay = (TextView) mAlarmView.findViewById(R.id.alarmTimer);
            final TextView alarmTime = (TextView) mAlarmView.findViewById(R.id.alarmSet);
            alarmTime.setText(Settings.System.getString(getApplicationContext().getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED));

            //mAlarmDisplay.setText("@string/alarmPrepare");
            mAlarmDisplay.setText("Preparing alarm...");
            mainContainer.addView(mAlarmView);
            mAlarm.start();
        }

    }

    public class AlarmTimer extends CountDownTimer {
        // Count down timer to use as the alarm
        public AlarmTimer(long delay, long ticker) {
            // Constructed
            super(delay, ticker);
        }

        @Override
        public void onFinish() {
            // What to do when we hit zero
            Log.i(logCatId, "Tock");
            mAlarmDisplay.setText(R.string.alarmTriggered);
            final LinearLayout parentView = (LinearLayout) findViewById(R.id.main_container);
            parentView.removeView(mAlarmView);
        }

        @Override
        public void onTick(long msecRemaining) {
            // Days...
            final int days = (int) msecRemaining / msecDay;
            final int hours = (int) (msecRemaining - (days * msecDay)) / msecHour;
            final int minutes = (int) (msecRemaining - (days * msecDay) - (hours * msecHour)) / msecMinute;

            if (days > 0)
                mAlarmDisplay.setText("Ages yet...");
            else if (hours > 1)
                mAlarmDisplay.setText(Integer.toString(hours) + " hours " + Integer.toString(minutes) + " mins");
            else if (hours > 0)
                mAlarmDisplay.setText(Integer.toString(hours) + " hour " + Integer.toString(minutes) + " mins");
            else if (minutes > 1)
                mAlarmDisplay.setText(Integer.toString(minutes) + " mins");
            else
                mAlarmDisplay.setText(Integer.toString(minutes) + " min");

        }
    }

    public static Date getNextAlarm(Context context) {
        // let's collect short names of days :-)
        DateFormatSymbols symbols = new DateFormatSymbols();
        // and fill with those names map...
        Map<String, Integer> map = new HashMap<String, Integer>();
        String[] dayNames = symbols.getShortWeekdays();
        // filing :-)
        map.put(dayNames[Calendar.MONDAY], Calendar.TUESDAY);
        map.put(dayNames[Calendar.TUESDAY], Calendar.WEDNESDAY);
        map.put(dayNames[Calendar.WEDNESDAY], Calendar.THURSDAY);
        map.put(dayNames[Calendar.THURSDAY], Calendar.FRIDAY);
        map.put(dayNames[Calendar.FRIDAY], Calendar.SATURDAY);
        map.put(dayNames[Calendar.SATURDAY], Calendar.SUNDAY);
        map.put(dayNames[Calendar.SUNDAY], Calendar.MONDAY);
        // Yeah, knowing next alarm will help.....
        String nextAlarm = Settings.System.getString(context.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED);
        // In case if it isn't set.....
        if ((nextAlarm == null) || ("".equals(nextAlarm))) return null;
        // let's see a day....
        String nextAlarmDay = nextAlarm.split(" ")[0];
        // and its number....
        int alarmDay = map.get(nextAlarmDay);

        // the same for day of week (I'm not sure why I didn't use Calendar.get(Calendar.DAY_OF_WEEK) here...
        Date now = new Date();
        String dayOfWeek = new SimpleDateFormat("EE", Locale.getDefault()).format(now);
        int today = map.get(dayOfWeek);

        // OK, so let's calculate how many days we have to next alarm :-)
        int daysToAlarm = alarmDay - today;
        // yep, sometimes it will  be negtive number so add 7.
        if (daysToAlarm < 0) daysToAlarm += 7;

        // Now we will build date, and parse it.....
        try {
            Calendar cal2 = Calendar.getInstance();
            String str = cal2.get(Calendar.YEAR) + "-" + (cal2.get(Calendar.MONTH) + 1) + "-" + (cal2.get(Calendar.DAY_OF_MONTH));

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-d hh:mm");

            cal2.setTime(df.parse(str + nextAlarm.substring(nextAlarm.indexOf(" "))));
            cal2.add(Calendar.DAY_OF_YEAR, daysToAlarm);
            // and return it
            return cal2.getTime();
        } catch (Exception e) {

        }
        // in case if we cannot calculate...
        return null;
    }
}

