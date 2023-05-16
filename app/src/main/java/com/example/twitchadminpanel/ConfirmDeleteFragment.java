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

import com.example.twitchadminpanel.databinding.FragmentConfirmDeletePresetBinding;
import com.example.twitchadminpanel.databinding.FragmentCredsBinding;
import com.example.twitchadminpanel.databinding.FragmentFirstBinding;
import com.example.twitchadminpanel.databinding.TwitchChannelPresetBinding;

public class ConfirmDeleteFragment extends Fragment {

    public static Preset CURRENT_PRESET_TO_DELETE = null;

    private FragmentConfirmDeletePresetBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentConfirmDeletePresetBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CURRENT_PRESET_TO_DELETE != null) {
                    Preset.DeleteFromPrefs(getActivity(), CURRENT_PRESET_TO_DELETE);
                    CURRENT_PRESET_TO_DELETE = null;
                }
                NavHostFragment.findNavController(ConfirmDeleteFragment.this)
                        .navigate(R.id.action_ConfirmDeleteFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}