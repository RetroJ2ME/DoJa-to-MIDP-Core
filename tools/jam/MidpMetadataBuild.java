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

package doja.tools.jam;

import doja.tools.io.FileIO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** 從 JAM 與原版 JAR 提取資料，產生 MIDP 設定檔。 */
public final class MidpMetadataBuild {
    private MidpMetadataBuild() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 3) throw new IllegalArgumentException("Usage: MidpMetadataBuild <app.jam> <app.jar> <output.properties>");
        build(new File(args[0]), new File(args[1]), new File(args[2]));
    }

    public static void build(File jamFile, File jarFile, File output) throws IOException {
        Jam jam = Jam.read(jamFile);
        String appClass = jam.require("AppClass");
        String version = jam.require("AppVer");
        String icon = selectIcon(jarFile, jam.get("AppIcon"));

        FileIO.ensureParent(output);
        FileOutputStream out = new FileOutputStream(output);
        try {
            write(out, "AppClass", appClass);
            write(out, "midlet.version", version);
            write(out, "midlet.icon", icon.length() == 0 ? "" : "/" + icon);
        } finally {
            out.close();
        }
    }

    private static String selectIcon(File jarFile, String value) throws IOException {
        if (value == null || value.length() == 0) return "";
        ZipFile jar = new ZipFile(jarFile);
        try {
            String[] names = value.split(",", -1);
            for (int i = 0; i < names.length; i++) {
                String name = names[i].trim();
                if (name.length() == 0) continue;
                ZipEntry entry = jar.getEntry(name);
                if (entry != null && !entry.isDirectory()) return name;
            }
        } finally {
            jar.close();
        }
        throw new IOException("JAM AppIcon does not name a file in the game JAR: " + value);
    }

    private static void write(FileOutputStream out, String key, String value) throws IOException {
        if (value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) throw new IOException("invalid JAM metadata line for " + key);
        out.write((key + "=" + value + "\n").getBytes("ISO-8859-1"));
    }
}
