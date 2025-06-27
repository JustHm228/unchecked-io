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

import org.junit.jupiter.api.*;
import com.github.justhm228.unchecked.io.IORunnable;
import org.junit.jupiter.params.ParameterizedTest;
import java.io.UncheckedIOException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import com.github.justhm228.unchecked.io.UncheckedIO;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(NameBySigGenerator.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class IORunnableTest {

    public IORunnableTest() {

        super();
    }

    @Test()
    public void test$failingWith() {

        final IORunnable function = IORunnable.failingWith();

        final IOException cause = assertThrows(IOException.class, function::run);

        assertNull(cause.getCause());
    }

    @ParameterizedTest()
    @RandomSource.Randomized()
    @Order(Integer.MIN_VALUE)
    public void test$failingWith$java_lang_String(final int randomness) {

        final String msg = TestConstants.generateExceptionMessage(randomness);

        final IORunnable function = IORunnable.failingWith(msg);

        final IOException cause = assertThrows(IOException.class, function::run);

        assertNull(cause.getCause());
        assertEquals(msg, cause.getMessage());
    }

    @ParameterizedTest()
    @RandomSource.Randomized()
    public void test$failingWith$java_util_function_Supplier(final int randomness) {

        final String msg = TestConstants.generateExceptionMessage(randomness);

        final IORunnable function = IORunnable.failingWith(() -> IOFailures.failure(msg));

        final IOException cause = assertThrowsExactly(IOException.class, function::run);

        assertNull(cause.getCause());
        assertEquals(msg, cause.getMessage());
    }

    @ParameterizedTest()
    @RandomSource.Randomized()
    public void test$asIORunnable$java_lang_Runnable(final int randomness) {

        final String msg = TestConstants.generateExceptionMessage(randomness);

        final IORunnable function = IORunnable.asIORunnable(() -> IOFailures.raiseFailure(msg));

        final IOException cause = assertThrowsExactly(IOException.class, function::run);

        assertNull(cause.getCause());
        assertEquals(msg, cause.getMessage());
    }

    @ParameterizedTest()
    @RandomSource.Randomized()
    public void test$asRunnable(final int randomness) {

        final String msg = TestConstants.generateExceptionMessage(randomness);

        final Runnable function = IORunnable.failingWith(msg).asRunnable();

        final IOException cause = assertThrows(UncheckedIOException.class, function::run).getCause();

        assertNotNull(cause);
        assertNull(cause.getCause());
        assertEquals(msg, cause.getMessage());
    }

    @ParameterizedTest()
    @RandomSource.Randomized()
    public void test$asRunnable$java_util_function_Consumer(final int randomness) {

        final String msg = TestConstants.generateExceptionMessage(randomness);

        final IORunnable function = IORunnable.failingWith(msg);

        final AtomicReference<IOException> caught = new AtomicReference<>(null);

        function.asRunnable(caught::set).run();

        final IOException cause = caught.get();

        assertNotNull(cause);
        assertNull(cause.getCause());
        assertEquals(msg, cause.getMessage());
    }

    @ParameterizedTest()
    @RandomSource.Randomized()
    public void test$asRunnable$java_util_function_Function(final int randomness) {

        final String msg = TestConstants.generateExceptionMessage(randomness);

        final Runnable function = IORunnable.failingWith(msg).asRunnable(ExpectedIOException::expectedException);

        final IOException cause = assertThrowsExactly(ExpectedIOException.class, function::run).getCause();

        assertNotNull(cause);
        assertNull(cause.getCause());
        assertEquals(msg, cause.getMessage());
    }

    @DisplayNameGeneration(NameBySigGenerator.class)
    @Nested()
    public final class UncheckedIOTest {

        public UncheckedIOTest() {

            super();
        }

        @ParameterizedTest()
        @RandomSource.Randomized()
        public void test$uncheckedIO$IORunnable(final int randomness) {

            final String msg = TestConstants.generateExceptionMessage(randomness);

            final IORunnable function = IORunnable.failingWith(msg);

            final IOException cause = assertThrows(UncheckedIOException.class, () -> UncheckedIO.uncheckedIO(function)).getCause();

            assertNotNull(cause);
            assertNull(cause.getCause());
            assertEquals(msg, cause.getMessage());
        }

        @ParameterizedTest()
        @RandomSource.Randomized()
        public void test$uncheckedIO$IORunnable$java_util_function_Consumer(final int randomness) {

            final String msg = TestConstants.generateExceptionMessage(randomness);

            final IORunnable function = IORunnable.failingWith(msg);

            final AtomicReference<IOException> caught = new AtomicReference<>(null);

            UncheckedIO.uncheckedIO(function, caught::set);

            final IOException cause = caught.get();

            assertNotNull(cause);
            assertNull(cause.getCause());
            assertEquals(msg, cause.getMessage());
        }
    }
}
