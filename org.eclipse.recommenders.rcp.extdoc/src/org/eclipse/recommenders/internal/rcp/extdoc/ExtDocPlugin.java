/**
 * Copyright (c) 2011 Stefan Henss.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Henss - initial API and implementation.
 */
package org.eclipse.recommenders.internal.rcp.extdoc;

import java.net.URL;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public final class ExtDocPlugin extends AbstractUIPlugin {

    private static ExtDocPlugin plugin;

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    public static ExtDocPlugin getDefault() {
        return plugin;
    }

    public static URL getBundleEntry(final String uri) {
        return plugin.getBundle().getEntry(uri);
    }

    public static Image getImage(final String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(plugin.getBundle().getSymbolicName(), "icons/full/" + path)
                .createImage();
    }

}
