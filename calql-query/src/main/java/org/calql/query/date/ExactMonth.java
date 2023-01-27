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

import java.util.Objects;
import org.calql.query.logic.Atom;

public final class ExactMonth extends Atom {
    private ExactMonth(final int month) {
        this.month = month;
    }

    public static Atom of(final int month) {
        return new ExactMonth(month);
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
        return RejectMonth.of(this.month);
    }

    @Override
    public boolean isTriviallyUnique() {
        return false;
    }

    @Override
    public boolean isTriviallyFinite() {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ExactMonth.class, this.month);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass() == ExactMonth.class && this.month == ((ExactMonth) obj).month;
    }

    @Override
    public String toString() {
        return String.format("month = %d", this.month);
    }

    private final int month;
}
