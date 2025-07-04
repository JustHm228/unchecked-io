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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Supplier;
import java.util.function.Function;
import static java.util.Objects.requireNonNull;

@FunctionalInterface()
public interface IOSupplier<T> {

    static <T> IOSupplier<T> asIOSupplier(final Supplier<T> function) throws NullPointerException {

        requireNonNull(function);

        return () -> {

            try {

                return function.get();

            } catch (final UncheckedIOException unchecked) {

                throw unchecked.getCause();
            }
        };
    }

    static <T> IOSupplier<T> failingWith(final Supplier<? extends IOException> exceptionFactory) throws NullPointerException {

        requireNonNull(exceptionFactory);

        return () -> {

            final IOException exception = exceptionFactory.get();

            if (exception != null) {

                throw exception;
            }

            return null;
        };
    }

    static <T> IOSupplier<T> failingWith(final String msg) {

        return failingWith(() -> new IOException(msg));
    }

    static <T> IOSupplier<T> failingWith() {

        return failingWith(IOException::new);
    }

    T get() throws IOException;

    default Supplier<T> asSupplier(final Function<? super IOException, ? extends T> exceptionHandler) throws NullPointerException {

        requireNonNull(exceptionHandler);

        return () -> {

            try {

                return get();

            } catch (final IOException io) {

                return exceptionHandler.apply(io);
            }
        };
    }

    default Supplier<T> asSupplier() {

        return asSupplier((io) -> {

            throw new UncheckedIOException(io);
        });
    }
}
