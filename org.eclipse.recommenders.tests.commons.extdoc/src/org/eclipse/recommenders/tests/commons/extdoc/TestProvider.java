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
package org.eclipse.recommenders.tests.commons.extdoc;

import org.eclipse.recommenders.commons.selection.IJavaElementSelection;
import org.eclipse.recommenders.commons.selection.JavaElementLocation;
import org.eclipse.recommenders.rcp.extdoc.AbstractProviderComposite;
import org.eclipse.recommenders.rcp.extdoc.ProviderUiJob;
import org.eclipse.recommenders.rcp.extdoc.SwtFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public final class TestProvider extends AbstractProviderComposite {

    private Label text;
    private Composite composite;

    @Override
    public String getProviderName() {
        return "TestProvider";
    }

    @Override
    public boolean isAvailableForLocation(final JavaElementLocation location) {
        return true;
    }

    @Override
    public boolean selectionChanged(final IJavaElementSelection context) {
        new ProviderUiJob() {
            @Override
            public Composite run() {
                text.setText(context.toString());
                return composite;
            }
        }.schedule();
        return true;
    }

    @Override
    protected Composite createContentComposite(final Composite parent) {
        composite = parent;
        text = SwtFactory.createLabel(parent, "");
        return parent;
    }

}
