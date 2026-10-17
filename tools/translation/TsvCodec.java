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

import doja.tools.io.FileIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/** 統一處理 UTF-8 TSV 回寫規則。 */
public final class TsvCodec {
    private TsvCodec() {}

    public static BufferedReader reader(File file) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    }

    public static BufferedWriter writer(File file) throws IOException {
        FileIO.ensureParent(file);
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
    }

    public static String stripBom(String text) {
        if (text != null && text.length() > 0 && text.charAt(0) == 0xFEFF) return text.substring(1);
        return text;
    }

    public static String escape(String text) {
        StringBuffer out = new StringBuffer(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\') out.append("\\\\");
            else if (c == '\t') out.append("\\t");
            else if (c == '\n') out.append("\\n");
            else if (c == '\r') out.append("\\r");
            else if (c < 0x20 || c == 0xFFFF) appendUnicodeEscape(out, c);
            else out.append(c);
        }
        return out.toString();
    }

    public static String unescape(String text, File file, int line) throws IOException {
        StringBuffer out = new StringBuffer(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c != '\\') {
                out.append(c);
                continue;
            }
            if (++i >= text.length()) throw error(file, line, "dangling escape");
            c = text.charAt(i);
            if (c == '\\') out.append('\\');
            else if (c == 't') out.append('\t');
            else if (c == 'n') out.append('\n');
            else if (c == 'r') out.append('\r');
            else if (c == 'u') {
                if (i + 4 >= text.length()) throw error(file, line, "short Unicode escape");
                int value = 0;
                for (int j = 1; j <= 4; j++) {
                    int digit = Character.digit(text.charAt(i + j), 16);
                    if (digit < 0) throw error(file, line, "invalid Unicode escape");
                    value = (value << 4) | digit;
                }
                out.append((char)value);
                i += 4;
            } else {
                throw error(file, line, "unsupported escape \\" + c);
            }
        }
        return out.toString();
    }

    public static String[] splitExact(String line, int count, File file, int number) throws IOException {
        String[] result = new String[count];
        int start = 0;
        for (int i = 0; i < count - 1; i++) {
            int tab = line.indexOf('\t', start);
            if (tab < 0) throw error(file, number, "not enough TSV columns");
            result[i] = line.substring(start, tab);
            start = tab + 1;
        }
        if (line.indexOf('\t', start) >= 0) throw error(file, number, "too many TSV columns");
        result[count - 1] = line.substring(start);
        return result;
    }

    private static IOException error(File file, int line, String message) {
        return new IOException(file + ":" + line + ": " + message);
    }

    private static void appendUnicodeEscape(StringBuffer out, char c) {
        out.append("\\u");
        String hex = Integer.toHexString(c);
        for (int i = hex.length(); i < 4; i++) out.append('0');
        out.append(hex);
    }
}
