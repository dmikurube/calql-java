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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;
import org.calql.query.date.DateAtom;

public final class NotEqualToDayOfWeek extends DateAtom {
    private NotEqualToDayOfWeek(final DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public static NotEqualToDayOfWeek of(final DayOfWeek dayOfWeek) {
        return new NotEqualToDayOfWeek(dayOfWeek);
    }

    public static NotEqualToDayOfWeek of(final int dayOfWeek) {
        return new NotEqualToDayOfWeek(DayOfWeek.of(dayOfWeek));
    }

    @Override
    public boolean test(final LocalDate target) {
        return this.dayOfWeek != target.getDayOfWeek();
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
        return EqualToDayOfWeek.of(this.dayOfWeek);
    }

    @Override
    public int hashCode() {
        return Objects.hash(NotEqualToDayOfWeek.class, this.dayOfWeek);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof NotEqualToDayOfWeek)) {
            return false;
        }

        final NotEqualToDayOfWeek other = (NotEqualToDayOfWeek) otherObject;
        return Objects.equals(this.dayOfWeek, other.dayOfWeek);
    }

    @Override
    public String toString() {
        return String.format("dayOfWeek = %s", this.dayOfWeek.toString());
    }

    private final DayOfWeek dayOfWeek;
}
