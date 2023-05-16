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

import com.example.twitchadminpanel.databinding.FragmentCredsBinding;
import com.example.twitchadminpanel.databinding.FragmentFirstBinding;
import com.example.twitchadminpanel.databinding.TwitchChannelPresetBinding;

public class CredsFragment extends Fragment {

    private FragmentCredsBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentCredsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            Creds creds = Creds.FromPrefs(getActivity());
            binding.channelName.setText(creds.channelName);
            binding.oauthToken.setText(creds.oauthToken);
            binding.clientId.setText(creds.clientId);
        }

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    Creds creds = new Creds();
                    creds.channelName = String.valueOf(binding.channelName.getText());
                    creds.oauthToken = String.valueOf(binding.oauthToken.getText());
                    creds.clientId = String.valueOf(binding.clientId.getText());
                    Creds.ToPrefs(getActivity(), creds);
                }

                NavHostFragment.findNavController(CredsFragment.this)
                        .navigate(R.id.action_CredsFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}