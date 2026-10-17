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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** 定位到已輸出檔案的翻譯文字。 */
public final class ResolvedTextTable {
    private static final String HEADER = "kind\tfile\tlocation\ttext";

    private ResolvedTextTable() {}

    public static void write(File file, List<ResolvedText> entries) throws IOException {
        BufferedWriter writer = TsvCodec.writer(file);
        try {
            writer.write(HEADER);
            writer.write('\n');
            for (int i = 0; i < entries.size(); i++) {
                ResolvedText entry = entries.get(i);
                writer.write(entry.kind);
                writer.write('\t');
                writer.write(entry.file);
                writer.write('\t');
                writer.write(String.valueOf(entry.location));
                writer.write('\t');
                writer.write(TsvCodec.escape(entry.text));
                writer.write('\n');
            }
        } finally {
            writer.close();
        }
    }

    public static List<ResolvedText> read(File file) throws IOException {
        ArrayList<ResolvedText> result = new ArrayList<ResolvedText>();
        BufferedReader reader = TsvCodec.reader(file);
        try {
            String line = TsvCodec.stripBom(reader.readLine());
            if (!HEADER.equals(line)) throw new IOException(file + ": bad resolved header");
            int number = 1;
            while ((line = reader.readLine()) != null) {
                number++;
                if (line.length() == 0) continue;
                String[] columns = TsvCodec.splitExact(line, 4, file, number);
                int location;
                try {
                    location = Integer.parseInt(columns[2]);
                } catch (NumberFormatException ex) {
                    throw new IOException(file + ":" + number + ": invalid location");
                }
                result.add(new ResolvedText(columns[0], columns[1], location,
                        TsvCodec.unescape(columns[3], file, number)));
            }
        } finally {
            reader.close();
        }
        return result;
    }
}
