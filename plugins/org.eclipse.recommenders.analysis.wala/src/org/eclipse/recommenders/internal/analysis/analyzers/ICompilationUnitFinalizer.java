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
package org.eclipse.recommenders.internal.analysis.analyzers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.recommenders.internal.analysis.codeelements.CompilationUnit;

import com.ibm.wala.classLoader.IClass;

public interface ICompilationUnitFinalizer {
    void finalizeClass(CompilationUnit compilationUnit, IClass exampleClass, IProgressMonitor monitor);
}