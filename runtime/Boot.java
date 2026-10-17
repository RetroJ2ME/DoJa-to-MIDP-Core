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

import javax.microedition.midlet.MIDlet;
import com.nttdocomo.ui.IApplication;
import doja.ApplicationDescriptor;

public final class Boot implements Runnable {
    private final MIDlet midlet;

    public Boot(MIDlet midlet) { this.midlet = midlet; }

    public void run() {
        try {
            IApplication.setMidlet(midlet);
            ApplicationDescriptor descriptor = ApplicationDescriptor.get();
            IApplication app = (IApplication)Class.forName(descriptor.getAppClass()).newInstance();
            IApplication.setCurrentApp(app);
            com.nttdocomo.io.ConnectorProxy.preflightScratchpad();
            app.start();
        } catch (Throwable failure) {
            failure.printStackTrace();
        }
    }
}
