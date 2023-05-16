package com.example.twitchadminpanel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.twitchadminpanel.databinding.FragmentFirstBinding;
import com.example.twitchadminpanel.databinding.TwitchChannelPresetBinding;

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

        String presets = getActivity().getPreferences(Context.MODE_PRIVATE).getString("presets", "");
        String[] presetsList = presets.split(MainActivity.SEPARATOR_IN_PRESET);

        for (String title : presetsList) {
            if (title.isEmpty()) {
                continue;
            }
            LayoutInflater inflater = LayoutInflater.from(binding.layoutFirst.getContext());
            TwitchChannelPresetBinding presetBinding = TwitchChannelPresetBinding.inflate(inflater, null, false);
            presetBinding.twitchChannelPresetTitle.setText(title);
            binding.layoutFirst.addView(presetBinding.getRoot());
        }

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
        binding.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_CredsFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}