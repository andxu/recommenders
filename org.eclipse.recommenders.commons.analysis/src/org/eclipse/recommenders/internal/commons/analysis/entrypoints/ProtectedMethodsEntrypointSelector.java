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
package org.eclipse.recommenders.internal.commons.analysis.entrypoints;

import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;

/**
 * This class is stateful, thus, should be used only once.
 */
public class ProtectedMethodsEntrypointSelector implements IEntrypointSelector {
    final LinkedList<Entrypoint> entrypoints = new LinkedList<Entrypoint>();

    @Override
    public List<Entrypoint> selectEntrypoints(final IClass clazz) {
        for (final IMethod method : clazz.getDeclaredMethods()) {
            if (method.isProtected() && hasImplementation(method) && isInstanceMethod(method)) {
                createAndRegisterEntrypoint(method);
            }
        }
        return entrypoints;
    }

    private boolean isInstanceMethod(final IMethod method) {
        return !method.isStatic();
    }

    private boolean hasImplementation(final IMethod method) {
        return !method.isAbstract();
    }

    private void createAndRegisterEntrypoint(final IMethod method) {
        final DefaultEntrypoint newEntrypoint = new DefaultEntrypoint(method, method.getClassHierarchy());
        entrypoints.add(newEntrypoint);
    }
}
