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
package org.eclipse.recommenders.internal.rcp.providers;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation.FIELD_DECLARATION;
import static org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation.FIELD_DECLARATION_INITIALIZER;
import static org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation.METHOD_BODY;
import static org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation.METHOD_DECLARATION;
import static org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation.METHOD_DECLARATION_PARAMETER;
import static org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation.METHOD_DECLARATION_RETURN;
import static org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation.METHOD_DECLARATION_THROWS;
import static org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation.TYPE_DECLARATION;
import static org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation.TYPE_DECLARATION_EXTENDS;
import static org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation.TYPE_DECLARATION_IMPLEMENTS;
import static org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation.UNKNOWN;
import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;
import static org.eclipse.recommenders.utils.rcp.JdtUtils.findTypeRoot;
import static org.eclipse.recommenders.utils.rcp.JdtUtils.log;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.recommenders.rcp.events.JavaSelectionEvent.JavaSelectionLocation;
import org.eclipse.ui.IEditorPart;

import com.google.common.base.Optional;

/**
 * Utility class that resolves a selected java element from editor selection or structured selection.
 */
@SuppressWarnings("restriction")
public class JavaSelectionUtils {

    @SuppressWarnings("serial")
    private static Map<StructuralPropertyDescriptor, JavaSelectionLocation> MAPPING = new HashMap<StructuralPropertyDescriptor, JavaSelectionLocation>() {
        {
            put(CompilationUnit.IMPORTS_PROPERTY, TYPE_DECLARATION);
            put(CompilationUnit.PACKAGE_PROPERTY, TYPE_DECLARATION);
            put(CompilationUnit.TYPES_PROPERTY, TYPE_DECLARATION);

            put(TypeDeclaration.BODY_DECLARATIONS_PROPERTY, TYPE_DECLARATION);
            put(TypeDeclaration.INTERFACE_PROPERTY, TYPE_DECLARATION);
            put(TypeDeclaration.JAVADOC_PROPERTY, TYPE_DECLARATION);
            put(TypeDeclaration.MODIFIERS2_PROPERTY, TYPE_DECLARATION);
            put(TypeDeclaration.NAME_PROPERTY, TYPE_DECLARATION);
            put(TypeDeclaration.SUPERCLASS_TYPE_PROPERTY, TYPE_DECLARATION_EXTENDS);
            put(TypeDeclaration.SUPER_INTERFACE_TYPES_PROPERTY, TYPE_DECLARATION_IMPLEMENTS);
            put(TypeDeclaration.TYPE_PARAMETERS_PROPERTY, UNKNOWN);

            put(MethodDeclaration.BODY_PROPERTY, METHOD_BODY);
            put(MethodDeclaration.CONSTRUCTOR_PROPERTY, METHOD_DECLARATION);
            put(MethodDeclaration.JAVADOC_PROPERTY, METHOD_DECLARATION);
            put(MethodDeclaration.MODIFIERS2_PROPERTY, METHOD_DECLARATION);
            put(MethodDeclaration.NAME_PROPERTY, METHOD_DECLARATION);
            put(MethodDeclaration.PARAMETERS_PROPERTY, METHOD_DECLARATION_PARAMETER);
            put(MethodDeclaration.RETURN_TYPE2_PROPERTY, METHOD_DECLARATION_RETURN);
            put(MethodDeclaration.THROWN_EXCEPTIONS_PROPERTY, METHOD_DECLARATION_THROWS);
            put(MethodDeclaration.TYPE_PARAMETERS_PROPERTY, UNKNOWN);

            put(Initializer.BODY_PROPERTY, METHOD_BODY);
            put(Initializer.MODIFIERS2_PROPERTY, METHOD_DECLARATION);

            put(FieldDeclaration.FRAGMENTS_PROPERTY, FIELD_DECLARATION_INITIALIZER);
            put(VariableDeclarationFragment.NAME_PROPERTY, FIELD_DECLARATION);
            put(FieldDeclaration.TYPE_PROPERTY, FIELD_DECLARATION);
            put(FieldDeclaration.JAVADOC_PROPERTY, FIELD_DECLARATION);
            put(FieldDeclaration.MODIFIERS2_PROPERTY, FIELD_DECLARATION);
        }
    };

    /**
     * Returns the {@link IJavaElement} at the current offset or {@link Optional#absent()} if resolving fails.
     */
    public static Optional<IJavaElement> resolveJavaElementFromEditor(final IEditorPart editor,
            final ITextSelection selection) {
        ensureIsNotNull(editor);
        ensureIsNotNull(selection);
        if (editor instanceof JavaEditor) {
            final JavaEditor javaEditor = (JavaEditor) editor;
            return resolveJavaElementFromEditor(javaEditor, selection.getOffset());
        }
        return absent();
    }

    /**
     * Returns the {@link IJavaElement} at the given offset in the editor.
     * 
     */
    public static Optional<IJavaElement> resolveJavaElementFromEditor(final JavaEditor editor, final int offset) {
        ensureIsNotNull(editor);
        final Optional<ITypeRoot> root = findTypeRoot(editor);
        if (root.isPresent()) {
            return resolveJavaElementFromTypeRootInEditor(root.get(), offset);
        }
        return absent();
    }

    /**
     * Returns the {@link IJavaElement} at the given offset. If no {@link IJavaElement} is selected, the innermost
     * enclosing {@link IJavaElement} is returned (e.g., the declaring method or type). If both selection resolutions
     * fail, {@link Optional#absent()} is returned.
     */
    public static Optional<IJavaElement> resolveJavaElementFromTypeRootInEditor(final ITypeRoot root, final int offset) {
        ensureIsNotNull(root);
        try {
            // try resolve elements at current offset
            final IJavaElement[] elements = root.codeSelect(offset, 0);
            if (elements.length > 0) {
                // return java element under cursor/selection start
                return of(elements[0]);
            } else {
                // if no java element has been selected, return the innermost Java element enclosing a given offset.
                // This might evaluate to null.
                IJavaElement enclosingElement = root.getElementAt(offset);
                if (enclosingElement == null) {
                    // selection occurred in empty space somewhere before the type declaration.
                    // return type-root then.
                    enclosingElement = root;
                }
                return of(enclosingElement);
            }
        } catch (final JavaModelException e) {
            log(e);
            return absent();
        }
    }

    public static JavaSelectionLocation resolveSelectionLocationFromAst(final CompilationUnit astRoot, final int offset) {
        ensureIsNotNull(astRoot);
        final ASTNode selectedNode = NodeFinder.perform(astRoot, offset, 0);
        if (selectedNode == null) {
            // this *should* never happen but it *can* happen...
            return JavaSelectionLocation.UNKNOWN;
        }
        final JavaSelectionLocation res = resolveSelectionLocationFromAstNode(selectedNode);
        return res;
    }

    public static JavaSelectionLocation resolveSelectionLocationFromAstNode(final ASTNode node) {
        if (node == null) {
            return JavaSelectionLocation.UNKNOWN;
        }

        // handle a direct selection on a declaration node, i.e., the users select a whitespace as in
        // "public $ void do(){}":
        // TODO Review: create second(?) mapping
        switch (node.getNodeType()) {
        case ASTNode.COMPILATION_UNIT:
        case ASTNode.TYPE_DECLARATION:
            return TYPE_DECLARATION;
        case ASTNode.METHOD_DECLARATION:
        case ASTNode.INITIALIZER:
            return METHOD_DECLARATION;
        case ASTNode.FIELD_DECLARATION:
            return FIELD_DECLARATION;
        default:
        }

        return resolveSelectionLocationFromNonMemberDeclarationNode(node);
    }

    /**
     * some inner node that is not a method, a type or a field declaration node...
     */
    private static JavaSelectionLocation resolveSelectionLocationFromNonMemberDeclarationNode(ASTNode node) {
        // deal with special case that no parent exists: for instance, if empty spaces before the package declaration
        // are selected, we translate this to type declaration:
        ASTNode parent = node.getParent();
        if (parent == null) {
            return JavaSelectionLocation.TYPE_DECLARATION;
        }
        // we have a child node selected. Let's figure out which location this translates best:
        while (node != null) {
            final StructuralPropertyDescriptor locationInParent = node.getLocationInParent();
            switch (parent.getNodeType()) {
            case ASTNode.VARIABLE_DECLARATION_FRAGMENT:
                if (isVariableNameSelectionInFieldDeclaration(parent, locationInParent)) {
                    return FIELD_DECLARATION;
                }
                break;
            case ASTNode.COMPILATION_UNIT:
            case ASTNode.TYPE_DECLARATION:
            case ASTNode.METHOD_DECLARATION:
            case ASTNode.FIELD_DECLARATION:
            case ASTNode.INITIALIZER:
                return mapLocationInParent(locationInParent);
            default:
                break;
            }
            node = parent;
            parent = parent.getParent();
        }
        return JavaSelectionLocation.UNKNOWN;
    }

    private static boolean isVariableNameSelectionInFieldDeclaration(final ASTNode parent,
            final StructuralPropertyDescriptor locationInParent) {
        final ASTNode superparent = parent.getParent();
        return superparent instanceof FieldDeclaration && VariableDeclarationFragment.NAME_PROPERTY == locationInParent;
    }

    private static JavaSelectionLocation mapLocationInParent(final StructuralPropertyDescriptor locationInParent) {
        final JavaSelectionLocation res = MAPPING.get(locationInParent);
        return res != null ? res : JavaSelectionLocation.UNKNOWN;
    }

    // TODO Review: rename method
    public static JavaSelectionLocation resolveSelectionLocationFromJavaElement(final IJavaElement element) {
        ensureIsNotNull(element);

        switch (element.getElementType()) {
        case IJavaElement.CLASS_FILE:
        case IJavaElement.COMPILATION_UNIT:
        case IJavaElement.PACKAGE_DECLARATION:
        case IJavaElement.IMPORT_DECLARATION:
        case IJavaElement.IMPORT_CONTAINER:
        case IJavaElement.TYPE:
            return TYPE_DECLARATION;
        case IJavaElement.METHOD:
        case IJavaElement.INITIALIZER:
            return METHOD_DECLARATION;
        case IJavaElement.FIELD:
            return FIELD_DECLARATION;
        case IJavaElement.LOCAL_VARIABLE:
            // shouldn't happen in a viewer selection, right?
            return METHOD_BODY;
        case IJavaElement.JAVA_MODEL:
        case IJavaElement.PACKAGE_FRAGMENT:
        case IJavaElement.PACKAGE_FRAGMENT_ROOT:
        case IJavaElement.ANNOTATION:
        default:
            return JavaSelectionLocation.UNKNOWN;
        }
    }
}
