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
package org.eclipse.recommenders.commons.utils.names;

/**
 * A {@link IPackageName} is simply a special formatted string that expresses a
 * package path.
 */
public interface IPackageName extends IName {
    // offers basically not more than a plain getIdentifier...

    // REVIEW: add to API
    // boolean isDefaultPackage()
}
