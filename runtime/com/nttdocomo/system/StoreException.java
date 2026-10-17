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

package com.nttdocomo.system;

public class StoreException extends Exception {
    public static final int UNDEFINED = 0;
    public static final int STORE_FULL = 1;
    public static final int NOT_FOUND = 2;

    private final int status;

    public StoreException() {
        this(UNDEFINED, null);
    }

    public StoreException(int status) {
        this(status, null);
    }

    public StoreException(int status, String message) {
        super(message);
        this.status = status;
    }


    public int getStatus() {
        return status;
    }
}
