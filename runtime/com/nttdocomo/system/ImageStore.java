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

package com.nttdocomo.system;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import com.nttdocomo.ui.MediaImage;
import com.nttdocomo.ui.MediaManager;

/**
 * ImageStore 負責記錄圖片來源，於需要時才重新開啟串流，
 * 避免相同的圖片編碼資料在記憶體中常駐並重複佔用雙份空間。
 */
public final class ImageStore {
    private static final Vector entries = new Vector();

    private final MediaImage source;

    private ImageStore(MediaImage image) {
        source = image;
    }

    public static synchronized int addEntry(MediaImage image) {
        if (image == null) return -1;
        InputStream input = null;
        try {
            // 先嘗試開啟串流驗證後再進行登記。雖然之後需要時會重新讀取來源，但可確保錯誤能在 addImage() 當下被即時捕捉。
            input = MediaManager.openImageData(image);
            entries.addElement(new ImageStore(image));
            return entries.size() - 1;
        } catch (IOException failure) {
            return -1;
        } finally {
            if (input != null) {
                try { input.close(); } catch (IOException ignored) {}
            }
        }
    }

    public static synchronized ImageStore getEntry(int id) throws StoreException {
        if (id < 0 || id >= entries.size()) throw new StoreException(StoreException.NOT_FOUND, "unknown image entry " + id);
        return (ImageStore)entries.elementAt(id);
    }

    public InputStream getInputStream() {
        try {
            return MediaManager.openImageData(source);
        } catch (IOException failure) {
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}
