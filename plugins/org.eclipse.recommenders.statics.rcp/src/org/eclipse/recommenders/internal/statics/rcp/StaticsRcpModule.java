/**
 * Copyright (c) 2017 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.statics.rcp;

import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.recommenders.statics.IStaticsModelProvider;
import org.eclipse.ui.IWorkbench;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;

public class StaticsRcpModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IStaticsModelProvider.class).to(RcpStaticsModelProvider.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    public StaticsRcpPreferences provide(IWorkbench wb) {
        IEclipseContext context = (IEclipseContext) wb.getService(IEclipseContext.class);
        return ContextInjectionFactory.make(StaticsRcpPreferences.class, context);
    }
}
