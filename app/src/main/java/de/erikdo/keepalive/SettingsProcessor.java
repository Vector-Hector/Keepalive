package de.erikdo.keepalive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.preference.PreferenceManager;

import java.net.MalformedURLException;
import java.net.URL;

public class SettingsProcessor {
    public static final String LATEST_STATE_KEY = "monitored_servers_latest_state";
    public static final String PREFERENCE_KEY = "monitored_servers";

    private final SharedPreferences shared;

    public SettingsProcessor(Context context) {
        this(PreferenceManager.getDefaultSharedPreferences(context));
    }

    public SettingsProcessor(SharedPreferences shared) {
        this.shared = shared;
    }


    private String getProcessedServersValue() {
        return (String) shared.getAll().get(LATEST_STATE_KEY);
    }

    private String getRawServersValue() {
        return filterValue(shared.getString(PREFERENCE_KEY, "http://www.google.com/"));
    }


    public String getServersDescriptionString() {
        String latestState = getProcessedServersValue();
        if (latestState.equals("")) {
            return getRawServersValue();
        }
        return latestState;
    }

    public String[] getServers() {
        return getRawServersValue().split("\n");
    }

    public void saveState(String[] names, boolean[] isOnline) {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<names.length; i++) {
            builder.append(names[i]).append(" is ").append(isOnline[i] ? "online" : "offline").append("\n");
        }

        SharedPreferences.Editor editor = shared.edit();
        editor.putString(LATEST_STATE_KEY, builder.toString());
        editor.apply();
    }

    public String filterValue(String input) {
        String[] urls = input.split("\n");
        StringBuilder builder = new StringBuilder();

        for (String s : urls) {
            String url = s.trim();
            if (!url.equals("")) {
                if (!url.startsWith("http")) {
                    url = "http://" + url;
                }

                try {
                    URL parsed = new URL(url);
                    builder.append(parsed.toString());
                    builder.append("\n");
                } catch (MalformedURLException ignored) {
                }
            }
        }

        return builder.toString();
    }

    public void printAllSettings() {
        System.out.println("Settings:\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            shared.getAll().forEach((s, o) -> {
                System.out.println(s + " : " + o);
            });
        }
    }

}
