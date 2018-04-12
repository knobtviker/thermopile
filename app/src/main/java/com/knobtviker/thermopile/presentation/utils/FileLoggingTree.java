package com.knobtviker.thermopile.presentation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.knobtviker.thermopile.R;

import org.joda.time.DateTimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import timber.log.Timber;

//TODO: This needs to be finished and made as JSON logger.
public class FileLoggingTree extends Timber.DebugTree {

    private static final String TAG = FileLoggingTree.class.getSimpleName();

    private Context context;

    public FileLoggingTree(Context context) {
        this.context = context;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("LogNotTimber")
    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        super.log(priority, tag, message, t);
        try {

            final File directory = new File(String.format("%s/logs", context.getFilesDir()));

            if (!directory.exists()) {
                directory.mkdir();
            }

            final String filename = String.format("%s.json", context.getString(R.string.app_name));

            final File file = new File(String.format("%s/logs%s%s", context.getFilesDir(), File.separator, filename));

            file.createNewFile();

            if (file.exists()) {

                final OutputStream fileOutputStream = new FileOutputStream(file, true);

                fileOutputStream.write(("<p style=\"background:lightgray;\"><strong style=\"background:lightblue;\">&nbsp&nbsp" + DateTimeUtils.currentTimeMillis() + " :&nbsp&nbsp</strong>&nbsp&nbsp" + message + "</p>").getBytes());
                fileOutputStream.close();

            }
        } catch (Exception e) {
            Log.e(TAG, "Error while logging into file : " + e);
        }

    }
}
