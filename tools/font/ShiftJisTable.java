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

import doja.tools.io.FileIO;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/** 建立需要的 Shift-JIS 解碼對照表，並補齊解碼後所需的字形。 */
final class ShiftJisTable {
    private ShiftJisTable() {}

    static int write(Set<Integer> codes, File output, Set<Integer> renderGlyphs) throws IOException {
        if (codes == null) throw new NullPointerException("codes");
        if (renderGlyphs == null) throw new NullPointerException("renderGlyphs");
        if (codes.size() > 65535) throw new IOException("Shift-JIS mapping count overflow: " + codes.size());

        Charset charset = charset();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(6 + codes.size() * 4);
        DataOutputStream out = new DataOutputStream(bytes);
        out.writeBytes("SMP1");
        out.writeShort(codes.size());
        for (Iterator<Integer> it = codes.iterator(); it.hasNext();) {
            int code = it.next().intValue();
            int value = decode(charset, code);
            if (value < 0) {
                out.close();
                throw new IOException("cannot decode required Shift-JIS code 0x" + hex4(code));
            }
            out.writeShort(code);
            out.writeShort(value);
            renderGlyphs.add(Integer.valueOf(value));
        }
        out.close();
        FileIO.write(output, bytes.toByteArray());
        return codes.size();
    }

    static Charset charset() {
        String[] names = { "windows-31j", "MS932", "Shift_JIS" };
        for (int i = 0; i < names.length; i++) {
            try { return Charset.forName(names[i]); }
            catch (Throwable ignored) {}
        }
        throw new IllegalStateException("No Shift-JIS compatible charset available in build JRE");
    }

    private static int decode(Charset charset, int code) {
        byte[] pair = { (byte)(code >>> 8), (byte)code };
        String text;
        try { text = new String(pair, charset); }
        catch (Throwable ignored) { return -1; }
        if (text.length() != 1) return -1;
        char value = text.charAt(0);
        return value == '\uFFFD' || value == 0 ? -1 : value;
    }

    private static String hex4(int value) {
        String text = Integer.toHexString(value & 0xFFFF).toUpperCase();
        while (text.length() < 4) text = "0" + text;
        return text;
    }
}
