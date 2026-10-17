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

package doja.tools.classfile;

import doja.tools.io.FileTree;

import java.io.File;
import java.io.IOException;
import java.util.List;

/** 重導 class 樹內符合條件的 Methodref，呼叫點本身不用逐個改 bytecode。 */
public final class MethodRefPatch {
    private MethodRefPatch() {}

    public static int redirect(File root, String fromOwner, String fromName, String descriptor,
            String toOwner, String toName) throws IOException {
        if (fromOwner.equals(toOwner) && fromName.equals(toName)) return 0;
        List<File> files = FileTree.filesWithSuffix(root, ".class");
        int total = 0;
        for (int f = 0; f < files.size(); f++) {
            File file = files.get(f);
            ClassFile cls = ClassFile.read(file);
            int changed = 0;
            while (true) {
                int match = cls.findMethodRef(fromOwner, fromName, descriptor);
                if (match == 0) break;
                cls.retargetRef(match, toOwner, toName, descriptor);
                changed++;
            }
            if (changed != 0) cls.write(file);
            total += changed;
        }
        return total;
    }
}
