package com.acework.shabaretailer.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.acework.shabaretailer.R;
import com.google.android.material.button.MaterialButton;

public class SignupTCDialog extends DialogFragment {
    public static String TAG = "sign_up_tc_dialog";
    private TCListener tcListener;

    public static SignupTCDialog newInstance(TCListener tcListener) {
        SignupTCDialog fragment = new SignupTCDialog();
        fragment.tcListener = tcListener;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_tc, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton accept = view.findViewById(R.id.accept);
        accept.setOnClickListener(v -> {
            tcListener.accept();
            dismiss();
        });
    }

    public interface TCListener {
        void accept();
    }
}
