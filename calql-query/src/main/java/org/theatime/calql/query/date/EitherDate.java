/*
 * Copyright 2021-2024 Dai MIKURUBE
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
import java.util.Optional;
import org.theatime.calql.query.date.DateAtom;

public final class EitherDate extends DateAtom {
    private EitherDate(final LocalDate date) {
        this.date = date;
    }

    public static EitherDate of(final LocalDate date) {
        return new EitherDate(date);
    }

    public static EitherDate of(final int year, final int month, final int dayOfMonth) {
        return new EitherDate(LocalDate.of(year, month, dayOfMonth));
    }

    @Override
    public Optional<LocalDate> earliest() {
        return Optional.of(this.date);
    }

    @Override
    public Optional<LocalDate> latest() {
        return Optional.of(this.date);
    }

    @Override
    public Optional<LocalDate> unique() {
        return Optional.of(this.date);
    }

    @Override
    public boolean test(final ChronoLocalDate targetChrono) {
        if (targetChrono instanceof LocalDate) {
            final LocalDate target = (LocalDate) targetChrono;
            return this.date.equals(target);
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
        return NeitherDate.of(this.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EitherDate.class, this.date);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof EitherDate)) {
            return false;
        }

        final EitherDate other = (EitherDate) otherObject;
        return Objects.equals(this.date, other.date);
    }

    @Override
    public String toString() {
        return String.format("date = %s", this.date.toString());
    }

    private final LocalDate date;
}
