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

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/** 用固定規則尋找並載入字型，避免檔案列舉順序影響建置結果。 */
final class FontFiles {
    private FontFiles() {}

    static File find(File input) {
        if (input == null) return null;
        if (input.isFile()) return isFont(input) ? input : null;
        if (!input.isDirectory()) return null;

        File[] files = input.listFiles();
        if (files == null) return null;
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File a, File b) { return a.getName().compareTo(b.getName()); }
        });
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && isFont(files[i])) return files[i];
        }
        return null;
    }

    static Font load(File file) throws IOException, FontFormatException {
        try {
            return load(file, Font.TRUETYPE_FONT);
        } catch (FontFormatException notTrueType) {
            return load(file, Font.TYPE1_FONT);
        }
    }

    private static Font load(File file, int type) throws IOException, FontFormatException {
        FileInputStream in = new FileInputStream(file);
        try { return Font.createFont(type, in); }
        finally { in.close(); }
    }

    private static boolean isFont(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".ttf") || name.endsWith(".otf");
    }
}
