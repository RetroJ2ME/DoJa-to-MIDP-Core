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

package doja.tools.font;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeSet;

/** 彙整各份清單中需要的字形與 Shift-JIS 編碼，供後續打包使用。 */
final class FontUsageCollector {
    static final String USAGE_FILE = "font-usage.bin";

    private FontUsageCollector() {}

    static Result collect(File[] roots) throws IOException {
        Result result = new Result();
        for (int cp = 0x20; cp <= 0x7E; cp++) result.glyphs.add(Integer.valueOf(cp));
        result.glyphs.add(Integer.valueOf(GlyphResolver.PLACEHOLDER_CODE_POINT));
        for (int i = 0; i < roots.length; i++) scan(result, roots[i]);
        if (result.manifests == 0) throw new IOException("no " + USAGE_FILE + " found in font source roots");
        return result;
    }

    private static void scan(Result result, File file) throws IOException {
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null) return;
            Arrays.sort(children, new Comparator<File>() {
                public int compare(File a, File b) { return a.getName().compareTo(b.getName()); }
            });
            for (int i = 0; i < children.length; i++) scan(result, children[i]);
            return;
        }
        if (!USAGE_FILE.equals(file.getName())) return;

        FontUsage usage = FontUsage.read(file);
        result.glyphs.addAll(usage.renderCodePoints());
        result.shiftJisCodes.addAll(usage.shiftJisCodes());
        result.manifests++;
    }

    static final class Result {
        final TreeSet<Integer> glyphs = new TreeSet<Integer>();
        final TreeSet<Integer> shiftJisCodes = new TreeSet<Integer>();
        int manifests;
    }
}
