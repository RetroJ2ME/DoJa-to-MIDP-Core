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

package doja;

import java.io.InputStream;

/**
 * 從 MIDlet 所在的 JAR 裡讀取打包進去的資產。
 * 使用一個實例當 anchor，而非直接使用 class literal，
 * 因為真機的 CLDC/J2ME 環境不接受 class-literal 這種 bytecode。
 */
public final class Resources {
    private static final Object ANCHOR = new Resources();

    private Resources() {}

    public static InputStream open(String path) {
        return ANCHOR.getClass().getResourceAsStream(path);
    }
}
