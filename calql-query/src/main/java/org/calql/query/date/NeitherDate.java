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

public final class NeitherDate extends DateAtom {
    private NeitherDate(final LocalDate date) {
        this.date = date;
    }

    public static NeitherDate of(final LocalDate date) {
        return new NeitherDate(date);
    }

    public static NeitherDate of(final int year, final int month, final int dayOfMonth) {
        return new NeitherDate(LocalDate.of(year, month, dayOfMonth));
    }

    @Override
    public boolean test(final ChronoLocalDate targetChrono) {
        if (targetChrono instanceof LocalDate) {
            final LocalDate target = (LocalDate) targetChrono;
            return !this.date.equals(target);
        }
        return false;
    }

    /**
     * Negates this atom.
     *
     * <p>It negates the atom.
     *
     * @return the negated atom
     */
    @Override
    public DateAtom negate() {
        return EitherDate.of(this.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(NeitherDate.class, this.date);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof NeitherDate)) {
            return false;
        }

        final NeitherDate other = (NeitherDate) otherObject;
        return Objects.equals(this.date, other.date);
    }

    @Override
    public String toString() {
        return String.format("date != %s", this.date.toString());
    }

    private final LocalDate date;
}
