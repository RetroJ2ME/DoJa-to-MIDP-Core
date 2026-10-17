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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/** 從建置產物載入選定的 image provider，讓遊戲專屬圖片格式保持可插拔。 */
public final class ImageRuntime {
    private static boolean initialized;
    private static ImageProvider provider;

    private ImageRuntime() {}

    public static ImageResource openEncoded(byte[] data) {
        ImageProvider value = provider();
        return value == null ? null : value.openEncoded(data);
    }

    public static ImageResource openScratchpad(String uri) {
        ImageProvider value = provider();
        return value == null ? null : value.openScratchpad(uri);
    }

    private static synchronized ImageProvider provider() {
        if (initialized) return provider;
        initialized = true;

        InputStream in = Resources.open("/assets/image-provider");
        if (in == null) return null;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            int value;
            while ((value = in.read()) >= 0) bytes.write(value);
            String className;
            try { className = new String(bytes.toByteArray(), "UTF-8").trim(); }
            catch (Exception ignored) { className = new String(bytes.toByteArray()).trim(); }
            if (className.length() == 0) return null;
            Object instance = Class.forName(className).newInstance();
            if (!(instance instanceof ImageProvider)) {
                throw new IllegalArgumentException(className + " is not an ImageProvider");
            }
            provider = (ImageProvider)instance;
            return provider;
        } catch (RuntimeException failure) {
            throw failure;
        } catch (Exception failure) {
            throw new IllegalStateException(failure.toString());
        } finally {
            try { in.close(); } catch (Exception ignored) {}
        }
    }
}
