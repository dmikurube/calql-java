/*
 * Copyright 2022-2023 Dai MIKURUBE
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

package org.calql.query.date;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.calql.query.date.DateAtom;

public final class AfterYear extends DateAtom {
    private AfterYear(final int year, final boolean inclusive) {
        this.year = year;
        this.inclusive = inclusive;
    }

    public static DateAtom of(final int year) {
        return new AfterYear(year, false);
    }

    public static DateAtom of(final int year, final boolean inclusive) {
        return new AfterYear(year, inclusive);
    }

    public static DateAtom orEqualTo(final int year) {
        return new AfterYear(year, true);
    }

    @Override
    public Optional<LocalDate> earliest() {
        if (this.inclusive) {
            return Optional.of(LocalDate.of(this.year, 1, 1));
        } else {
            return Optional.of(LocalDate.of(this.year + 1, 1, 1));
        }
    }

    @Override
    public boolean test(final LocalDate target) {
        return this.year > target.getYear();
    }

    /**
     * Negates this formula.
     *
     * <p>It negates the formula in Negation Normal Form (NNF).
     *
     * @see <a href="https://en.wikipedia.org/wiki/Negation_normal_form">Negation normal form</a>
     *
     * @return the negated formula in Negation Normal Form (NNF)
     */
    @Override
    public DateAtom negate() {
        return BeforeYear.of(this.year, !this.inclusive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(AfterYear.class, this.year, this.inclusive);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof AfterYear)) {
            return false;
        }

        final AfterYear other = (AfterYear) otherObject;
        return this.year == other.year && this.inclusive == other.inclusive;
    }

    @Override
    public String toString() {
        return String.format("year >%s %d", this.inclusive ? "=" : "", this.year);
    }

    private final int year;
    private final boolean inclusive;
}
