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

/**
 * Static utility methods to perform unchecked I/O operations.
 *
 * @author JustHuman228
 * @see IORunnable
 * @see IOSupplier
 * @see #uncheckedIO(IORunnable)
 * @see #uncheckedIO(IOSupplier)
 */
public final class UncheckedIO {

    /**
     * Prevents initialization.
     *
     * @throws UnsupportedOperationException Always.
     */
    private UncheckedIO() throws UnsupportedOperationException {

        super();
        throw new UnsupportedOperationException("No instances for you!");
    }

    /**
     * Runs the provided function and handles thrown {@link IOException} with the provided exception handler.
     *
     * @param function An {@link IORunnable} to run.
     * @param exceptionHandler An exception handler.
     * @throws NullPointerException If any of the provided arguments is <code>null</code>.
     * @see UncheckedIO
     * @see #uncheckedIO(IORunnable)
     * @see IORunnable
     * @see IORunnable#asRunnable(Consumer)
     */
    public static void uncheckedIO(final IORunnable function, final Consumer<? super IOException> exceptionHandler) throws NullPointerException {

        requireNonNull(function);
        function.asRunnable(exceptionHandler).run();
    }

    /**
     * Runs the provided function and wraps thrown {@link IOException} to {@link UncheckedIOException}.
     *
     * @param function An {@link IORunnable} to run.
     * @throws NullPointerException If the provided function is <code>null</code>.
     * @throws UncheckedIOException If the provided function throws an {@link IOException}.
     * @see UncheckedIO
     * @see #uncheckedIO(IORunnable, Consumer)
     * @see IORunnable
     * @see IORunnable#asRunnable()
     */
    public static void uncheckedIO(final IORunnable function) throws NullPointerException, UncheckedIOException {

        requireNonNull(function);
        function.asRunnable().run();
    }

    /**
     * Runs the provided function and returns the result, while handling thrown {@link IOException} with the provided exception handler.
     *
     * @param function An {@link IOSupplier} to run.
     * @param exceptionHandler An exception handler.
     * @return The result.
     * @param <T> The type of result.
     * @throws NullPointerException If any of the provided arguments is <code>null</code>.
     * @see UncheckedIO
     * @see #uncheckedIO(IOSupplier)
     * @see IOSupplier
     * @see IOSupplier#asSupplier(Function)
     */
    public static <T> T uncheckedIO(final IOSupplier<T> function, final Function<? super IOException, ? extends T> exceptionHandler) throws NullPointerException {

        requireNonNull(function);
        return function.asSupplier(exceptionHandler).get();
    }

    /**
     * Runs the provided function and returns the result, while wrapping thrown {@link IOException} to {@link UncheckedIOException}.
     *
     * @param function An {@link IOSupplier} to run.
     * @return The result.
     * @param <T> The type of result.
     * @throws NullPointerException If the provided function is <code>null</code>.
     * @throws UncheckedIOException If the provided function throws an {@link IOException}.
     * @see UncheckedIO
     * @see #uncheckedIO(IOSupplier, Function)
     * @see IOSupplier
     * @see IOSupplier#asSupplier()
     */
    public static <T> T uncheckedIO(final IOSupplier<T> function) throws NullPointerException, UncheckedIOException {

        requireNonNull(function);
        return function.asSupplier().get();
    }
}
