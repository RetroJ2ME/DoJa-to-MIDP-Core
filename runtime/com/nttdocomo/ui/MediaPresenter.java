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

public class MediaPresenter {
    protected MediaListener listener;

    public void setAttribute(int attr, int value) {}

    public void setMediaListener(MediaListener listener) {
        this.listener = listener;
    }

    protected void fireMediaAction(int type, int option) {
        MediaListener current = listener;
        if (current != null) {
            current.mediaAction(this, type, option);
        }
    }
}

