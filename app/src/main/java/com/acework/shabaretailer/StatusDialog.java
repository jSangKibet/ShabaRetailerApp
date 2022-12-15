package com.acework.shabaretailer;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;

public class StatusDialog extends DialogFragment {
    public static final String TAG = "status_dialog";
    private LottieAnimationView animation;
    private TextView message;
    private MaterialButton okay;
    private int animationRes;
    private String messageText;
    private boolean dismissible;
    private NotificationDialogDismissListener dismissListener;

    public StatusDialog() {
    }

    public static StatusDialog newInstance(int animationRes, String messageText, boolean dismissible, NotificationDialogDismissListener dismissListener) {
        StatusDialog dialog = new StatusDialog();
        dialog.animationRes = animationRes;
        dialog.messageText = messageText;
        dialog.dismissListener = dismissListener;
        dialog.dismissible = dismissible;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.WindowAnimationUpDown;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_status, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        setValues();
        setListeners();
        setCancelable(false);
        animation.playAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDisplayMetrics().widthPixels;
        WindowManager.LayoutParams lp = requireDialog().getWindow().getAttributes();
        lp.width = width;
        lp.gravity = Gravity.BOTTOM;
        requireDialog().getWindow().setAttributes(lp);
        requireDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void bindViews(View view) {
        animation = view.findViewById(R.id.animation);
        message = view.findViewById(R.id.message);
        okay = view.findViewById(R.id.okay);
        if (dismissible) okay.setVisibility(View.VISIBLE);
    }

    private void setValues() {
        animation.setAnimation(animationRes);
        message.setText(messageText);
    }

    private void setListeners() {
        okay.setOnClickListener(v -> {
            dismiss();
            dismissListener.dialogDismissed();
        });
    }

    public interface NotificationDialogDismissListener {
        void dialogDismissed();
    }
}
