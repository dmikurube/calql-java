/*
 * Copyright 2026 Dai MIKURUBE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.theatime.calql.query;

import java.util.Objects;
import java.util.Optional;

/**
 * A range of values of type {@code T}, which is a closed interval that may be unbounded at either end, or an empty range.
 *
 * <p>Both ends are inclusive when bounded. Exclusive ends are not needed here because it is used for discrete values
 * such as {@link java.time.LocalDate}, where "after 2020-12-31" is "at least 2021-01-01".
 *
 * <p>An empty range is explicit and special. When a requested range has a lower end that is greater than its upper end,
 * a normalized special "empty" range is created. It has no ends. Its {@link #lower()} and {@link #upper()} throw
 * {@link IllegalStateException}.
 *
 * @param <T> the type of values in the range
 */
public final class Range<T extends Comparable<? super T>> {
    private Range(final T lower, final T upper, final boolean empty) {
        this.lower = lower;
        this.upper = upper;
        this.empty = empty;
    }

    /**
     * Returns the empty range, which contains no values.
     */
    public static <T extends Comparable<? super T>> Range<T> empty() {
        return new Range<>(null, null, true);
    }

    /**
     * Returns the whole range, which is unbounded at both ends, and contains all values.
     */
    public static <T extends Comparable<? super T>> Range<T> whole() {
        return new Range<>(null, null, false);
    }

    /**
     * Returns the range of values greater than or equal to {@code lower}, which is unbounded at the upper end.
     */
    public static <T extends Comparable<? super T>> Range<T> atLeast(final T lower) {
        return new Range<>(Objects.requireNonNull(lower, "lower"), null, false);
    }

    /**
     * Returns the range of values less than or equal to {@code upper}, which is unbounded at the lower end.
     */
    public static <T extends Comparable<? super T>> Range<T> atMost(final T upper) {
        return new Range<>(null, Objects.requireNonNull(upper, "upper"), false);
    }

    /**
     * Returns the range of values from {@code lower} to {@code upper}, both inclusive, or the empty range if
     * {@code lower} is greater than {@code upper}.
     *
     * <p>Both ends are inclusive in the same way as {@code BETWEEN} in SQL.
     */
    public static <T extends Comparable<? super T>> Range<T> between(final T lower, final T upper) {
        return ofNullableEnds(Objects.requireNonNull(lower, "lower"), Objects.requireNonNull(upper, "upper"));
    }

    public boolean isEmpty() {
        return this.empty;
    }

    /**
     * Returns the lower end of this range, or {@link Optional#empty()} if this range is unbounded at the lower end.
     *
     * @throws IllegalStateException  if this range is empty, which has no ends
     */
    public Optional<T> lower() {
        this.requireNonEmpty();
        return Optional.ofNullable(this.lower);
    }

    /**
     * Returns the upper end of this range, or {@link Optional#empty()} if this range is unbounded at the upper end.
     *
     * @throws IllegalStateException  if this range is empty, which has no ends
     */
    public Optional<T> upper() {
        this.requireNonEmpty();
        return Optional.ofNullable(this.upper);
    }

    public boolean contains(final T value) {
        Objects.requireNonNull(value, "value");
        if (this.empty) {
            return false;
        }
        if (this.lower != null && value.compareTo(this.lower) < 0) {
            return false;
        }
        if (this.upper != null && value.compareTo(this.upper) > 0) {
            return false;
        }
        return true;
    }

    /**
     * Returns the range of values contained in both this range and {@code other}.
     */
    public Range<T> intersection(final Range<T> other) {
        if (this.empty || other.empty) {
            return empty();
        }
        return ofNullableEnds(greaterLower(this.lower, other.lower), lesserUpper(this.upper, other.upper));
    }

    /**
     * Returns the minimal range that contains both this range and {@code other}.
     *
     * <p>For example, the span of {@code [1, 3]} and {@code [6, 9]} is {@code [1, 9]}.
     *
     * <p>If the two ranges overlap, or one of them is empty, the span is their union. Otherwise, the span also contains
     * the values between the two ranges, which are contained in neither of them, such as 4 and 5 in the example above.
     */
    public Range<T> span(final Range<T> other) {
        if (this.empty) {
            return other;
        }
        if (other.empty) {
            return this;
        }
        return ofNullableEnds(lesserLower(this.lower, other.lower), greaterUpper(this.upper, other.upper));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lower, this.upper, this.empty);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        if (otherObject instanceof Range) {
            final Range<?> other = (Range<?>) otherObject;
            return this.empty == other.empty
                    && Objects.equals(this.lower, other.lower)
                    && Objects.equals(this.upper, other.upper);
        }

        return false;
    }

    @Override
    public String toString() {
        if (this.empty) {
            return "[]";
        }
        return String.format("%s%s, %s%s",
                             this.lower == null ? "(" : "[",
                             this.lower == null ? "-Infinity" : this.lower,
                             this.upper == null ? "Infinity" : this.upper,
                             this.upper == null ? ")" : "]");
    }

    /**
     * Returns the range from {@code lower} to {@code upper}, where {@code null} means unbounded at the end, or the empty
     * range if {@code lower} is greater than {@code upper}.
     */
    private static <T extends Comparable<? super T>> Range<T> ofNullableEnds(final T lower, final T upper) {
        if (lower != null && upper != null && lower.compareTo(upper) > 0) {
            return empty();
        }
        return new Range<>(lower, upper, false);
    }

    private static <T extends Comparable<? super T>> T greaterLower(final T a, final T b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.compareTo(b) >= 0 ? a : b;
    }

    private static <T extends Comparable<? super T>> T lesserLower(final T a, final T b) {
        if (a == null || b == null) {
            return null;
        }
        return a.compareTo(b) <= 0 ? a : b;
    }

    private static <T extends Comparable<? super T>> T lesserUpper(final T a, final T b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.compareTo(b) <= 0 ? a : b;
    }

    private static <T extends Comparable<? super T>> T greaterUpper(final T a, final T b) {
        if (a == null || b == null) {
            return null;
        }
        return a.compareTo(b) >= 0 ? a : b;
    }

    private void requireNonEmpty() {
        if (this.empty) {
            throw new IllegalStateException("an empty range has no ends");
        }
    }

    // true if this range contains no values. Note that it cannot be represented only by lower == null and upper == null.
    //
    // If it is true, lower and upper are meaningless. They must always be null in that case so that all empty Ranges are equal.
    private final boolean empty;

    // lower == null means that the lower end is unbounded. Same for upper == null.
    //
    // Note that a combination of lower == null and upper == null means "the whole range", not "the empty range".
    //
    // They are T itself, not Optional<T>, so that intersection(), span() and contains() can work quickly with immediate
    // values, without resolving Optional. The values are not expected to be read by lower() and upper() so often.
    private final T lower;
    private final T upper;
}
