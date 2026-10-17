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

package doja.tools.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** 遞迴列檔後固定排序，檔案系統順序不同也不會改變產物。 */
public final class FileTree {
    private FileTree() {}

    public static List<File> filesWithSuffix(File root, String suffix) {
        ArrayList<File> files = new ArrayList<File>();
        collect(root, suffix, files);
        Collections.sort(files, new Comparator<File>() {
            public int compare(File a, File b) {
                return a.getPath().compareTo(b.getPath());
            }
        });
        return files;
    }

    private static void collect(File file, String suffix, List<File> out) {
        if (file == null || !file.exists()) return;
        if (file.isFile()) {
            if (file.getName().endsWith(suffix)) out.add(file);
            return;
        }
        File[] children = file.listFiles();
        if (children == null) return;
        for (int i = 0; i < children.length; i++) collect(children[i], suffix, out);
    }
}
