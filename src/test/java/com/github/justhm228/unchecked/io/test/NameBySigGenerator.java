/*
 * The MIT License
 *
 * Copyright (c) 2025 JustHuman228
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.justhm228.unchecked.io.test;

import org.junit.jupiter.api.DisplayNameGenerator;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Set;

public final class NameBySigGenerator implements DisplayNameGenerator {

    public static final char CLASS_PATH_SEPARATOR_CHAR = '$';

    public static final char METHOD_NAME_PART_SEPARATOR_CHAR = '$';

    public static final char METHOD_PARAM_PATH_SEPARATOR_CHAR = '_';

    public static final String CLASS_PATH_SEPARATOR = Character.toString(CLASS_PATH_SEPARATOR_CHAR);

    public static final String METHOD_NAME_PART_SEPARATOR = Character.toString(METHOD_NAME_PART_SEPARATOR_CHAR);

    public static final String METHOD_PARAM_PATH_SEPARATOR = Character.toString(METHOD_PARAM_PATH_SEPARATOR_CHAR);

    public static final String TEST_CLASS_SUFFIX = "Test";

    public static final String TEST_METHOD_PREFIX = "test" + METHOD_NAME_PART_SEPARATOR;

    private static final String PREFIX = "";

    private static final String CLASS_PREFIX = "";

    private static final String METHOD_PREFIX = "";

    private static final String CLASS_PREFIXES = PREFIX + CLASS_PREFIX;

    private static final String METHOD_PREFIXES = PREFIX + METHOD_PREFIX;

    private static final String DEFAULT_PACKAGE = "";

    private static final String STATIC_PACKAGE = "";

    private static final String PARAMS_START = "(";

    private static final String ARRAY_DECLARATION = "[]";

    private static final String PARAM_SEPARATOR = ", ";

    private static final String PARAMS_END = ")";

    private static final char JAVA_PACKAGE_SEPARATOR_CHAR = '.';

    private static final Collection<String> PRIMITIVE_TYPES = Set.of("void", "boolean", "byte", "short", "int", "char", "long", "float", "double");

    public NameBySigGenerator() {

        super();
    }

    private static String generateDisplayName(final Class<?> cls) {

        final String simpleName = cls.getSimpleName();

        if (!simpleName.endsWith(TEST_CLASS_SUFFIX)) {

            return null;
        }

        String qualifier = "";

        final Class<?> declaringClass = cls.getDeclaringClass();

        if (declaringClass != null && Modifier.isStatic(cls.getModifiers())) {

            qualifier = generateDisplayName(declaringClass) + CLASS_PATH_SEPARATOR;
        }

        final String testSubjectName = simpleName.substring(0, simpleName.length() - 4);

        final StringBuilder displayName = new StringBuilder(CLASS_PREFIXES);

        if (!testSubjectName.contains(CLASS_PATH_SEPARATOR) && !PRIMITIVE_TYPES.contains(testSubjectName)) {

            displayName.append(DEFAULT_PACKAGE);
        }

        return qualifier + displayName.append(STATIC_PACKAGE).append(testSubjectName.replace(CLASS_PATH_SEPARATOR_CHAR, JAVA_PACKAGE_SEPARATOR_CHAR));
    }

    @Override()
    public String generateDisplayNameForClass(final Class<?> testClass) {

        return generateDisplayName(testClass);
    }

    @Override()
    public String generateDisplayNameForNestedClass(final Class<?> nestedClass) {

        return generateDisplayName(nestedClass);
    }

    @Override()
    public String generateDisplayNameForMethod(final Class<?> testClass, final Method testMethod) {

        final String name = testMethod.getName();

        if (!name.startsWith(TEST_METHOD_PREFIX) || name.endsWith(METHOD_NAME_PART_SEPARATOR)) {

            return null;
        }

        final String[] parts = name.substring(5).split("\\" + METHOD_NAME_PART_SEPARATOR_CHAR);

        final StringBuilder builder = new StringBuilder(METHOD_PREFIXES).append(parts[0]).append(PARAMS_START);

        for (int i = 1; i < parts.length; i++) {

            String part = parts[i];

            final StringBuilder partBuilder = new StringBuilder(part);

            while (partBuilder.toString().endsWith(METHOD_PARAM_PATH_SEPARATOR)) {

                partBuilder.replace(part.length() - 1, part.length(), ARRAY_DECLARATION);
            }

            part = partBuilder.toString();

            if (part.contains(METHOD_PARAM_PATH_SEPARATOR)) {

                builder.append(part.replace(METHOD_PARAM_PATH_SEPARATOR_CHAR, JAVA_PACKAGE_SEPARATOR_CHAR));

            } else if (!PRIMITIVE_TYPES.contains(part)) {

                builder.append(DEFAULT_PACKAGE).append(part);
            }

            if (i == parts.length - 1) {

                continue;
            }

            builder.append(PARAM_SEPARATOR);
        }

        return builder.append(PARAMS_END).toString();
    }
}
