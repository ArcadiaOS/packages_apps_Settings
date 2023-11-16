/*
 * Copyright (C) 2024 the ArcadiaOS Android Project
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

package com.android.settings.preferences.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemProperties
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.DeviceInfoUtils
import com.android.settingslib.widget.LayoutPreference

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

class mtxInfoPreferenceController(context: Context) : AbstractPreferenceController(context) {

    private val defaultFallback = mContext.getString(R.string.device_info_default)

    private fun getPropertyOrDefault(propName: String): String {
        return SystemProperties.get(propName, defaultFallback)
    }

    private fun getDeviceName(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }

    private fun getArcadiaBuildVersion(): String {
        return getPropertyOrDefault(PROP_ARCADIA_BUILD_VERSION)
    }

    private fun getArcadiaChipset(): String {
        return getPropertyOrDefault(PROP_ARCADIA_CHIPSET)
    }

    private fun getArcadiaBattery(): String {
        return getPropertyOrDefault(PROP_ARCADIA_BATTERY)
    }
    
    private fun getArcadiaResolution(): String {
        return getPropertyOrDefault(PROP_ARCADIA_DISPLAY)
    }

    private fun getArcadiaSecurity(): String {
        return getPropertyOrDefault(PROP_ARCADIA_SECURITY)
    }

    private fun getArcadiaVersion(): String {
        return SystemProperties.get(PROP_ARCADIA_VERSION)
    }

    private fun getArcadiaReleaseType(): String {
        val releaseType = getPropertyOrDefault(PROP_ARCADIA_RELEASETYPE)
        return releaseType.substring(0, 1).uppercase() +
               releaseType.substring(1).lowercase()
    }

    private fun getArcadiaBuildStatus(releaseType: String): String {
        return mContext.getString(if (releaseType == "official") R.string.build_is_official_title else R.string.build_is_community_title)
    }

    private fun getArcadiaMaintainer(releaseType: String): String {
        val arcadiaMaintainer = getPropertyOrDefault(PROP_ARCADIA_MAINTAINER)
        if (arcadiaMaintainer.equals("Unknown", ignoreCase = true)) {
            return mContext.getString(R.string.unknown_maintainer)
        }
        return mContext.getString(R.string.maintainer_summary, arcadiaMaintainer)
    }

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)

        val releaseType = getPropertyOrDefault(PROP_ARCADIA_RELEASETYPE).lowercase()
        val arcadiaMaintainer = getArcadiaMaintainer(releaseType)
        val isOfficial = releaseType == "official"
        
        val hwInfoPreference = screen.findPreference<LayoutPreference>(KEY_HW_INFO)!!
        val sw2InfoPreference = screen.findPreference<LayoutPreference>(KEY_SW2_INFO)!!
        val deviceInfoPreference = screen.findPreference<LayoutPreference>(KEY_DEVICE_INFO)!!
        val aboutHwInfoView: View = hwInfoPreference.findViewById(R.id.about_device_hardware)
        val hwInfoView: View = hwInfoPreference.findViewById(R.id.device_hardware)
        val phoneImage: View = hwInfoPreference.findViewById(R.id.phone_image_container)
        val blurView: View = hwInfoPreference.findViewById(R.id.blurView)

        deviceInfoPreference.apply {
            findViewById<TextView>(R.id.firmware_version).text = "Arcadia" + " " + getArcadiaVersion()
            findViewById<TextView>(R.id.firmware_build_summary).text = arcadiaMaintainer
            findViewById<TextView>(R.id.build_variant_title).text = getArcadiaBuildStatus(releaseType)
        }

        hwInfoPreference.apply {
            findViewById<TextView>(R.id.device_name).text = getDeviceName()
            findViewById<TextView>(R.id.device_chipset).text = getArcadiaChipset()
            findViewById<TextView>(R.id.device_battery_capacity).text = getArcadiaBattery()
            findViewById<TextView>(R.id.device_resolution).text = getArcadiaResolution()
            findViewById<TextView>(R.id.device_name_model).text = getDeviceName()
        }

        aboutHwInfoView.setOnClickListener {
            if (hwInfoView.visibility == View.VISIBLE) {
                hwInfoView.visibility = View.GONE
                blurView.visibility = View.GONE
                phoneImage.visibility = View.VISIBLE
            } else {
                hwInfoView.visibility = View.VISIBLE
                blurView.visibility = View.VISIBLE
                phoneImage.visibility = View.GONE
            }
        }

        sw2InfoPreference.apply {
            findViewById<TextView>(R.id.security_patch_summary).text = getArcadiaSecurity()
            findViewById<TextView>(R.id.kernel_info_summary).text = DeviceInfoUtils.getFormattedKernelVersion(mContext)
        }

    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun getPreferenceKey(): String {
        return KEY_DEVICE_INFO
    }

    companion object {
        private const val KEY_HW_INFO = "my_device_hw_header"
        private const val KEY_SW2_INFO = "my_device_sw2_header"
        private const val KEY_DEVICE_INFO = "my_device_info_header"
        
        private const val KEY_CHIPSET = "device_chipset"
        private const val KEY_BATTERY = "device_battery_capacity"
        private const val KEY_DISPLAY = "device_resolution"

        private const val PROP_ARCADIA_VERSION = "ro.arcadia.modversion"
        private const val PROP_ARCADIA_RELEASETYPE = "ro.arcadia.release.type"
        private const val PROP_ARCADIA_MAINTAINER = "ro.arcadia.maintainer"
        private const val PROP_ARCADIA_DEVICE = "ro.arcadia.device"
        private const val PROP_ARCADIA_BUILD_TYPE = "ro.arcadia.build.variant"
        private const val PROP_ARCADIA_BUILD_VERSION = "ro.arcadia.version"
        private const val PROP_ARCADIA_CHIPSET = "ro.arcadia.chipset"
        private const val PROP_ARCADIA_BATTERY = "ro.arcadia.battery"
        private const val PROP_ARCADIA_DISPLAY = "ro.arcadia.display_resolution"
        private const val PROP_ARCADIA_SECURITY = "ro.build.version.security_patch"
    }
}
