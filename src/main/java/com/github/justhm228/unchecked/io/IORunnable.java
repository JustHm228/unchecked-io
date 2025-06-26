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
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.function.Supplier;

@FunctionalInterface()
public interface IORunnable {

    static IORunnable asIORunnable(final Runnable function) {

        return () -> {

            try {

                function.run();

            } catch (final UncheckedIOException unchecked) {

                throw unchecked.getCause();
            }
        };
    }

    static IORunnable failingWith(final Supplier<? extends IOException> exceptionFactory) {

        return () -> {

            final IOException exception = exceptionFactory.get();

            if (exception != null) {

                throw exception;
            }
        };
    }

    static IORunnable failingWith(final String msg) {

        return failingWith(() -> new IOException(msg));
    }

    static IORunnable failingWith() {

        return failingWith(IOException::new);
    }

    void run() throws IOException;

    default Runnable asRunnable(final Function<? super IOException, ? extends RuntimeException> exceptionTransformer) {

        return asRunnable((Consumer<IOException>) (io) -> {

            throw exceptionTransformer.apply(io);
        });
    }

    default Runnable asRunnable(final Consumer<? super IOException> exceptionHandler) {

        return () -> {

            try {

                run();

            } catch (final IOException io) {

                exceptionHandler.accept(io);
            }
        };
    }

    default Runnable asRunnable() {

        return asRunnable((Function<IOException, UncheckedIOException>) UncheckedIOException::new);
    }
}
