/*
 * SPDX-FileCopyrightText: 2026 Alch3myOS
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.updater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.lineageos.updater.misc.Constants;
import org.lineageos.updater.misc.Utils;

/**
 * Lets the setup wizard choose how often updates are checked for, as the Updater's own
 * preferences dialog does. Only callers holding WRITE_SECURE_SETTINGS can send it.
 */
public class SetupPreferencesReceiver extends BroadcastReceiver {

    private static final String TAG = "SetupPreferencesReceiver";

    public static final String ACTION_SET_AUTO_UPDATES_CHECK_INTERVAL =
            "org.lineageos.updater.action.SET_AUTO_UPDATES_CHECK_INTERVAL";
    public static final String EXTRA_INTERVAL = "interval";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ACTION_SET_AUTO_UPDATES_CHECK_INTERVAL.equals(intent.getAction())) {
            return;
        }
        int interval = intent.getIntExtra(EXTRA_INTERVAL, -1);
        if (interval < Constants.AUTO_UPDATES_CHECK_INTERVAL_NEVER
                || interval > Constants.AUTO_UPDATES_CHECK_INTERVAL_MONTHLY) {
            Log.w(TAG, "Ignoring interval " + interval);
            return;
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(Constants.PREF_AUTO_UPDATES_CHECK_INTERVAL, interval)
                .apply();
        if (Utils.isUpdateCheckEnabled(context)) {
            UpdatesCheckReceiver.scheduleRepeatingUpdatesCheck(context);
        } else {
            UpdatesCheckReceiver.cancelRepeatingUpdatesCheck(context);
            UpdatesCheckReceiver.cancelUpdatesCheck(context);
        }
    }
}
