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

package com.github.justhm228.unchecked.io;

import java.util.function.Function;
import java.util.function.Consumer;
import java.io.IOException;
import java.io.UncheckedIOException;
import static java.util.Objects.requireNonNull;

public final class UncheckedIO {

    private UncheckedIO() throws UnsupportedOperationException {

        super();
        throw new UnsupportedOperationException("No instances for you!");
    }

    public static void uncheckedIO(final IORunnable function, final Consumer<? super IOException> exceptionHandler) throws NullPointerException {

        requireNonNull(function);
        function.asRunnable(exceptionHandler).run();
    }

    public static void uncheckedIO(final IORunnable function) throws NullPointerException, UncheckedIOException {

        requireNonNull(function);
        function.asRunnable().run();
    }

    public static <T> T uncheckedIO(final IOSupplier<T> function, final Function<? super IOException, ? extends T> exceptionHandler) throws NullPointerException {

        requireNonNull(function);
        return function.asSupplier(exceptionHandler).get();
    }

    public static <T> T uncheckedIO(final IOSupplier<T> function) throws NullPointerException, UncheckedIOException {

        requireNonNull(function);
        return function.asSupplier().get();
    }
}
