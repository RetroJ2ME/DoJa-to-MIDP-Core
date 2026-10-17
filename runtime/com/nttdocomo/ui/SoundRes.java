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

package com.nttdocomo.ui;

final class SoundRes implements MediaSound {
    private final String resourcePath;
    private final SoundData data;

    SoundRes(String resourcePath, String ignoredContentType) {
        this.resourcePath = resourcePath;
        this.data = SoundCatalog.find(resourcePath);
    }

    public void use() {
        // 在需要 Player 時再創建。
    }

    String getResourcePath() { return resourcePath; }
    String getContentType() {
        return getContentType(0);
    }
    String getContentType(int segment) {
        String path = data.path(segment);
        return path != null && path.endsWith(".wav")
                ? "audio/x-wav" : "audio/midi";
    }
    int getDurationMillis() { return data.durationMillis(); }
    int getSegmentCount() { return data.segmentCount(); }
    String getSegmentPath(int segment) { return data.path(segment); }
    boolean hasNativeLoop() { return data.hasNativeLoop(); }
    boolean playerCanLoopSegment(int segment) { return data.playerCanLoopSegment(segment); }
    SoundData.Position positionAt(long timelineMillis) { return data.positionAt(timelineMillis); }
    long millisUntilSegmentBoundary(long timelineMillis) {
        return data.millisUntilSegmentBoundary(timelineMillis);
    }
}
