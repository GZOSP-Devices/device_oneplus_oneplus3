/*
 * Copyright (C) 2014 SlimRoms Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.slim.device;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.slim.device.KernelControl;
import com.slim.device.settings.ScreenOffGesture;
import com.slim.device.settings.SliderSettings;
import com.slim.device.util.FileUtils;

import java.io.File;


public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                enableComponent(context, ScreenOffGesture.class.getName());
                SharedPreferences screenOffGestureSharedPreferences = context.getSharedPreferences(
                        ScreenOffGesture.GESTURE_SETTINGS, Activity.MODE_PRIVATE);
                KernelControl.enableGestures(
                        screenOffGestureSharedPreferences.getBoolean(
                        ScreenOffGesture.PREF_GESTURE_ENABLE, true));
            }

            // Disable slider settings if needed
            if (!KernelControl.hasSlider()) {
                disableComponent(context, SliderSettings.class.getName());
            } else {
                enableComponent(context, SliderSettings.class.getName());

                String sliderTop = getPreferenceString(context, "keycode_top_position", "601");
                String sliderMiddle = getPreferenceString(context, "keycode_middle_position", "602");
                String sliderBottom = getPreferenceString(context, "keycode_bottom_position", "603");

                FileUtils.writeLine(KernelControl.KEYCODE_SLIDER_TOP, sliderTop);
                FileUtils.writeLine(KernelControl.KEYCODE_SLIDER_MIDDLE, sliderMiddle);
                FileUtils.writeLine(KernelControl.KEYCODE_SLIDER_BOTTOM, sliderBottom);
            }
    }

    private String getPreferenceString(Context context, String key, String defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, defaultValue);
    }

    private boolean getPreferenceBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, defaultValue);
    }

    private void disableComponent(Context context, String component) {
        ComponentName name = new ComponentName(context, component);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(name,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void enableComponent(Context context, String component) {
        ComponentName name = new ComponentName(context, component);
        PackageManager pm = context.getPackageManager();
        if (pm.getComponentEnabledSetting(name)
                == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            pm.setComponentEnabledSetting(name,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
