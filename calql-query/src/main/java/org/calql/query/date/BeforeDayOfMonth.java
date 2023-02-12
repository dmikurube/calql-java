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
import org.calql.query.date.DateAtom;

public final class BeforeDayOfMonth extends DateAtom {
    private BeforeDayOfMonth(final int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public static DateAtom of(final int dayOfMonth) {
        return new BeforeDayOfMonth(dayOfMonth);
    }

    @Override
    public boolean test(final LocalDate target) {
        return this.dayOfMonth < target.getDayOfMonth();
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
        return AfterOrEqualToDayOfMonth.of(this.dayOfMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(BeforeDayOfMonth.class, this.dayOfMonth);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == BeforeDayOfMonth.class && this.dayOfMonth == ((BeforeDayOfMonth) obj).dayOfMonth;
    }

    @Override
    public String toString() {
        return String.format("dayOfMonth < %d", this.dayOfMonth);
    }

    private final int dayOfMonth;
}
