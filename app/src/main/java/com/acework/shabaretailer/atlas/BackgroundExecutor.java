package com.acework.shabaretailer.atlas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundExecutor {
    public static final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

    public static void execute(Runnable r) {
        backgroundExecutor.execute(r);
    }
}
