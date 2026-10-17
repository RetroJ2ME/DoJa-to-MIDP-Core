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

package com.nttdocomo.io;

import java.io.IOException;
import java.io.InputStream;

public interface HttpConnection extends javax.microedition.io.ContentConnection {
    String GET = "GET";
    String POST = "POST";

    void setRequestMethod(String method) throws IOException;
    void setRequestProperty(String key, String value) throws IOException;
    void connect() throws IOException;
    int getResponseCode() throws IOException;
    InputStream openInputStream() throws IOException;
    void close() throws IOException;
}
