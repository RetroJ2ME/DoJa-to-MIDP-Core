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

package doja.tools.translation;

/** 字串在建置來源中的位置。 */
public final class TextOccurrence {
    public final String kind;
    public final String file;
    public final int location;
    public final String source;

    public TextOccurrence(String kind, String file, int location, String source) {
        if (kind == null || file == null || source == null) throw new NullPointerException();
        this.kind = kind;
        this.file = file;
        this.location = location;
        this.source = source;
    }
}
