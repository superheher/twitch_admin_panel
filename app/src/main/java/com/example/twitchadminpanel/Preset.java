package com.example.twitchadminpanel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

public class Preset {

    public static Preset Deserialize(String string) {
        String[] presetList = string.split(MainActivity.SEPARATOR_IN_PRESET);
        Preset result = new Preset();
        if (presetList.length >= 5) {
            result.title = presetList[0];
            result.gameName = presetList[1];
            result.gameId = presetList[2];
            result.partNumber = Integer.parseInt(presetList[3]);
            result.isEnglish = (presetList[4].equals("en"));
        }
        return result;
    }

    public static String Serialize(Preset preset) {
        return preset.title
            + MainActivity.SEPARATOR_IN_PRESET
            + preset.gameName
            + MainActivity.SEPARATOR_IN_PRESET
            + preset.gameId
            + MainActivity.SEPARATOR_IN_PRESET
            + preset.partNumber
            + MainActivity.SEPARATOR_IN_PRESET
            + (preset.isEnglish ? "en" : "ru");
    }

    public static ArrayList<Preset> FromPrefs(FragmentActivity context) {
        ArrayList<Preset> list = new ArrayList<Preset>();
        SharedPreferences pref = context.getPreferences(Context.MODE_PRIVATE);
        int counter = 0;
        while (true) {
            String preset = pref.getString("preset" + counter, "");
            if (preset.isEmpty()) {
                break;
            } else {
                Preset result = Deserialize(preset);
                result.uniqueNumber = counter;
                list.add(result);
            }
            counter++;
        }
        return list;
    }
    public static void AddToPrefs(FragmentActivity context, Preset preset) {
        SharedPreferences pref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        int counter = 0;
        while (true) {
            if (pref.getString("preset" + counter, "").isEmpty()) {
                break;
            }
            counter++;
        }

        editor.putString("preset" + counter, Serialize(preset));
        editor.apply();
    }
    public static void EditInPrefs(FragmentActivity context, Preset preset) {
        SharedPreferences pref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("preset" + preset.uniqueNumber, Serialize(preset));
        editor.apply();
    }
    public static void DeleteFromPrefs(FragmentActivity context, Preset preset) {
        SharedPreferences pref = context.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.remove("preset" + preset.uniqueNumber);
        int counter = preset.uniqueNumber + 1;
        while (true) {
            String string = pref.getString("preset" + counter, "");
            if (string.isEmpty()) {
                break;
            } else {
                editor.remove("preset" + counter);
                editor.putString("preset" + (counter - 1), string);
            }
            counter++;
        }

        editor.apply();
    }

    public int uniqueNumber = 0; // Is not in prefs.
    public String title;
    public String gameName;
    public String gameId;
    public int partNumber = 0;
    public boolean isEnglish = true;
}
