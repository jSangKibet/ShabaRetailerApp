package com.acework.shabaretailer.welcome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.transition.Scene;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;

import com.acework.shabaretailer.R;
import com.acework.shabaretailer.WelcomeActivity;
import com.acework.shabaretailer.atlas.SimplifiedTL;
import com.google.android.material.button.MaterialButton;

public class WelcomeWelcomeFragment extends Fragment {
    private ConstraintLayout root;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome_welcome_pre, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        root = view.findViewById(R.id.root);
        animate();
    }

    private void animate() {
        root.postDelayed(() -> {
            Scene next = Scene.getSceneForLayout(root, R.layout.fragment_welcome_welcome, requireContext());
            Transition set = TransitionInflater.from(requireContext()).inflateTransition(R.transition.welcome_welcome);
            set.addListener(SimplifiedTL.getListener(this::initialize));
            TransitionManager.go(next, set);
        }, 1000);
    }

    private void initialize() {
        MaterialButton s = requireView().findViewById(R.id.skip);
        MaterialButton n = requireView().findViewById(R.id.next);
        s.setOnClickListener(v -> ((WelcomeActivity) requireActivity()).toLogin());
        n.setOnClickListener(v -> ((WelcomeActivity) requireActivity()).toAboutUs());
    }
}
