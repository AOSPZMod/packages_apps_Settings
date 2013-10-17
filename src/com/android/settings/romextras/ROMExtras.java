/*
 * Copyright (C) 2013 The AOSPZ Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.romextras;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;


public class ROMExtras extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String EXTRA_CRT = "extra_crt";
    private static final String EXTRA_BATTERY_TEXT = "extra_battery_text";
    private static final String MEDIA_SCANNER_ON_BOOT = "media_scanner_on_boot";
    private static final String EXTRA_STATUSBAR_COLOR = "extra_statusbar_color";

    private ColorPickerPreference mStatusBarColor;
    private CheckBoxPreference mToggleCrt;
    private CheckBoxPreference mToggleBatteryText;
    private ListPreference mMSOB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.romextras_settings);
        initializeAllPreferences();
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (mToggleCrt == preference) {
            Settings.System.putInt(getContentResolver(), Settings.System.SYSTEM_POWER_ENABLE_CRT_OFF, mToggleCrt.isChecked() ? 1 : 0);
            return true;
        } else if (mToggleBatteryText == preference) {
            Settings.System.putInt(getContentResolver(), Settings.System.STATUSBAR_BATTERY_ICON, mToggleBatteryText.isChecked() ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mMSOB) {
            writeMSOBOptions(newValue);
            return true;
        } else if (preference == mStatusBarColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_COLOR, intHex);
            return true;
        }

        return true;
    }

    private void initializeAllPreferences() {
        mToggleCrt = (CheckBoxPreference) findPreference(EXTRA_CRT);
        mToggleCrt.setChecked((Settings.System.getInt(getContentResolver(), Settings.System.SYSTEM_POWER_ENABLE_CRT_OFF, 0) == 1));

        mToggleBatteryText = (CheckBoxPreference) findPreference(EXTRA_BATTERY_TEXT);
        mToggleBatteryText.setChecked((Settings.System.getInt(getContentResolver(), Settings.System.STATUSBAR_BATTERY_ICON, 1) == 1));

        mMSOB = (ListPreference) findPreference(MEDIA_SCANNER_ON_BOOT);
        updateMSOBOptions();
        mMSOB.setOnPreferenceChangeListener(this);

        mStatusBarColor = (ColorPickerPreference) findPreference(EXTRA_STATUSBAR_COLOR);
        mStatusBarColor.setOnPreferenceChangeListener(this);
    }


    private void resetMSOBOptions() {
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.MEDIA_SCANNER_ON_BOOT, 0);
    }

    private void writeMSOBOptions(Object newValue) {
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.MEDIA_SCANNER_ON_BOOT,
                Integer.valueOf((String) newValue));
        updateMSOBOptions();
    }

    private void updateMSOBOptions() {
        int value = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.MEDIA_SCANNER_ON_BOOT, 0);
        mMSOB.setValue(String.valueOf(value));
        mMSOB.setSummary(mMSOB.getEntry());
    }


}
