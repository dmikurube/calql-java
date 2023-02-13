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
import java.util.Objects;
import java.util.Optional;
import org.calql.query.date.DateAtom;

public final class EqualToDate extends DateAtom {
    private EqualToDate(final LocalDate date) {
        this.date = date;
    }

    public static EqualToDate of(final LocalDate date) {
        return new EqualToDate(date);
    }

    public static EqualToDate of(final int year, final int month, final int dayOfMonth) {
        return new EqualToDate(LocalDate.of(year, month, dayOfMonth));
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
    public boolean test(final LocalDate target) {
        return this.date.equals(target);
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
        return NotEqualToDate.of(this.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EqualToDate.class, this.date);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == EqualToDate.class && this.date.equals(((EqualToDate) obj).date);
    }

    @Override
    public String toString() {
        return String.format("date = %s", this.date.toString());
    }

    private final LocalDate date;
}
