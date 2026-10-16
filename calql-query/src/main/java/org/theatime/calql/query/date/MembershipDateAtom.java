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

/**
 * A base of the date atoms that test whether a column of a date is one of the given values, such as {@link EitherMonth}.
 *
 * <p>Its negation is an atom of the same class with the same values, which tests whether the column is none of them.
 *
 * @param <V> the type of the values of the column
 */
abstract class MembershipDateAtom<V> extends DateAtom {
    MembershipDateAtom(final Set<V> values, final boolean includes) {
        this.values = values;
        this.includes = includes;
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

    public final boolean includes() {
        return this.includes;
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
        final boolean contains = this.values.contains(this.columnValueOf(target));
        if (this.includes) {
            return contains;
        } else {
            return !contains;
        }
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.getClass(), this.values, this.includes);
    }

    @Override
    public final boolean equals(final Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        // Compares the classes, not only instanceof, so that EitherMonth.of(2) does not equal EitherDayOfMonth.of(2).
        if (otherObject != null && otherObject.getClass() == this.getClass()) {
            final MembershipDateAtom<?> other = (MembershipDateAtom<?>) otherObject;
            return Objects.equals(this.values, other.values) && this.includes == other.includes;
        }

        return false;
    }

    @Override
    public final String toString() {
        if (this.values.size() == 1) {
            return String.format("%s %s %s", this.columnName(), this.includes ? "=" : "<>", this.values.iterator().next());
        } else {
            return String.format("%s %s %s", this.columnName(), this.includes ? "in" : "not in", this.values);
        }
    }

    private final Set<V> values;
    private final boolean includes;
}
