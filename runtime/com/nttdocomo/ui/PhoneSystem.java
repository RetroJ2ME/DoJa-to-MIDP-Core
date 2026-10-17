/*
 * Copyright (C) 2026 Magstic
 * SPDX-License-Identifier: GPL-3.0-only
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.nttdocomo.ui;

/** DoJa 手機原生資源控制。 */
public final class PhoneSystem {
    public static final int DEV_BACKLIGHT = 0;
    public static final int DEV_VIBRATOR = 1;

    public static final int ATTR_BACKLIGHT_OFF = 0;
    public static final int ATTR_BACKLIGHT_ON = 1;
    public static final int ATTR_VIBRATOR_OFF = 0;
    public static final int ATTR_VIBRATOR_ON = 1;

    /* DoJa 是持續 ON/OFF；MIDP 用短脈衝，因此在畫面 present 時續振。 */
    private static final int VIBRATION_PULSE_MS = 100;
    private static boolean vibratorOn;

    private PhoneSystem() {
    }

    public static void setAttribute(int attribute, int value) {
        if (attribute == DEV_VIBRATOR) {
            if (value != ATTR_VIBRATOR_OFF && value != ATTR_VIBRATOR_ON) {
                throw new IllegalArgumentException("invalid vibrator attribute: " + value);
            }
            vibratorOn = value == ATTR_VIBRATOR_ON;
            Display.__midpVibrate(vibratorOn ? VIBRATION_PULSE_MS : 0);
            return;
        }

        if (attribute == DEV_BACKLIGHT) {
            if (value != ATTR_BACKLIGHT_OFF && value != ATTR_BACKLIGHT_ON) {
                throw new IllegalArgumentException("invalid backlight attribute: " + value);
            }
        }
    }

    static void __midpRefreshVibration() {
        if (vibratorOn) {
            Display.__midpVibrate(VIBRATION_PULSE_MS);
        }
    }

    public static int getAttribute(int attribute) {
        if (attribute != DEV_VIBRATOR || !isAvailable(attribute)) return -1;
        return vibratorOn ? ATTR_VIBRATOR_ON : ATTR_VIBRATOR_OFF;
    }

    public static boolean isAvailable(int attribute) {
        return attribute == DEV_VIBRATOR && (vibratorOn || Display.__midpVibrate(0));
    }
}
