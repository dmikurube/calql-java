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

/**
 * A base of the date atoms that test whether a column of a date is after or before a value in its order, such as
 * {@link AfterMonth} and {@link BeforeMonth}.
 *
 * <p>Its negation is an atom of the opposite direction with the opposite inclusiveness. For example, the negation of
 * {@code month > 6} is {@code month <= 6}.
 */
abstract class OrderingDateAtom extends DateAtom {
    OrderingDateAtom(final int value, final boolean inclusive, final boolean after) {
        this.value = value;
        this.inclusive = inclusive;
        this.after = after;
    }

    /**
     * Returns the name of the column, such as {@code "month"}.
     */
    protected abstract String columnName();

    /**
     * Returns the value of the column for the date, such as its month.
     */
    protected abstract int columnValueOf(LocalDate date);

    final int value() {
        return this.value;
    }

    final boolean inclusive() {
        return this.inclusive;
    }

    @Override
    public final boolean test(final LocalDate target) {
        final int compared = Integer.compare(this.columnValueOf(target), this.value);
        if (compared == 0) {
            return this.inclusive;
        }
        if (this.after) {
            return compared > 0;
        } else {
            return compared < 0;
        }
    }

    @Override
    public final int hashCode() {
        return Objects.hash(this.getClass(), this.value, this.inclusive);
    }

    @Override
    public final boolean equals(final Object otherObject) {
        if (otherObject == this) {
            return true;
        }

        // Compares the classes, not only instanceof, so that AfterMonth.of(6) does not equal BeforeMonth.of(6).
        if (otherObject != null && otherObject.getClass() == this.getClass()) {
            final OrderingDateAtom other = (OrderingDateAtom) otherObject;
            return this.value == other.value && this.inclusive == other.inclusive;
        }

        return false;
    }

    @Override
    public final String toString() {
        return String.format("%s %s%s %d", this.columnName(), this.after ? ">" : "<", this.inclusive ? "=" : "", this.value);
    }

    private final int value;
    private final boolean inclusive;
    private final boolean after;
}
