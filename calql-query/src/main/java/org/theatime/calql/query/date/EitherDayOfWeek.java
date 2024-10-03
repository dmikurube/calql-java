/*
 * Copyright 2022-2024 Dai MIKURUBE
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.theatime.calql.query.date.DateAtom;

@SuppressWarnings("checkstyle:OverloadMethodsDeclarationOrder")
public final class EitherDayOfWeek extends DateAtom {
    private EitherDayOfWeek(final Set<DayOfWeek> daysOfWeek, final boolean includes) {
        this.daysOfWeek = daysOfWeek;
        this.includes = includes;
    }

    static EitherDayOfWeek of(final Collection<DayOfWeek> daysOfWeek, final boolean includes) {
        return new EitherDayOfWeek(Set.copyOf(daysOfWeek), includes);
    }

    public static EitherDayOfWeek of(final DayOfWeek dayOfWeek) {
        return new EitherDayOfWeek(Set.of(dayOfWeek), true);
    }

    public static EitherDayOfWeek notOf(final DayOfWeek dayOfWeek) {
        return new EitherDayOfWeek(Set.of(dayOfWeek), false);
    }

    public static EitherDayOfWeek of(final Collection<DayOfWeek> daysOfWeek) {
        return of(daysOfWeek, true);
    }

    public static EitherDayOfWeek notOf(final Collection<DayOfWeek> daysOfWeek) {
        return of(daysOfWeek, false);
    }

    public static EitherDayOfWeek of(final DayOfWeek... daysOfWeek) {
        return new EitherDayOfWeek(Set.of(daysOfWeek), true);
    }

    public static EitherDayOfWeek notOf(final DayOfWeek... daysOfWeek) {
        return new EitherDayOfWeek(Set.of(daysOfWeek), false);
    }

    @Override
    public boolean test(final ChronoLocalDate targetChrono) {
        if (targetChrono instanceof LocalDate) {
            final LocalDate target = (LocalDate) targetChrono;
            final boolean contains = this.daysOfWeek.contains(target.getDayOfWeek());
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
        return new EitherDayOfWeek(this.daysOfWeek, !this.includes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(EitherDayOfWeek.class, this.daysOfWeek, this.includes);
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof EitherDayOfWeek)) {
            return false;
        }

        final EitherDayOfWeek other = (EitherDayOfWeek) otherObject;
        return Objects.equals(this.daysOfWeek, other.daysOfWeek) && Objects.equals(this.includes, other.includes);
    }

    @Override
    public String toString() {
        if (this.includes) {
            return String.format("dayOfWeek in %s", this.daysOfWeek);
        } else {
            return String.format("dayOfWeek not in %s", this.daysOfWeek);
        }
    }

    private final Set<DayOfWeek> daysOfWeek;
    private final boolean includes;
}
