/*
 * Copyright (C) 2016 The CyanogenMod Project
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

package com.slim.device.settings;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

import com.slim.device.KernelControl;
import com.slim.device.R;
import com.slim.device.util.FileUtils;

public class SliderSettings extends PreferenceActivity
        implements OnPreferenceChangeListener {

    private ListPreference mSliderTop;
    private ListPreference mSliderMiddle;
    private ListPreference mSliderBottom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.slider_panel);

        mSliderTop = (ListPreference) findPreference("keycode_top_position");
        mSliderTop.setOnPreferenceChangeListener(this);

        mSliderMiddle = (ListPreference) findPreference("keycode_middle_position");
        mSliderMiddle.setOnPreferenceChangeListener(this);

        mSliderBottom = (ListPreference) findPreference("keycode_bottom_position");
        mSliderBottom.setOnPreferenceChangeListener(this);
    }

    private void setSummary(ListPreference preference, String file) {
        String keyCode;
        if ((keyCode = FileUtils.readOneLine(file)) != null) {
            preference.setValue(keyCode);
            preference.setSummary(preference.getEntry());
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String file;
        if (preference == mSliderTop) {
            file = KernelControl.KEYCODE_SLIDER_TOP;
        } else if (preference == mSliderMiddle) {
            file = KernelControl.KEYCODE_SLIDER_MIDDLE;
        } else if (preference == mSliderBottom) {
            file = KernelControl.KEYCODE_SLIDER_BOTTOM;
        } else {
            return false;
        }

        FileUtils.writeLine(file, (String) newValue);
        setSummary((ListPreference) preference, file);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Remove padding around the listview
            getListView().setPadding(0, 0, 0, 0);

        setSummary(mSliderTop, KernelControl.KEYCODE_SLIDER_TOP);
        setSummary(mSliderMiddle, KernelControl.KEYCODE_SLIDER_MIDDLE);
        setSummary(mSliderBottom, KernelControl.KEYCODE_SLIDER_BOTTOM);
    }
}
