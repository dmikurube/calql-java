/*
 * Copyright 2021-2023 Dai MIKURUBE
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
import java.time.chrono.ChronoLocalDate;
import java.util.Objects;
import org.calql.query.date.DateAtom;

public final class NeitherDayOfMonth extends DateAtom {
    private NeitherDayOfMonth(final int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public static NeitherDayOfMonth of(final int dayOfMonth) {
        return new NeitherDayOfMonth(dayOfMonth);
    }

    @Override
    public boolean test(final ChronoLocalDate targetChrono) {
        if (targetChrono instanceof LocalDate) {
            final LocalDate target = (LocalDate) targetChrono;
            return this.dayOfMonth != target.getDayOfMonth();
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
        return EitherDayOfMonth.of(this.dayOfMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(NeitherDayOfMonth.class, this.dayOfMonth);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof NeitherDayOfMonth)) {
            return false;
        }

        final NeitherDayOfMonth other = (NeitherDayOfMonth) otherObject;
        return this.dayOfMonth == other.dayOfMonth;
    }

    @Override
    public String toString() {
        return String.format("dayOfMonth != %d", this.dayOfMonth);
    }

    private final int dayOfMonth;
}
