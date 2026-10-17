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

public class UIException extends RuntimeException {
    public static final int UNDEFINED = 0;
    public static final int ILLEGAL_STATE = 1;
    public static final int NO_RESOURCES = 2;
    public static final int BUSY_RESOURCE = 3;
    public static final int UNSUPPORTED_FORMAT = 4;

    protected static final int STATUS_FIRST = 0;
    protected static final int STATUS_LAST = 63;

    private final int status;

    public UIException() {
        this(UNDEFINED, null);
    }

    public UIException(int status) {
        this(status, null);
    }

    public UIException(int status, String message) {
        super(message);
        this.status = status;
    }


    public int getStatus() {
        return status;
    }
}
