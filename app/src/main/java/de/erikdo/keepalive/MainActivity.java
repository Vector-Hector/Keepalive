package de.erikdo.keepalive;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Button start, stop, checkNow;
    private TextView status;
    private TextView monitoredServers;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private boolean serviceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.buttonStart);
        stop = findViewById(R.id.buttonStop);
        checkNow = findViewById(R.id.checkNow);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        checkNow.setOnClickListener(this);

        status = findViewById(R.id.statusText);

        monitoredServers = findViewById(R.id.monitoredServers);

        refreshMonitoredServers();

        setupAlarm();

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshMonitoredServers();
    }

    private void refreshMonitoredServers(SettingsProcessor processor) {
        String state = processor.getServersDescriptionString();
        monitoredServers.setText(state);
    }

    private void refreshMonitoredServers() {
        refreshMonitoredServers(new SettingsProcessor(getApplicationContext()));
    }

    private void setupAlarm() {
        Context context = getApplicationContext();

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, de.erikdo.keepalive.BackgroundExecutor.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                alarmIntent);
        serviceRunning = true;
        updateText();
    }

    private void disableAlarm() {
        alarmMgr.cancel(alarmIntent);
        serviceRunning = false;
        updateText();
    }

    private void updateText() {
        status.setText("Status: " + (serviceRunning ? "running" : "stopped"));
    }


    @Override
    public void onClick(View v) {
        if (v.equals(start)) {
            setupAlarm();
        } else if (v.equals(stop)) {
            disableAlarm();
        } else if (v.equals(checkNow)) {
            BackgroundExecutor.asyncUpdateState(getApplicationContext());
        }
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settingsButton) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SettingsProcessor.LATEST_STATE_KEY)) {
            refreshMonitoredServers(new SettingsProcessor(sharedPreferences));
        }
    }
}
