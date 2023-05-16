package com.example.twitchadminpanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.twitchadminpanel.databinding.FragmentSecondBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.save.setOnClickListener(view1 -> {
            {
                Preset preset = new Preset();
                preset.title = String.valueOf(binding.titleEditor.getText());
                preset.gameName = String.valueOf(binding.gameEditor.getText());
                preset.gameId = String.valueOf(binding.gameId.getText());
                Preset.AddToPrefs(getActivity(), preset);
            }

            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        });

        binding.gameSearchResult.setOnItemClickListener((parent, view12, position, id) -> {
            String item = binding.gameSearchResult.getItemAtPosition(position).toString();
            String[] data = item.split(MainActivity.SEPARATOR_GAME_ID);
            binding.gameEditor.setText(data[0]);
            binding.gameId.setText(data[1]);
        });
        binding.search.setOnClickListener(view13 -> {
            RequestQueue queue = Volley.newRequestQueue(getContext());
            String url = "https://api.twitch.tv/helix/search/categories?query=" + binding.gameEditor.getText();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
                ArrayList<String> strings = new ArrayList<String>();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject oneObject = jsonArray.getJSONObject(i);

                        strings.add(oneObject.getString("name")
                                + MainActivity.SEPARATOR_GAME_ID
                                + oneObject.getString("id"));
                    }
                } catch (JSONException ignored) {
                }
                adapter = new ArrayAdapter<String>(getContext(), R.layout.simple_list_item_1, strings);
                binding.gameSearchResult.setAdapter(adapter);
                adapter.notifyDataSetChanged();
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
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}