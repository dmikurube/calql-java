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
public final class EitherDayOfMonth extends DateAtom {
    private EitherDayOfMonth(final Set<Integer> daysOfMonth, final boolean includes) {
        this.daysOfMonth = daysOfMonth;
        this.includes = includes;
    }

    static EitherDayOfMonth of(final Collection<Integer> daysOfMonth, final boolean includes) {
        return new EitherDayOfMonth(Set.copyOf(daysOfMonth), includes);
    }

    public static EitherDayOfMonth of(final int dayOfMonth) {
        return new EitherDayOfMonth(Set.of(dayOfMonth), true);
    }

    public static EitherDayOfMonth notOf(final int dayOfMonth) {
        return new EitherDayOfMonth(Set.of(dayOfMonth), false);
    }

    public static EitherDayOfMonth of(final Collection<Integer> daysOfMonth) {
        return of(daysOfMonth, true);
    }

    public static EitherDayOfMonth notOf(final Collection<Integer> daysOfMonth) {
        return of(daysOfMonth, false);
    }

    public static EitherDayOfMonth of(final int... daysOfMonth) {
        return of(Arrays.stream(daysOfMonth).boxed().collect(Collectors.toSet()));
    }

    public static EitherDayOfMonth notOf(final int... daysOfMonth) {
        return notOf(Arrays.stream(daysOfMonth).boxed().collect(Collectors.toSet()));
    }

    @Override
    public boolean test(final ChronoLocalDate targetChrono) {
        if (targetChrono instanceof LocalDate) {
            final LocalDate target = (LocalDate) targetChrono;
            final boolean contains = this.daysOfMonth.contains(target.getDayOfMonth());
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
        return new EitherDayOfMonth(this.daysOfMonth, !this.includes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EitherDayOfMonth.class, this.daysOfMonth, this.includes);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof EitherDayOfMonth)) {
            return false;
        }

        final EitherDayOfMonth other = (EitherDayOfMonth) otherObject;
        return Objects.equals(this.daysOfMonth, other.daysOfMonth) && Objects.equals(this.includes, other.includes);
    }

    @Override
    public String toString() {
        if (this.includes) {
            return String.format("dayOfMonth in %s", this.daysOfMonth);
        } else {
            return String.format("dayOfMonth not in %s", this.daysOfMonth);
        }
    }

    private final Set<Integer> daysOfMonth;
    private final boolean includes;
}
