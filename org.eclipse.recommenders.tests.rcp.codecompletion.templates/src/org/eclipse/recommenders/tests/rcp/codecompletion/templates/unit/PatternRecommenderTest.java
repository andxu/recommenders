/**
 * Copyright (c) 2010 Stefan Henss.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Henss - initial API and implementation.
 */
package org.eclipse.recommenders.tests.rcp.codecompletion.templates.unit;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import junit.framework.Assert;

import org.eclipse.recommenders.commons.utils.Tuple;
import org.eclipse.recommenders.commons.utils.names.IMethodName;
import org.eclipse.recommenders.commons.utils.names.ITypeName;
import org.eclipse.recommenders.internal.rcp.codecompletion.calls.CallsModelStore;
import org.eclipse.recommenders.internal.rcp.codecompletion.calls.net.ObjectMethodCallsNet;
import org.eclipse.recommenders.internal.rcp.codecompletion.calls.net.PatternNode;
import org.eclipse.recommenders.internal.rcp.codecompletion.templates.PatternRecommender;
import org.eclipse.recommenders.internal.rcp.codecompletion.templates.types.CompletionTargetVariable;
import org.eclipse.recommenders.internal.rcp.codecompletion.templates.types.PatternRecommendation;
import org.eclipse.recommenders.rcp.codecompletion.IVariableUsageResolver;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Provider;

public final class PatternRecommenderTest {

    @Test
    public void testComputeRecommendations() {
        final CompletionTargetVariable targetVariable = UnitTestSuite.getMockedTargetVariable(
                "Button butto = new Button();\nbutto.", "butto", "Lorg/eclipse/swt/widgets/Button", false);
        final PatternRecommender recommender = getPatternRecommenderMock(targetVariable.getType());

        final Set<PatternRecommendation> recommendations = recommender.computeRecommendations(targetVariable);

        Assert.assertEquals(1, recommendations.size());
        for (final PatternRecommendation recommendation : recommendations) {
            Assert.assertEquals("Pattern 1", recommendation.getName());
            Assert.assertEquals(50, recommendation.getProbability());
            Assert.assertEquals(2, recommendation.getMethods().size());
        }
    }

    protected static PatternRecommender getPatternRecommenderMock(final ITypeName receiverType) {
        final CallsModelStore store = Mockito.mock(CallsModelStore.class);
        final ObjectMethodCallsNet net = getCallsNetMock(receiverType);
        Mockito.when(Boolean.valueOf(store.hasModel(receiverType))).thenReturn(Boolean.TRUE);
        Mockito.when(store.getModel(receiverType)).thenReturn(net);

        return new PatternRecommender(store, new Provider<Set<IVariableUsageResolver>>() {
            @Override
            public Set<IVariableUsageResolver> get() {
                return Collections.emptySet();
            }
        });
    }

    private static ObjectMethodCallsNet getCallsNetMock(final ITypeName receiverType) {
        final ObjectMethodCallsNet model = Mockito.mock(ObjectMethodCallsNet.class);

        final PatternNode node = Mockito.mock(PatternNode.class);
        final List<Tuple<String, Double>> patterns = Lists.newArrayList();
        patterns.add(Tuple.create("Pattern 1", 0.5));
        Mockito.when(node.getPatternsWithProbability()).thenReturn(patterns);

        Mockito.when(model.getType()).thenReturn(receiverType);
        Mockito.when(model.getPatternsNode()).thenReturn(node);
        Mockito.when(model.getPatternsWithProbability()).thenReturn(patterns);
        Mockito.when(model.getRecommendedMethodCalls(Matchers.anyDouble())).thenReturn(getRecommendedMethods());
        return model;
    }

    private static SortedSet<Tuple<IMethodName, Double>> getRecommendedMethods() {
        final SortedSet<Tuple<IMethodName, Double>> methodsSet = Sets
                .newTreeSet(new Comparator<Tuple<IMethodName, Double>>() {
                    @Override
                    public int compare(final Tuple<IMethodName, Double> arg0, final Tuple<IMethodName, Double> arg1) {
                        return arg0.getFirst().compareTo(arg1.getFirst());
                    }
                });
        methodsSet.add(Tuple.create(UnitTestSuite.getDefaultConstructorCall().getInvokedMethod(), 0.5));
        methodsSet.add(Tuple.create(UnitTestSuite.getDefaultMethodCall().getInvokedMethod(), 0.5));
        return methodsSet;
    }
}
