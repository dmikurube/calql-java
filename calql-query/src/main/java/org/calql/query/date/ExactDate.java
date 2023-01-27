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
import org.calql.query.logic.Atom;

public final class ExactDate extends Atom {
    private ExactDate(final LocalDate date) {
        this.date = date;
    }

    public static Atom of(final LocalDate date) {
        return new ExactDate(date);
    }

    public static Atom of(final int year, final int month, final int dayOfMonth) {
        return new ExactDate(LocalDate.of(year, month, dayOfMonth));
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
    public Atom negate() {
        return RejectDate.of(this.date);
    }

    @Override
    public boolean isTriviallyUnique() {
        return true;
    }

    @Override
    public boolean isTriviallyFinite() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ExactDate.class, this.date);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == ExactDate.class && this.date.equals(((ExactDate) obj).date);
    }

    @Override
    public String toString() {
        return String.format("date = %s", this.date.toString());
    }

    private final LocalDate date;
}
