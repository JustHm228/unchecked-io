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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import java.lang.annotation.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class RandomSource implements ArgumentsProvider {

    private static final int STATIC_CAPACITY = 2;

    private static final int DEFAULT_CAPACITY = 1;

    public RandomSource() {

        super();
    }

    public static IntStream randomInts(final int size) {

        final ThreadLocalRandom random = ThreadLocalRandom.current();

        return random.ints(size);
    }

    public static IntStream randomInts() {

        return randomInts(STATIC_CAPACITY);
    }

    @Override()
    public Stream<? extends Arguments> provideArguments(final ExtensionContext ctx) {

        final int size = estimateCapacity(ctx);

        return randomInts(size).mapToObj(Arguments::of);
    }

    private static int estimateCapacity(final ExtensionContext ctx) {

        return ctx.getTestMethod().filter((method) -> method.isAnnotationPresent(Randomized.class))
                .map((method) -> method.getAnnotation(Randomized.class))
                .map(Randomized::value).orElse(DEFAULT_CAPACITY);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented()
    @ArgumentsSource(RandomSource.class)
    public @interface Randomized {

        int value() default DEFAULT_CAPACITY;
    }
}
