/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.extdoc.server.wiring;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.recommenders.internal.extdoc.server.ExtdocResource;
import org.eclipse.recommenders.server.GuiceInjectableProvider;
import org.eclipse.recommenders.webclient.GsonProvider;

import com.google.inject.Inject;
import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.core.DefaultResourceConfig;

public class WebserviceResourceConfig extends DefaultResourceConfig {

    private final GuiceInjectableProvider guiceProvider;

    @Inject
    public WebserviceResourceConfig(final GuiceInjectableProvider guiceProvider) {
        this.guiceProvider = guiceProvider;
    }

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> result = new HashSet<Class<?>>();
        result.add(ExtdocResource.class);
        return result;
    }

    @Override
    public Set<Object> getSingletons() {
        final HashSet<Object> result = new HashSet<Object>();
        result.add(guiceProvider);
        result.add(new GsonProvider());
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List getContainerRequestFilters() {
        final List filters = super.getContainerRequestFilters();
        filters.add(new GZIPContentEncodingFilter());
        return filters;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List getContainerResponseFilters() {
        final List filters = super.getContainerResponseFilters();
        filters.add(new GZIPContentEncodingFilter());
        return filters;
    }
}
