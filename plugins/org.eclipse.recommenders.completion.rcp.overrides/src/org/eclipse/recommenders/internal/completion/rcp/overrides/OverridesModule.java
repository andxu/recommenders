/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.overrides;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.FrameworkUtil;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class OverridesModule extends AbstractModule {
    @Override
    protected void configure() {
        bindCompletionEngine();
    }

    private void bindCompletionEngine() {
        bind(IOverridesModelLoader.class).to(OverridesModelLoader.class).in(Scopes.SINGLETON);
        bind(URL.class).annotatedWith(Names.named("overrides.model.fileUrl")).toInstance(getOverridesModelFileUrl());

        bind(OverridesModelStore.class).in(Scopes.SINGLETON);
        bind(OverridesCompletionProposalComputer.class).in(Scopes.SINGLETON); //
        bind(InstantOverridesRecommender.class).in(Scopes.SINGLETON); //

    }

    private URL getOverridesModelFileUrl() {
        final Path basedir = new Path("/data/models.zip");
        try {
            return FileLocator.resolve(FileLocator.find(FrameworkUtil.getBundle(getClass()), basedir, null));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}