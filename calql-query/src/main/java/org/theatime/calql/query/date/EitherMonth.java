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
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.theatime.calql.query.date.DateAtom;

@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public final class EitherMonth extends DateAtom {
    private EitherMonth(final Set<Integer> months, final boolean includes) {
        this.months = months;
        this.includes = includes;
    }

    static EitherMonth of(final Collection<Integer> months, final boolean includes) {
        return new EitherMonth(Set.copyOf(months), includes);
    }

    public static EitherMonth of(final int month) {
        return new EitherMonth(Set.of(month), true);
    }

    public static EitherMonth notOf(final int month) {
        return new EitherMonth(Set.of(month), false);
    }

    public static EitherMonth of(final Collection<Integer> months) {
        return of(months, true);
    }

    public static EitherMonth notOf(final Collection<Integer> months) {
        return of(months, false);
    }

    public static EitherMonth of(final int... months) {
        return of(Arrays.stream(months).boxed().collect(Collectors.toSet()));
    }

    public static EitherMonth notOf(final int... months) {
        return notOf(Arrays.stream(months).boxed().collect(Collectors.toSet()));
    }

    @Override
    public boolean test(final ChronoLocalDate targetChrono) {
        if (targetChrono instanceof LocalDate) {
            final LocalDate target = (LocalDate) targetChrono;
            final boolean contains = this.months.contains(target.getMonthValue());
            if (this.includes) {
                return contains;
            } else {
                return !contains;
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
        return new EitherMonth(this.months, !this.includes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EitherMonth.class, this.months, this.includes);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof EitherMonth)) {
            return false;
        }

        final EitherMonth other = (EitherMonth) otherObject;
        return Objects.equals(this.months, other.months) && Objects.equals(this.includes, other.includes);
    }

    @Override
    public String toString() {
        if (this.includes) {
            return String.format("month in %s", this.months);
        } else {
            return String.format("month not in %s", this.months);
        }
    }

    private final Set<Integer> months;
    private final boolean includes;
}
