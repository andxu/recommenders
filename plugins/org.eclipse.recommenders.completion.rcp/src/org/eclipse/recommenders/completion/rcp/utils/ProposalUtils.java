/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Andreas Sewe - better handling of generics.
 *    Johannes Dorn - refactoring.
 */
package org.eclipse.recommenders.completion.rcp.utils;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Optional.absent;
import static org.eclipse.jdt.core.compiler.CharOperation.NO_CHAR;
import static org.eclipse.recommenders.utils.LogMessages.LOG_WARNING_REFLECTION_FAILED;
import static org.eclipse.recommenders.utils.Logs.log;
import static org.eclipse.recommenders.utils.Reflections.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.codeassist.InternalCompletionProposal;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.recommenders.internal.completion.rcp.l10n.LogMessages;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.VmMethodName;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

@SuppressWarnings("restriction")
public final class ProposalUtils {

    private ProposalUtils() {
    }

    private static final IMethodName OBJECT_CLONE = VmMethodName.get("Ljava/lang/Object.clone()Ljava/lang/Object;"); //$NON-NLS-1$

    private static final char[] INIT = "<init>".toCharArray(); //$NON-NLS-1$
    private static final char[] JAVA_LANG_OBJECT = "Ljava.lang.Object;".toCharArray(); //$NON-NLS-1$

    /**
     * Workaround needed to handle proposals with generic signatures properly.
     *
     * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=380203">Bug 380203</a>.
     */
    private static final Field ORIGINAL_SIGNATURE = getDeclaredField(InternalCompletionProposal.class,
            "originalSignature") //$NON-NLS-1$
                    .orNull();

    private static final Method GET_DECLARATION_TYPE_NAME = getDeclaredMethod(InternalCompletionProposal.class,
            "getDeclarationTypeName") //$NON-NLS-1$
                    .orNull();
    private static final Method GET_DECLARATION_PACKAGE_NAME = getDeclaredMethod(InternalCompletionProposal.class,
            "getDeclarationPackageName") //$NON-NLS-1$
                    .orNull();

    public static Optional<IMethodName> toMethodName(CompletionProposal proposal) {
        Preconditions.checkArgument(isKindSupported(proposal));

        if (isArrayCloneMethod(proposal)) {
            return Optional.of(OBJECT_CLONE);
        }

        if (GET_DECLARATION_TYPE_NAME == null || GET_DECLARATION_PACKAGE_NAME == null) {
            return absent();
        }

        final char[] declarationPackageName;
        final char[] declarationTypeName;
        try {
            declarationPackageName = (char[]) GET_DECLARATION_PACKAGE_NAME.invoke(proposal);
            declarationTypeName = (char[]) GET_DECLARATION_TYPE_NAME.invoke(proposal);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return absent();
        }

        if (declarationTypeName == null) {
            return absent();
        }

        StringBuilder builder = new StringBuilder();
        builder.append('L');
        if (declarationPackageName != null && declarationPackageName.length > 0) {
            builder.append(CharOperation.replaceOnCopy(declarationPackageName, '.', '/'));
            builder.append('/');
        }
        builder.append(CharOperation.replaceOnCopy(declarationTypeName, '.', '$'));
        builder.append('.');
        builder.append(proposal.isConstructor() ? INIT : proposal.getName());
        builder.append('(');
        char[] signature = getSignature(proposal);
        char[][] typeParameters = Signature.getTypeParameters(proposal.getDeclarationSignature());
        char[][] parameterTypes = Signature.getParameterTypes(signature);
        for (char[] parameterType : parameterTypes) {
            appendType(builder, parameterType, typeParameters);
        }
        builder.append(')');
        appendType(builder, Signature.getReturnType(signature), typeParameters);

        String methodName = builder.toString();
        try {
            return Optional.<IMethodName>of(VmMethodName.get(methodName));
        } catch (Exception e) {
            log(LogMessages.ERROR_SYNTATICALLY_INCORRECT_METHOD_NAME, e, methodName, toLogString(proposal));
            return absent();
        }
    }

    private static void appendType(StringBuilder builder, char[] type, char[][] typeParameters) {
        switch (Signature.getTypeSignatureKind(type)) {
        case Signature.TYPE_VARIABLE_SIGNATURE:
            char[] typeVariableName = CharOperation.subarray(type, 1, type.length - 1);
            char[] resolvedTypeVariable = resolveTypeVariable(typeVariableName, typeParameters);
            builder.append(CharOperation.replaceOnCopy(resolvedTypeVariable, '.', '/'));
            break;
        case Signature.ARRAY_TYPE_SIGNATURE:
            int dimensions = Signature.getArrayCount(type);
            builder.append(type, 0, dimensions);
            appendType(builder, Signature.getElementType(type), typeParameters);
            break;
        default:
            char[] erasedParameterType = Signature.getTypeErasure(type);
            builder.append(CharOperation.replaceOnCopy(erasedParameterType, '.', '/'));
            break;
        }
    }

    private static char[] resolveTypeVariable(char[] typeVariableName, char[][] typeParameters) {
        for (char[] typeParameter : typeParameters) {
            if (CharOperation.equals(typeVariableName, Signature.getTypeVariable(typeParameter))) {
                char[][] typeParameterBounds = Signature.getTypeParameterBounds(typeParameter);
                if (typeParameterBounds.length > 0) {
                    return typeParameterBounds[0];
                } else {
                    return JAVA_LANG_OBJECT;
                }
            }
        }
        // No match found. Assume Object.
        return JAVA_LANG_OBJECT;
    }

    private static String toLogString(CompletionProposal proposal) {
        if (proposal == null) {
            return "null proposal"; //$NON-NLS-1$
        }
        return new StringBuilder().append(firstNonNull(proposal.getDeclarationSignature(), NO_CHAR)).append('#')
                .append(firstNonNull(proposal.getName(), NO_CHAR)).append('#')
                .append(firstNonNull(proposal.getSignature(), NO_CHAR)).toString();
    }

    private static boolean isKindSupported(CompletionProposal proposal) {
        switch (proposal.getKind()) {
        case CompletionProposal.METHOD_REF:
            return true;
        case CompletionProposal.METHOD_REF_WITH_CASTED_RECEIVER:
            return true;
        case CompletionProposal.METHOD_DECLARATION:
            return true;
        case CompletionProposal.CONSTRUCTOR_INVOCATION:
            return true;
        default:
            return false;
        }
    }

    private static boolean isArrayCloneMethod(CompletionProposal proposal) {
        if (proposal.isConstructor()) {
            // Not a method proposal
            return false;
        }

        char[] declarationSignature = proposal.getDeclarationSignature();
        if (declarationSignature[0] != '[') {
            // Not an array
            return false;
        }

        if (!CharOperation.equals(TypeConstants.CLONE, proposal.getName())) {
            // Not named clone
            return false;
        }

        char[] signature = proposal.getSignature();
        if (signature.length != declarationSignature.length + 2 || signature[0] != '(' || signature[1] != ')') {
            // Overload of real (no-args) clone method
            return false;
        }

        if (!CharOperation.endsWith(signature, declarationSignature)) {
            // Wrong return type
            return false;
        }

        return true;
    }

    private static char[] getSignature(CompletionProposal proposal) {
        char[] signature = null;
        if (canUseReflection(proposal)) {
            try {
                signature = (char[]) ORIGINAL_SIGNATURE.get(proposal);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                log(LOG_WARNING_REFLECTION_FAILED, e, ORIGINAL_SIGNATURE);
            }
        }
        return signature != null ? signature : proposal.getSignature();
    }

    private static boolean canUseReflection(CompletionProposal proposal) {
        return proposal instanceof InternalCompletionProposal && ORIGINAL_SIGNATURE != null
                && ORIGINAL_SIGNATURE.isAccessible();
    }
}
