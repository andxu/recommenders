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
package org.eclipse.recommenders.rcp.extdoc.features;

import java.util.Collection;

import org.eclipse.recommenders.commons.utils.names.IName;

public interface IUserFeedback {

    IRatingSummary getRatingSummary();

    Collection<? extends IComment> getComments();

    void addRating(IRating rating);

    void addComment(IComment comment);

    IName getElement();

    String getRevision();

}
