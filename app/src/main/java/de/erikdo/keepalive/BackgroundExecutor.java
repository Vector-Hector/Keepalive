package de.erikdo.keepalive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackgroundExecutor extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        asyncUpdateState(context);
    }

    public static void asyncUpdateState(final Context context) {
        new Thread(){
            @Override
            public void run() {
                updateState(context);
            }
        }.start();
    }

    public static void updateState(Context context) {
        if (!isOnline("https://www.google.com")) { // otherwise, we may have no internet connection
            System.out.println("It seems, we don't have an internet connection.");
            return;
        }

        SettingsProcessor processor = new SettingsProcessor(context);
        String[] servers = processor.getServers();
        boolean[] isOnline = new boolean[servers.length];
        for (int i=0; i<servers.length; i++) {
            String server = servers[i];
            isOnline[i] = isOnline(server);

            if (!isOnline[i]) {
                Notify.showNotification(context, "Server offline!", "Your server " + server + " is offline");
                System.out.println("Server " + server + " is not online.");
            }
        }

        processor.saveState(servers, isOnline);
    }

    private static boolean isOnline(String url) {
        return isOnline(url, 0);
    }

    private static boolean isOnline(String url, int retry) {
        if (retry >= 2) {
            return false;
        }

        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setConnectTimeout(3000);
            con.getResponseCode();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return isOnline(url, retry + 1);
    }

}
