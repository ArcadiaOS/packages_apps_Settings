/*
 * Copyright (C) 2023 the RisingOS Android Project
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

import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.hardware.display.DisplayManager
import android.view.Display
import com.android.internal.os.PowerProfile
import com.android.internal.util.MemInfoReader
import kotlin.math.ceil
import kotlin.math.roundToInt

object DeviceInfoUtil {

    fun getTotalRam(): String {
        val memInfoReader = MemInfoReader()
        memInfoReader.readMemInfo()
        val totalMemoryBytes = memInfoReader.totalSize
        val totalMemoryGB = totalMemoryBytes / (1024.0 * 1024.0 * 1024.0)
        val roundedMemoryGB = ceil(totalMemoryGB).toInt()

        val aproxRam = when {
            roundedMemoryGB in 1..2 -> "2"
            roundedMemoryGB <= 3 -> "3"
            roundedMemoryGB <= 4 -> "4"
            roundedMemoryGB <= 6 -> "6"
            roundedMemoryGB <= 8 -> "8"
            roundedMemoryGB <= 12 -> "12"
        else -> "12+"
        }
        return "$aproxRam GB"
    }

    fun getStorageTotal(context: Context): String {
        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalStorageBytes = statFs.totalBytes
        val totalStorageGB = totalStorageBytes / (1024.0 * 1024.0 * 1024.0)
        val roundedStorageGB = roundToNearestKnownStorageSize(totalStorageGB)
        return if (roundedStorageGB >= 1024) {
            "${roundedStorageGB / 1024} TB"
        } else {
            "$roundedStorageGB GB"
        }
    }

    private fun roundToNearestKnownStorageSize(storageGB: Double): Int {
        val knownSizes = arrayOf(16, 32, 64, 128, 256, 512, 1024)
        if (storageGB <= 8) return ceil(storageGB).toInt()
        for (size in knownSizes) {
            if (storageGB <= size) return size
        }
        return ceil(storageGB).toInt()
    }
}
