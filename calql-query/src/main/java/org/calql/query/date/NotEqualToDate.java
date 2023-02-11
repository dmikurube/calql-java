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
import org.calql.query.date.DateAtom;

public final class NotEqualToDate extends DateAtom {
    private NotEqualToDate(final LocalDate date) {
        this.date = date;
    }

    public static DateAtom of(final LocalDate date) {
        return new NotEqualToDate(date);
    }

    public static DateAtom of(final int year, final int month, final int dayOfMonth) {
        return new NotEqualToDate(LocalDate.of(year, month, dayOfMonth));
    }

    @Override
    public boolean test(final LocalDate target) {
        return !this.date.equals(target);
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
        return EqualToDate.of(this.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(NotEqualToDate.class, this.date);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == NotEqualToDate.class && this.date.equals(((NotEqualToDate) obj).date);
    }

    @Override
    public String toString() {
        return String.format("date != %s", this.date.toString());
    }

    private final LocalDate date;
}
