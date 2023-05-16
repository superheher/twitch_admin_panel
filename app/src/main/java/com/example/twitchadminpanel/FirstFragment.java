package com.example.twitchadminpanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.twitchadminpanel.databinding.FragmentFirstBinding;
import com.example.twitchadminpanel.databinding.TwitchChannelPresetBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for (Preset preset : Preset.FromPrefs(getActivity())) {
            LayoutInflater inflater = LayoutInflater.from(binding.layoutFirst.getContext());
            TwitchChannelPresetBinding presetBinding = TwitchChannelPresetBinding.inflate(inflater, null, false);
            presetBinding.twitchChannelPresetTitle.setText(preset.title);
            presetBinding.twitchChannelPresetGame.setText(preset.gameName
                + MainActivity.SEPARATOR_GAME_ID
                + preset.gameId);
            presetBinding.twitchChannelPresetNumber.setText("#" + preset.uniqueNumber);
            presetBinding.twitchChannelPresetPart.setText(preset.partNumber + "");
            presetBinding.twitchChannelPresetEnglish.setChecked(preset.isEnglish);

            presetBinding.twitchChannelPresetPartPlus.setOnClickListener(v -> {
                int was = Integer.parseInt(presetBinding.twitchChannelPresetPart.getText().toString());
                Preset copy = preset;
                copy.partNumber = was + 1;
                Preset.EditInPrefs(getActivity(), copy);
                presetBinding.twitchChannelPresetPart.setText(copy.partNumber + "");
            });
            presetBinding.twitchChannelPresetPartMinus.setOnClickListener(v -> {
                int was = Integer.parseInt(presetBinding.twitchChannelPresetPart.getText().toString());
                Preset copy = preset;
                copy.partNumber = was - 1;
                Preset.EditInPrefs(getActivity(), copy);
                presetBinding.twitchChannelPresetPart.setText(copy.partNumber + "");
            });
            presetBinding.twitchChannelPresetEnglish.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Preset copy = preset;
                copy.isEnglish = isChecked;
                Preset.EditInPrefs(getActivity(), copy);
            });

            presetBinding.twitchChannelPresetDelete.setOnClickListener(view12 -> {
                ConfirmDeleteFragment.CURRENT_PRESET_TO_DELETE = preset;
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_ConfirmDeleteFragment);
            });

            presetBinding.twitchChannelPresetConfirm.setOnClickListener(view12 -> {
                requestUserId(preset);
            });

            binding.layoutFirst.addView(presetBinding.getRoot());
        }

        binding.buttonFirst.setOnClickListener(view1 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));
        binding.settings.setOnClickListener(view12 -> NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_CredsFragment));
    }

    public void requestUserId(Preset preset) {
        Creds creds = Creds.FromPrefs(getActivity());
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://api.twitch.tv/helix/users?login=" + creds.channelName;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < 1; i++) {
                    JSONObject oneObject = jsonArray.getJSONObject(i);

                    requestModifyChannel(preset, oneObject.getString("id"));
                }
            } catch (JSONException ignored) {
            }
        }, error -> {
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Creds creds = Creds.FromPrefs(getActivity());

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + creds.oauthToken);
                params.put("Client-Id", creds.clientId);

                return params;
            }
        };

        queue.add(stringRequest);
    }

    public void requestModifyChannel(Preset wasPreset, String broadcasterId) {
        try {
            Preset nowPreset = Preset.Deserialize(
                    getActivity().getPreferences(Context.MODE_PRIVATE).getString(
                            "preset" + wasPreset.uniqueNumber,
                            ""));
            if (nowPreset.gameId.isEmpty()) {
                return;
            }
            final String title = (nowPreset.partNumber > 0)
                    ? nowPreset.title + (" Part " + nowPreset.partNumber + ".")
                    : nowPreset.title;

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = "https://api.twitch.tv/helix/channels?broadcaster_id=" + broadcasterId;
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("game_id", nowPreset.gameId);
            jsonBody.put("title", title);
            jsonBody.put("broadcaster_language", nowPreset.isEnglish ? "en" : "ru");
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.PATCH, URL, response -> {
                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
            }, error -> {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        if (response.statusCode == 204) {
                            responseString = "Channel modified successfully.";
                        } else {
                            responseString = String.valueOf(response.statusCode);
                        }
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }

                @Override
                public Map<String, String> getHeaders() {
                    Creds creds = Creds.FromPrefs(getActivity());

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + creds.oauthToken);
                    params.put("Client-Id", creds.clientId);

                    return params;
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}