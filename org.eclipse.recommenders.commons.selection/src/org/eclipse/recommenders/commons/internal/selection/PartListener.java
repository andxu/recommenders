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
package org.eclipse.recommenders.commons.internal.selection;

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaSourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Listens for newly created workbench parts and append a listener if it is a
 * Java editor.
 */
@SuppressWarnings("restriction")
final class PartListener implements IPartListener {

    private static InternalSelectionListener selectionListener;

    /**
     * @param cursorListener
     *            The {@link ViewerListener} to register with Java editors.
     */
    public PartListener(final InternalSelectionListener selectionListener) {
        PartListener.selectionListener = selectionListener;
    }

    /**
     * @param workbenchPart
     *            Workbench part to append the listener to in case it is a Java
     *            editor.
     */
    protected void addViewerListener(final IWorkbenchPart workbenchPart) {
        if (workbenchPart instanceof JavaEditor) {
            final JavaEditor editor = (JavaEditor) workbenchPart;
            final ViewerListener listener = new ViewerListener(workbenchPart);
            ((JavaSourceViewer) editor.getViewer()).addPostSelectionChangedListener(listener);
        }
    }

    @Override
    public void partActivated(final IWorkbenchPart part) {
        // Not of interest for us.
    }

    @Override
    public void partBroughtToTop(final IWorkbenchPart part) {
        // Not of interest for us.
    }

    @Override
    public void partClosed(final IWorkbenchPart part) {
        // Not of interest for us.
    }

    @Override
    public void partDeactivated(final IWorkbenchPart part) {
        // Not of interest for us.
    }

    @Override
    public void partOpened(final IWorkbenchPart part) {
        addViewerListener(part);
    }

    private final static class ViewerListener implements ISelectionChangedListener {

        private final IWorkbenchPart part;

        public ViewerListener(final IWorkbenchPart part) {
            this.part = part;
        }

        @Override
        public void selectionChanged(final SelectionChangedEvent event) {
            selectionListener.update(part, event.getSelection());
        }

    }

}
