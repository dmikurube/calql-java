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

public final class BeforeDayOfMonth extends DateAtom {
    private BeforeDayOfMonth(final int dayOfMonth, final boolean inclusive) {
        this.dayOfMonth = dayOfMonth;
        this.inclusive = inclusive;
    }

    public static BeforeDayOfMonth of(final int dayOfMonth) {
        return new BeforeDayOfMonth(dayOfMonth, false);
    }

    public static BeforeDayOfMonth of(final int dayOfMonth, final boolean inclusive) {
        return new BeforeDayOfMonth(dayOfMonth, inclusive);
    }

    public static BeforeDayOfMonth orEqualTo(final int dayOfMonth) {
        return new BeforeDayOfMonth(dayOfMonth, true);
    }

    @Override
    public boolean test(final ChronoLocalDate targetChrono) {
        if (targetChrono instanceof LocalDate) {
            final LocalDate target = (LocalDate) targetChrono;
            if (this.inclusive) {
                return target.getDayOfMonth() <= this.dayOfMonth;
            } else {
                return target.getDayOfMonth() < this.dayOfMonth;
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
        return AfterDayOfMonth.of(this.dayOfMonth, !this.inclusive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(BeforeDayOfMonth.class, this.dayOfMonth, this.inclusive);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof BeforeDayOfMonth)) {
            return false;
        }

        final BeforeDayOfMonth other = (BeforeDayOfMonth) otherObject;
        return this.dayOfMonth == other.dayOfMonth && this.inclusive == other.inclusive;
    }

    @Override
    public String toString() {
        return String.format("dayOfMonth <%s %d", this.inclusive ? "=" : "", this.dayOfMonth);
    }

    private final int dayOfMonth;
    private final boolean inclusive;
}
