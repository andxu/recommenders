/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.tests.completion.rcp.calls;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.TreeSet;

import org.eclipse.jdt.core.IType;
import org.eclipse.recommenders.internal.completion.rcp.calls.net.IObjectMethodCallsNet;
import org.eclipse.recommenders.internal.rcp.models.store.DefaultModelArchiveStore;
import org.eclipse.recommenders.utils.Tuple;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.VmMethodName;
import org.mockito.Mockito;

import com.google.common.base.Optional;

public class ModelStoreMock extends DefaultModelArchiveStore<IType, IObjectMethodCallsNet> {

    public ModelStoreMock() {
        super(new File("fail.txt"), null, null, null);
    }

    @Override
    public Optional<IObjectMethodCallsNet> aquireModel(final IType type) {
        final IObjectMethodCallsNet net = Mockito.mock(IObjectMethodCallsNet.class);
        when(net.getRecommendedMethodCalls(anyDouble())).thenReturn(new TreeSet<Tuple<IMethodName, Double>>() {
            {
                add(Tuple.newTuple((IMethodName) VmMethodName.get("Ljava/lang/Object.hashCode()I"), 0.8d));
            }
        });
        return Optional.of(net);
    }

    @Override
    public void releaseModel(final IObjectMethodCallsNet model) {
    }
}