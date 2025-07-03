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
import static java.util.Objects.requireNonNull;

/**
 * A checked equivalent of {@link Runnable} that can throw {@link IOException}.
 * <p>
 *     It is primarily intended to represent I/O operations and to perform them on demand
 *     either synchronously, or asynchronously.
 * </p>
 *
 * <h3>Synchronous execution</h3>
 *
 * <p>
 *     To execute {@link IORunnable} synchronously, you can use {@link UncheckedIO} class:
 *     <pre>{@code
 *     UncheckedIO.uncheckedIO(() -> {
 *         // do something that might throw IOException . . .
 *         throw new IOException();
 *     });
 *     }</pre>
 *     You may consider using static imports:
 *     <pre>{@code
 *     import static com.github.justhm228.unchecked.io.UncheckedIO.*;
 *     }</pre>
 * </p>
 * <p>
 *     {@link UncheckedIO} can also be used if you, for some reason, do not want to bother
 *     handling checked {@link IOException}s in your code.
 *     <pre>{@code
 *     Path filepath = ...
 *     UncheckedIO.uncheckedIO(() -> Files.createFile(filepath)); // Creates file with no need to handle IOException
 *     }</pre>
 *     <b>
 *         You should note that thrown {@link IOException}s would not "magically" disappear;
 *         they would be re-thrown as {@link UncheckedIOException}s instead!
 *     </b>
 * </p>
 *
 * <h3>Asynchronous execution</h3>
 *
 * {@link IORunnable} can also be used to perform I/O operations asynchronously. Like this:
 * <pre>{@code
 * IORunnable task = IORunnable.failingWith(); // <- task to be executed
 * ExecutorService scheduler = Executors.newSingleThreadExecutor();
 *
 * Future<?> future = scheduler.submit(task.asRunnable()); // schedule execution
 *
 * try {
 *     future.get(); // wait until finish
 *     // ...
 * } catch (ExecutionException e) {
 *     Throwable thrown = e.getCause();
 *     // Note: asRunnable() wraps IOExceptions to UncheckedIOExceptions, so you should account for that
 *     if (thrown instanceof UncheckedIOException) thrown = thrown.getCause();
 *     thrown.printStackTrace(); // handle exception
 * } catch (InterruptedException e) {}
 * }</pre>
 * But the given example uses {@link #asRunnable()}, so that means the need to manually unwrap
 * {@link UncheckedIOException}s to handle thrown {@link IOException}s directly.
 * It can be better if you would use {@link java.util.concurrent.Callable}:
 * <pre>{@code
 * IORunnable task = IORunnable.failingWith(); // <- task to be executed
 * ExecutorService scheduler = Executors.newSingleThreadExecutor();
 *
 * // Using Callable instead of Runnable to schedule execution:
 * Future<?> future = scheduler.submit(() -> { task.run(); return null; });
 *
 * try {
 *     future.get(); // wait until finish
 *     // ...
 * } catch (ExecutionException e) {
 *     Throwable thrown = e.getCause();
 *     // And now we don't need to unwrap UncheckedIOExceptions! :)
 *     thrown.printStackTrace(); // handle exception
 * } catch (InterruptedException e) {}
 * }</pre>
 *
 * @author JustHuman228
 * @see UncheckedIO
 * @see #failingWith(String)
 * @see #failingWith()
 * @see #run()
 */
@FunctionalInterface()
public interface IORunnable {

    /**
     * Returns a checked {@link IORunnable} equivalent of the provided {@link Runnable}.
     * <p>
     *     The resulting {@link Runnable} just {@link Runnable#run() runs} and throws the
     *     {@link UncheckedIOException#getCause() cause} of thrown {@link UncheckedIOException}, if any.
     *     Other unchecked exceptions will be re-thrown unchanged.
     * </p>
     *
     * @param function An unchecked {@link Runnable} to be transformed.
     * @return A checked {@link IORunnable} equivalent of the provided {@link Runnable}.
     * @throws NullPointerException If the provided {@link Runnable} is <code>null</code>.
     */
    static IORunnable asIORunnable(final Runnable function) throws NullPointerException {

        requireNonNull(function);

        return () -> {

            try {

                function.run();

            } catch (final UncheckedIOException unchecked) {

                throw unchecked.getCause();
            }
        };
    }

    /**
     * Returns an {@link IORunnable} that always throws {@link IOException}s constructed by the
     * provided factory.
     *
     * @param exceptionFactory A {@link Supplier} that constructs and returns an {@link IOException}
     *                         to be thrown. The returned exception can be <code>null</code>, while the
     *                         factory itself can't.
     * @return An {@link IORunnable} that always fails.
     * @throws NullPointerException If the provided exception factory is <code>null</code>.
     * @apiNote This API is primarily intended for testing purposes, but it remains supported so you can
     *          still use it in your production code without any risks.
     * @implNote If the provided exception factory returns <code>null</code> instead of an
     *           {@link IOException} to throw, the resulting {@link IORunnable} should simply return without
     *           performing any operations or throwing any exceptions.
     *           <pre>{@code
     *           IOException e = exceptionFactory.get();
     *           if (e != null) throw e; // only if non-null!
     *           }</pre>
     *           This should be made so because the current context can't guarantee that the provided
     *           exception factory won't return <code>null</code>. If it happens and there will be no
     *           check for <code>null</code>, it will lead to executing the following code:
     *           <pre>{@code
     *           IOException e = null; // exceptionFactory returned null
     *           throw e; // <- NullPointerException!
     *           }</pre>
     *           which will result in a very confusing {@link NullPointerException} because
     *           <code>throw</code> operator expects the throwing exception to be non-<code>null</code>.
     * @see IORunnable
     * @see #failingWith(String)
     */
    static IORunnable failingWith(final Supplier<? extends IOException> exceptionFactory) throws NullPointerException {

        requireNonNull(exceptionFactory);

        return () -> {

            final IOException exception = exceptionFactory.get();

            if (exception != null) {

                throw exception;
            }
        };
    }

    /**
     * Returns an {@link IORunnable} that always throws an {@link IOException} with the provided
     * exception message.
     *
     * @param msg An exception message. Can be <code>null</code>.
     * @return An {@link IORunnable} that always fails.
     * @apiNote This API is primarily intended for testing purposes, but it remains supported so you can
     *          still use it in your production code without any risks.
     * @implNote If the provided exception message is <code>null</code>, the resulting {@link IORunnable}
     *           throws an {@link IOException} that is equal to <code>new IOException((String)null)</code>,
     *           which is an "exception with no additional information".
     * @see IORunnable
     * @see #failingWith(Supplier)
     * @see #failingWith()
     */
    static IORunnable failingWith(final String msg) {

        return failingWith(() -> new IOException(msg));
    }

    /**
     * Returns an {@link IORunnable} that always throws an {@link IOException} with no
     * additional information.
     *
     * @return An {@link IORunnable} that always fails.
     * @apiNote This API is primarily intended for testing purposes, but it remains supported so you can
     *          still use it in your production code without any risks.
     * @see IORunnable
     * @see #failingWith(String)
     */
    static IORunnable failingWith() {

        return failingWith(IOException::new);
    }

    /**
     * Performs an I/O exception or throws an {@link IOException} on failure.
     *
     * @throws IOException If failed to perform an I/O operation.
     * @see IORunnable
     * @see UncheckedIO
     */
    void run() throws IOException;

    /**
     * Returns a {@link Runnable} equivalent of this {@link IORunnable} that transforms thrown
     * {@link IOException}s with the provided {@link Function function}.
     * <p>
     *     The returned {@link Runnable} just {@link #run() runs} the I/O operation and throws an
     *     unchecked exception returned by the provided exception transformer as a result to thrown
     *     {@link IOException}, if any.
     * </p>
     *
     * @param exceptionTransformer A {@link Function} that transforms thrown {@link IOException} to an
     *                             unchecked exception to be thrown instead.
     * @return A {@link Runnable} equivalent of this {@link IORunnable}.
     * @throws NullPointerException If the provided exception transformer is <code>null</code>.
     * @see UncheckedIO
     * @see #run()
     * @see #asRunnable(Consumer)
     */
    default Runnable asRunnable(final Function<? super IOException, ? extends RuntimeException> exceptionTransformer) throws NullPointerException {

        requireNonNull(exceptionTransformer);

        return asRunnable((Consumer<IOException>) (io) -> {

            throw exceptionTransformer.apply(io);
        });
    }

    /**
     * Returns a {@link Runnable} equivalent of this {@link IORunnable} that uses the provided
     * exception handler to handle thrown {@link IOException}s.
     * <p>
     *     The returned {@link Runnable} just {@link #run() runs} the I/O operation and handles thrown
     *     {@link IOException} with the provided exception handler.
     * </p>
     *
     * @param exceptionHandler A {@link Supplier} that handles the received {@link IOException}, if any.
     * @return A {@link Runnable} equivalent of this {@link IORunnable}.
     * @throws NullPointerException If the provided exception handler is <code>null</code>.
     * @see UncheckedIO
     * @see #run()
     * @see #asRunnable(Function)
     */
    default Runnable asRunnable(final Consumer<? super IOException> exceptionHandler) throws NullPointerException {

        requireNonNull(exceptionHandler);

        return () -> {

            try {

                run();

            } catch (final IOException io) {

                exceptionHandler.accept(io);
            }
        };
    }

    /**
     * Returns an unchecked {@link Runnable} equivalent of this {@link IORunnable}.
     * <p>
     *     The resulting {@link Runnable} just {@link #run() runs} the I/O operation and wraps thrown
     *     {@link IOException} to {@link UncheckedIOException}, if any.
     * </p>
     *
     * @return An unchecked {@link Runnable} equivalent of this {@link IORunnable}.
     * @see UncheckedIO
     * @see #run()
     * @see #asRunnable(Consumer)
     * @see #asRunnable(Function)
     */
    default Runnable asRunnable() {

        return asRunnable((Function<IOException, UncheckedIOException>) UncheckedIOException::new);
    }
}
