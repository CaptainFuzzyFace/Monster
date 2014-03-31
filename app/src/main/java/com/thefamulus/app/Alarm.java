package com.thefamulus.app;

import android.content.Context;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.widget.LinearLayout;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by martin on 29/03/14.
 */
public class Alarm extends Block {

    private static final int msecMinute = 1000 * 60;
    private static final int msecHour = msecMinute * 60;
    private static final int msecDay = msecHour * 24;

    public Alarm(LinearLayout container, LinearLayout myView) {
        // Constructor

    }

    AlarmTimer mAlarm;

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

    public class AlarmTimer extends CountDownTimer {
        // Count down timer to use as the alarm
        public AlarmTimer(long delay, long ticker) {
            // Constructed
            super(delay, ticker);
        }

        @Override
        public void onFinish() {
            // What to do when we hit zero
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

}
