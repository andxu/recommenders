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
package org.eclipse.recommenders.utils.names;

import static org.eclipse.recommenders.utils.Checks.*;
import static org.eclipse.recommenders.utils.Throws.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.MapMaker;

public class VmTypeName implements ITypeName {

    // These private fields need to be intialized before the public ones below.
    private static Map<String /* vmTypeName */, VmTypeName> index = new MapMaker().weakValues().makeMap();
    private static final Pattern GENERICS_PATTERN = Pattern.compile("<[^<>]*>");

    public static final VmTypeName OBJECT = VmTypeName.get("Ljava/lang/Object");

    public static final VmTypeName JAVA_LANG_NULL_POINTER_EXCEPTION = VmTypeName.get("Ljava/lang/NullPointerException");

    public static final VmTypeName JAVA_LANG_STRING = VmTypeName.get("Ljava/lang/String");

    public static final VmTypeName JAVA_LANG_EXCEPTION_IN_INITIALIZER_ERROR = VmTypeName
            .get("Ljava/lang/ExceptionInInitializerError");

    public static final VmTypeName STRING = VmTypeName.get("Ljava/lang/String");

    public static final VmTypeName NULL = get("Lnull");

    public static final VmTypeName BYTE = get("B");
    public static final VmTypeName BOOLEAN = get("Z");
    public static final VmTypeName CHAR = get("C");
    public static final VmTypeName DOUBLE = get("D");
    public static final VmTypeName FLOAT = get("F");
    public static final VmTypeName INT = get("I");
    public static final VmTypeName LONG = get("J");
    public static final VmTypeName SHORT = get("S");
    public static final VmTypeName VOID = get("V");

    public static synchronized VmTypeName get(String typeName) {
        typeName = removeGenerics(typeName);
        VmTypeName res = index.get(typeName);
        if (res == null) {
            res = new VmTypeName(typeName);
            index.put(typeName, res);
        }
        return res;
    }

    private static String removeGenerics(String typeName) {
        int oldLength;

        do {
            oldLength = typeName.length();
            Matcher matcher = GENERICS_PATTERN.matcher(typeName);
            typeName = matcher.replaceAll("");
        } while (typeName.length() < oldLength);

        return typeName;
    }

    private String identifier;

    /**
     * @see #get(String)
     */
    @VisibleForTesting
    protected VmTypeName(final String vmTypeName) {
        ensureIsNotNull(vmTypeName);
        ensureIsFalse(vmTypeName.length() == 0, "empty size for type name not permitted");
        loop: for (int i = 0; i < vmTypeName.length(); i++) {
            switch (vmTypeName.charAt(i)) {
            case '[':
                continue;
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'V':
            case 'Z':
            case 'L':
                break loop;
            default:
                throwUnreachable("Invalid type name: " + vmTypeName);
            }

        }

        int off = 0;
        while (off < vmTypeName.length()) {
            final char c = vmTypeName.charAt(off);
            // '-' as in package-info.class
            if (c == '-' || c == '[' || c == '/' || c == '<' || c == '>' || Character.isJavaIdentifierPart(c)) {
                off++;
                continue;
            }
            throwIllegalArgumentException("Cannot parse '%s' as vm type name.", vmTypeName);
            break;
        }
        identifier = vmTypeName;
    }

    @Override
    public ITypeName getArrayBaseType() {
        ensureIsTrue(isArrayType(), "only array-types have a base type!");
        int start = 0;
        while (identifier.charAt(++start) == '[') {
            // start counter gets increased
        }
        return get(identifier.substring(start));
    }

    @Override
    public int getArrayDimensions() {
        int count = 0;
        int start = 0;
        while (identifier.charAt(start++) == '[') {
            count++;
        }
        return count;
    }

    @Override
    public String getClassName() {
        final int indexOf = identifier.lastIndexOf('/');
        if (indexOf < 0 && !isPrimitiveType()) {
            return identifier.substring(1);
        }
        return identifier.substring(indexOf + 1);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public IPackageName getPackage() {
        final int lastSlash = identifier.lastIndexOf('/');
        if (lastSlash == -1 || identifier.charAt(0) == '[') {
            return VmPackageName.DEFAULT_PACKAGE;
        }
        return VmPackageName.get(identifier.substring(1, lastSlash));
    }

    @Override
    public boolean isAnonymousType() {
        return identifier.matches(".*\\$\\d+");
    }

    @Override
    public boolean isArrayType() {
        return identifier.charAt(0) == '[';
    }

    @Override
    public boolean isDeclaredType() {
        return identifier.charAt(0) == 'L';
    }

    @Override
    public boolean isNestedType() {
        return identifier.contains("$");
    }

    @Override
    public boolean isPrimitiveType() {
        return !(isArrayType() || isDeclaredType());
    }

    @Override
    public boolean isVoid() {
        return this == VOID;
    }

    @Override
    public int compareTo(final ITypeName o) {
        return getIdentifier().compareTo(o.getIdentifier());
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

    @Override
    public IMethodName getDeclaringMethod() {
        ensureIsTrue(isNestedType(), "only valid on nested types");
        final int lastPathSegmentSeparator = identifier.lastIndexOf('/');
        final String path = identifier.substring(0, lastPathSegmentSeparator);
        final int bracket = path.lastIndexOf('(');
        final int methodSeparator = path.lastIndexOf('/', bracket);
        final String newFQName = path.substring(0, methodSeparator) + "." + path.substring(methodSeparator + 1);
        return VmMethodName.get(newFQName);
    }

    @Override
    public ITypeName getDeclaringType() {
        ensureIsTrue(isNestedType(), "only valid on nested types");
        final int lastIndexOf = identifier.lastIndexOf('$');
        final String declaringTypeName = identifier.substring(0, lastIndexOf);
        return get(declaringTypeName);
    }
}
