package com.knobtviker.thermopile.shared;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.knobtviker.thermopile.shared.constants.Action;
import com.knobtviker.thermopile.shared.constants.Uid;

import timber.log.Timber;

public class MessageFactory {

    public static void sendToForeground(@Nullable final Messenger messenger, @NonNull final Message message) {
        try {
            if (messenger != null) {
                messenger.send(message);
            }
        } catch (RemoteException e) {
            Timber.e(e);
        }
    }

    public static void registerToBackground(@NonNull final Messenger foregroundMessenger, @NonNull final Messenger backgroundMessenger) {
        try {
            final Message message = Message.obtain(null, Action.REGISTER);
            message.replyTo = foregroundMessenger;
            backgroundMessenger.send(message);
        } catch (RemoteException e) {
            Timber.e(e);
        }
    }

    public static void currentFromBackground(@NonNull final Messenger foregroundMessenger, @NonNull final Messenger backgroundMessenger) {
        try {
            final Message message = Message.obtain(null, Action.CURRENT);
            message.replyTo = foregroundMessenger;
            backgroundMessenger.send(message);
        } catch (RemoteException e) {
            Timber.e(e);
        }
    }

    public static void resetToBackground(@NonNull final Messenger foregroundMessenger, @NonNull final Messenger backgroundMessenger) {
        try {
            final Message message = Message.obtain(null, Action.RESET);
            message.replyTo = foregroundMessenger;
            backgroundMessenger.send(message);
        } catch (RemoteException e) {
            Timber.e(e);
        }
    }

    public static Message drivers(@NonNull final Bundle data) {
        return create(Uid.DRIVERS, data);
    }

    public static Message fram(@NonNull final Bundle data) {
        return create(Uid.FRAM, data);
    }

    private static Message create(@Uid final int what, @NonNull final Bundle data) {
        final Message message = Message.obtain(null, what);

        message.setData(data);

        return message;
    }
}
