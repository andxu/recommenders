/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 */
package org.eclipse.recommenders.internal.server.extdoc.proxy;

import javax.servlet.ServletException;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import com.google.inject.Inject;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class WebserviceActivator {

    private final String servicePath = "/extdoc";
    private final HttpService httpService;

    private final WebserviceResourceConfig app;

    @Inject
    public WebserviceActivator(final WebserviceResourceConfig app, final HttpService httpService) {
        this.app = app;
        this.httpService = httpService;
    }

    public void start() throws ServletException, NamespaceException {
        final ServletContainer servletContainer = new ServletContainer(app);
        httpService.registerServlet(servicePath, servletContainer, null, null);
    }

    public void stop() {
        try {
            httpService.unregister(servicePath);
        } catch (final Exception e) {
            // ignore that. Errors occur when the http server is currently going
            // down.
        }
    }
}