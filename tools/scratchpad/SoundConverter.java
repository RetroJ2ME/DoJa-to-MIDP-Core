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

package doja.tools.scratchpad;

import doja.tools.io.FileIO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import mld.api.MldConversion;
import mld.api.MldConverter;
import mld.api.MldMidiPlayback;
import mld.api.MldPcm16;

/** 將 MLD payload 轉成 MIDP 較容易播放的 MIDI/WAV，並順手算好實際長度。 */
public final class SoundConverter {
    public static final class Result {
        public final String[] suffixes;
        public final int[] durationsMillis;
        public final int loopSegmentIndex;

        Result(String[] suffixes, int[] durationsMillis, int loopSegmentIndex) {
            this.suffixes = suffixes;
            this.durationsMillis = durationsMillis;
            this.loopSegmentIndex = loopSegmentIndex;
        }

        public String primarySuffix() {
            return suffixes[0];
        }

        public SoundIndex.Entry indexEntry(String resourceStem) {
            String[] resources = new String[suffixes.length];
            for (int i = 0; i < resources.length; i++) resources[i] = resourceStem + suffixes[i];
            return new SoundIndex.Entry(resources[0], resources, durationsMillis, loopSegmentIndex);
        }
    }

    private SoundConverter() {}

    public static Result convert(byte[] bytes, File stem, boolean forceWav) throws Exception {
        MldConversion conversion = MldConverter.convert(bytes);
        boolean midi = conversion.hasMidi();
        boolean sampled = conversion.hasRenderableSampledAudio();
        if (midi == sampled) throw new IOException("MLD must resolve to exactly one MIDP sound representation");

        long micros;
        if (midi) {
            if (forceWav) {
                Sequence sequence = conversion.createMidiSequence();
                int millis = MidiToWav.render(sequence, new File(stem.getPath() + ".wav"));
                return single(".wav", millis);
            }
            MldMidiPlayback playback = conversion.createMidiPlayback();
            String[] suffixes = new String[playback.getSegmentCount()];
            int[] durations = new int[suffixes.length];
            for (int i = 0; i < suffixes.length; i++) {
                suffixes[i] = segmentSuffix(i, playback.getLoopSegmentIndex(), suffixes.length);
                Sequence sequence = playback.createSegmentSequence(i);
                File output = new File(stem.getPath() + suffixes[i]);
                FileIO.ensureParent(output);
                if (MidiSystem.write(sequence, 1, output) <= 0) {
                    throw new IOException("no MIDI writer for " + output);
                }
                durations[i] = millis(sequence.getMicrosecondLength());
            }
            return new Result(suffixes, durations, playback.getLoopSegmentIndex());
        }

        MldPcm16 pcm = conversion.renderSampledPcm16();
        File output = new File(stem.getPath() + ".wav");
        writeWav(pcm, output);
        micros = pcm.getFrameCount() * 1000000L / pcm.getSampleRate();
        return single(".wav", millis(micros));
    }

    private static Result single(String suffix, int durationMillis) {
        return new Result(new String[] {suffix}, new int[] {durationMillis}, -1);
    }

    private static String segmentSuffix(int index, int loopIndex, int count) {
        if (index == 0) return ".mid";
        if (count == 2 && index == loopIndex) return ".loop.mid";
        return ".part" + index + ".mid";
    }

    private static int millis(long micros) {
        return (int)Math.max(1L, Math.min(Integer.MAX_VALUE, (micros + 999L) / 1000L));
    }

    private static void writeWav(MldPcm16 pcm, File output) throws Exception {
        int sampleRate = pcm.getSampleRate();
        int channels = pcm.getChannels();
        int frames = pcm.getFrameCount();
        short[] samples = pcm.copyInterleavedSamples();
        if (sampleRate <= 0 || channels <= 0 || frames < 0 || samples.length != frames * channels) {
            throw new IOException("invalid PCM returned by MLD player");
        }
        byte[] bytes = new byte[samples.length * 2];
        for (int i = 0, p = 0; i < samples.length; i++) {
            int value = samples[i];
            bytes[p++] = (byte)value;
            bytes[p++] = (byte)(value >>> 8);
        }
        FileIO.ensureParent(output);
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                sampleRate, 16, channels, channels * 2, sampleRate, false);
        AudioInputStream stream = new AudioInputStream(new ByteArrayInputStream(bytes), format, frames);
        try {
            if (AudioSystem.write(stream, AudioFileFormat.Type.WAVE, output) <= 0) throw new IOException("no WAV writer for " + output);
        } finally {
            stream.close();
        }
    }
}
