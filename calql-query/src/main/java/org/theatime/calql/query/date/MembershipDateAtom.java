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

package org.theatime.calql.query.date;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A base of the date atoms that test whether a column of a date is one of the given values,
 * such as {@link EitherMonth}, or none of them, such as {@link NeitherMonth}.
 *
 * <p>Its negation is an atom of the opposite class with the same values. For example, the negation of
 * {@code month in [1, 2]} ({@link EitherMonth}) is {@code month not in [1, 2]} ({@link NeitherMonth}).
 *
 * @param <V> the type of the values of the column, which are comparable so that {@link #toString()} can sort them
 */
abstract class MembershipDateAtom<V extends Comparable<? super V>> extends DateAtom {
    MembershipDateAtom(final Set<V> values, final boolean neither) {
        this.values = values;
        this.neither = neither;
    }

    /**
     * Returns the name of the column, such as {@code "month"}.
     */
    abstract String columnName();

    /**
     * Returns the value of the column for the date, such as its month.
     */
    abstract V columnValueOf(LocalDate date);

    final Set<V> values() {
        return this.values;
    }

    /**
     * Returns whether this atom tests that a column of a date is none of its values, or one of them.
     *
     * <p>It is true for the "neither" atoms, such as {@link NeitherMonth}, and false for the "either" atoms, such as
     * {@link EitherMonth}.
     *
     * @return true if this atom matches the dates whose column is none of its values
     */
    public final boolean isNeither() {
        return this.neither;
    }

    /**
     * Tests whether the column of non-null {@code target} satisfies this atom.
     *
     * @param target  the date to test, not null
     * @return true if the column of {@code target} satisfies this atom
     * @throws NullPointerException  if {@code target} is null
     */
    @Override
    public final boolean test(final LocalDate target) {
        Objects.requireNonNull(target, "target");
        // Possible optimization: columnValueOf() boxes the value of the column for every target. A year such as 2024 is
        // out of the Integer cache (-128 to 127), so it may allocate an Integer every time unless the JIT eliminates it.
        // Months and days of month could be tested by bit masks, and days of week by an EnumSet, without boxing.
        final boolean contains = this.values.contains(this.columnValueOf(target));
        if (this.neither) {
            return !contains;
        } else {
            return contains;
        }
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.getClass(), this.values);
    }

    @Override
    public final boolean equals(final Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        // Compares the classes, not only instanceof, so that EitherMonth.of(2) equals neither EitherDayOfMonth.of(2)
        // nor NeitherMonth.of(2).
        if (otherObject != null && otherObject.getClass() == this.getClass()) {
            final MembershipDateAtom<?> other = (MembershipDateAtom<?>) otherObject;
            return Objects.equals(this.values, other.values);
        }

        return false;
    }

    @Override
    public final String toString() {
        if (this.values.size() == 1) {
            return String.format("%s %s %s", this.columnName(), this.neither ? "<>" : "=", this.values.iterator().next());
        } else {
            // The values are sorted, as the iteration order of the set is not stable, even between runs of the same program.
            final String values = this.values.stream().sorted().map(String::valueOf).collect(Collectors.joining(", ", "[", "]"));
            return String.format("%s %s %s", this.columnName(), this.neither ? "not in" : "in", values);
        }
    }

    private final Set<V> values;
    private final boolean neither;
}
