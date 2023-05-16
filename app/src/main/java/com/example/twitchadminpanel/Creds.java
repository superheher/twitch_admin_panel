package com.example.twitchadminpanel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentActivity;

public class Creds {

    public static Creds FromPrefs(FragmentActivity context) {
        String creds = context.getPreferences(Context.MODE_PRIVATE).getString("creds", "");
        String[] credsList = creds.split(MainActivity.SEPARATOR_IN_CREDS);
        Creds result = new Creds();
        if (credsList.length > 2) {
            result.channelName = credsList[0];
            result.oauthToken = credsList[1];
            result.clientId = credsList[2];
        }
        return result;
    }
    public static void ToPrefs(FragmentActivity context, Creds creds) {
        SharedPreferences pref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String result = creds.channelName
            + MainActivity.SEPARATOR_IN_CREDS
            + creds.oauthToken
            + MainActivity.SEPARATOR_IN_CREDS
            + creds.clientId;
        editor.putString("creds", result);
        editor.apply();
    }

    public String channelName;
    public String oauthToken;
    public String clientId;
}
