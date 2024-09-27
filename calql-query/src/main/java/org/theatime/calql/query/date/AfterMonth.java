/*
 * Copyright 2022-2024 Dai MIKURUBE
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
import java.time.chrono.ChronoLocalDate;
import java.util.Objects;
import org.theatime.calql.query.date.DateAtom;

public final class AfterMonth extends DateAtom {
    private AfterMonth(final int month, final boolean inclusive) {
        this.month = month;
        this.inclusive = inclusive;
    }

    public static AfterMonth of(final int month) {
        return new AfterMonth(month, false);
    }

    public static AfterMonth of(final int month, final boolean inclusive) {
        return new AfterMonth(month, inclusive);
    }

    public static AfterMonth orEqualTo(final int month) {
        return new AfterMonth(month, true);
    }

    @Override
    public boolean test(final ChronoLocalDate targetChrono) {
        if (targetChrono instanceof LocalDate) {
            final LocalDate target = (LocalDate) targetChrono;
            if (this.inclusive) {
                return this.month >= target.getMonthValue();
            } else {
                return this.month > target.getMonthValue();
            }
        }
        return false;
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
        return BeforeMonth.of(this.month, !this.inclusive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(AfterMonth.class, this.month, this.inclusive);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof AfterMonth)) {
            return false;
        }

        final AfterMonth other = (AfterMonth) otherObject;
        return this.month == other.month && this.inclusive == other.inclusive;
    }

    @Override
    public String toString() {
        return String.format("month >%s %d", this.inclusive ? "=" : "", this.month);
    }

    private final int month;
    private final boolean inclusive;
}
