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
package org.eclipse.recommenders.internal.extdoc.server;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.recommenders.extdoc.ClassOverrideDirectives;
import org.eclipse.recommenders.extdoc.ClassOverridePatterns;
import org.eclipse.recommenders.extdoc.ClassSelfcallDirectives;
import org.eclipse.recommenders.extdoc.MethodSelfcallDirectives;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.sun.jersey.api.NotFoundException;

// TODO: refactor paths to constants and declare them in extdoc commons plugin.
@Path("/")
public class ExtdocResource {

    @Inject
    private CouchDataAccess dataAccess;

    @Produces({ APPLICATION_JSON })
    @Consumes({ APPLICATION_JSON })
    @Path("/class-overrides")
    @POST
    public ClassOverrideDirectives getClassOverrideDirectives(final ITypeName type) {
        final Optional<ClassOverrideDirectives> opt = dataAccess.getClassOverrideDirectives(type);
        if (!opt.isPresent()) {
            throw new NotFoundException("no overrides directives found for " + type);
        }
        return opt.get();
    }

    @Produces({ APPLICATION_JSON })
    @Consumes({ APPLICATION_JSON })
    @Path("/class-selfcalls")
    @POST
    public ClassSelfcallDirectives getClassSelfcallDirectives(final ITypeName type) {
        final Optional<ClassSelfcallDirectives> opt = dataAccess.getClassSelfcallDirectives(type);
        if (!opt.isPresent()) {
            throw new NotFoundException("no selfcall directives found for " + type);
        }
        return opt.get();
    }

    @Produces({ APPLICATION_JSON })
    @Consumes({ APPLICATION_JSON })
    @Path("/method-selfcalls")
    @POST
    public MethodSelfcallDirectives getMethodSelfcallDirectives(final IMethodName method) {
        final Optional<MethodSelfcallDirectives> opt = dataAccess.getMethodSelfcallDirectives(method);
        if (!opt.isPresent()) {
            throw new NotFoundException("no selfcall directives found for " + method);
        }
        return opt.get();
    }

    @Produces({ APPLICATION_JSON })
    @Consumes({ APPLICATION_JSON })
    @Path("/class-overrides-patterns")
    @POST
    public ClassOverridePatterns getClassOverridePatterns(final ITypeName type) {
        final Optional<ClassOverridePatterns> opt = dataAccess.getClassOverridePatterns(type);
        if (!opt.isPresent()) {
            throw new NotFoundException("no subclassing pattern found for " + type);
        }
        return opt.get();
    }
}
