package com.acework.shabaretailer.atlas;

import androidx.annotation.NonNull;
import androidx.transition.Transition;

public class SimplifiedTL {
    public static Transition.TransitionListener getListener(Runnable r) {
        return new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionEnd(@NonNull Transition transition) {
                r.run();
            }

            @Override
            public void onTransitionCancel(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionPause(@NonNull Transition transition) {

            }

            @Override
            public void onTransitionResume(@NonNull Transition transition) {

            }
        };
    }
}
